package zx.soft.ann.core.util;

import java.util.ArrayList;

import zx.soft.ann.conf.ClassificationNetworkConf;
import zx.soft.ann.conf.CongressNetworkConf;

public class BinaryUtils {

	public static double[] toBinary(ClassificationNetworkConf conf, double[] toReturn, boolean isInput) {
		ArrayList<Double> binaryDoubles = new ArrayList<>();
		int padding = 0;
		if (isInput) {
			String binString = Long.toBinaryString(conf.getInputMax()).replace(' ', '0');
			;
			padding = binString.length();
		} else {
			String binString = Long.toBinaryString(conf.getOutputMax()).replace(' ', '0');
			;
			padding = binString.length();
		}

		for (double value : toReturn) {
			String binaryString = String.format("%" + padding + "s", Long.toBinaryString((long) value)).replace(' ',
					'0');
			char[] binaryArr = binaryString.toCharArray();
			for (char chara : binaryArr) {
				binaryDoubles.add(Double.parseDouble(chara + ""));
			}
		}
		Double[] dubs = new Double[binaryDoubles.size()];
		binaryDoubles.toArray(dubs);
		double[] ret = new double[binaryDoubles.size()];
		for (int i = 0; i < binaryDoubles.size(); i++) {
			ret[i] = dubs[i];
		}
		return ret;
	}

	public static double[] toBinary(int max, double[] toReturn, boolean isInput) {
		ClassificationNetworkConf conf = new ClassificationNetworkConf();
		if (isInput) {
			conf.setInputMax(max);
		} else {
			conf.setOutputMax(max);
		}
		return BinaryUtils.toBinary(conf, toReturn, isInput);
	}

	public static int sizeOfBinary(int maximumNumber) {
		return (Long.toBinaryString(maximumNumber) + "").length();
	}

	public static double[] toBinary(CongressNetworkConf conf, double[] toReturn, boolean isInput, int numOfInputs) {
		ArrayList<Double> binaryDoubles = new ArrayList<>();
		int padding = 0;
		if (isInput) {
			String binString = Long.toBinaryString(numOfInputs).replace(' ', '0');
			padding = binString.length();
		} else {
			String binString = Long.toBinaryString(1).replace(' ', '0');
			;
			padding = binString.length();
		}

		for (double value : toReturn) {
			String binaryString = String.format("%" + padding + "s", Long.toBinaryString((long) value)).replace(' ',
					'0');
			char[] binaryArr = binaryString.toCharArray();
			for (char chara : binaryArr) {
				binaryDoubles.add(Double.parseDouble(chara + ""));
			}
		}
		Double[] dubs = new Double[binaryDoubles.size()];
		binaryDoubles.toArray(dubs);
		double[] ret = new double[binaryDoubles.size()];
		for (int i = 0; i < binaryDoubles.size(); i++) {
			ret[i] = dubs[i];
		}
		return ret;
	}

}
