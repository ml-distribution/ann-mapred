package zx.soft.ann.core;

import org.junit.BeforeClass;
import org.junit.Test;

import zx.soft.ann.core.util.foreman.MnemosyneAccumuloAdministrator;
import zx.soft.ann.util.TestHelper;

public class TrainProcessTest {

	@BeforeClass
	public static void beforeClass() throws Exception {
		MnemosyneAccumuloAdministrator.setup();
		TestHelper.ingestTestArtifacts();
		TestHelper.buildArtifacts();
		TestHelper.constructBaseClassificationNetwork();
	}

	@Test
	public void test() throws Exception {
		TrainProcess pro = new TrainProcess();
		pro.process();
	}

}
