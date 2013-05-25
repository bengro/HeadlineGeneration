package generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.TypedDependency;

public class TypedDependencyTree {
	
	private HashMap<String, Node> treeDict = new HashMap<String, Node>();
	private Node head;
	private Node pointer;
	
	protected Node getHead() {
		return head;
	}
	
	protected Node getNode() {
		return pointer;
	}
	
	protected Node getNode(String identifier) {
		return treeDict.get(identifier);
	}
	
	protected void addNode(TypedDependency dep) {
	
		// TAKING CARE OF DEPENDENT
		// if node was already we only have to fill out missing fields, otherwise create new node.
		Node newNode;
		if(treeDict.get(dep.dep().toString()) != null) {
			newNode = treeDict.get(dep.dep().toString());
			newNode.setGrammaticalRelation(dep.reln());
			
		} else {
			newNode = new Node();
			newNode.setWord(dep.dep().label().word());
			newNode.setIdentifier(dep.dep().toString());
			newNode.setGrammaticalRelation(dep.reln());
			
			// add created to dictionary.
			treeDict.put(dep.dep().toString(), newNode);
		}
		
		// TAKING CARE OF GOVERNOR
		// add dependent to existing governor node or generate parent node if it does not exist
		if(treeDict.get(dep.gov().toString()) != null) {
			treeDict.get(dep.gov().toString()).addDependent(newNode);
		} else {
			Node newParentNode = new Node();
			newParentNode.setWord(dep.gov().label().word());
			newParentNode.setIdentifier(dep.gov().toString());
			newParentNode.addDependent(newNode);
			treeDict.put(dep.gov().toString(), newParentNode);
		}
		
		// set head
		if(dep.reln().getShortName().equals("root")) {
			this.head = treeDict.get(dep.dep().toString());
		}
		
		// adding the created governor to parent member of dependent.
		newNode.setParent(treeDict.get(dep.gov().toString()));
		
	}
	
	/**
	 * Print all elements of tree.
	 */
	public String toString() {
		return null;
	}
	
	public String toSentence() {
		return null;
	}
	
}

class Node {
	
	/**
	 * Unique identifier of this word.
	 */
	private String identifier;
	
	/**
	 * Containts the acutal word without position.
	 */
	private String word;
	
	/**
	 * Pointer to parent which it has a relationship with.
	 */
	private Node parent;
	
	/**
	 * Keeps track of undergone modifications by rule.
	 */
	private boolean isModified = false;
	
	/**
	 * Given by Stanford NLP library, defines relation between parent and this node.
	 */
	private GrammaticalRelation relation; 
	
	/**
	 * Contains pointers to the dependents. NULL if leaf.
	 */
	private List<Node> dependents = new ArrayList<Node>();

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public GrammaticalRelation getGrammaticalRelation() {
		return relation;
	}

	public void setGrammaticalRelation(GrammaticalRelation relation) {
		this.relation = relation;
	}

	public List<Node> getDependents() {
		return dependents;
	}

	public void setDependents(List<Node> dependents) {
		this.dependents = dependents;
	}
	
	public void addDependent(Node node) {
		this.dependents.add(node);
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}
	
}
