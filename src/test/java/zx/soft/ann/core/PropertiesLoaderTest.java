package zx.soft.ann.core;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import zx.soft.ann.core.util.MnemosyneConstants;

public class PropertiesLoaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void loadPropertiesTest() {
		assertNotNull(MnemosyneConstants.getAccumuloInstance());
		assertNotNull(MnemosyneConstants.getAccumuloPassword());
		assertNotNull(MnemosyneConstants.getAccumuloUser());
		assertNotNull(MnemosyneConstants.getAllIngestableFiles());
		assertNotNull(MnemosyneConstants.getDefaultAuths());
		assertNotNull(MnemosyneConstants.getDefaultTable());
		assertNotNull(MnemosyneConstants.getMnemosyneHome());
		assertNotNull(MnemosyneConstants.getZookeeperInstance());
		assertNotNull(MnemosyneConstants.getZookeeperInstanceName());
	}

}
