package zx.soft.ann.core.util.factory;

import zx.soft.ann.conf.ClassificationNetworkConf;
import zx.soft.ann.conf.CongressNetworkConf;
import zx.soft.ann.conf.NetworkConf;
import zx.soft.ann.core.ClassificationNNProcessor;
import zx.soft.ann.core.CongressNNProcessor;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.framework.NNProcessor;

public class NNProcessorFactory {

	public static NNProcessor getProcessorBean(NetworkConf conf) throws RepositoryException {
		if (conf instanceof ClassificationNetworkConf) {
			return new ClassificationNNProcessor((ClassificationNetworkConf) conf);
		} else if (conf instanceof CongressNetworkConf) {
			return new CongressNNProcessor((CongressNetworkConf) conf);
		}

		return null;
	}

}
