package zx.soft.ann.core.framework;

import java.lang.reflect.Array;
import java.util.Random;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.structure.NetworkCODEC;

import zx.soft.ann.conf.ClassificationNetworkConf;

public class ClassificationNetwork {

	public static BasicNetwork constructNetworks(ClassificationNetworkConf conf) {
		BasicNetwork network = new BasicNetwork();
		BasicLayer input = new BasicLayer(conf.getInputActivation(), conf.getInputBias(), conf.getInputNeuronCount());
		BasicLayer hidden = new BasicLayer(conf.getHiddenActivation(), conf.getHiddenBias(),
				conf.getNumberOfCategories() * new Random().nextInt(15));
		BasicLayer output = new BasicLayer(conf.getOutputActivation(), conf.getOutputBias(),
				conf.getOutputNeuronCount());

		network.addLayer(input);// input layer
		network.addLayer(hidden);// hidden layer
		network.addLayer(output);// output layer;
		network.getStructure().finalizeStructure();
		network.reset();

		return network;
	}

	/**
	 * This function is far from perfect. But is used as a demonstration.
	 *
	 * One of the main problems is that it adds this new layer right after the input layer....
	 *
	 * which I imagine severs the links between the old hidden lay0
	 * FIXME:
	 * @param network
	 * @param conf
	 * @param layer
	 * @return
	 */
	public static BasicNetwork addLayerToNetwork(BasicNetwork network, ClassificationNetworkConf conf, BasicLayer layer) {
		FlatNetwork toCopy = network.getStructure().getFlat();
		FlatNetwork toInstall = new FlatNetwork();

		// these properties stay the same
		toInstall.setConnectionLimit(toCopy.getConnectionLimit());
		toInstall.setHasContext(toCopy.getHasContext());
		toInstall.setInputCount(toCopy.getInputCount());
		toInstall.setOutputCount(toCopy.getOutputCount());
		toInstall.setBeginTraining(toCopy.getBeginTraining());

		// Add new stuff before the output layer
		ActivationFunction[] functions = toCopy.getActivationFunctions();
		ActivationFunction[] toInstallFunc = new ActivationFunction[functions.length + 1];

		double[] toCopyBiasActivations = toCopy.getBiasActivation();
		double[] toInstallBiasActivations = new double[toCopyBiasActivations.length + 1];

		int[] toCopyTargetOffset = toCopy.getContextTargetOffset();
		int[] toInstallTargetOffset = new int[toCopyTargetOffset.length + 1];

		int[] toCopyTargetSize = toCopy.getContextTargetSize();
		int[] toInstallTargetSize = new int[toCopyTargetSize.length + 1];

		int toCopyEndTraining = toCopy.getEndTraining();
		int toInstallEndTraining = toCopyEndTraining + 1;

		int[] toCopyLayerContextCount = toCopy.getLayerContextCount();
		int[] toInstallLayerContextCount = new int[toCopyLayerContextCount.length + 1];

		int[] toCopyLayerCounts = toCopy.getLayerCounts();
		int[] toInstallLayerCounts = new int[toCopyLayerCounts.length + 1];

		int[] toCopyFeedCounts = toCopy.getLayerFeedCounts();
		int[] toInstallFeedCounts = new int[toCopyFeedCounts.length + 1];

		int[] toCopyLayerIndex = toCopy.getLayerIndex();
		int[] toInstallLayerIndex = new int[toCopyLayerIndex.length + 1];

		double[] toCopyLayerSums = toCopy.getLayerSums();
		double[] toInstallLayerSums = new double[toCopyLayerSums.length + layer.getTotalCount()];
		double[] toCopyLayerOutput = toCopy.getLayerOutput();
		double[] toInstallLayerOutput = new double[toCopyLayerOutput.length + layer.getTotalCount()];

		int[] toCopyWeightIndex = toCopy.getWeightIndex();
		int[] toInstallWeightIndex = new int[toCopyWeightIndex.length + 1];

		double[] toCopyWeights = toCopy.getWeights();
		double[] toInstallWeights = new double[toCopyWeights.length
				+ (layer.getCount() * toCopyLayerCounts[toCopyLayerCounts.length - 2])];
		//standard assignments
		for (int i = 0; i < functions.length - 1; i++) {
			toInstallFunc[i] = functions[i];
			toInstallBiasActivations[i] = toCopyBiasActivations[i];
			toInstallTargetOffset[i] = toCopyTargetOffset[i];
			toInstallTargetSize[i] = toCopyTargetSize[i];
			toInstallLayerContextCount[i] = toCopyLayerContextCount[i];
			toInstallLayerCounts[i] = toCopyLayerCounts[i];
			toInstallFeedCounts[i] = toCopyFeedCounts[i];
			toInstallLayerIndex[i] = toCopyLayerIndex[i];
			toInstallWeightIndex[i] = toCopyWeightIndex[i];
		}

		toInstallFunc[functions.length - 1] = layer.getActivation();
		toInstallFunc[functions.length] = functions[functions.length - 1];

		toInstallBiasActivations[toCopyBiasActivations.length - 1] = layer.getBiasActivation();
		toInstallBiasActivations[toCopyBiasActivations.length] = toCopyBiasActivations[toCopyBiasActivations.length - 1];

		toInstallTargetOffset[toCopyTargetOffset.length - 1] = toCopyTargetOffset[toCopyTargetOffset.length - 2];
		toInstallTargetOffset[toCopyTargetOffset.length] = toCopyTargetOffset[toCopyTargetOffset.length - 1];

		toInstallTargetSize[toCopyTargetSize.length - 1] = toCopyTargetSize[toCopyTargetSize.length - 2];
		toInstallTargetSize[toCopyTargetSize.length] = toCopyTargetSize[toCopyTargetSize.length - 1];

		toInstallLayerContextCount[toCopyLayerContextCount.length - 1] = toCopyLayerContextCount[toCopyLayerContextCount.length - 2];
		toInstallLayerContextCount[toCopyLayerContextCount.length] = toCopyLayerContextCount[toCopyLayerContextCount.length - 1];

		toInstallLayerCounts[toCopyLayerCounts.length - 1] = layer.getTotalCount();
		toInstallLayerCounts[toCopyLayerCounts.length] = toCopyLayerCounts[toCopyLayerCounts.length - 1];

		toInstallFeedCounts[toCopyFeedCounts.length - 1] = layer.getNeuronCount();
		toInstallFeedCounts[toCopyFeedCounts.length] = toCopyFeedCounts[toCopyFeedCounts.length - 1];

		toInstallLayerIndex[toCopyLayerIndex.length - 1] = toCopyLayerIndex[toCopyLayerIndex.length - 1];
		toInstallLayerIndex[toCopyLayerIndex.length] = toInstallLayerIndex[toCopyLayerIndex.length - 1]
				+ layer.getTotalCount();

		toInstallWeightIndex[toCopyWeightIndex.length - 1] = toCopyWeightIndex[toCopyWeightIndex.length - 1];
		toInstallWeightIndex[toCopyLayerIndex.length] = toInstallWeightIndex[toCopyWeightIndex.length - 1]
				+ (layer.getCount() * toInstallLayerCounts[toCopyWeightIndex.length - 1]);

		//assignments with special considerations
		//note this assumes that the input layer has a bias
		for (int i = 0; i < toCopyLayerOutput.length - (toCopy.getInputCount() + 1); i++) {
			toInstallLayerOutput[i] = toCopyLayerOutput[i];
		}

		for (int i = toCopyLayerOutput.length - (toCopy.getInputCount() + 1); i < toInstallLayerOutput.length
				- (toCopy.getOutputCount() + 2); i++) {
			toInstallLayerOutput[i] = 0;
		}
		toInstallLayerOutput[toInstallLayerOutput.length - (toCopy.getOutputCount() + 2)] = 1;

		for (int i = toInstallLayerOutput.length - (toCopy.getOutputCount() + 1); i < toInstallLayerOutput.length; i++) {
			toInstallLayerOutput[i] = toCopyLayerOutput[i - (toCopy.getOutputCount() + 1)];
		}
		for (int i = 0; i < toCopyWeights.length; i++) {
			toInstallWeights[i] = toCopyWeights[i];
		}
		for (int i = toCopyWeights.length; i < toInstallWeights.length; i++) {
			toInstallWeights[i] = new Random().nextDouble() - new Random().nextDouble();
		}
		for (int i = 0; i < toCopyLayerSums.length - toCopyLayerCounts[toCopyLayerCounts.length - 1]; i++) {
			toInstallLayerSums[i] = toCopyLayerSums[i];
		}
		for (int i = toCopyLayerSums.length; i < toInstallLayerSums.length; i++) {
			toInstallLayerSums[i] = toCopyLayerSums[i - layer.getTotalCount()];
		}
		toInstall.setBiasActivation(toInstallBiasActivations);
		toInstall.setActivationFunctions(toInstallFunc);
		toInstall.setContextTargetOffset(toInstallTargetOffset);
		toInstall.setContextTargetSize(toInstallTargetSize);
		toInstall.setEndTraining(toInstallEndTraining);
		toInstall.setLayerContextCount(toInstallLayerContextCount);
		toInstall.setLayerFeedCounts(toInstallFeedCounts);
		toInstall.setLayerIndex(toInstallLayerIndex);
		toInstall.setLayerOutput(toInstallLayerOutput);
		toInstall.setLayerCounts(toInstallLayerCounts);
		toInstall.setWeightIndex(toInstallWeightIndex);
		toInstall.setWeights(toInstallWeights);
		toInstall.setLayerSums(toInstallLayerSums);
		compare(toCopy, toInstall);
		BasicNetwork toReturn = new BasicNetwork();
		BasicLayer input = new BasicLayer(conf.getInputActivation(), conf.getInputBias(), conf.getInputNeuronCount());
		BasicLayer output = new BasicLayer(conf.getOutputActivation(), conf.getOutputBias(),
				conf.getOutputNeuronCount());

		//construct the input
		toReturn.addLayer(input);
		//construct hidden
		for (int i = 0; i < network.getLayerCount() - 2 + 1; i++) {
			BasicLayer hidden = new BasicLayer(conf.getHiddenActivation(), conf.getHiddenBias(),
					conf.getNumberOfCategories());
			toReturn.addLayer(hidden);
		}
		//construct output
		toReturn.addLayer(output);
		toReturn.getStructure().finalizeStructure();
		NetworkCODEC.arrayToNetwork(toInstall.encodeNetwork(), toReturn);
		return toReturn;

	}

	/**
	 * XXX: for debug
	 *
	 * @param toCopy
	 * @param toInstall
	 */
	private static void compare(FlatNetwork toCopy, FlatNetwork toInstall) {
		//print(toCopy.getActivationFunctions(), "COPY ACTIVATION FUNCTIONS", toInstall.getActivationFunctions(), "INSTALL ACTIVATION FUNCTIONS", "ACTIVATION FUNCTIONS:");
		//print(new Object[]{ toCopy.getBeginTraining() }, "COPY BEGIN TRAINING", new Object[]{ toInstall.getBeginTraining() }, "INSTALL BEGIN TRAINING", "BEGIN TRAINING:");
		//print(getArray(toCopy.getBiasActivation()), "COPY BIAS ACTIVATIONS:", getArray(toInstall.getBiasActivation()), "INSTALL BIAS ACTIVATIONS:", "BIAS ACTIVATIONS:");
		//print(getArray(new Double[]{ new Double(toCopy.getConnectionLimit()) }), "COPY CONNECTION LIMIT", getArray(new Double[]{ new Double(toInstall.getConnectionLimit()) }), "INSTALL CONNECTION LIMIT", "CONNECTION LIMIT:");
		//print(getArray(toCopy.getContextTargetOffset()), "COPY CONTEXT TARGET OFFSET", getArray(toInstall.getContextTargetOffset()), "INSTALL CONTEXT TARGET OFFSET", "CONTEXT TARGET OFFSET:");
		//	print(getArray(toCopy.getContextTargetSize()), "COPY CONTEXT TARGET SIZE", getArray(toInstall.getContextTargetSize()), "INSTALL CONTEXT TARGET SIZE", "CONTEXT TARGET SIZE:");
		//	print(getArray(new int[]{ toCopy.getEndTraining() }), "COPY END TRAINING", getArray(new int[]{ toInstall.getEndTraining() }), "INSTALL END TRAINING", "END TRAINGING:");
		//	print(getArray(new boolean[]{ toCopy.getHasContext() }), "COPY HAS CONTEXT", getArray(new boolean[]{ toInstall.getHasContext() }), "INSTALL HAS CONTEXT", "HAS CONTEXT:");
		//	print(getArray(new int[]{ toCopy.getInputCount() }), "COPY INPUT COUNT", getArray(new int[]{ toInstall.getInputCount() }), "INSTALL INPUT COUNT", "INPUT COUNT:");
		//	print(getArray(toCopy.getLayerContextCount()), "COPY LAYER CONTEXT COUNT", getArray(toInstall.getLayerContextCount()), "INSTALL LAYER CONTEXT COUNT", "LAYER CONTEXT COUNT:");
		//	print(getArray(toCopy.getLayerFeedCounts()), "COPY LAYER FEED COUNT", getArray(toInstall.getLayerFeedCounts()), "INSTALL LAYER FEED COUNT", "LAYER FEED COUNT:");
		//	print(getArray(toCopy.getLayerIndex()), "COPY LAYER INDEX", getArray(toInstall.getLayerIndex()), "INSTALL LAYER INDEX", "LAYER INDEX:");
		//	print(getArray(new int[]{ toCopy.getOutputCount() }), "COPY OUTPUT COUNT", getArray(new int[]{ toInstall.getOutputCount() }), "INSTALL OUTPUT COUNT", "OUTPUT COUNT:");
		//print(getArray(), "", getArray(), "", "");
		//print(getArray(toCopy.getLayerOutput()),"COPY LAYER OUTPUT",getArray(toInstall.getLayerOutput()),"INSTALL LAYER OUTPUT","LAYER OUTPUT:");
		//	print(getArray(toCopy.getLayerCounts()), "COPY LAYER COUNTS", getArray(toInstall.getLayerCounts()), "INSTALL LAYER COUNTS", "LAYER COUNTS:");
		//	print(getArray(toCopy.getWeightIndex()),"COPY WEIGHT INDEX",getArray(toInstall.getWeightIndex()),"INSTALL WEIGHT INDEX","WEIGHT INDEX:");
		//	print(getArray(toCopy.getWeights()), "COPY WEIGHTS", getArray(toInstall.getWeights()), "INSTALL WEIGHTS", "WEIGHTS:");
		//	print(getArray(toCopy.getLayerSums()), "COPY LAYER SUMS", getArray(toInstall.getLayerSums()), "INSTALL LAYER SUMS", "LAYER SUMS");
	}

	private final static Class<?>[] ARRAY_PRIMITIVE_TYPES = { int[].class, float[].class, double[].class,
			boolean[].class, byte[].class, short[].class, long[].class, char[].class };

	@SuppressWarnings("unused")
	private static Object[] getArray(Object val) {
		if (val != null) {
			Class<?> valKlass = val.getClass();
			Object[] outputArray = null;

			for (Class<?> arrKlass : ARRAY_PRIMITIVE_TYPES) {
				if (valKlass.isAssignableFrom(arrKlass)) {
					int arrlength = Array.getLength(val);
					outputArray = new Object[arrlength];
					for (int i = 0; i < arrlength; ++i) {
						outputArray[i] = Array.get(val, i);
					}
					break;
				}
			}
			if (outputArray == null) // not primitive type array
				outputArray = (Object[]) val;

			return outputArray;
		}
		return null;

	}

	public static void print(Object[] a, String nameA, Object[] b, String nameB, String info) {
		System.out.println("[" + info + "]" + " PRINTING " + nameA);
		try {
			for (Object obj : a) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			System.out.println("NOT IMPLEMENTED");
		}

		System.out.println("[" + info + "]" + " PRINTING " + nameB);
		try {
			for (Object obj : b) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			System.out.println("NOT IMPLEMENTED");
		}
	}

}
