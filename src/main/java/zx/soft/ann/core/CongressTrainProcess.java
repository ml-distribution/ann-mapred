package zx.soft.ann.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import zx.soft.ann.core.exception.StopMapperException;
import zx.soft.ann.core.framework.MnemosyneProcess;
import zx.soft.ann.core.model.Artifact;
import zx.soft.ann.core.model.InputSet;
import zx.soft.ann.core.model.NNInput;
import zx.soft.ann.core.model.NNOutput;
import zx.soft.ann.core.model.Neuron;
import zx.soft.ann.core.model.OutputSet;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.HadoopForeman;
import zx.soft.ann.core.util.foreman.TrainForeman;

public class CongressTrainProcess implements MnemosyneProcess {

	private static int round = 0;

	@Override
	public void process() throws ProcessException {
		List<Artifact> artifacts = artifactForeman.returnArtifacts();
		for (Artifact artifact : artifacts) {
			HadoopForeman hForeman = new HadoopForeman();
			HadoopJobConfiguration conf = new HadoopJobConfiguration();
			conf.setJobName(HadoopJobConfiguration.buildJobName(this.getClass()));
			conf.setMapperClass(CongressTrainMapper.class);
			conf.setJarClass(this.getClass());
			conf.overrideDefaultTable(AccumuloForeman.getArtifactRepositoryName());
			Collection<Pair<Text, Text>> cfPairs = new ArrayList<>();
			cfPairs.add(new Pair<Text, Text>(new Text(artifact.getArtifactId() + ":FIELD"), null));
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

	public static class CongressTrainMapper extends Mapper<Key, Value, Writable, Writable> {

		private static final Logger log = Logger.getLogger(CongressTrainMapper.class.getName());
		private AccumuloForeman aForeman = new AccumuloForeman();
		private TrainForeman tForeman = new TrainForeman();

		@Override
		public void map(Key ik, Value iv, Context context) {
			try {
				System.out.println(iv.toString());
				aForeman.connect();
				tForeman.connect();
				String artifactId = ik.getRow().toString();
				CongressNetworkConf conf = aForeman.inflateCongressConfiguration(artifactId);
				int numOfInputs = aForeman.getCongressNumberOfInputs(artifactId);
				System.out.println(round);
				if (round % 2 == 1) {
					double[] input = NNInput.inflate(conf, iv.toString(), numOfInputs);
					double[] output = NNOutput.inflate(iv.toString());
					tForeman.register(ik.getRow().toString(), input, output);
					round++;
				} else {
					log.log(Level.INFO, "Grabbing a random neuron...");
					Neuron toTrain = aForeman.getATrainableNeuron(artifactId);
					log.log(Level.INFO, "FOUND THIS ONE" + toTrain.getHash());

					InputSet<Integer> is = new InputSet<>();
					OutputSet<Integer> os = new OutputSet<>();
					//num of inputs = number of weights
					//num of outputs = 2

					double[] input = NNInput.inflate(conf, iv.toString(), numOfInputs);
					List<Integer> set = new ArrayList<>();
					for (double in : input) {
						set.add((int) in);
					}
					is.addSet(set);
					double[] output = NNOutput.inflate(iv.toString());
					List<Integer> outset = new ArrayList<>();
					for (double out : output) {
						outset.add((int) out);
					}
					os.addSet(outset);
					double aerror = .0002;
					toTrain.train(is, os, aerror, 30000);
					aForeman.setNeuronAvailable(artifactId, toTrain);
					aForeman.incrementNumberProcessed(artifactId, toTrain.getHash());
					round++;
				}

			} catch (Exception e) {
				String gripe = CongressTrainProcess.class.getSimpleName() + " failed!";
				throw new StopMapperException(gripe, e);
			}
		}

	}

}
