
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.Config;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.tagger.common.TaggerConstants;
import edu.stanford.nlp.util.CoreMap;
import generators.Runner;

public class PosTagging {

	String text;
	Annotation article;
	List<CoreMap> sentences;
	Set<String> closed;
	
	@Before
	public void produceData() {
		
		// example doc
		File document = new File(Config.exampleDoc);
		
		// extract string
		text = extractArticle(document);
		
		// create sentences
		article = new Annotation(text);
		
		// annotate
		Runner.pipeline.annotate(article);
		
		// generate notion of sentences
		sentences = article.get(SentencesAnnotation.class);	
	}
	
	@Test
	public void test() {
		
		closedWords();
		
		if(sentences.size() == 0) {
			fail("Sentences are not parsed.");
		}
		
		List<CoreLabel> labels = sentences.get(0).get(TokensAnnotation.class);
		
		if(labels.size() == 0) {
			fail("No tokens in sentence.");
		}

		for(CoreLabel word : sentences.get(0).get(TokensAnnotation.class)) {
			String wordString = word.get(edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation.class);
			if(!closed.contains(wordString)) {
				System.out.println(word.word());
			}
			
		}
		
	}
	
	protected void closedWords() {
	  closed = new HashSet<String>();
	  closed.add(".");
	  closed.add(",");
	  closed.add("``");
	  closed.add("''");
	  closed.add(":");
	  closed.add("$");
	  closed.add("EX");
	  closed.add("(");
	  closed.add(")");
	  closed.add("#");
	  closed.add("MD");
	  closed.add("CC");
	  closed.add("DT");
	  closed.add("LS");
	  closed.add("PDT");
	  closed.add("POS");
	  closed.add("PRP");
	  closed.add("PRP$");
	  closed.add("RP");
	  closed.add("TO");
	  closed.add(TaggerConstants.EOS_TAG);
	  closed.add("UH");
	  closed.add("WDT");
	  closed.add("WP");
	  closed.add("WP$");
	  closed.add("WRB");
	  closed.add("-LRB-");
	  closed.add("-RRB-");
	}
	
	protected String extractArticle(File document) {
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

}
