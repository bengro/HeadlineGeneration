package generators;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Filter;

public class DependencyBaseline extends AbstractGenerator {
	
	Tree dependencyTree;
	Collection<TypedDependency> dependencies;
	String generatedHeadline = "";
	
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
		dependencyTree = dependencies(sentences.get(0));
		
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(dependencyTree);
		dependencies = gs.typedDependenciesCCprocessed();

		
		TypedDependencyTree depTree = new TypedDependencyTree();
		//depTree.addNode(node)
		for(TypedDependency dep : dependencies) {
			System.out.println("Added " + dep.toString() + " to the tree.");
			depTree.addNode(dep);
		}
		
		// apply magic algorithm for removing unimportant relationships
		analyseTree(depTree);
	
	}

	//TODO: implement tree inspector and trimmer.
	protected void analyseTree(TypedDependencyTree depTree) {
		
		System.out.println("root is: " + depTree.getHead().getWord());
		System.out.println("root has " + depTree.getHead().getDependents().size() + " children");
		
		for(Node node : depTree.getHead().getDependents()) {
			System.out.println("Outgoing relation: " + node.getGrammaticalRelation().getShortName() + " -> " + node.getWord());
		}
		
		Node[] children = depTree.getHead().getDependents().toArray(new Node[depTree.getHead().getDependents().size()]);
		
		String tempHeadline = children[0].getWord() + " " + depTree.getHead().getWord() + " " + children[1].getWord();
		System.out.println("Temp headline: " + tempHeadline);
		
		this.generatedHeadline = tempHeadline;
	}
	
	public Tree getDependencyTree() {
		return dependencyTree;
	}

	public Collection<TypedDependency> getDependencies() {
		return dependencies;
	}
	
	public CoreMap getFirstSentence() {
		return sentences.get(0);
	}
	
}

