package zx.soft.ann.core;

import java.util.List;

import zx.soft.ann.conf.CongressNetworkConf;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.framework.CongressNetwork;
import zx.soft.ann.core.framework.NNProcessor;
import zx.soft.ann.core.model.Neuron;
import zx.soft.ann.core.util.foreman.AccumuloForeman;

public class CongressNNProcessor extends NNProcessor {

	/**
	 * The definitions of the Classification Network (passed)
	 */
	private CongressNetworkConf conf;

	/**
	 * An instance of the Accumulo Interface
	 */
	private AccumuloForeman aForeman = new AccumuloForeman();

	public CongressNNProcessor(CongressNetworkConf conf) throws RepositoryException {
		this.conf = conf;
		aForeman.connect();
	}

	@Override
	public void constructNetworks(String artifactId) throws RepositoryException {
		List<Neuron> neuronsCreated = CongressNetwork.constructNetworks(conf);
		aForeman.assertCongress(neuronsCreated, artifactId, conf);
	}

}
