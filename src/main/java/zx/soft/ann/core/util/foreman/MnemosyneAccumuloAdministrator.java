package zx.soft.ann.core.util.foreman;

import zx.soft.ann.core.exception.RepositoryException;

public class MnemosyneAccumuloAdministrator {

	public static void main(String[] args) throws RepositoryException {
		MnemosyneAccumuloAdministrator.setup();
	}

	public static void setup() throws RepositoryException {
		System.out.println("Starting Accumulo Setup");
		AccumuloForeman aForeman = new AccumuloForeman();
		aForeman.connect();
		aForeman.deleteTables();
		aForeman.makeTable(AccumuloForeman.getBaseNetworkRepositoryName());
		aForeman.makeTable(AccumuloForeman.getArtifactRepositoryName());
		aForeman.makeTable("CONGRESS");
	}

}
