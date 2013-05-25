package generators;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class DependencyBaseline extends AbstractGenerator {
	
	Tree phraseTree;
	TypedDependencyTree dependencyTree;
	Collection<TypedDependency> dependencies;
	String generatedHeadline;
	
	/**
	 * For GUI only.
	 */
	String rootNodeType; 
	
	public DependencyBaseline(File doc) {
		super(doc);
	}

	@Override
	public String returnHeadline() {
		return generatedHeadline;
	}

	@Override
	public void generateHeadline() {
		
		// get dependency of first sentence
		phraseTree = dependencies(sentences.get(0));
		
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(phraseTree);
		dependencies = gs.typedDependenciesCCprocessed();
		
		dependencyTree = new TypedDependencyTree();
		//depTree.addNode(node)
		for(TypedDependency dep : dependencies) {
			//System.out.println("Added " + dep.toString() + " to the tree.");
			dependencyTree.addNode(dep);
			
		}
		
		// apply magic algorithm for removing unimportant relationships
		analyseTree();
	
	}

	protected void analyseTree() {
		
		// get children of head: level 1
		Node headNode = dependencyTree.getHead();
		System.out.println("root is: " + headNode.getWord() + " and has " + headNode.getDependents().size() + " children");
		
		for(Node node : dependencyTree.getHead().getDependents()) {
			System.out.println("Outgoing relation: " + node.getGrammaticalRelation().getShortName() + " -> " + node.getWord());
		}
		
		// get PoS tag of head
		List<CoreLabel> words = phraseTree.taggedLabeledYield();
		
		boolean headNodeIsVerb = false;
		for(CoreLabel word : words) {
			System.out.println("word " + word.word() + " head word " + headNode.getWord());
			
			if(word.word().equals(headNode.getWord())) {
				headNodeIsVerb = true;
				rootNodeType = word.tag();
				System.out.println("verb lemma: " + word.lemma());
				break;
			}
		}
		
		// analyse branches, focus on most important ones: nsubj, dobj, prep
		List<Node> headDependents = headNode.getDependents();
		
		String verb;
		if(headNodeIsVerb) {
			verb = headNode.getWord();
			//TODO: translate verb to present tense.
		} else {
			verb = "-no verb-";
		}
		
		String subject = "";
		Node subjectNode = returnSubject(headDependents);
		if(subjectNode != null) {
			subject = subjectNode.getWord();
		} else {
			subject = "-no subject-";
		}
		
		String object = "";
		Node objectNode = returnObject(headDependents);
		if(objectNode != null) {
			object = objectNode.getWord();
		} else {
			object = "-no object-";
		}
		
		String tempHeadline = subject + " " + verb + " " + object;
		
		generatedHeadline = tempHeadline;
		System.out.println(generatedHeadline);
		
		// drill down in the branches we consider most important - until 75 bytes
		
	}
	
	/**
	 * This method returns the subject of the sentence.
	 * @param headDependents
	 * @return
	 */
	private Node returnSubject(List<Node> headDependents) {
		
		HashSet<String> important = new HashSet<String>();
		important.add("nsubj");
		important.add("nsubjpass");
		
		List<Node> relevant = new ArrayList<Node>();
		
		for(Node node : headDependents) {
			if(important.contains(node.getGrammaticalRelation().getShortName())) {
				relevant.add(node);
				System.out.println("outgoing subject relation: " + node.getGrammaticalRelation().getShortName());
				return node;
			}
		}
		
		return null;
	}

	private Node returnObject(List<Node> headDependents) {
		
		HashSet<String> important = new HashSet<String>();
		important.add("dobj");
		important.add("iobj");
		
		List<Node> relevant = new ArrayList<Node>();
		
		for(Node node : headDependents) {
			if(important.contains(node.getGrammaticalRelation().getShortName())) {
				relevant.add(node);
				System.out.println("outgoing object relation: " + node.getGrammaticalRelation().getShortName());
				return node;
			}
		}
		
		return null;
	}
	
	public Tree getPhraseTree() {
		return phraseTree;
	}

	public Collection<TypedDependency> getDependencies() {
		return dependencies;
	}
	
	public CoreMap getFirstSentence() {
		return sentences.get(0);
	}
	
	public String getRootNodeType() {
		return rootNodeType;
	}
	
}

