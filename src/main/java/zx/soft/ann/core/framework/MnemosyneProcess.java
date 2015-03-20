package zx.soft.ann.core.framework;

import zx.soft.ann.core.exception.ProcessException;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.ArtifactForeman;

public interface MnemosyneProcess {

	AccumuloForeman aForeman = new AccumuloForeman();
	ArtifactForeman artifactForeman = new ArtifactForeman();

	public void process() throws ProcessException;

	public void setup() throws ProcessException;

}
