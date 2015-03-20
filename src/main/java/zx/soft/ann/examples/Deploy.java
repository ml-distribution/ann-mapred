package zx.soft.ann.examples;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Deploy {

	public static void deployPsuedoDistrubtedSystem() throws IOException {
		sanityCheck();
		setupDirectories();
	}

	@SuppressWarnings("unused")
	private static void setupDirectories() throws IOException {
		//copy over breast cancer data
		InputStream is = Deploy.class.getClassLoader().getResourceAsStream("ingest/breast-cancer-wisconsin.xml");
		writeToFile(is, "/opt/mnemosyne/ingest/breast-cancer-wisconsin.xml");
		//copy over card data
		InputStream nis = Deploy.class.getClassLoader().getResourceAsStream("ingest/card.xml");
		//	writeToFile(nis,"/opt/mnemosyne/ingest/card.xml");
		//copy over conf
		InputStream cis = Deploy.class.getClassLoader().getResourceAsStream("conf/mnemosyne-site.conf");
		writeToFile(cis, "/opt/mnemosyne/conf/mnemosyne-site.conf");
	}

	private static void writeToFile(InputStream is, String filePath) throws IOException {
		File f = new File(filePath);
		OutputStream out = new FileOutputStream(f);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
	}

	private static void sanityCheck() {
		File optDir = new File("/opt/");
		if (!optDir.exists()) {
			throw new RuntimeException("/opt/ does not exist!");
		}

		File mnemosyneDir = new File("/opt/mnemosyne");
		if (!mnemosyneDir.exists()) {
			boolean returned = mnemosyneDir.mkdir();
			if (!returned) {
				throw new RuntimeException("Could not make the mnemosyne dir in /opt/");
			}
		}

		File ingestDir = new File("/opt/mnemosyne/ingest");
		if (!ingestDir.exists()) {
			boolean returned = ingestDir.mkdir();
			if (!returned) {
				throw new RuntimeException("Could not make ingest directory:/opt/mnemosyne/ingest");
			}
		}

		File confDir = new File("/opt/mnemosyne/conf/");
		if (!confDir.exists()) {
			boolean returned = confDir.mkdir();
			if (!returned) {
				throw new RuntimeException("Could not make conf directory:/opt/mnemosyne/conf/");
			}
		}

		File binDir = new File("/opt/mnemosyne/bin/");
		if (!binDir.exists()) {
			boolean returned = binDir.mkdir();
			if (!returned) {
				throw new RuntimeException("Could not make bin directory:");
			}
		}
	}

}
