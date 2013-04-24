package generators;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;

public interface IGenerator {

	public String returnPeer();
	public void generateHeadlines(ArrayList<List<HasWord>> sentences);
	
}
