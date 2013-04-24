package generators;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;

public class FirstSentencePoSBaseline extends AbstractGenerator {

	public FirstSentencePoSBaseline(File doc) {
		super(doc);
	}

	@Override
	public String returnHeadline() {
		
		return null;
	}

	public void generateHeadlines(ArrayList<List<HasWord>> sentences) {
		
		// iterate over each sentence
		Iterator<List<HasWord>> sentenceIterator = sentences.iterator();
		while(sentenceIterator.hasNext()) {
			// get the sequence of words in that sentence.
			List<HasWord> listOfWords = sentenceIterator.next();
			
			Iterator<HasWord> word = listOfWords.listIterator();
			while(word.hasNext()) {
				HasWord token = word.next();
			}
			
		}
	}

	

}
