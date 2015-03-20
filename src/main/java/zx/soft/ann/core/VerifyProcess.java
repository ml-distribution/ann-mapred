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
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.ProcessException;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.framework.MnemosyneProcess;
import zx.soft.ann.core.model.Artifact;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.HadoopForeman;

@SuppressWarnings("unused")
public class VerifyProcess implements MnemosyneProcess {

	//	private static final Logger logger = Logger.getLogger(VerifyProcess.class.getName());

	@Override
	public void process() throws ProcessException {
		artifactForeman.connect();
		List<Artifact> artifacts = artifactForeman.returnArtifacts();
		for (Artifact artifact : artifacts) {
			HadoopForeman hForeman = new HadoopForeman();
			HadoopJobConfiguration conf = new HadoopJobConfiguration();
			conf.setJobName(HadoopJobConfiguration.buildJobName(this.getClass()));
			conf.setMapperClass(VerifyMapper.class);
			conf.setJarClass(this.getClass());
			conf.overrideDefaultTable(AccumuloForeman.getArtifactRepositoryName());
			Collection<Pair<Text, Text>> cfPairs = new ArrayList<Pair<Text, Text>>();
			cfPairs.add(new Pair<Text, Text>(new Text(AccumuloForeman.getArtifactRepository().verifyEntry()), null));
			conf.setFetchColumns(cfPairs);
			conf.setInputFormatClass(AccumuloInputFormat.class);
			conf.setOutputFormatClass(AccumuloOutputFormat.class);
			hForeman.runJob(conf);
		}
	}

	@Override
	public void setup() throws ProcessException {
		aForeman.connect();
	}

	public static class VerifyMapper extends Mapper<Key, Value, Writable, Writable> {
		@Override
		public void map(Key ik, Value iv, Context context) {
			//inflate the appropriate NN
			//this is the ArtifactId

			String artifactId = ik.getRow().toString();
			//VERIFY_ENTRY
			ik.getColumnFamily().toString();
			//timestamp
			ik.getColumnQualifier().toString();

			//input | output
			String inOut = iv.toString();

			double[] input = getInput(inOut);
			System.out.println("FOR INPUT:");
			for (double in : input) {
				System.out.print(in + " ");
			}
			System.out.println();
			double output = getOutput(inOut);

			try {
				BasicNetwork[] ntws = aForeman.getBaseNetworkCommittees(ik.getRow().toString());
				for (BasicNetwork ntw : ntws) {
					MLData out = ntw.compute(new BasicMLData(input));
					double[] expected = out.getData();

					for (double expect : expected) {
						System.out.print(expect + " ");
					}
					System.out.println();
					System.out.println(" Expected:" + output);
					aForeman.associateOutput(artifactId, expected, output);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}

			//use input/output to get result
			//save result

		}

		private double getOutput(String inOut) {
			String value = inOut.split("\\|")[1];
			String[] outs = value.split(",");
			String binary = "";
			for (String out : outs) {
				binary += (int) Double.parseDouble(out) + "";
			}
			int toReturn = Integer.parseInt(binary, 2);
			return toReturn;
		}

		private double[] getInput(String inOut) {
			String[] ins = inOut.split("\\|")[0].split(",");
			double[] toReturn = new double[ins.length];
			for (int i = 0; i < toReturn.length; i++) {
				toReturn[i] = Double.parseDouble(ins[i]);
			}
			return toReturn;
		}

	}

}
