package generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.Config;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.tagger.common.TaggerConstants;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public abstract class AbstractGenerator {
	
	protected Annotation article;
	protected List<CoreMap> sentences;
	protected HashSet<String> closedWordClasses;
	protected Integer headlineByteLength = 75;
	
	public AbstractGenerator(File doc) {
		
		// instantiate close word dictionary
		closedWords();
		
		// convert File into String and create Annotation.
		article = new Annotation(extractArticle(doc));
		
		// Annotate the article
		Runner.pipeline.annotate(article);
		
		// extract relevant information
		ssplit();
		
		// execute headline generation
		generateHeadline();
		
	}

	/**
	 * Returns a set of sentences, given a text.
	 * @param text
	 * @return sentences detected by Stanford CoreNLP.
	 */
	protected void ssplit() {
		sentences = article.get(SentencesAnnotation.class);	
	}
	

	/***
	 * Loads the dependencies of a given sentence.
	 * @param sentence
	 * @return
	 */
	protected Tree dependencies(CoreMap sentence) {

		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		ArrayList<CoreLabel> words = new ArrayList<CoreLabel>();
		for(CoreLabel word : sentence.get(TokensAnnotation.class)) {
			words.add(word);
		}
		
		Tree tree = lp.apply(words);
		
		return tree;
	}

	
	/**
	 * Given a sentence this method returns tagged words of that sentence.
	 * @param sentence
	 * @return
	 */
	protected List<CoreLabel> pos(CoreMap sentence) {
		
		ArrayList<CoreLabel> posFiltered = new ArrayList<CoreLabel>();
		
		for(CoreLabel word : sentence.get(TokensAnnotation.class)) {	
			String wordString = word.get(PartOfSpeechAnnotation.class);
			if(!closedWordClasses.contains(wordString)) {
				posFiltered.add(word);
			}
		}
		
		return posFiltered;
		
	}

	/**
	 * Lemmatization of a given sentence.
	 */
	protected ArrayList<String> lemmatize(CoreMap sentence) {
		
		ArrayList<String> lemmas = new ArrayList<String>();
		
		for(CoreLabel word : sentence.get(TokensAnnotation.class)) {
			lemmas.add(word.lemma());
		}
		
		return lemmas;
	}

	
	/**
	 * Returns the relevant content, given an XML file.
	 * @param document
	 */
	public String extractArticle(File document) {
		String content = null;
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
		try {
			
			// loads file in memory and replaces invalid &AMP; with &amp;
			if(Config.overwriteInvalidXMLFiles) {
				ArrayList<String> lines = new ArrayList<String>();
				
				BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
				String line = bufferedReader.readLine();
				while(line != null) {
					line = line.replace("&AMP;", "&amp;");
					lines.add(line);
					line = bufferedReader.readLine();
				}
				
				PrintWriter out = new PrintWriter(document);
				for(String newLine : lines) {
					out.println(newLine);
				}
				out.close();
				bufferedReader.close();
			}
			
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(document);
			content = doc.getElementsByTagName("TEXT").item(0).getTextContent();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return content;
	}
	
	protected void closedWords() {
		closedWordClasses = new HashSet<String>();
		closedWordClasses.add(".");
		closedWordClasses.add(",");
		closedWordClasses.add("``");
		closedWordClasses.add("''");
		closedWordClasses.add(":");
		closedWordClasses.add("$");
		closedWordClasses.add("EX");
		closedWordClasses.add("(");
		closedWordClasses.add(")");
		closedWordClasses.add("#");
		closedWordClasses.add("MD");
		closedWordClasses.add("CC");
		closedWordClasses.add("DT");
		closedWordClasses.add("LS");
		closedWordClasses.add("PDT");
		closedWordClasses.add("POS");
		closedWordClasses.add("PRP");
		closedWordClasses.add("PRP$");
		closedWordClasses.add("RP");
		closedWordClasses.add("TO");
		closedWordClasses.add(TaggerConstants.EOS_TAG);
		closedWordClasses.add("UH");
		closedWordClasses.add("WDT");
		closedWordClasses.add("WP");
		closedWordClasses.add("WP$");
		closedWordClasses.add("WRB");
		closedWordClasses.add("-LRB-");
		closedWordClasses.add("-RRB-");
	}
	
	/**
	 * Is implemented by concrete generator.
	 * @return
	 */
	public abstract String returnHeadline();
	
	/**
	 * Contains the actual headline generation code.
	 */
	public abstract void generateHeadline();
}
