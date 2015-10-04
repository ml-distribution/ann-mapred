package zx.soft.ann.conf;

import java.util.Collection;

import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.util.Pair;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import zx.soft.ann.core.util.MnemosyneConstants;

/**
 * Hadoop作业的配置类
 *
 * @author wanggang
 *
 */
public class HadoopJobConfiguration {

	/**
	 * Hadoop作业名
	 */
	private String jobName = "DefaultANNJobName";

	/**
	 * Mapper类
	 */
	private Class<? extends Mapper<?, ?, ?, ?>> mapperClass;

	/**
	 * Reducer类
	 */
	private Class<? extends Reducer<?, ?, ?, ?>> reducerClass;

	/**
	 * Mapper的InputFormat类
	 */
	private Class<?> inputClass;

	/**
	 * Mapper的OutputFormat类
	 */
	private Class<?> outputClass;

	/**
	 * Mapper的OutputKeyFormat类
	 */
	private Class<?> outputKeyClass;

	/**
	 * Mapper的OutputValueFormat类
	 */
	private Class<?> outputValueClass;

	/**
	 * Reducer任务数，即Reducer个数
	 */
	private int numOfReduceTasks = 0;

	/**
	 * 默认的表，用于获取AccumuloInputFormat的数据；
	 * 使用带overrideDefaultTable函数改变该值。
	 *
	 * 通过配置文件mnemosyne-site.conf改变
	 */
	private String defaultTable = MnemosyneConstants.getDefaultTable();

	/**
	 * 默认的累积授权
	 *
	 * 通过配置文件mnemosyne-site.conf改变
	 */
	private Authorizations defaultAuths = new Authorizations(MnemosyneConstants.getDefaultAuths());

	/**
	 * 如果作业正在使用FileInputFormat，这个路径就是指向需要处理的文件
	 */
	private Path pathToProcess = null;

	/**
	 * 列族（Column Family）-列修饰符（Column Qualifier）对集合，从AccumuloInputFormat中得到
	 */
	private Collection<Pair<Text, Text>> cfPairs;

	private Class<?> jarClass;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Class<? extends Mapper<?, ?, ?, ?>> getMapperClass() {
		return mapperClass;
	}

	public void setMapperClass(Class<? extends Mapper<?, ?, ?, ?>> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public Class<?> getInputFormatClass() {
		return inputClass;
	}

	public void setInputFormatClass(Class<?> inputClass) {
		this.inputClass = inputClass;
	}

	public Class<?> getOutputFormatClass() {
		return this.outputClass;
	}

	public void setOutputFormatClass(Class<?> outputClass) {
		this.outputClass = outputClass;
	}

	public Class<?> getOutputKeyClass() {
		return outputKeyClass;
	}

	public void setOutputKeyClass(Class<?> outputKeyClass) {
		this.outputKeyClass = outputKeyClass;
	}

	public Class<?> getOutputValueClass() {
		return outputValueClass;
	}

	public void setOutputValueClass(Class<?> outputValueClass) {
		this.outputValueClass = outputValueClass;
	}

	public void setNumReduceTasks(int i) {
		this.numOfReduceTasks = i;
	}

	public int getNumReduceTasks() {
		return this.numOfReduceTasks;
	}

	public void overrideDefaultTable(String defaultTable) {
		this.defaultTable = defaultTable;
	}

	public String getDefaultTable() {
		return this.defaultTable;
	}

	public void setDefaultAuths(Authorizations auths) {
		this.defaultAuths = auths;
	}

	public Authorizations getDefaultAuths() {
		return this.defaultAuths;
	}

	public Class<? extends Reducer<?, ?, ?, ?>> getReducerClass() {
		return this.reducerClass;
	}

	public void setReducerClass(Class<? extends Reducer<?, ?, ?, ?>> reducerClass) {
		this.reducerClass = reducerClass;
	}

	public static String buildJobName(Class<?> className) {
		return className.getSimpleName();
	}

	/**
	 * 如果使用FileInputFormat，这里定义MR需要处理的文件路径
	 */
	public void overridePathToProcess(Path path) {
		this.pathToProcess = path;
	}

	/**
	 * 如果使用FileInputFormat，返回需要处理的路径
	 */
	public Path getPathToProcess() {
		return this.pathToProcess;
	}

	public Collection<Pair<Text, Text>> getFetchColumns() {
		return cfPairs;
	}

	public void setFetchColumns(Collection<Pair<Text, Text>> pairs) {
		this.cfPairs = pairs;
	}

	public Class<?> getJarClass() {
		return this.jarClass;
	}

	public void setJarClass(Class<?> cls) {
		this.jarClass = cls;
	}

}
