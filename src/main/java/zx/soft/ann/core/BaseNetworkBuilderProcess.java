package zx.soft.ann.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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
import org.encog.engine.network.activation.ActivationSigmoid;

import zx.soft.ann.conf.ClassificationNetworkConf;
import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.ArtifactException;
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

/**
 * 构建基本的神经网络
 *
 * @author wanggang
 *
 */
public class BaseNetworkBuilderProcess implements MnemosyneProcess {

	private final static Logger log = Logger.getLogger(BaseNetworkBuilderProcess.class.getName());

	/**
	 * For every artifact, Build a base network in Accumulo
	 */
	@Override
	public void process() throws ProcessException {
		List<Artifact> artifacts = artifactForeman.returnArtifacts();
		for (Artifact artifact : artifacts) {
			HadoopForeman hForeman = new HadoopForeman();
			HadoopJobConfiguration conf = new HadoopJobConfiguration();
			conf.setJobName(HadoopJobConfiguration.buildJobName(BaseNetworkBuilderProcess.class));
			conf.setMapperClass(BaseNetworkBuilderMapper.class);
			conf.overrideDefaultTable(AccumuloForeman.getArtifactRepositoryName());
			conf.setJarClass(this.getClass());
			Collection<Pair<Text, Text>> cfPairs = new ArrayList<Pair<Text, Text>>();
			cfPairs.add(new Pair<Text, Text>(new Text(artifact.getArtifactId()), null));
			conf.setFetchColumns(cfPairs);
			conf.setInputFormatClass(AccumuloInputFormat.class);
			conf.setOutputFormatClass(AccumuloOutputFormat.class);
			hForeman.runJob(conf);
		}
	}

	/**
	 * Connect to the artifact foreman
	 */
	@Override
	public void setup() throws ArtifactException {
		artifactForeman.connect();
	}

	/**
	 * Mapper to inflate the inputs, outputs and metadata from ingest, then create a network and asser it
	 */
	public static class BaseNetworkBuilderMapper extends Mapper<Key, Value, Writable, Writable> {

		@Override
		public void map(Key ik, Value iv, Context context) {
			NNMetadata metadata = NNMetadata.inflate(iv.toString(), ik.getRow().toString());
			int inputNeuronCount = BinaryUtils.toBinary(metadata.getInputMax(),
					new double[] { metadata.getInputMax() }, true).length;
			int num = BinaryUtils.toBinary(metadata.getOutputMax(), new double[] { metadata.getOutputMax() }, false).length;
			int categories = metadata.getOutputNameFields().size();

			ClassificationNetworkConf conf = new ClassificationNetworkConf();
			conf.setInputMax(metadata.getInputMax());
			conf.setOutputMax(metadata.getOutputMax());
			conf.setInputActivation(null);
			conf.setInputBias(true);
			conf.setInputNeuronCount(inputNeuronCount);

			conf.setHiddenActiviation(new ActivationSigmoid());
			conf.setHiddenBias(true);
			conf.setHiddenNeuronCount(2 ^ categories);
			conf.setOutputActivation(new ActivationSigmoid());
			conf.setOutputNeuronCount(num);

			conf.setNumberOfCategories(categories);//FIXME:This is bogus now
			conf.setBasicMLInput(this.getRandomArray(inputNeuronCount));//FIXME:This is bogus now
			conf.setBasicIdealMLOutput(this.getRandomArray(num));//FIXME:This is bogus now

			try {
				NNProcessor processor = NNProcessorFactory.getProcessorBean(conf);
				processor.constructNetworks(metadata.getArtifactId());
			} catch (RepositoryException e) {
				String gripe = "Access to the Repository Services died";
				log.log(Level.SEVERE, gripe, e);
				throw new StopMapperException(gripe, e);
			}

		}

		private double[][] getRandomArray(int inputNeuronCount) {
			double[][] toReturn = new double[1][inputNeuronCount];
			for (int i = 0; i < inputNeuronCount; i++) {
				toReturn[0][i] = new Random().nextDouble();
			}
			return toReturn;
		}

	}

}
