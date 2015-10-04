package zx.soft.ann.core.util.foreman;

import zx.soft.ann.core.exception.DataspaceException;
import zx.soft.ann.core.exception.RepositoryException;

public class TrainForeman implements Foreman {

	private AccumuloForeman aForeman = new AccumuloForeman();
	private ArtifactForeman artifactForeman = new ArtifactForeman();

	public TrainForeman() {
	}

	public void connect() throws DataspaceException {
		aForeman.connect();
		artifactForeman.connect();
	}

	public void register(String artifactId, double[] input, double[] output) throws RepositoryException {
		String fam = AccumuloForeman.getArtifactRepository().verifyEntry();
		String qual = System.currentTimeMillis() + "";
		String value = toString(input, output);
		aForeman.add(AccumuloForeman.getArtifactRepositoryName(), artifactId, fam, qual, value);
	}

	private String toString(double[] input, double[] output) {
		String toReturn = "";

		for (double in : input) {
			toReturn += in + ",";
		}

		toReturn += "|";

		for (double out : output) {
			toReturn += out + ",";
		}
		toReturn = toReturn.substring(0, toReturn.length() - 1);

		return toReturn;
	}

}
