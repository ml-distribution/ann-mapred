package zx.soft.ann.util;

import zx.soft.ann.core.ArtifactBuilderProcess;
import zx.soft.ann.core.BaseNetworkBuilderProcess;
import zx.soft.ann.core.CongressBuilderProcess;
import zx.soft.ann.core.CongressTrainProcess;
import zx.soft.ann.core.IngestProcess;
import zx.soft.ann.core.TrainProcess;
import zx.soft.ann.core.VerifyProcess;

public class TestHelper {

	public static void constructBaseClassificationNetwork() throws Exception {
		BaseNetworkBuilderProcess pro = new BaseNetworkBuilderProcess();
		pro.setup();
		pro.process();
	}

	public static void ingestTestArtifacts() throws Exception {
		IngestProcess pro = new IngestProcess();
		pro.setup();
		pro.process();
	}

	public static void buildArtifacts() throws Exception {
		ArtifactBuilderProcess pro = new ArtifactBuilderProcess();
		pro.setup();
		pro.process();
	}

	public static void trainNetworks() throws Exception {
		TrainProcess pro = new TrainProcess();
		pro.setup();
		pro.process();

	}

	public static void constructCongress() throws Exception {
		CongressBuilderProcess pro = new CongressBuilderProcess();
		pro.setup();
		pro.process();

	}

	public static void trainCongress() throws Exception {
		CongressTrainProcess pro = new CongressTrainProcess();
		pro.setup();
		pro.process();

	}

	public static void verifyCongress() throws Exception {
		VerifyProcess pro = new VerifyProcess();
		pro.setup();
		pro.process();
	}

}
