package zx.soft.ann.core;

import org.encog.neural.networks.BasicNetwork;

import zx.soft.ann.conf.ClassificationNetworkConf;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.framework.ClassificationNetwork;
import zx.soft.ann.core.framework.NNProcessor;
import zx.soft.ann.core.util.foreman.AccumuloForeman;

public class ClassificationNNProcessor extends NNProcessor {

	/**
	 * The definitions of the Classification Network (passed)
	 */
	private ClassificationNetworkConf conf;

	/**
	 * An instance of the Accumulo Interface
	 */
	private AccumuloForeman aForeman = new AccumuloForeman();

	/**
	 * Constructs a Classification Network given a configuration
	 * @param conf2
	 * @throws RepositoryException
	 */
	public ClassificationNNProcessor(ClassificationNetworkConf conf2) throws RepositoryException {
		this.conf = conf2;
		aForeman.connect();
	}

	/**
	 * Constructs a Classification Network given the ArtifactId
	 */
	@Override
	public void constructNetworks(String artifactId) throws RepositoryException {
		BasicNetwork network = ClassificationNetwork.constructNetworks(conf);
		aForeman.assertBaseNetwork(network, artifactId, conf);
	}

}
