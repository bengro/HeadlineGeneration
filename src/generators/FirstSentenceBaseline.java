package generators;

import java.io.File;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.util.CoreMap;

public class FirstSentenceBaseline extends AbstractGenerator {
	
	String peer;
	
	/**
	 * Extracts text of a document, generates sentences and then applies headline generation.
	 * @param document
	 */
	public FirstSentenceBaseline(File document) {
		super(document);

		generateHeadlines();	
	}
	
	/**
	 * Implements the actual headline.
	 */
	public void generateHeadlines() {
		CoreMap sentence = sentences.get(0);
		List<CoreLabel> labels = sentence.get(TokensAnnotation.class);
		
		peer = Sentence.listToString(labels);
	}
	
	/**
	 * Returns the generated peers as string.
	 */
	@Override
	public String returnHeadline() {
		return peer;
	}
	
}
