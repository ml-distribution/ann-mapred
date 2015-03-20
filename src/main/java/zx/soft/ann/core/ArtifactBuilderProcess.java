package zx.soft.ann.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.accumulo.core.client.mapreduce.AccumuloOutputFormat;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.Pair;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;

import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.DataspaceException;
import zx.soft.ann.core.exception.ProcessException;
import zx.soft.ann.core.framework.MnemosyneProcess;
import zx.soft.ann.core.model.Artifact;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.HadoopForeman;

/**
 * 构建和持久化人工网络数据（从@IngestProcess）到ARTIFACT_TABLE中
 *
 * @author wanggang
 *
 */
public class ArtifactBuilderProcess implements MnemosyneProcess {

	/**
	 * Calls the MR Process to look over RAW BYTES asserted from IngestProcess in the ARTIFACT_TABLE
	 * The MR Process created ARTIFACT_ENTRYs which the ArtifactForman then persists
	 */
	@Override
	public void process() throws ProcessException {
		HadoopForeman hForeman = new HadoopForeman();
		HadoopJobConfiguration conf = new HadoopJobConfiguration();

		conf.setJobName(HadoopJobConfiguration.buildJobName(this.getClass()));
		conf.setMapperClass(ArtifactBuilderMapper.class);
		conf.overrideDefaultTable(AccumuloForeman.getArtifactRepositoryName());

		Collection<Pair<Text, Text>> cfPairs = new ArrayList<Pair<Text, Text>>();
		cfPairs.add(new Pair<Text, Text>(new Text(AccumuloForeman.getArtifactRepository().rawBytes()), null));
		conf.setFetchColumns(cfPairs);
		conf.setJarClass(this.getClass());
		conf.setInputFormatClass(AccumuloInputFormat.class);
		conf.setOutputFormatClass(AccumuloOutputFormat.class);

		hForeman.runJob(conf);

		List<Artifact> artifacts = artifactForeman.returnArtifacts();
		artifactForeman.persistArtifacts();
		for (Artifact artifact : artifacts) {
			String definitions = artifact.grabDefinitions();
			aForeman.add(AccumuloForeman.getArtifactRepositoryName(), artifact.getArtifactId(),
					artifact.getArtifactId(), "DEFINITIONS", definitions);
			List<String> sets = artifact.grabSets();
			for (int j = 0; j < sets.size(); j++) {
				aForeman.add(AccumuloForeman.getArtifactRepositoryName(), artifact.getArtifactId(),
						artifact.getArtifactId() + ":" + "FIELD", "SET" + j, sets.get(j));
			}
		}
	}

	/**
	 * Connect to the ArtifactForman
	 */
	@Override
	public void setup() throws DataspaceException {
		aForeman.connect();
		artifactForeman.connect();
	}

	/**
	 * For every RAW_BYTES entry in ARTIFACT_TABLE, register that artifact with the artifact foreman
	 */
	public static class ArtifactBuilderMapper extends Mapper<Key, Value, Writable, Writable> {

		@Override
		public void map(Key ik, Value iv, Context context) {
			String row = ik.getRow().toString();//This is the Artifact ID
			int lineNumber = Integer.parseInt(ik.getColumnQualifier().toString());
			String raw = iv.toString();
			artifactForeman.register(row, lineNumber, raw);
		}

	}

}
