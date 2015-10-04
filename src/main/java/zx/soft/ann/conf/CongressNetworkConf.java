package zx.soft.ann.conf;

import java.io.Serializable;

/**
 * 代表性网络配置
 *
 * @author wanggang
 *
 */
public class CongressNetworkConf extends NetworkConf implements Serializable {

	private static final long serialVersionUID = 7668921475431018909L;

	// 神经元数量
	private int numOfNeurons = 0;
	// 输入数量
	private int numOfInputs = 0;

	public CongressNetworkConf() {
	}

	public CongressNetworkConf(int numOfNeurons, int numOfInputs) {
		this.numOfNeurons = numOfNeurons;
		this.numOfInputs = numOfInputs;
	}

	public void setNumberOfInputs(int x) {
		this.numOfInputs = x;
	}

	public void setNumberOfNeurons(int x) {
		this.numOfNeurons = x;
	}

	public int getNumberOfNeurons() {
		return numOfNeurons;
	}

	public int getNumberOfInputs() {
		return numOfInputs;
	}

	/**
	 * 序列化网络参数
	 *
	 * @return 序列化值
	 */
	public String serialize() {
		return numOfNeurons + "|" + numOfInputs;
	}

	/**
	 * 扩大网络
	 *
	 * @param serialized 序列化网络参数值
	 * @return 网络对象
	 */
	public static CongressNetworkConf inflate(String serialized) {
		String[] values = serialized.split("\\|");
		return new CongressNetworkConf(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
	}

}
