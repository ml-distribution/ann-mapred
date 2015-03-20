package zx.soft.ann.core.framework;

import zx.soft.ann.core.exception.RepositoryException;

public abstract class NNProcessor {

	public abstract void constructNetworks(String artifactId) throws RepositoryException;

}
