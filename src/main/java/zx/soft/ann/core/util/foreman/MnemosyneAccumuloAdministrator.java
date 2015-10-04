package zx.soft.ann.core.util.foreman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.ann.core.exception.RepositoryException;

public class MnemosyneAccumuloAdministrator {

	private static Logger logger = LoggerFactory.getLogger(MnemosyneAccumuloAdministrator.class);

	public static void main(String[] args) throws RepositoryException {
		MnemosyneAccumuloAdministrator.setup();
	}

	public static void setup() throws RepositoryException {
		logger.info("Starting Accumulo Setup");
		AccumuloForeman aForeman = new AccumuloForeman();
		aForeman.connect();
		aForeman.deleteTables();
		aForeman.makeTable(AccumuloForeman.getBaseNetworkRepositoryName());
		aForeman.makeTable(AccumuloForeman.getArtifactRepositoryName());
		aForeman.makeTable("CONGRESS");
	}

}
