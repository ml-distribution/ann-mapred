package zx.soft.ann.core.framework;

import zx.soft.ann.core.util.MnemosyneConstants;

public class BaseNetworkRepository implements AccumuloTable {

	private String repositoryName = MnemosyneConstants.getBaseNetworkRepositoryName();
	private String rawBytesField = MnemosyneConstants.getBaseNetworkTableRawBytes();
	private String baseConfiguration = MnemosyneConstants.getBaseNetworkTableConfiguration();
	private String baseError = MnemosyneConstants.getBaseNetworkTableError();
	private String trainData = MnemosyneConstants.getBaseNetworkTableTrainData();
	private String baseNetwork = MnemosyneConstants.getBaseNetworkTableBaseNetwork();

	@Override
	public String toString() {
		return repositoryName;
	}

	public String getRawBytesField() {
		return rawBytesField;
	}

	public String baseConfiguration() {
		return baseConfiguration;

	}

	public String baseError() {
		return baseError;
	}

	public String trainData() {
		return trainData;
	}

	public String baseNetwork() {
		return baseNetwork;
	}

}
