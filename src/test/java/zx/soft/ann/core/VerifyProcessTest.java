package zx.soft.ann.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import zx.soft.ann.core.util.foreman.MnemosyneAccumuloAdministrator;
import zx.soft.ann.util.TestHelper;

public class VerifyProcessTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MnemosyneAccumuloAdministrator.setup();
		TestHelper.ingestTestArtifacts();
		TestHelper.buildArtifacts();
		TestHelper.constructBaseClassificationNetwork();
		TestHelper.trainNetworks();
		TestHelper.verifyCongress();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
	}

}
