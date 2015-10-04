package zx.soft.ann.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NNMetadata {

	Map<String, List<String>> fieldMap = new HashMap<>();
	private String artifactId = "";

	public NNMetadata() {
		//
	}

	public static NNMetadata inflate(String value, String artifactID) {
		NNMetadata toReturn = new NNMetadata();
		toReturn.setArtifactId(artifactID);
		String[] values = value.split("~");

		for (String valueString : values) {
			String longField = valueString.split("\\)")[0].replace("\\(", "");
			int longFieldSize = longField.split("\\|").length;
			String shortField = longField.split("\\|")[longFieldSize - 1];

			String fieldValue = valueString.split("\\)")[1].replace("{", "").replace("}", "");
			if (fieldValue.trim().length() > 0) {
				toReturn.addField(shortField, fieldValue);
			}
		}

		return toReturn;
	}

	private void setArtifactId(String artifactID) {
		this.artifactId = artifactID;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	private void addField(String shortField, String fieldValue) {
		if (fieldMap.containsKey(shortField)) {
			fieldMap.get(shortField).add(fieldValue);
		} else {
			ArrayList<String> toAdd = new ArrayList<>();
			toAdd.add(fieldValue);
			fieldMap.put(shortField, toAdd);
		}
	}

	public List<String> getValues(String field) {
		return fieldMap.get(field);
	}

	public Iterator<Entry<String, List<String>>> iterator() {
		return fieldMap.entrySet().iterator();
	}

	public List<String> getInputNameFields() {
		return fieldMap.get("inputname");
	}

	public List<String> getOutputNameFields() {
		return fieldMap.get("outputname");
	}

	public List<String> getOutputValueFields() {
		return fieldMap.get("value");
	}

	public List<String> getFileNameField() {
		return fieldMap.get("fileName");
	}

	public int getInputMax() {
		return (int) Double.parseDouble(fieldMap.get("inputmax").get(0));
	}

	public int getOutputMax() {
		return (int) Double.parseDouble(fieldMap.get("outputmax").get(0));
	}

}
