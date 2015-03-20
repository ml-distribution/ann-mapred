package zx.soft.ann.core.util.foreman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.obj.SerializeObject;

import zx.soft.ann.conf.ClassificationNetworkConf;
import zx.soft.ann.conf.CongressNetworkConf;
import zx.soft.ann.core.exception.RepositoryException;
import zx.soft.ann.core.framework.ArtifactRepository;
import zx.soft.ann.core.framework.BaseNetworkRepository;
import zx.soft.ann.core.model.Neuron;
import zx.soft.ann.core.util.InputOutputHolder;
import zx.soft.ann.core.util.MnemosyneConstants;

public class AccumuloForeman implements Foreman {

	private Connector conn;
	private static final Logger log = Logger.getLogger(AccumuloForeman.class.getName());

	public AccumuloForeman() {
		//
	}

	@SuppressWarnings("deprecation")
	public boolean connect() throws RepositoryException {
		try {
			Instance instance = new ZooKeeperInstance(MnemosyneConstants.getAccumuloInstance(),
					MnemosyneConstants.getZookeeperInstance());
			conn = new Connector(instance, MnemosyneConstants.getAccumuloUser(), MnemosyneConstants
					.getAccumuloPassword().getBytes());
		} catch (AccumuloException e) {
			String gripe = "Could not connect the Accumulo Foreman. Check the configuration files.";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (AccumuloSecurityException e) {
			String gripe = "Could not connect the Accumulo Foreman. Check the configuration files.";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
		return true;
	}

	public Connector getConnector() throws RepositoryException {
		if (conn == null) {
			String gripe = "Could not grab the Accumulo Connector. Check Accumulo.";
			log.log(Level.SEVERE, gripe);
			throw new RepositoryException(gripe);
		}
		return conn;
	}

	public TableOperations getTableOps() throws RepositoryException {
		if (conn == null || conn.tableOperations() == null) {
			String gripe = "Could not modify tables in Accumulo. Check Accumulo.";
			log.log(Level.SEVERE, gripe);
			throw new RepositoryException(gripe);
		}
		return conn.tableOperations();
	}

	public boolean deleteTables() throws RepositoryException {
		Map<String, String> tableMap = this.getTableOps().tableIdMap();
		Iterator<Entry<String, String>> it = tableMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = (it.next());
			if (!entry.getKey().startsWith("!")) {
				log.log(Level.INFO, "Deleting " + entry.getKey() + " ... ");
				this.deleteTable(entry.getKey());
			}
		}
		return true;
	}

	public boolean deleteTable(String name) throws RepositoryException {
		TableOperations tableOps = this.getTableOps();
		try {
			tableOps.delete(name);
		} catch (AccumuloException e) {
			String gripe = "Could not delete this table from Accumulo:" + name;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (AccumuloSecurityException e) {
			String gripe = "Could not delete this table from Accumulo:" + name;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (TableNotFoundException e) {
			String gripe = "Could not delete this table from Accumulo:" + name + " (It doesn't exist)";
			log.log(Level.WARNING, gripe);

		}

		return true;
	}

	public BatchWriter getBatchWriter(String tableName) throws RepositoryException {
		long memBuf = 1000000L; // bytes to store before sending a batch
		long timeout = 1000L; // milliseconds to wait before sending
		int numThreads = 10;

		BatchWriter writer = null;
		try {
			writer = conn.createBatchWriter(tableName, memBuf, timeout, numThreads);
		} catch (TableNotFoundException e) {
			String gripe = "Could not write to " + tableName + " (It doesn't exist)";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
		return writer;
	}

	public void addBytes(String table, String row, String fam, String qual, byte[] value) throws RepositoryException {
		BatchWriter writer = this.getBatchWriter(table);
		Mutation m = new Mutation(row);
		Value v = new Value();
		v.set(value);
		m.put(fam, qual, new ColumnVisibility(MnemosyneConstants.getDefaultAuths()), System.currentTimeMillis(), v);
		try {
			writer.addMutation(m);
			writer.close();
		} catch (MutationsRejectedException e) {
			String gripe = "Could not assert this mutation:table=" + table + " row=" + row + " fam=" + fam;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}

	}

	public void add(String table, String row, String fam, String qual, String value) throws RepositoryException {
		this.addBytes(table, row, fam, qual, value.getBytes());

	}

	public void makeTable(String tableName) throws RepositoryException {
		TableOperations tableOps = this.getTableOps();
		try {
			tableOps.create(tableName);
		} catch (AccumuloException e) {
			String gripe = "Could not create table:" + tableName;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (AccumuloSecurityException e) {
			String gripe = "Could not create table:" + tableName;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (TableExistsException e) {
			String gripe = "Could not create table:" + tableName + " (Table already exists)";
			log.log(Level.SEVERE, gripe, e);
			return;
		}

		log.log(Level.INFO, tableName + " created ...");

	}

	public List<Entry<Key, Value>> fetchByRowColumnFamily(String table, String row, String fam)
			throws RepositoryException {
		Authorizations auths = new Authorizations(MnemosyneConstants.getDefaultAuths());

		Scanner scan;
		List<Entry<Key, Value>> toRet = new ArrayList<Entry<Key, Value>>();
		try {
			scan = conn.createScanner(table, auths);
			scan.setRange(Range.exact(row));
			scan.fetchColumnFamily(new Text(fam));
			for (Entry<Key, Value> entry : scan) {
				toRet.add(entry);
			}
		} catch (TableNotFoundException e) {
			String gripe = "Couldn't fetch columns. (Table does not exist)";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}

		return toRet;
	}

	public List<Entry<Key, Value>> fetchByColumnFamily(String table, String fam) throws RepositoryException {
		Authorizations auths = new Authorizations(MnemosyneConstants.getDefaultAuths());

		Scanner scan;
		List<Entry<Key, Value>> toRet = new ArrayList<Entry<Key, Value>>();
		try {
			scan = conn.createScanner(table, auths);
			scan.fetchColumnFamily(new Text(fam));
			for (Entry<Key, Value> entry : scan) {
				toRet.add(entry);
			}
		} catch (TableNotFoundException e) {
			String gripe = "Couldn't fetch columns. (Table does not exist)";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}

		return toRet;
	}

	public List<Entry<Key, Value>> fetchByQualifier(String table, String fam, String qual) throws RepositoryException {
		Authorizations auths = new Authorizations(MnemosyneConstants.getDefaultAuths());
		List<Entry<Key, Value>> toRet = new ArrayList<Entry<Key, Value>>();
		Scanner scan;
		try {
			scan = conn.createScanner(table, auths);
			scan.fetchColumn(new Text(fam), new Text(qual));
			for (Entry<Key, Value> entry : scan) {
				toRet.add(entry);
			}
		} catch (TableNotFoundException e) {
			String gripe = "Could not fetch columns. (Table:" + table + " doesn't exist)";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}

		return toRet;
	}

	public void saveNetwork(String TABLE_TO_SAVE, String FAMILY_NAME, String artifactId, BasicNetwork network,
			ClassificationNetworkConf conf) throws RepositoryException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(network);
			out.close();
			byte[] arr = baos.toByteArray();
			this.addBytes(TABLE_TO_SAVE, artifactId, FAMILY_NAME, artifactId, arr);

			baos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(baos);
			out.writeObject(conf);
			out.close();
			arr = baos.toByteArray();

			this.addBytes(TABLE_TO_SAVE, artifactId, AccumuloForeman.getBaseNetworkRepository().baseConfiguration(),
					artifactId, arr);

			this.add(TABLE_TO_SAVE, artifactId, AccumuloForeman.getBaseNetworkRepository().baseError(), artifactId, "1");
		} catch (IOException e) {
			String gripe = "Could not save a base network in Accumulo.";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}

	}

	public BasicNetwork inflateNetwork(String tableName, String fam, String artifactId) throws RepositoryException {
		try {
			List<Entry<Key, Value>> rows = this.fetchByQualifier(tableName, fam, artifactId);
			for (Entry<Key, Value> entry : rows) {
				byte[] arr = entry.getValue().get();
				ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(arr));
				return (BasicNetwork) objectIn.readObject();
			}

		} catch (IOException e) {
			String gripe = "Could not inflate a network from Accumulo";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (ClassNotFoundException e) {
			String gripe = "Attempted to inflate a network from Accumulo. It wasn't of type BaseNetwork";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
		return null;

	}

	public boolean tableExists(String name) throws RepositoryException {
		return this.getTableOps().exists(name);
	}

	public void assertBaseNetwork(BasicNetwork network, String artifactId, ClassificationNetworkConf conf)
			throws RepositoryException {
		this.saveNetwork(AccumuloForeman.getBaseNetworkRepositoryName(), AccumuloForeman.getBaseNetworkRepository()
				.getRawBytesField(), artifactId, network, conf);
	}

	public BasicNetwork getBaseNetwork(String artifactId) throws RepositoryException {
		return this.inflateNetwork(AccumuloForeman.getBaseNetworkRepositoryName(), AccumuloForeman
				.getBaseNetworkRepository().getRawBytesField(), artifactId);
	}

	public static String getArtifactRepositoryName() {
		return AccumuloForeman.getArtifactRepository().toString();
	}

	public static String getBaseNetworkRepositoryName() {
		return AccumuloForeman.getBaseNetworkRepository().toString();
	}

	public static BaseNetworkRepository getBaseNetworkRepository() {
		return new BaseNetworkRepository();
	}

	public static ArtifactRepository getArtifactRepository() {
		return new ArtifactRepository();
	}

	public ClassificationNetworkConf getBaseNetworkConf(String artifactId) throws RepositoryException {
		return this.inflateNetworkConfiguration(artifactId);
	}

	private ClassificationNetworkConf inflateNetworkConfiguration(String artifactId) throws RepositoryException {
		List<Entry<Key, Value>> rows = this.fetchByQualifier(AccumuloForeman.getBaseNetworkRepositoryName(),
				AccumuloForeman.getBaseNetworkRepository().baseConfiguration(), artifactId);
		try {
			for (Entry<Key, Value> entry : rows) {
				byte[] arr = entry.getValue().get();
				ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(arr));
				return (ClassificationNetworkConf) objectIn.readObject();
			}
		} catch (IOException e) {
			String gripe = "Could not inflate the Base Network Configuration for " + artifactId;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (ClassNotFoundException e) {
			String gripe = "Attempted to inflate a Base Network Configuration that returned object not in type ClassificationNetworkConf";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
		return null;
	}

	public double getBaseNetworkError(String artifactId) throws RepositoryException {
		return this.inflateNetworkError(artifactId);
	}

	private double inflateNetworkError(String artifactId) throws RepositoryException {
		return Double.parseDouble(this
				.fetchByQualifier(AccumuloForeman.getBaseNetworkRepositoryName(),
						AccumuloForeman.getBaseNetworkRepository().baseError(), artifactId).get(0).getValue()
				.toString());
	}

	public void assertBaseNetworkError(double error, String artifactId) throws RepositoryException {
		this.add(AccumuloForeman.getBaseNetworkRepositoryName(), artifactId, AccumuloForeman.getBaseNetworkRepository()
				.baseError(), artifactId, error + "");
	}

	public void addTrainingData(String artifactId, double[][] input, double[][] output) throws RepositoryException {
		try {
			InputOutputHolder holder = new InputOutputHolder(input, output);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(holder);
			out.close();
			byte[] arr = baos.toByteArray();
			this.addBytes(AccumuloForeman.getBaseNetworkRepositoryName(), artifactId, AccumuloForeman
					.getBaseNetworkRepository().trainData(), artifactId + System.currentTimeMillis() + "", arr);
		} catch (IOException e) {
			String gripe = "Couldn't add training data for " + artifactId;
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
	}

	public ArrayList<InputOutputHolder> getInputOutputHolders(String artifactId) throws RepositoryException {
		List<Entry<Key, Value>> entries = this.fetchByColumnFamily(AccumuloForeman.getBaseNetworkRepository()
				.baseNetwork(), AccumuloForeman.getBaseNetworkRepository().trainData());
		ArrayList<InputOutputHolder> pastInputs = new ArrayList<InputOutputHolder>();
		for (Entry<Key, Value> entry : entries) {
			Key key = entry.getKey();
			Text cq = key.getColumnQualifier();
			try {
				if (cq.toString().startsWith(artifactId)) {
					Value val = entry.getValue();
					byte[] arr = val.get();
					ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(arr));
					InputOutputHolder store = (InputOutputHolder) objectIn.readObject();
					pastInputs.add(store);
				}
			} catch (IOException e) {
				String gripe = "Could not get this past training input :" + key.toString();
				log.log(Level.SEVERE, gripe, e);
				throw new RepositoryException(gripe, e);
			} catch (ClassNotFoundException e) {
				String gripe = "Could not inflate this past training input:" + key.toString()
						+ " (Inflated input is not of type InputOutputHolder)";
				log.log(Level.SEVERE, gripe, e);
				throw new RepositoryException(gripe, e);
			}

		}
		return pastInputs;
	}

	public ArrayList<double[][]> getPastTrainingInput(String artifactId) throws RepositoryException {
		List<Entry<Key, Value>> entries = this.fetchByColumnFamily(AccumuloForeman.getBaseNetworkRepository()
				.baseNetwork(), AccumuloForeman.getBaseNetworkRepository().trainData());
		ArrayList<double[][]> pastInputs = new ArrayList<double[][]>();
		for (Entry<Key, Value> entry : entries) {
			Key key = entry.getKey();
			Text cq = key.getColumnQualifier();
			try {
				if (cq.toString().startsWith(artifactId)) {
					Value val = entry.getValue();
					byte[] arr = val.get();
					ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(arr));
					InputOutputHolder store = (InputOutputHolder) objectIn.readObject();
					pastInputs.add(store.getInput());
				}
			} catch (IOException e) {
				String gripe = "Could not get this past training input :" + key.toString();
				log.log(Level.SEVERE, gripe, e);
				throw new RepositoryException(gripe, e);
			} catch (ClassNotFoundException e) {
				String gripe = "Could not inflate this past training input:" + key.toString()
						+ " (Inflated input is not of type InputOutputHolder)";
				log.log(Level.SEVERE, gripe, e);
				throw new RepositoryException(gripe, e);
			}

		}
		return pastInputs;
	}

	public ArrayList<double[][]> getPastTrainingOutput(String artifactId) throws RepositoryException {
		List<Entry<Key, Value>> entries = this.fetchByColumnFamily(AccumuloForeman.getBaseNetworkRepository()
				.baseNetwork(), AccumuloForeman.getBaseNetworkRepository().trainData());
		ArrayList<double[][]> pastOutputs = new ArrayList<double[][]>();
		for (Entry<Key, Value> entry : entries) {
			Key key = entry.getKey();
			Text cq = key.getColumnQualifier();
			if (cq.toString().startsWith(artifactId)) {
				Value val = entry.getValue();
				byte[] arr = val.get();
				try {
					ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(arr));
					InputOutputHolder store = (InputOutputHolder) objectIn.readObject();
					pastOutputs.add(store.getOutput());
				} catch (IOException e) {
					String gripe = "Could not get past training output:" + key.toString();
					log.log(Level.SEVERE, gripe, e);
					throw new RepositoryException(gripe, e);
				} catch (ClassNotFoundException e) {
					String gripe = "Could not inflate a past training output:" + key.toString()
							+ " (Type is not InputOutputHolder)";
					log.log(Level.SEVERE, gripe, e);
					throw new RepositoryException(gripe, e);
				}

			}

		}
		return pastOutputs;
	}

	public void saveNetworkToFile(String fileName, String artifactId) throws RepositoryException {
		BasicNetwork n = this.inflateNetwork(AccumuloForeman.getBaseNetworkRepositoryName(), AccumuloForeman
				.getBaseNetworkRepository().baseNetwork(), artifactId);
		try {
			SerializeObject.save(new File(fileName), n);
		} catch (IOException e) {
			String gripe = "Failed to save a network to a file";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
	}

	public void assertInputOutputHolder(String artifactId, InputOutputHolder newHolder) throws RepositoryException {
		addTrainingData(artifactId, newHolder.getInput(), newHolder.getOutput());
	}

	public void assertCongress(List<Neuron> neuronsCreated, String artifactId, CongressNetworkConf conf)
			throws RepositoryException {
		this.add("CONGRESS", artifactId, "NEURON", "CONF", conf.serialize());

		for (Neuron n : neuronsCreated) {
			this.add("CONGRESS", artifactId, "NEURON", n.getHash(), n.serialize());
			this.add("CONGRESS", artifactId, "NEURON", n.getHash() + " IN USE", new String(false + ""));
			this.add("CONGRESS", artifactId, "NEURON", n.getHash() + " TRAINED INPUT COUNT", new String(0 + ""));

		}
	}

	public Neuron getATrainableNeuron(String artifactId) throws RepositoryException {
		List<Entry<Key, Value>> entries = this.fetchByRowColumnQualifier("CONGRESS", artifactId, "NEURON", "IN USE");
		boolean neuronNotFound = true;
		Neuron toRet = null;
		while (neuronNotFound) {
			int index = new Random().nextInt(entries.size());
			Entry<Key, Value> entry = entries.get(index);
			Key key = entry.getKey();
			Value val = entry.getValue();
			if (val.toString().equals("false")) {
				String cf = key.getColumnQualifier().toString();
				String neuronHash = cf.replace("IN USE", "").trim();
				toRet = inflateNeuron(neuronHash);
				neuronNotFound = true;
				this.add("CONGRESS", artifactId, "NEURON", cf, "true");
				neuronNotFound = false;
			}

		}
		return toRet;
	}

	private Neuron inflateNeuron(String neuronHash) throws RepositoryException {
		List<Entry<Key, Value>> entry = this.fetchByQualifier("CONGRESS", "NEURON", neuronHash);
		String serializedNeuron = entry.get(0).getValue().toString();
		return Neuron.inflate(serializedNeuron);
	}

	private List<Entry<Key, Value>> fetchByRowColumnQualifier(String table, String row, String fam,
			String qualifierRegex) throws RepositoryException {
		Authorizations auths = new Authorizations(MnemosyneConstants.getDefaultAuths());

		Scanner scan;
		List<Entry<Key, Value>> toRet = new ArrayList<Entry<Key, Value>>();
		try {
			scan = conn.createScanner(table, auths);
			scan.setRange(Range.exact(row));
			scan.fetchColumnFamily(new Text(fam));
			for (Entry<Key, Value> entry : scan) {
				String qualifier = entry.getKey().getColumnQualifier().toString();
				if (qualifier.contains(qualifierRegex)) {
					toRet.add(entry);
				}
			}
		} catch (TableNotFoundException e) {
			String gripe = "Couldn't fetch columns. (Table does not exist)";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}

		return toRet;
	}

	public void setNeuronAvailable(String artifactId, Neuron toTrain) throws RepositoryException {
		this.add("CONGRESS", artifactId, "NEURON", toTrain.getHash() + " IN USE", "false");
	}

	public CongressNetworkConf inflateCongressConfiguration(String artifactId) throws RepositoryException {
		Entry<Key, Value> entry = this.fetchByRowColumnQualifier("CONGRESS", artifactId, "NEURON", "CONF").get(0);
		Value v = entry.getValue();

		return CongressNetworkConf.inflate(v.toString());
	}

	@SuppressWarnings("unused")
	public List<Neuron> getCongress(String artifactId) throws RepositoryException {
		List<Entry<Key, Value>> entries = this.fetchByRowColumnQualifier("CONGRESS", artifactId, "NEURON", "IN USE");

		List<Neuron> congress = new ArrayList<Neuron>();
		for (Entry<Key, Value> entry : entries) {
			Key key = entry.getKey();
			Value val = entry.getValue();
			String cf = key.getColumnQualifier().toString();
			String neuronHash = cf.replace("IN USE", "").trim();
			Neuron n = inflateNeuron(neuronHash);

			Entry<Key, Value> numberProcessed = this.fetchByRowColumnQualifier("CONGRESS", artifactId, "NEURON",
					n.getHash() + " TRAINED INPUT COUNT").get(0);
			int numProcessed = Integer.parseInt(numberProcessed.getValue().toString());
			if (numProcessed > 0) {
				congress.add(n);
			}
		}
		return congress;
	}

	public void assertCongressNumberOfInputs(String artifactId, int inputNeuronCount) throws RepositoryException {
		this.add("CONGRESS", artifactId, "NEURON", "NUM OF INPUTS", inputNeuronCount + "");
	}

	public int getCongressNumberOfInputs(String artifactId) throws RepositoryException {
		Entry<Key, Value> entry = this.fetchByRowColumnQualifier("CONGRESS", artifactId, "NEURON", "NUM OF INPUTS")
				.get(0);
		return Integer.parseInt(entry.getValue().toString());
	}

	public void incrementNumberProcessed(String artifactId, String hash) throws RepositoryException {
		Entry<Key, Value> entry = this.fetchByRowColumnQualifier("CONGRESS", artifactId, "NEURON",
				hash + " TRAINED INPUT COUNT").get(0);
		int numProcessed = Integer.parseInt(entry.getValue().toString());
		numProcessed++;
		this.add("CONGRESS", artifactId, "NEURON", hash + " TRAINED INPUT COUNT", new String(numProcessed + ""));
	}

	public void associateOutput(String artifactId, double[] expected, double output) throws RepositoryException {
		this.add(MnemosyneConstants.getBaseNetworkRepositoryName(), artifactId, "ASSOCIATION", expected[0] + "", output
				+ "");
	}

	public BasicNetwork[] getBaseNetworkCommittees(String artifactId) throws RepositoryException {
		return this.inflateCommittees(AccumuloForeman.getBaseNetworkRepositoryName(), AccumuloForeman
				.getBaseNetworkRepository().getRawBytesField(), artifactId);
	}

	private BasicNetwork[] inflateCommittees(String tableName, String fam, String artifactId)
			throws RepositoryException {
		BasicNetwork[] toReturn = null;
		try {
			List<Entry<Key, Value>> rows = this.fetchByRowColumnQualifier(tableName, strip(artifactId), fam,
					strip(artifactId));
			toReturn = new BasicNetwork[rows.size()];
			for (int i = 0; i < rows.size(); i++) {
				Entry<Key, Value> entry = rows.get(i);
				byte[] arr = entry.getValue().get();
				ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(arr));
				toReturn[i] = (BasicNetwork) objectIn.readObject();
			}
		} catch (IOException e) {
			String gripe = "Could not inflate a network from Accumulo";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (ClassNotFoundException e) {
			String gripe = "Attempted to inflate a network from Accumulo. It wasn't of type BaseNetwork";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		} catch (RepositoryException e) {
			String gripe = "Attempted to inflate a network from Accumulo. It wasn't of type BaseNetwork";
			log.log(Level.SEVERE, gripe, e);
			throw new RepositoryException(gripe, e);
		}
		return toReturn;
	}

	private String strip(String artifactId) {
		String toReturn = artifactId.split("\\.xml")[0] + ".xml";
		return toReturn;
	}

	public List<Entry<Key, Value>> getAssocations(String artifactId) throws RepositoryException {
		return this
				.fetchByRowColumnFamily(MnemosyneConstants.getBaseNetworkRepositoryName(), artifactId, "ASSOCIATION");
		//return null;
	}

	public double getAssocation(String artifactId, double closest) throws NumberFormatException, RepositoryException {
		return Double.parseDouble(this
				.fetchByRowColumnQualifier(MnemosyneConstants.getBaseNetworkRepositoryName(), artifactId,
						"ASSOCIATION", closest + "").get(0).getValue().toString());
	}

}
