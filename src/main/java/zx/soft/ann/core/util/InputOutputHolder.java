package zx.soft.ann.core.util;

import java.io.Serializable;

public class InputOutputHolder implements Serializable {

	private static final long serialVersionUID = -7455128628876031515L;

	private double[][] input;
	private double[][] output;

	public InputOutputHolder(double[][] input, double[][] output) {
		this.input = input;
		this.output = output;
	}

	public double[][] getInput() {
		return this.input;
	}

	public double[][] getOutput() {
		return this.output;
	}

}
