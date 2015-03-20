package zx.soft.ann.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import zx.soft.ann.core.BaseNetworkBuilderProcessTest;
import zx.soft.ann.core.HadoopForemanTest;
import zx.soft.ann.core.IngestProcessTest;
import zx.soft.ann.core.PropertiesLoaderTest;

@RunWith(Suite.class)
@SuiteClasses({ PropertiesLoaderTest.class, HadoopForemanTest.class, IngestProcessTest.class,
		BaseNetworkBuilderProcessTest.class })
public class CoreTestSuite {

}
