package zx.soft.ann.core.cli;

import java.util.List;
import java.util.Map.Entry;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.encog.ml.data.MLData;
import org.encog.neural.networks.BasicNetwork;

import zx.soft.ann.core.ArtifactBuilderProcess;
import zx.soft.ann.core.BaseNetworkBuilderProcess;
import zx.soft.ann.core.IngestProcess;
import zx.soft.ann.core.TrainProcess;
import zx.soft.ann.core.VerifyProcess;
import zx.soft.ann.core.exception.DataspaceException;
import zx.soft.ann.core.framework.MnemosyneAPI;
import zx.soft.ann.core.model.NNMetadata;
import zx.soft.ann.core.util.CLIConstants;
import zx.soft.ann.core.util.MnemosyneConstants;
import zx.soft.ann.core.util.foreman.AccumuloForeman;
import zx.soft.ann.core.util.foreman.MnemosyneAccumuloAdministrator;

/**
 * TODO 需要cleanup操作
 *
 * @author wanggang
 *
 */
public class NNAnalyst extends MnemosyneAPI implements CLI {

	public NNAnalyst(String[] args) throws Exception {

		setup();
		CommandLineParser posix = new PosixParser();
		// create Options object
		Options options = new Options();

		// add t option
		options.addOption(CLIConstants.START.getTitle(), false, "Start the Analyst");
		options.addOption(CLIConstants.INGEST.getTitle(), false,
				"Ingest from " + MnemosyneConstants.getIngestDirectory());
		options.addOption(CLIConstants.SETUP.getTitle(), false, "Start Mnemosyne setup");
		options.addOption(CLIConstants.BUILD.getTitle(), false, "Build artifacts");
		options.addOption(CLIConstants.CONSTRUCT.getTitle(), false, "Build base Neural Network");
		options.addOption(CLIConstants.TRAIN.getTitle(), false, "Train Neural Networks");
		options.addOption(CLIConstants.SAVE.getTitle(), false, "Save a NN");
		options.addOption(CLIConstants.INFLATE.getTitle(), false, "Inflate a NN");
		options.addOption(CLIConstants.VERIFY.getTitle(), false, "Use the train foreman to verify the neural network");
		Option help = new Option("help", "print this message");
		options.addOption(help);
		CommandLine cmd = posix.parse(options, args);
		if (cmd.hasOption(CLIConstants.START.getTitle())) {
			String artifactId = getOptionArtifact();
			BasicNetwork n = aForeman.getBaseNetwork(artifactId);
			List<Entry<Key, Value>> metadata = aForeman.fetchByColumnFamily(
					AccumuloForeman.getArtifactRepositoryName(), artifactId);
			for (Entry<Key, Value> entry : metadata) {
				NNMetadata data = NNMetadata.inflate(entry.getValue().toString(), entry.getKey().getRow().toString());
				System.out.println(n == null);
				System.out.println(data == null);
				;
				MLData result = n.compute(getMLDataInput(aForeman.getBaseNetworkConf(artifactId),
						data.getInputNameFields()));
				System.out.println("### RESULT ###");
				@SuppressWarnings("unused")
				double[] results = result.getData();
				//				String stringResults = NNOutput.combine(results,data);
				//				System.out.println(stringResults);
				break;
			}
		} else if (cmd.hasOption(CLIConstants.INGEST.getTitle())) {
			IngestProcess ip = new IngestProcess();
			ip.setup();
			ip.process();
		} else if (cmd.hasOption(CLIConstants.SETUP.getTitle())) {
			MnemosyneAccumuloAdministrator.setup();
		} else if (cmd.hasOption(CLIConstants.BUILD.getTitle())) {
			ArtifactBuilderProcess pro = new ArtifactBuilderProcess();
			pro.setup();
			pro.process();
		} else if (cmd.hasOption(CLIConstants.CONSTRUCT.getTitle())) {
			BaseNetworkBuilderProcess pro = new BaseNetworkBuilderProcess();
			pro.setup();
			pro.process();
		} else if (cmd.hasOption(CLIConstants.TRAIN.getTitle())) {
			TrainProcess train = new TrainProcess();
			train.setup();
			train.process();
		} else if (cmd.hasOption(CLIConstants.SAVE.getTitle())) {
			String artifactId = getOptionArtifact();
			aForeman.saveNetworkToFile("/home/cam/Desktop/card", artifactId);
		} else if (cmd.hasOption(CLIConstants.INFLATE.getTitle())) {
			aForeman = new AccumuloForeman();
			aForeman.connect();
			String artifactId = this.getOptionArtifact();
			BasicNetwork ntw = aForeman.getBaseNetwork(artifactId);
			List<Entry<Key, Value>> metadata = aForeman.fetchByColumnFamily(
					AccumuloForeman.getArtifactRepositoryName(), artifactId);
			for (Entry<Key, Value> entry : metadata) {
				NNMetadata data = NNMetadata.inflate(entry.getValue().toString(), entry.getKey().getRow().toString());

				MLData result = ntw.compute(getMLDataInput(aForeman.getBaseNetworkConf(artifactId),
						data.getInputNameFields()));
				double[] results = result.getData();
				List<Entry<Key, Value>> associations = aForeman.getAssocations(artifactId);
				double[] associationValues = new double[associations.size()];
				for (int i = 0; i < associations.size(); i++) {
					Entry<Key, Value> ent = associations.get(i);
					associationValues[i] = Double.parseDouble(ent.getKey().getColumnQualifier().toString());
				}
				double closest = closest2(results[0], associationValues);
				double stringResults = aForeman.getAssocation(artifactId, closest);
				System.out.println("###RESULT###");
				System.out.println(stringResults);
			}
		} else if (cmd.hasOption(CLIConstants.VERIFY.getTitle())) {
			VerifyProcess pro = new VerifyProcess();
			pro.setup();
			pro.process();
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("nnanalyst", options);
		}
		//serialize the neural net to a file
		//call ingest on a different set of data using the same neural network
		// calculate on one piece of data
	}

	@SuppressWarnings("unused")
	private void print(double[][] input) {
		for (double[] doubs : input) {
			System.out.print("{");
			for (double dub : doubs) {
				System.out.print(dub + " ");
			}
			System.out.print("}\n");
		}
	}

	public static double closest2(double find, double[] values) {
		double closest = values[0];
		double distance = Math.abs(closest - find);
		for (double i : values) {
			double distanceI = Math.abs(i - find);
			if (distance > distanceI) {
				closest = i;
				distance = distanceI;
			}
		}
		return closest;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		NNAnalyst analyst = new NNAnalyst(args);
	}

	@Override
	public void setup() throws DataspaceException {
		aForeman.connect();
		artifactForeman.connect();
	}

}
