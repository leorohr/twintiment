package org.twintiment.analysis.geolocation.textfeatures;

import java.util.ArrayList;
import java.util.List;

public abstract class Tree<K,V> {
	
	protected Node root;
	
	public Tree() {
		root = new Node(null, null);
	}
	
	public Tree(K rootKey, V rootValue) {
		root = new Node(rootKey, rootValue);
	}
	
	public abstract void insert(K key, V value);
	
	public Node getRoot() {
		return this.root;
	}
	
	protected class Node {
		
		private K key;
		private V value;
		private Node parent;
		private List<Node> children;
		
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
			this.children = new ArrayList<Node>();
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}
		
		@Override
		public String toString() {

			return "[" + key + " : " + value + "]";
		}
	}
}


