package generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import main.Config;
import main.Generator;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

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
		
		System.out.println("Processing done.");
		
		// generate rouge xml and label it accordingly.
		//System.out.println("Generating ROUGE XML file...");
		//Rouge rouge = new Rouge(Generator.fileExplorer, headlineClass.getSimpleName());
		//rouge.generateRougeXML();
		
	}

	/**
	 * This method executes the headline generator for a given article.
	 * @param headlineClass
	 * @param article
	 */
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
	
	public static void learnModelPriors() {
		HashMap<String, Integer> counts = new HashMap<String, Integer>();

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		
		File[] topics = Generator.fileExplorer.getTopics();
		
		for(File topic : topics) {
			HashMap<String, File[]> docs = Generator.fileExplorer.getDocuments();
			
			for(File doc : docs.get(topic.getAbsolutePath())) {
				HashMap<String, File[]> models = Generator.fileExplorer.getModels();
				
				for(File model : models.get(doc.getAbsolutePath())) {
				
					// analyse dependencies of each model
					try {
						Scanner modelFile = new Scanner(model);
					
						String modelHeadline = "";
						while(modelFile.hasNext()) {
							modelHeadline += modelFile.next() + " ";
						}
						modelFile.close();
						
						Annotation headline = new Annotation(modelHeadline);
						pipeline.annotate(headline);
						
						List<CoreMap> sentences = headline.get(SentencesAnnotation.class);	
						ArrayList<CoreLabel> words = new ArrayList<CoreLabel>();
						for(CoreLabel word : sentences.get(0).get(TokensAnnotation.class)) {
							words.add(word);
						}
						
						Tree tree = lp.apply(words);
						
						GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
						Collection<TypedDependency> dependencies = gs.typedDependenciesCCprocessed();
						
						for(TypedDependency dep : dependencies) {
							
							if(counts.get(dep.reln().getShortName()) == null) {
								counts.put(dep.reln().getShortName(), 1);
							} else {
								counts.put(dep.reln().getShortName(), counts.get(dep.reln().getShortName()) + 1);
							}
						}
				
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		float total = 0;
		System.out.println("Evaluation: ");
		for(Entry<String, Integer> count : counts.entrySet()) {
			total += count.getValue();
		}
		
		// write tag-prior tag line by line to file
		try {
			File priors = new File(Config.ducDirectory + "eval/" + "priors.txt");
			
			FileWriter fileWriter = new FileWriter(priors);
			for(Entry<String, Integer> count : counts.entrySet()) {
				double prior = (double) count.getValue() / total;
					fileWriter.write(count.getKey() + "," + String.format("%.5g%n", prior));
			}
			fileWriter.close();
			
			System.out.println("Done. Priors saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
