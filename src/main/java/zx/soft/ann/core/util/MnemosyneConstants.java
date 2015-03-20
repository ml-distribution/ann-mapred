package zx.soft.ann.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.fs.Path;

public class MnemosyneConstants {

	public static String confDir = "";
	public static String mnemosyneSite = confDir + "conf.mnemosyne-site";
	private final static Logger log = Logger.getLogger(MnemosyneConstants.class.getName());

	public static String getAccumuloInstance() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("accumuloInstance");
		//		return toReturn;
		return Constants.ACCUMULO_INSTANCE.getValue();
	}

	public static String getAccumuloPassword() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("accumuloPassword");
		//		return toReturn;

		return Constants.ACCUMULO_PASSWORD.getValue();
	}

	public static String getDefaultTable() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("defaultTable");
		//		return toReturn;
		return Constants.ACCUMULO_DEFAULT_TABLE.getValue();
	}

	public static String getZookeeperInstance() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("zookeeperInstance");
		//		return toReturn;
		return Constants.ZOOKEEPER_INSTANCE.getValue();
	}

	public static String getZookeeperInstanceName() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("zookeeperInstanceName");
		//		return toReturn;

		return Constants.ZOOKEEPER_INSTANCE_NAME.getValue();
	}

	public static String getAccumuloUser() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("accumuloUser");
		//		return toReturn;

		return Constants.ACCUMULO_USER.getValue();
	}

	public static String getNeuralNetworkRowName() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("networkRowName");
		//		return toReturn;
		return Constants.NETWORK_ROW_NAME.getValue();
	}

	public static int getNetworksPerNode() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("networksPerNode");
		//		return Integer.parseInt(toReturn);
		return Integer.parseInt(Constants.NETWORKS_PER_NODE.getValue());
	}

	public static int getNumberOfNodes() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("numberOfNodes");
		//		return Integer.parseInt(toReturn);
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
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("defaultAuths");
		//		return toReturn;
		return Constants.ACCUMULO_AUTHS.getValue();
	}

	public static String getMnemosyneHome() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("mnemosyneHome");
		//		return toReturn;
		return Constants.MNEMOSYNE_HOME.getValue();
	}

	public static String getIngestDirectory() {
		return MnemosyneConstants.getMnemosyneHome() + "ingest/";
	}

	public static Path[] getAllIngestableFiles() {
		ArrayList<Path> paths = walk(new File(MnemosyneConstants.getIngestDirectory()));
		return paths.toArray(new Path[paths.size()]);
	}

	static ArrayList<Path> toReturn = new ArrayList<Path>();

	private static ArrayList<Path> walk(File dir) {
		String pattern = ".xml";
		File listFile[] = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {
					walk(listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(pattern)) {
						log.log(Level.INFO, "Ingesting " + listFile[i].getPath());
						toReturn.add(new Path(listFile[i].getPath()));
					}
				}
			}
		}
		return toReturn;
	}

	public static String getArtifactTableName() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("artifactTable");
		//		return toReturn;
		return Constants.ARTIFACT_TABLE.getValue();
	}

	public static String getArtifactTableRawBytes() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("artifactTableRawBytes");
		//		return toReturn;
		return Constants.ARTIFACT_TABLE_RAW_BYTES.getValue();
	}

	public static String getArtifactTableArtifactEntry() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("artifactTableArtifactEntry");
		//		return toReturn;
		return Constants.ARTIFACT_TABLE_ARTIFACT_ENTRY.getValue();
	}

	public static String getBaseNetworkRepositoryName() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("baseNetworkTable");
		//		return toReturn;
		return Constants.BASE_NETWORK_TABLE.getValue();
	}

	public static String getBaseNetworkTableRawBytes() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("baseNetworkTableRawBytes");
		//		return toReturn;
		return Constants.BASE_NETWORK_TABLE_RAW_BYTES.getValue();
	}

	public static String getBaseNetworkTableConfiguration() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("baseNetworkTableConfiguration");
		//		return toReturn;
		return Constants.BASE_NETWORK_TABLE_CONFIGURATION.getValue();
	}

	public static String getBaseNetworkTableError() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("baseNetworkTableError");
		//		return toReturn;
		return Constants.BASE_NETWORK_TABLE_ERROR.getValue();
	}

	public static String getBaseNetworkTableTrainData() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("baseNetworkTableTrainData");
		//		return toReturn;
		return Constants.BASE_NETWORK_TABLE_TRAIN_DATA.getValue();
	}

	public static String getBaseNetworkTableBaseNetwork() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("baseNetworkTableBaseNetwork");
		//		return toReturn;
		return Constants.BASE_NETWORK_TABLE_BASE_NETWORK.getValue();
	}

	public static String getArtifactTableVerifyEntry() {
		//		Properties properties = PropertyLoader.loadProperties(confDir+mnemosyneSite);
		//		String toReturn = (String) properties.get("artifactTableVerifyEntry");
		//		return toReturn;
		return Constants.ARTIFACT_TABLE_VERIFY_ENTRY.getValue();
	}

	public enum Constants {

		//ACCUMULO SETTINGS
		ACCUMULO_INSTANCE("mnemosyne"), ACCUMULO_PASSWORD("pass"), ACCUMULO_USER("root"), NETWORK_ROW_NAME("network"), ACCUMULO_DEFAULT_TABLE(
				"table"), ACCUMULO_AUTHS("public"),

		//ZOOKEEPER SETTINGS
		ZOOKEEPER_INSTANCE("localhost"), ZOOKEEPER_INSTANCE_NAME("mnemosyne"),

		//MNEMOSYNE SETTINGS
		NETWORKS_PER_NODE("1"), NUMBER_OF_NODES("2"), MNEMOSYNE_HOME("/opt/mnemosyne/"), ARTIFACT_TABLE(
				"ARTIFACT_TABLE"), ARTIFACT_TABLE_RAW_BYTES("RAW_BYTES"), ARTIFACT_TABLE_ARTIFACT_ENTRY(
				"ARTIFACT_ENTRY"), ARTIFACT_TABLE_VERIFY_ENTRY("VERIFY_ENTRY"), BASE_NETWORK_TABLE("BASE_NETWORK"), BASE_NETWORK_TABLE_RAW_BYTES(
				"RAW_BYTES"), BASE_NETWORK_TABLE_CONFIGURATION("BASE_CONFIGURATION"), BASE_NETWORK_TABLE_ERROR(
				"BASE_ERROR"), BASE_NETWORK_TABLE_TRAIN_DATA("TRAIN_DATA"), BASE_NETWORK_TABLE_BASE_NETWORK(
				"BASE_NETWORK");

		private String value = "";

		Constants(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

}
