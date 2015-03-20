package zx.soft.ann.core.util.factory;

public class ArtifactIdFactory {

	public static String buildArtifactId(String uuid, String fileName) {
		return "ARTIFACT_" + uuid + "_" + fileName;
	}

}
