package org.twintiment.analysis.geolocation.textfeatures;

import java.util.HashMap;
import java.util.Map;

/**
 * Unordered b-Tree containing NUTS-Codes as values. A NUTS-Code node is a the
 * child of a node of its prefix-string.
 *
 */
public class NUTSTree extends Tree<String, Integer> {

	public NUTSTree() {
		super("", 0);
	}

	private Map<String, Integer> featureVector = new HashMap<>();
	
	@Override
	public void insert(String key, Integer value) {

		if(find(key, root) != null) {
			return;
		}
		Node newNode = new Node(key, value);
		
		// Keys with length 2 are country nodes ("UK", "DE", ...)
		if (root.getChildren().size() == 0 || key.length() == 2) {
			newNode.setParent(root);
			root.getChildren().add(newNode);
		} else {
			Node parent = findInsertionNode(key, root);
			newNode.setParent(parent);
			parent.getChildren().add(newNode);
		}
	}

	public void incValue(String key) {
		Node n = find(key, root);
		n.setValue(n.getValue() + 1);
	}

	private int clusterValue(Node n) {
		if (n.getChildren().size() == 0)
			return n.getValue();

		int sum = 0;
		for (Node c : n.getChildren()) {
			sum += clusterValue(c);
		}
		return sum + n.getValue();
	}

	/**
	 * 
	 * @param key
	 * @return The sum of the values of all child nodes of the node with the
	 *         given key
	 */
	public int clusterValue(String key) {
		return clusterValue(find(key, root));
	}

	private Node findInsertionNode(String key, Node parent) {
		for (Node c : parent.getChildren()) {
			if (key.indexOf(c.getKey()) == 0) {
				// value is one char longer than c -> direct child
				if (c.getKey().length() == key.length() - 1)
					return c;
				// else go deeper along this subtree
				else
					return findInsertionNode(key, c);
			}
		}
		return null;
	}

	private Node find(String key, Node parent) {
		for (Node c : parent.getChildren()) {
			// if prefix
			if (key.indexOf(c.getKey()) == 0) {
				// value is one char longer than c -> direct child
				if (c.getKey().equals(key))
					return c;
				// else go deeper along this subtree
				else
					return find(key, c);
			}
		}
		return null;
	}

	/**
	 * Creates a map with the all nodes that contain keys of length <=
	 * {@code codeLength}. For those nodes with keys of length exactly equal to
	 * {@code codeLength}, the cluster value of the node's children is put into
	 * the map. Hence map does not have the same length as the trees number
	 * of nodes!
	 * 
	 * @param codeLength
	 * @return array containing the (cluster-)values of the tree's nodes.
	 */
	public Map<String, Integer> toVector(int codeLength) {
		featureVector = new HashMap<>();
		fillFeatureVector(codeLength, root);
		return featureVector;
	}
	
	private void fillFeatureVector(int codeLength, Node parent) {
		if(parent != root) {
			if(parent.getKey().length() < codeLength)
				featureVector.put(parent.getKey(), parent.getValue());
			else if(parent.getKey().length() == codeLength) {
				featureVector.put(parent.getKey(), clusterValue(parent));
				return;
			}
		}
		for(Node c : parent.getChildren()) {
			fillFeatureVector(codeLength, c);
		}
	}

	private String toString(Node n) {

		String str = "";
		if (n.getKey() != null) {
			for (int i = 0; i < n.getKey().length(); i++)
				str += "-";
		}
		str += n.toString() + "\n";
		for (Node c : n.getChildren()) {
			str += toString(c);
		}

		return str;
	}

	@Override
	public String toString() {
		return toString(root);
	}

}
