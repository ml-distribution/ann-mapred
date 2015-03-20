package zx.soft.ann.conf;

import java.io.Serializable;

public class CongressNetworkConf extends NetworkConf implements Serializable {

	private static final long serialVersionUID = 7668921475431018909L;

	private int numOfNeurons = 0;
	private int numOfInputs = 0;

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

	public String serialize() {
		String toReturn = numOfNeurons + "|" + numOfInputs;
		return toReturn;
	}

	public static CongressNetworkConf inflate(String serialized) {
		String[] values = serialized.split("\\|");

		CongressNetworkConf toReturn = new CongressNetworkConf();
		toReturn.setNumberOfNeurons(Integer.parseInt(values[0]));
		toReturn.setNumberOfInputs(Integer.parseInt(values[1]));
		return toReturn;
	}

}
