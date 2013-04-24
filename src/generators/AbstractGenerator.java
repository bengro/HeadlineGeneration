package generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.Config;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public abstract class AbstractGenerator {
	
	Annotation article;
	List<CoreMap> sentences;
	
	public AbstractGenerator(File doc) {
		
		// convert File into String and create Annotation.
		article = new Annotation(extractArticle(doc));
		
		// Annotate the article
		Runner.pipeline.annotate(article);
		
		// extract relevant information
		tokenizeArticle();
	}
	
	/**
	 * This method tokenizes a given input text.
	 * @param article2
	 */
	private void tokenizeArticle() {
		ssplit();
		lemmatization();
	}

	/**
	 * Returns a set of sentences, given a text.
	 * @param text
	 * @return sentences detected by Stanford CoreNLP.
	 */
	private void ssplit() {
		sentences = article.get(SentencesAnnotation.class);		
	}
	
	
	/**
	 * Given a sentence this method returns tagged words of that sentence.
	 * @param sentence
	 * @return
	 */
	private ArrayList<TaggedWord> pos(List<HasWord> sentence) {
		MaxentTagger tagger = new MaxentTagger("taggers/left3words-distsim-wsj-0-18.tagger");
		return tagger.apply(sentence);
	}

	/**
	 * Lemmatization of a given sentence.
	 */
	private void lemmatization() {
		
	}

	
	/**
	 * Returns the relevant content, given an XML file.
	 * @param document
	 */
	private String extractArticle(File document) {
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
	
	/**
	 * Is implemented by concrete generator.
	 * @return
	 */
	public abstract String returnHeadline();
	
}
