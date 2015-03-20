package zx.soft.ann.core;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.accumulo.core.client.mapreduce.AccumuloOutputFormat;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.HadoopException;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.HadoopForeman;
import zx.soft.ann.core.util.foreman.MnemosyneAccumuloAdministrator;

public class HadoopForemanTest {

	private static long time = System.currentTimeMillis();
	private static AccumuloForeman aForeman = new AccumuloForeman();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		aForeman.connect();
		aForeman.makeTable(time + "");
		aForeman.add(time + "", "ROW", "FAM", "QUAL", "VALUE");

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MnemosyneAccumuloAdministrator.setup();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void hadoopForemanTest() throws IOException, InterruptedException, ClassNotFoundException, HadoopException {
		HadoopJobConfiguration hConfig = new HadoopJobConfiguration();
		hConfig.setJobName("Hadoop Test");

		hConfig.setMapperClass(MyMapper.class);
		hConfig.setInputFormatClass(AccumuloInputFormat.class);
		hConfig.setOutputFormatClass(AccumuloOutputFormat.class);

		hConfig.overrideDefaultTable(time + "");

		HadoopForeman hForeman = new HadoopForeman();
		assertTrue(hForeman.runJob(hConfig));
	}

	public static class MyMapper extends Mapper<Key, Value, Writable, Writable> {
		@Override
		public void map(Key ik, Value iv, Context context) {
			System.out.println(ik.toString() + " " + iv.toString());

		}
	}

}
