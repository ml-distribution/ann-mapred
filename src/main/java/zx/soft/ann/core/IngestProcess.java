package zx.soft.ann.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.ProcessException;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.exception.StopMapperException;
import zx.soft.ann.core.framework.MnemosyneProcess;
import zx.soft.ann.core.util.MnemosyneConstants;
import zx.soft.ann.core.util.factory.ArtifactIdFactory;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.HadoopForeman;

/**
 * 读取ingest中的xml文件，数据用于神经网络输入和输出
 *
 * @author wanggang
 *
 */
public class IngestProcess implements MnemosyneProcess {

	/**
	 * The UUID used to make an artifact id
	 */
	static String uuid;
	static List<Integer> linesProcessed = new ArrayList<Integer>();
	static String fileName = "";
	static Logger log = Logger.getLogger(IngestProcess.class.getName());

	/**
	 * Multiple files to ingest
	 */
	static Path[] pathsToProcess;

	/**
	 * @see MnemosyneProcess
	 */
	@Override
	public void setup() throws ProcessException {
		aForeman.connect();
		pathsToProcess = MnemosyneConstants.getAllIngestableFiles();
	}

	/**
	 * for every path
	 * grab every line of the file, and throw it into accumulo
	 */
	@Override
	@SuppressWarnings("static-access")
	public void process() throws ProcessException {
		for (Path path : pathsToProcess) {
			uuid = UUID.randomUUID().toString();
			this.fileName = path.getName();
			HadoopForeman hForeman = new HadoopForeman();
			HadoopJobConfiguration conf = new HadoopJobConfiguration();

			conf.setJarClass(this.getClass());
			conf.setMapperClass(IngestMapper.class);
			conf.setInputFormatClass(TextInputFormat.class);
			conf.overridePathToProcess(path);
			conf.setOutputFormatClass(NullOutputFormat.class);
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(IntWritable.class);
			hForeman.runJob(conf);

		}

	}

	/**
	 * Throws every line of a file into accumulo
	 * @author cam
	 *
	 */
	public static class IngestMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		@Override
		public void map(LongWritable ik, Text iv, Context context) {
			try {
				aForeman.add(AccumuloForeman.getArtifactRepositoryName(), ArtifactIdFactory.buildArtifactId(uuid,
						fileName), AccumuloForeman.getArtifactRepository().rawBytes(), ik.toString(), iv.toString());
			} catch (RepositoryException e) {
				String gripe = "Could not access the Repository Services";
				log.log(Level.SEVERE, gripe, e);
				throw new StopMapperException(gripe, e);
			}

		}
	}

}
