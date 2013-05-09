package generators;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Scanner;

import main.Config;
import main.Generator;
import output.Rouge;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Runner {

	public static StanfordCoreNLP pipeline = new StanfordCoreNLP();
	public static String className;
		
	/**
	 * This method iterates over all topics and all documents in order to generate a headline for 
	 * each topic-document pair. The headline is written to disk and the path is added to the file explorer.
	 * @param <T>
	 */
	public static <T> void generateHeadlines(Class<T> headlineClass) {
	
		className = headlineClass.getSimpleName();
		
		// create output directory 
		File dir = new File(Config.outputDirectory + headlineClass.getSimpleName());
		dir.mkdir();
		
		// iterate over all topics and all documents to generate peers
		File[] topics = Generator.fileExplorer.getTopics();
		for(File topic : topics) {
			
			// get documents
			File[] documents = Generator.fileExplorer.getDocuments().get(topic.getAbsolutePath());
			for(File document : documents) {
				
				try {
					// run headline generation
					Constructor<T> con = headlineClass.getConstructor(File.class);
					AbstractGenerator baseline = (AbstractGenerator) con.newInstance(document);
					
					// return headlines - just one in this case.
					String peerText = baseline.returnHeadline();
					
					Generator.fileExplorer.createPeer(peerText, topic.getName(), document, headlineClass.getSimpleName());
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		// generate rouge xml and label it accordingly.
		System.out.println("Generating ROUGE XML file...");
		Rouge rouge = new Rouge(Generator.fileExplorer, headlineClass.getSimpleName());
		rouge.generateRougeXML();
		
	}

	public static <T> void generateHeadline(Class<T> headlineClass, File article) {
	
		System.out.println("Document: " + article.getAbsolutePath());
		System.out.println("Generator class: " + headlineClass.getName());
		
		try {
			// run headline generation
			Constructor<T> con = headlineClass.getConstructor(File.class);
			AbstractGenerator baseline = (AbstractGenerator) con.newInstance(article);
		
			// return headlines - just one in this case.
			String peerText = baseline.returnHeadline();
			System.out.println("Headline: " + peerText);
			
			// show models
			File[] models = Generator.fileExplorer.getModels().get(article.getAbsolutePath());
			int i = 1;
			for(File model : models) {
				
				Scanner modelFile = new Scanner(model);
				String modelHeadline = "";
				while(modelFile.hasNext()) {
					modelHeadline += modelFile.next() + " ";
				}
				modelFile.close();
				
				System.out.println("Model " + i + ": " + modelHeadline);
				i++;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	
}
