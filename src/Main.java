import java.io.File;
import java.io.IOException;

import output.Rouge;
import filesystem.FileExplorer;
import filesystem.PeerGenerator;
import generators.FirstSentenceBaseline;

public class Main {

	//TODO: 4 run ROUGE with XML file as input - pipe console output into file via Java. (WED)
	//TODO: 5 Bug fixing (THU-FRI)
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Start indexing documents, models.");
		
		PeerGenerator peerGenerator = new PeerGenerator();
		
		// preprocess
		FileExplorer fileExplorer = new FileExplorer();
		fileExplorer.process();
		
		System.out.println("Headline generation and peer file creation.");
		
		// iterate over all topics and all documents to generate peers
		File[] topics = fileExplorer.getTopics();
		for(File topic : topics) {
			
			// get documents
			File[] documents = fileExplorer.getDocuments().get(topic.getAbsolutePath());
			for(File document : documents) {
				
				// run headline generation
				FirstSentenceBaseline baseline = new FirstSentenceBaseline(document);
				
				// return headlines - just one in this case.
				String peerText = baseline.returnPeer();
				
				// write headline to file
				try {
					// add peer files to file explorer.
					peerGenerator.createPeer(peerText, topic.getName(), document.getName());
					File peerFile = peerGenerator.getPeerFile();
					fileExplorer.addPeer(document, peerFile);
				} catch (IOException e) {
					System.out.println("Error: could not create peer for document " + document.getName());
				}	
				
			}
			
		}
		
		Rouge rouge = new Rouge(fileExplorer);
		rouge.generateRougeXML();
		System.out.println("ROUGE XML file created.");
		
	}

}
