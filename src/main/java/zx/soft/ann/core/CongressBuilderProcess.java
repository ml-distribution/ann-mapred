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

import zx.soft.ann.conf.CongressNetworkConf;
import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.ProcessException;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.exception.StopMapperException;
import zx.soft.ann.core.framework.MnemosyneProcess;
import zx.soft.ann.core.framework.NNProcessor;
import zx.soft.ann.core.model.Artifact;
import zx.soft.ann.core.model.NNMetadata;
import zx.soft.ann.core.util.BinaryUtils;
import zx.soft.ann.core.util.factory.NNProcessorFactory;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.HadoopForeman;

public class CongressBuilderProcess implements MnemosyneProcess {

	@Override
	public void process() throws ProcessException {
		List<Artifact> artifacts = artifactForeman.returnArtifacts();
		for (Artifact artifact : artifacts) {
			HadoopForeman hForeman = new HadoopForeman();
			HadoopJobConfiguration conf = new HadoopJobConfiguration();
			conf.setJobName(HadoopJobConfiguration.buildJobName(CongressBuilderProcess.class));
			conf.setMapperClass(CongressBuilderMapper.class);
			conf.setJarClass(this.getClass());
			conf.overrideDefaultTable(AccumuloForeman.getArtifactRepositoryName());
			Collection<Pair<Text, Text>> cfPairs = new ArrayList<Pair<Text, Text>>();
			cfPairs.add(new Pair<Text, Text>(new Text(artifact.getArtifactId()), null));
			conf.setFetchColumns(cfPairs);
			conf.setInputFormatClass(AccumuloInputFormat.class);
			conf.setOutputFormatClass(AccumuloOutputFormat.class);
			hForeman.runJob(conf);
		}

	}

	@Override
	public void setup() throws ProcessException {
		artifactForeman.connect();
	}

	public static class CongressBuilderMapper extends Mapper<Key, Value, Writable, Writable> {

		@Override
		public void map(Key ik, Value iv, Context context) {
			NNMetadata metadata = NNMetadata.inflate(iv.toString(), ik.getRow().toString());
			int inputNeuronCount = BinaryUtils.sizeOfBinary(metadata.getInputMax())
					* metadata.getInputNameFields().size();
			//create a couple hundred neurons
			CongressNetworkConf conf = new CongressNetworkConf();
			conf.setNumberOfInputs(inputNeuronCount);
			conf.setNumberOfNeurons(20);

			try {
				aForeman.assertCongressNumberOfInputs(metadata.getArtifactId(), metadata.getInputMax());
				NNProcessor processor = NNProcessorFactory.getProcessorBean(conf);
				processor.constructNetworks(metadata.getArtifactId());
			} catch (RepositoryException e) {
				String gripe = "Access to the Repository Services died";
				//				log.log(Level.SEVERE,gripe,e);
				throw new StopMapperException(gripe, e);
			}
		}

	}

}
