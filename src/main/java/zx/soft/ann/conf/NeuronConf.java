package zx.soft.ann.conf;

import com.google.common.base.MoreObjects;

/**
 * 神经元配置数据模型
 *
 * @author wanggang
 *
 */
public class NeuronConf {

	// 阈值
	private double threshold = 0.0;
	// 学习率
	private double learningRate = 0.0;

	public NeuronConf() {
	}

	public NeuronConf(double threshold, double learningRate) {
		this.threshold = threshold;
		this.learningRate = learningRate;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("threshold", this.threshold).add("learningRate", this.learningRate)
				.toString();
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

}
