package zx.soft.ann.core.framework;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import zx.soft.ann.conf.CongressNetworkConf;
import zx.soft.ann.conf.NeuronConf;
import zx.soft.ann.core.model.Neuron;

public class CongressNetwork {

	public static List<Neuron> constructNetworks(CongressNetworkConf conf) {
		Logger.getLogger(CongressNetwork.class.getName()).log(Level.INFO, "Constructing networks");
		int numOfNeurons = conf.getNumberOfNeurons();
		int numberOfInputs = conf.getNumberOfInputs();
		List<Neuron> toReturn = new Vector<Neuron>();
		for (int i = 0; i < numOfNeurons; i++) {
			NeuronConf nConf = new NeuronConf();
			nConf.setLearningRate(.02);
			nConf.setThreshold(.2);
			Neuron toAdd = new Neuron(nConf);
			toAdd.setup(numberOfInputs);
			toReturn.add(toAdd);
		}
		return toReturn;
	}

}
