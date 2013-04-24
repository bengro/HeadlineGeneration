package generators;

import java.io.File;
import java.io.IOException;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import main.Start;

public class Runner {

	public static StanfordCoreNLP pipeline = new StanfordCoreNLP();
	
	public static void generateHeadlines() {
	
		// iterate over all topics and all documents to generate peers
		File[] topics = Start.fileExplorer.getTopics();
		for(File topic : topics) {
			
			// get documents
			File[] documents = Start.fileExplorer.getDocuments().get(topic.getAbsolutePath());
			for(File document : documents) {
				
				// run headline generation
				FirstSentenceBaseline baseline = new FirstSentenceBaseline(document);
				
				// return headlines - just one in this case.
				String peerText = baseline.returnHeadline();
				
				// write headline to file
				try {
					Start.fileExplorer.createPeer(peerText, topic.getName(), document);
				} catch (IOException e) {
					System.out.println("Error: could not create peer for document " + document.getName());
				}	
				
			}
			
		}
		
	}

	
}
