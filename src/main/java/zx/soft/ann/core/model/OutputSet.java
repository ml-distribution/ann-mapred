package zx.soft.ann.core.model;

import java.util.List;
import java.util.Vector;

public class OutputSet<K extends Number> {

	private List<List<K>> outputSet = new Vector<List<K>>();

	public OutputSet() {
		//
	}

	public void addSet(List<K> set) {
		outputSet.add(set);
	}

	public List<List<K>> getOutputSets() {
		return this.outputSet;
	}

}
