package zx.soft.ann.core.model;

import zx.soft.ann.conf.ClassificationNetworkConf;
import zx.soft.ann.conf.CongressNetworkConf;
import zx.soft.ann.core.util.BinaryUtils;

public class NNInput {

	public static double[] inflate(ClassificationNetworkConf conf, String value) {
		String doubles = value.split("\\)")[0].replace("(", "");
		String[] inputs = doubles.split(",");
		double[] toReturn = new double[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			toReturn[i] = Double.parseDouble(inputs[i].replace("(", ""));
		}

		return BinaryUtils.toBinary(conf, toReturn, true);
	}

	public static double[] inflate(CongressNetworkConf conf, String value, int numOfInputs) {
		String doubles = value.split("\\)")[0].replace("(", "");
		String[] inputs = doubles.split(",");
		double[] toReturn = new double[inputs.length];
		for (int i = 0; i < inputs.length; i++) {

			toReturn[i] = Double.parseDouble(inputs[i].replace("(", ""));
		}

		return BinaryUtils.toBinary(conf, toReturn, true, numOfInputs);
	}

}
