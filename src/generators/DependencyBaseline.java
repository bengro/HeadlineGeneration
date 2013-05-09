package generators;

import java.io.File;

import edu.stanford.nlp.trees.Tree;

public class DependencyBaseline extends AbstractGenerator {

	String generatedHeadline = null;
	
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
		Tree dependencyTree = dependencies(sentences.get(0));
		dependencyTree.pennPrint();
		
		// apply magic algorithm for removing unimportant relationships
		analyseTree(dependencyTree);

	}

	//TODO: implement tree inspector and trimmer.
	protected void analyseTree(Tree dependencyTree) {
		
		// loop while headline length > 75 byte
			
			// remove least important word
		
		
	}
	
}
