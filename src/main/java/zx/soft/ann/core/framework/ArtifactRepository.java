package zx.soft.ann.core.framework;

import zx.soft.ann.core.util.MnemosyneConstants;

public class ArtifactRepository implements AccumuloTable {

	private String repositoryName = MnemosyneConstants.getArtifactTableName();
	private String rawBytes = MnemosyneConstants.getArtifactTableRawBytes();
	private String artifactEntry = MnemosyneConstants.getArtifactTableArtifactEntry();
	private String verifyEntry = MnemosyneConstants.getArtifactTableVerifyEntry();

	@Override
	public String toString() {
		return repositoryName;
	}

	public String rawBytes() {
		return rawBytes;
	}

	public String artifactEntry() {
		return artifactEntry;
	}

	public String verifyEntry() {
		return verifyEntry;
	}

}
