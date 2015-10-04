package zx.soft.ann.core.util;

import java.io.File;
import java.util.ArrayList;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MnemosyneConstants {

	private final static Logger logger = LoggerFactory.getLogger(MnemosyneConstants.class);

	public static String confDir = "";
	public static String mnemosyneSite = confDir + "conf.mnemosyne-site";

	public static String getAccumuloInstance() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("accumuloInstance");
		//		return toReturn;
		return Constants.ACCUMULO_INSTANCE.getValue();
	}

	public static String getAccumuloPassword() {
		return Constants.ACCUMULO_PASSWORD.getValue();
	}

	public static String getDefaultTable() {
		return Constants.ACCUMULO_DEFAULT_TABLE.getValue();
	}

	public static String getZookeeperInstance() {
		return Constants.ZOOKEEPER_INSTANCE.getValue();
	}

	public static String getZookeeperInstanceName() {
		return Constants.ZOOKEEPER_INSTANCE_NAME.getValue();
	}

	public static String getAccumuloUser() {
		return Constants.ACCUMULO_USER.getValue();
	}

	public static String getNeuralNetworkRowName() {
		return Constants.NETWORK_ROW_NAME.getValue();
	}

	public static int getNetworksPerNode() {
		return Integer.parseInt(Constants.NETWORKS_PER_NODE.getValue());
	}

	public static int getNumberOfNodes() {
		return Integer.parseInt(Constants.NUMBER_OF_NODES.getValue());
	}

	public static double[][] getTestInput() {
		double[][] toReturn = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
		return toReturn;
	}

	public static double[][] getTestIdeal() {
		double[][] toReturn = { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } };
		return toReturn;
	}

	public static String getDefaultAuths() {
		return Constants.ACCUMULO_AUTHS.getValue();
	}

	public static String getMnemosyneHome() {
		return Constants.MNEMOSYNE_HOME.getValue();
	}

	public static String getIngestDirectory() {
		return MnemosyneConstants.getMnemosyneHome() + "ingest/";
	}

	public static Path[] getAllIngestableFiles() {
		ArrayList<Path> paths = walk(new File(MnemosyneConstants.getIngestDirectory()));
		return paths.toArray(new Path[paths.size()]);
	}

	static ArrayList<Path> toReturn = new ArrayList<>();

	private static ArrayList<Path> walk(File dir) {
		String pattern = ".xml";
		File listFile[] = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {
					walk(listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(pattern)) {
						logger.info("Ingesting " + listFile[i].getPath());
						toReturn.add(new Path(listFile[i].getPath()));
					}
				}
			}
		}
		return toReturn;
	}

	public static String getArtifactTableName() {
		return Constants.ARTIFACT_TABLE.getValue();
	}

	public static String getArtifactTableRawBytes() {
		return Constants.ARTIFACT_TABLE_RAW_BYTES.getValue();
	}

	public static String getArtifactTableArtifactEntry() {
		return Constants.ARTIFACT_TABLE_ARTIFACT_ENTRY.getValue();
	}

	public static String getBaseNetworkRepositoryName() {
		return Constants.BASE_NETWORK_TABLE.getValue();
	}

	public static String getBaseNetworkTableRawBytes() {
		return Constants.BASE_NETWORK_TABLE_RAW_BYTES.getValue();
	}

	public static String getBaseNetworkTableConfiguration() {
		return Constants.BASE_NETWORK_TABLE_CONFIGURATION.getValue();
	}

	public static String getBaseNetworkTableError() {
		return Constants.BASE_NETWORK_TABLE_ERROR.getValue();
	}

	public static String getBaseNetworkTableTrainData() {
		return Constants.BASE_NETWORK_TABLE_TRAIN_DATA.getValue();
	}

	public static String getBaseNetworkTableBaseNetwork() {
		return Constants.BASE_NETWORK_TABLE_BASE_NETWORK.getValue();
	}

	public static String getArtifactTableVerifyEntry() {
		return Constants.ARTIFACT_TABLE_VERIFY_ENTRY.getValue();
	}

	public enum Constants {

		// ACCUMULO SETTINGS
		ACCUMULO_INSTANCE("mnemosyne"), //
		ACCUMULO_PASSWORD("pass"), //
		ACCUMULO_USER("root"), //
		NETWORK_ROW_NAME("network"), //
		ACCUMULO_DEFAULT_TABLE("table"), //
		ACCUMULO_AUTHS("public"),

		// ZOOKEEPER SETTINGS
		ZOOKEEPER_INSTANCE("localhost"), //
		ZOOKEEPER_INSTANCE_NAME("mnemosyne"),

		// MNEMOSYNE SETTINGS
		NETWORKS_PER_NODE("1"), //
		NUMBER_OF_NODES("2"), //
		MNEMOSYNE_HOME("/opt/mnemosyne/"), //
		ARTIFACT_TABLE("ARTIFACT_TABLE"), //
		ARTIFACT_TABLE_RAW_BYTES("RAW_BYTES"), //
		ARTIFACT_TABLE_ARTIFACT_ENTRY("ARTIFACT_ENTRY"), //
		ARTIFACT_TABLE_VERIFY_ENTRY("VERIFY_ENTRY"), //
		BASE_NETWORK_TABLE("BASE_NETWORK"), //
		BASE_NETWORK_TABLE_RAW_BYTES("RAW_BYTES"), //
		BASE_NETWORK_TABLE_CONFIGURATION("BASE_CONFIGURATION"), //
		BASE_NETWORK_TABLE_ERROR("BASE_ERROR"), //
		BASE_NETWORK_TABLE_TRAIN_DATA("TRAIN_DATA"), //
		BASE_NETWORK_TABLE_BASE_NETWORK("BASE_NETWORK");

		private String value = "";

		Constants(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

}
