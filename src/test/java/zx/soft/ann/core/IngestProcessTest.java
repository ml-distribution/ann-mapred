package zx.soft.ann.core;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map.Entry;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.MnemosyneAccumuloAdministrator;
import zx.soft.ann.util.TestHelper;

public class IngestProcessTest {

	static AccumuloForeman aForeman = new AccumuloForeman();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MnemosyneAccumuloAdministrator.setup();
		TestHelper.ingestTestArtifacts();
		aForeman.connect();
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
	public void ingestProcessTest() throws Exception {
		List<Entry<Key, Value>> results = aForeman.fetchByQualifier(AccumuloForeman.getArtifactRepositoryName(),
				"RAW_BYTES", "0");
		assertEquals("It appears there aren't two artifacts with line 0", results.size(), 2);
	}

}
