package zx.soft.ann.core.util.foreman;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.accumulo.core.client.mapreduce.AccumuloOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.ann.conf.HadoopJobConfiguration;
import zx.soft.ann.core.exception.HadoopException;
import zx.soft.ann.core.util.MnemosyneConstants;

public class HadoopForeman extends Configured implements Tool {

	private final static Logger logger = LoggerFactory.getLogger(HadoopForeman.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Job getHadoopJob(HadoopJobConfiguration conf) throws HadoopException {
		Job job;
		try {
			job = new Job();
			DistributedCache
					.setCacheArchives(new URI[] { new URI("/cache/accumulo-core-1.4.1.jar"),
							new URI("/cache/accumulo-server-1.4.1.jar"), new URI("/cache/accumulo-start-1.4.1.jar"),
							new URI("/cache/cloudtrace-1.4.1.jar"), new URI("/cache/commons-collections-3.2.jar"),
							new URI("/cache/commons-configuration-1.5.jar"), new URI("/cache/commons-io-1.4.jar"),
							new URI("/cache/commons-jci-core-1.0.jar"), new URI("/cache/commons-jci-fam-1.0.jar"),
							new URI("/cache/commons-lang-2.4.jar"), new URI("/cache/commons-logging-1.0.4.jar"),
							new URI("/cache/commons-logging-api-1.0.4.jar"), new URI("/cache/jline-0.9.94.jar"),
							new URI("/cache/libthrift-0.6.1.jar"), new URI("/cache/log4j-1.2.16.jar") },
							job.getConfiguration());
			job.setJobName(conf.getJobName());
			logger.info("Setting jar class: {}", conf.getJarClass());
			((JobConf) job.getConfiguration()).setJar("/opt/mnemosyne.jar");
			job.setJarByClass(conf.getJarClass());
			job.setMapperClass(conf.getMapperClass());
			job.setInputFormatClass((Class<? extends InputFormat>) conf.getInputFormatClass());

			if (conf.getOutputFormatClass() != null) {
				job.setOutputFormatClass((Class<? extends OutputFormat>) conf.getOutputFormatClass());
			}
			if (conf.getOutputKeyClass() != null) {
				job.setOutputKeyClass(conf.getOutputKeyClass());
			}
			if (conf.getOutputValueClass() != null) {
				job.setOutputValueClass(conf.getOutputValueClass());
			}
			if (conf.getReducerClass() != null) {
				job.setReducerClass(conf.getReducerClass());
			}

			job.setNumReduceTasks(conf.getNumReduceTasks());
			Configuration conf1 = job.getConfiguration();
			if (conf.getInputFormatClass() == AccumuloInputFormat.class) {
				AccumuloInputFormat.setInputInfo(conf1, MnemosyneConstants.getAccumuloUser(), MnemosyneConstants
						.getAccumuloPassword().getBytes(), conf.getDefaultTable(), conf.getDefaultAuths());
				AccumuloInputFormat.setZooKeeperInstance(conf1, MnemosyneConstants.getZookeeperInstanceName(),
						MnemosyneConstants.getZookeeperInstance());

			}
			if (conf.getFetchColumns() != null) {
				AccumuloInputFormat.fetchColumns(conf1, conf.getFetchColumns());
			} else if (conf.getInputFormatClass() == TextInputFormat.class) {
				if (conf.getPathToProcess() != null) {
					FileInputFormat.setInputPaths(job, conf.getPathToProcess());
				}
			}
			if (conf.getOutputFormatClass() == AccumuloOutputFormat.class) {
				AccumuloOutputFormat.setOutputInfo(conf1, MnemosyneConstants.getAccumuloUser(), MnemosyneConstants
						.getAccumuloPassword().getBytes(), true, conf.getDefaultTable());
				AccumuloOutputFormat.setZooKeeperInstance(conf1, MnemosyneConstants.getZookeeperInstanceName(),
						MnemosyneConstants.getZookeeperInstance());

			}

			return job;

		} catch (IOException e) {
			logger.error("Could not configure a Hadoop job: {}", e);
			throw new HadoopException("Could not configure a Hadoop job", e);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;

	}

	private HadoopJobConfiguration hConfig;

	public boolean runJob(HadoopJobConfiguration hConfig) throws HadoopException {
		this.hConfig = hConfig;
		try {
			ToolRunner.run(this, new String[] {});
		} catch (Exception e) {
			logger.error("Could not start a Hadoop job: {}", e);
			throw new HadoopException("Could not start a Hadoop job", e);
		}
		return true;
	}

	@Override
	public int run(String[] arg0) throws Exception {
		this.getHadoopJob(hConfig).waitForCompletion(true);
		return 0;
	}

}
