package generators;

import java.io.File;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;

public class FirstSentencePoSBaseline extends AbstractGenerator {

	String headline;
	
	public FirstSentencePoSBaseline(File doc) {
		super(doc);
	}

	@Override
	public void generateHeadline() {
		List<CoreLabel> filtered = pos(sentences.get(0));
		headline = Sentence.listToString(filtered);
	}	
	
	@Override
	public String returnHeadline() {
		return headline;
	}

}
