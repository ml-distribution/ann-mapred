package zx.soft.ann.conf;

import java.io.Serializable;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;

/**
 * 通过大量信息构建分类神经网络
 *
 * @author wanggang
 *
 */
public class ClassificationNetworkConf extends NetworkConf implements Serializable {

	/**
	 * Only important  for the Serializable interface
	 */
	private static final long serialVersionUID = 8482110912070036554L;

	/**
	 * Defines an Activation node on the Input layer. It is not common for an input layer to have
	 * an Activation, so by default, it should be null
	 */
	private ActivationFunction inputActivation = null;

	/**
	 * Defines an Activation node for the initial hidden layer. It is not common for a hidden layer to have
	 * NO Activation, so by default, we use ActivationSigmoid
	 */
	private ActivationFunction hiddenActivation = new ActivationSigmoid();

	/**
	 * Defines an Activation node for the output layer. Usually, but not always, an output layer has
	 * an Activation, so by default, we use ActivationSigmoid
	 */
	private ActivationFunction outputActivation = new ActivationSigmoid();

	/**
	 * Does the input layer have a bias node? It is common for it to.
	 */
	private boolean inputBias = true;

	/**
	 * Does the initial hidden layer have a bias node? It is common for it to.
	 */
	private boolean hiddenBias = true;

	/**
	 * Does the output layer have a bias node? It is common for it NOT to.
	 */
	private boolean outputBias = false;

	/**
	 * The initial amount of input neurons (excluding a bias node)
	 */
	private int inputNeuronCount = 1;

	/**
	 * The initial amount of hidden neurons (excluding a bias node)
	 */
	private int hiddenNeuronCount = 3;

	/**
	 * The initial amount of output neurons (excluding a bias node)
	 */
	private int outputNeuronCount = 1;

	/**
	 * The input set to train the Neural Network over.
	 * It is usually arranged
	 * [ {InputA1,InputB1,InputC1..} {InputA2,InputB2,InputC2..}.... ]
	 *
	 * The number of inputs per double[] represent the number of input neurons (excluding the bias neuron)
	 */
	private double[][] basicMLInput;

	/**
	 * The ideal output to the input set defined above.
	 * The number of outputs per double[] represent the number of output neurons (excluding the bias neuron)
	 * [ [{outputA1, outputB1 ...} {outputA2, outputB2 ...}  ...]
	 */
	private double[][] basicIdealOutput;

	/**
	 * The amount of acceptable error on the first iterator (virtually meaningless because Mnemosyne will figure this out)
	 */
	private double error = .000005;

	/**
	 * The maximum input value (for example, if an input is between 0-10000, the max input is 10000
	 */
	private int inputMax = 1;

	/**
	 * The maximum output value(for example, if an output is betwenn 0-10000, the max output is 10000
	 */
	private int outputMax = 1;

	/**
	 * The amount of time to elapse before a training session times out (default: 1 minute)
	 */
	private long timeout = 60000;

	/**
	 * The amount of epochs before a training session times out (default:10000 epochs)
	 */
	private int epochTimeout = 10000;

	/**
	 * Since this is a classification network , clarifying how many categories it is classifying into helps its creation
	 */
	private int numOfCategories = 1;

	/**
	 * Returns the Activation Function (if there is one) for the Input layer
	 * @return the activation function for the input layer (may be null)
	 */
	public ActivationFunction getInputActivation() {
		return inputActivation;
	}

	/**
	 * Does the Input Layer have a bias neuron?
	 * @return whether or not the Input Layer has a bias neuron
	 */
	public boolean getInputBias() {
		return inputBias;
	}

	/**
	 * Returns the number of input layer neurons (excluding bias neurons)
	 * @return the number of input layer neurons (excluding bias neurons)
	 */
	public int getInputNeuronCount() {
		return inputNeuronCount;
	}

	/**
	 * Returns the Hidden Layer's Activation Function
	 * @return the Hidden Layer's Activation Function
	 */
	public ActivationFunction getHiddenActivation() {
		return hiddenActivation;
	}

	/**
	 * Does the Hidden Layer have a bias neuron?
	 * @return whether or not the Hidden Layer has a bias neuron
	 */
	public boolean getHiddenBias() {
		return hiddenBias;
	}

	/**
	 * Returns the number of hidden layer neurons (excluding bias neurons)
	 * @return the number of hidden layer neurons (excluding bias neurons)
	 */
	public int getHiddenNeuronCount() {
		return hiddenNeuronCount;
	}

	/**
	 * Returns the Activation Function (if there is one) of the Output layer
	 * @return the Activation of the Output layer (may be null)
	 */
	public ActivationFunction getOutputActivation() {
		return outputActivation;
	}

	/**
	 * Does the hidden layer have an output bias?
	 * @return whether or not the output layer has a bias neuron
	 */
	public boolean getOutputBias() {
		return this.outputBias;
	}

	/**
	 * Returns the number of output neurons (excluding the bias neurons)
	 * @return
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * Returns the sample inputs to train the base network
	 *
	 * It is usually arranged
	 * [ {InputA1,InputB1,InputC1..} {InputA2,InputB2,InputC2..}.... ]
	 *
	 * The number of inputs per double[] represent the number of input neurons (excluding the bias neuron)
	 * @return the sample inputs to train the base network
	 */
	public double[][] getBasicMLInput() {
		return this.basicMLInput;
	}

	/**
	 * Returns the sample outputs to train the base network
	 * The number of outputs per double[] represent the number of output neurons (excluding the bias neuron)
	 * [ [{outputA1, outputB1 ...} {outputA2, outputB2 ...}  ...]
	 * @return sample outputs to train the base network
	 */
	public double[][] getBasicIdealOutput() {
		return this.basicIdealOutput;
	}

	/**
	 * Returns the amount of acceptable error
	 * @return the amount of acceptable error
	 */
	public double getErrorBound() {
		return this.error;
	}

	/**
	 * Sets whether or not the input layer has a bias neuron
	 * @param b
	 */
	public void setInputBias(boolean b) {
		this.inputBias = b;

	}

	/**
	 * Sets whether or not the hidden layer has a bias neuron
	 * @param b
	 */
	public void setHiddenBias(boolean b) {
		this.hiddenBias = b;

	}

	/**
	 * Sets whether or not the output layer has bias neuron
	 * @param activation
	 */
	public void setOutputActivation(ActivationFunction activation) {
		this.outputActivation = activation;

	}

	/**
	 * Sets the Activation function for the Hidden Layer
	 * @param activation
	 */
	public void setHiddenActiviation(ActivationFunction activation) {
		this.hiddenActivation = activation;

	}

	/**
	 * Sets the Activation function for the Hidden Layer
	 * @param activation
	 */
	public void setInputActivation(ActivationFunction activation) {
		this.inputActivation = activation;
	}

	/**
	 * Sets the Input Neuron Count (excluding the bias neuron)
	 * @param i
	 */
	public void setInputNeuronCount(int i) {
		this.inputNeuronCount = i;

	}

	/**
	 * Sets the Hidden Neuron Count (excluding the bias neuron)
	 * @param i
	 */
	public void setHiddenNeuronCount(int i) {
		this.hiddenNeuronCount = i;

	}

	/**
	 * Sets the Output Neuron Count (excluding the bias neuron)
	 * @param i
	 */
	public void setOutputNeuronCount(int i) {
		this.outputNeuronCount = i;

	}

	/**
	 * Sets the Input to train the base neural network over.
	 * It is usually arranged
	 * [ {InputA1,InputB1,InputC1..} {InputA2,InputB2,InputC2..}.... ]
	 *
	 * The number of inputs per double[] represent the number of input neurons (excluding the bias neuron)
	 * @param ds
	 */
	public void setBasicMLInput(double[][] ds) {
		this.basicMLInput = ds;
	}

	/**
	 * Sets the Ideal Output
	 * The number of outputs per double[] represent the number of output neurons (excluding the bias neuron)
	 * [ [{outputA1, outputB1 ...} {outputA2, outputB2 ...}  ...]
	 * @param ds
	 */
	public void setBasicIdealMLOutput(double[][] ds) {
		this.basicIdealOutput = ds;

	}

	/**
	 * Returns the number of categories to classify the input in
	 * @return
	 */
	public int getNumberOfCategories() {
		return numOfCategories;
	}

	/**
	 * Sets the number of categories to classify the input in
	 * @return
	 */
	public void setNumberOfCategories(int num) {
		this.numOfCategories = num;
	}

	public void setInputMax(int inputMax) {
		this.inputMax = inputMax;
	}

	public int getInputMax() {
		return this.inputMax;
	}

	public void setOutputMax(int outputMax) {
		this.outputMax = outputMax;
	}

	public int getOutputMax() {
		return this.outputMax;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long time) {
		this.timeout = time;
	}

	public int getEpochTimeout() {
		return this.epochTimeout;
	}

	public void setEpochTimeout(int epochTimeout) {
		this.epochTimeout = epochTimeout;
	}

}
