package zx.soft.ann.examples;

import zx.soft.ann.core.cli.NNAnalyst;

public class BreastCancerNN {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		//		Deploy.deployPsuedoDistrubtedSystem();
		//		String[] setup = {"-setup"};
		NNAnalyst analyst = new NNAnalyst(new String[] { "-ingest" });
		//		String[] ingest = {"-ingest"};
		//		analyst = new NNAnalyst(ingest);
		//		String[] build = {"-build"};
		//		analyst = new NNAnalyst(build);
		//		String[] construct = {"-construct"};
		//		analyst = new NNAnalyst(construct);
		//		String[] train = {"-train"};
		//		analyst = new NNAnalyst(train);
		//		analyst = new NNAnalyst(new String[]{"-verify"});
	}

}
