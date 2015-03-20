package zx.soft.ann.conf;

public class NeuronConf {

	private double threshold = 0.0;
	private double learningRate = 0.0;

	public void setThreshold(double thrshld) {
		this.threshold = thrshld;
	}

	public double getThreshold() {
		return this.threshold;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

	public double getLearningRate() {
		return learningRate;
	}

}
