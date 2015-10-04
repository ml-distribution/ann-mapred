package zx.soft.ann.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.ann.conf.NeuronConf;

public class Neuron implements Serializable {

	private static final long serialVersionUID = 1L;

	private double threshold = 0.0;
	private double learningRate = 0.0;
	private double error = 1;
	private List<Double> weights = new Vector<>();
	private static final Logger log = LoggerFactory.getLogger(Neuron.class);
	private String hash = "";

	public Neuron(NeuronConf conf) {
		this.threshold = conf.getThreshold();
		this.learningRate = conf.getLearningRate();
		hash = new String((System.currentTimeMillis() + UUID.randomUUID().toString()));
	}

	private Neuron() {
		//
	}

	public String getHash() {
		return hash;
	}

	public void setup(int numberOfInputs) {
		for (int i = 0; i < numberOfInputs; i++) {
			weights.add(new Random().nextDouble());
		}
	}

	public void train(InputSet<Integer> is, OutputSet<Integer> os, double aerror, long timeout) {
		log.info("Training");
		long start = System.currentTimeMillis();
		long current = System.currentTimeMillis();
		int timesCalculated = 1;
		while (error > aerror && start + timeout > current) {
			current = System.currentTimeMillis();
			List<List<Integer>> iSets = is.getInputSets();

			for (int i = 0; i < iSets.size(); i++) {
				List<Integer> expected = os.getOutputSets().get(i);
				log.debug("Expecting:" + expected);
				List<Integer> inputs = iSets.get(i);
				double sum = 0.0;
				if (inputs.size() != weights.size()) {
					String ins = "";
					for (int in : inputs) {
						ins += in + "&";
					}
					throw new RuntimeException("input size:" + inputs.size() + " weights size:" + weights.size() + "\n"
							+ this.serialize() + "\n" + ins);
				}
				for (int j = 0; j < inputs.size(); j++) {
					int in = inputs.get(j);
					sum += in * weights.get(j);
				}
				log.debug("Sum = " + sum);
				int result = 0;
				if (sum >= threshold) {
					result = 1;
				}
				log.debug("Result = " + result);
				double inerror = expected.get(0) - result;
				error = ((error * timesCalculated) + Math.abs(inerror)) / (timesCalculated + 1);
				log.info("Error = " + error);
				double correction = learningRate * inerror;
				log.debug("Correction = " + correction);
				for (int k = 0; k < weights.size(); k++) {
					log.debug("Setting weight " + k + " from " + weights.get(k) + " to " + weights.get(k) + correction);
					weights.set(k, weights.get(k) + correction);
				}
				timesCalculated++;

			}
		}

	}

	@SuppressWarnings("unused")
	public int compute(InputSet<Integer> is) throws Exception {
		List<List<Integer>> iSets = is.getInputSets();
		for (int i = 0; i < iSets.size(); i++) {
			List<Integer> inputs = iSets.get(i);
			String inputString = "";
			for (Integer in : inputs) {
				inputString += in + " ";
			}
			log.info("For input " + inputString + " ...");
			double sum = 0.0;
			for (int j = 0; j < inputs.size(); j++) {
				int in = inputs.get(j);
				sum += in * weights.get(j);
			}
			int result = 0;
			if (sum >= threshold) {
				result = 1;
			}
			return result;
		}
		return -1;
	}

	public String serialize() {
		String toReturn = this.getHash() + "|" + this.getError() + "|" + this.getLearningRate() + "|"
				+ this.getThreshold() + "|";
		List<Double> weights = this.getWeights();
		for (Double d : weights) {
			toReturn += d + "|";
		}
		return toReturn;
	}

	public List<Double> getWeights() {
		return this.weights;
	}

	public double getError() {
		return this.error;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public double getThreshold() {
		return this.threshold;
	}

	public static Neuron inflate(String serializedNeuron) {
		String[] in = serializedNeuron.split("\\|");
		Neuron n = new Neuron();
		n.setHash(in[0]);
		n.setError(in[1]);
		n.setLearningRate(in[2]);
		n.setThreshold(in[3]);
		for (int i = 4; i < in.length; i++) {
			n.addWeight(in[i]);
		}
		return n;
	}

	private void addWeight(String w) {
		if (this.weights == null) {
			this.weights = new Vector<>();
		}
		this.weights.add(Double.parseDouble(w));

	}

	private void setThreshold(String thres) {
		this.threshold = Double.parseDouble(thres);
	}

	private void setLearningRate(String lr) {
		this.learningRate = Double.parseDouble(lr);
	}

	private void setError(String err) {
		this.error = Double.parseDouble(err);
	}

	private void setHash(String hash) {
		this.hash = hash;
	}

}
