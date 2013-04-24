package generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import config.Config;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.DocumentPreprocessor.DocType;

public class FirstSentenceBaseline implements IGenerator {
	
	String peer;
	
	/**
	 * Extracts text of a document, generates sentences and then applies headline generation.
	 * @param document
	 */
	public FirstSentenceBaseline(File document) {
		
		// tokenize
		String text = returnText(document);
		
		// return sentences
		ArrayList<List<HasWord>> sentences = returnSentences(text);
        
		// generate peer
		generateHeadlines(sentences);
		
	}
	
	/**
	 * Returns the text of a given document.
	 */
	private String returnText(File document) {
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
	 * Returns a set of sentences, given a text.
	 * @param text
	 * @return sentences detected by Stanford CoreNLP.
	 */
	private ArrayList<List<HasWord>> returnSentences(String text) {
		DocumentPreprocessor preprocessor = new DocumentPreprocessor(new StringReader(text), DocType.Plain);
		ArrayList<List<HasWord>> sentences = new ArrayList<List<HasWord>>();
		
		for (List<HasWord> sentence : preprocessor) {
			sentences.add(sentence);
	    }
		
		return sentences;
	}

	
	/**
	 * Implements the actual headline.
	 */
	@Override
	public void generateHeadlines(ArrayList<List<HasWord>> sentences) {
		peer = Sentence.listToString(sentences.get(0));
	}
	
	/**
	 * Returns the generated peers as string.
	 */
	@Override
	public String returnPeer() {
		return peer;
	}
	
}
