package zx.soft.ann.core.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.accumulo.core.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import zx.soft.ann.core.exception.ArtifactException;

public class Artifact {

	private static final Logger logger = LoggerFactory.getLogger(Artifact.class);

	private List<String> fields = new ArrayList<>();
	private List<Pair<String, String>> fieldMap = new ArrayList<>();
	private String artifactId = "";
	int lineSize = 0;
	private ArrayList<String> organizedFile = new ArrayList<>();

	public List<String> getFields() {
		return fields;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public List<String> getValue(String field) {
		List<String> toReturn = new ArrayList<>();
		for (Pair<String, String> entry : fieldMap) {
			if (entry.getFirst().startsWith(field)) {
				toReturn.add(entry.getSecond());
			}
		}
		return toReturn;
	}

	public void setArtifactId(String key) {
		this.artifactId = key;
	}

	public void addLine(Integer lineNumber, String value) {
		if (lineNumber > lineSize) {
			lineSize = lineNumber;
		}
		ensureCapasity(lineNumber + 1);
		organizedFile.set(lineNumber, value);

	}

	private void ensureCapasity(Integer lineNumber) {
		while (organizedFile.size() < lineNumber) {
			organizedFile.add("");
		}
	}

	public void finalizeStructure() throws ArtifactException {
		StringBuilder sb = new StringBuilder();
		for (String line : organizedFile) {
			if (!line.equals("")) {
				sb.append(line + "\n");
			}

		}
		constructFieldMap(sb.toString());
	}

	private void constructFieldMap(String fullFile) throws ArtifactException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(fullFile.getBytes()));
			Element docEle = dom.getDocumentElement();
			walk(docEle);
		} catch (IOException e) {
			logger.error("Could not parse artifact XML: {}", e);
			throw new ArtifactException("Could not parse artifact XML ", e);
		} catch (ParserConfigurationException e) {
			logger.error("Could not parse artifact XM: {}", e);
			throw new ArtifactException("Could not parse artifact XML ", e);
		} catch (SAXException e) {
			logger.error("Could not parse artifact XML: {}", e);
			throw new ArtifactException("Could not parse artifact XML ", e);
		}
	}

	private void walk(Node node) {
		if (node.getNodeValue() != (null) && node.getNodeValue().trim().length() > 0) {
			String namespace = constructNameSpace(node, "");

			if (!fields.contains(namespace)) {
				fields.add(namespace);
			}

			fieldMap.add(new Pair<String, String>(namespace, node.getNodeValue().replace("\n", "").trim()));
		}

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			walk(list.item(i));
		}
	}

	private String constructNameSpace(Node node, String toPass) {
		if (node.getParentNode() != null) {
			toPass += node.getParentNode().getNodeName() + "|";
			return constructNameSpace(node.getParentNode(), toPass);
		}
		return reverse(toPass);
	}

	private String reverse(String toPass) {
		String toReturn = "";
		String[] split = toPass.split("\\|");
		for (int i = split.length - 1; i >= 0; i--) {
			if (!split[i].startsWith("#")) {
				toReturn += split[i] + "|";
			}
		}
		return toReturn;
	}

	public String grabDefinitions() {
		List<String> fields = this.getFields();
		String toReturn = "";
		for (String field : fields) {
			if (field.startsWith("mnemosyne|definitions")) {
				List<String> values = this.getValue(field);
				for (String value : values) {
					toReturn += "(" + field + ")" + "{" + value + "}" + "~";
				}
			}
		}
		return toReturn;
	}

	public List<String> grabSets() {
		List<String> inputs = this.getValue("mnemosyne|set|input");

		List<String> outputs = this.getValue("mnemosyne|set|output");
		List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < inputs.size(); i++) {
			toReturn.add("(" + inputs.get(i) + ")" + "(" + outputs.get(i) + ")");
		}
		return toReturn;
	}

	public List<String> getInputs() {
		List<String> inputs = this.getValue("mnemosyne|set|input");
		return inputs;
	}

	public static Artifact inflate(String artifactId, String serialized) {
		Artifact toReturn = new Artifact();
		toReturn.setArtifactId(artifactId);
		String[] fields = serialized.split("\\)");
		for (String fieldPair : fields) {
			String[] values = fieldPair.replace("\\(", "").split(",");
			if (values.length > 1) {
				toReturn.addToFieldMap(values[0], values[1]);
			}
		}
		return toReturn;
	}

	private void addToFieldMap(String field, String value) {
		fields.add(field);
		fieldMap.add(new Pair<String, String>(field, value));

	}

	public List<String> getOutputs() {
		List<String> outputs = this.getValue("mnemosyne|set|output");
		return outputs;
	}

}
