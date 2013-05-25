package generators;

import java.io.File;
import java.util.Collection;

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

		RuleEvaluator eval = new RuleEvaluator();
        Headline headline = eval.evaluateRules(dependencyTree.getHead());
        String headlineString = "";


        for (Node node: headline){
            headlineString += node.getWord() + " ";
        }
        headlineString.trim();

        generatedHeadline = headlineString;
        System.out.println(generatedHeadline);

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

