package output;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import main.Config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import filesystem.FileExplorer;

public class Rouge {

	FileExplorer files;
	
	public Rouge(FileExplorer fileExplorer) {
		this.files = fileExplorer;
	}
	
	public void generateRougeXML() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("ROUGE_EVAL");
			rootElement.setAttribute("version", "1.0");
			doc.appendChild(rootElement);
			
			int i = 1;
			for(File topic : files.getTopics()) {
				
				for(File document : files.getDocuments().get(topic.getAbsolutePath())) {
					
					// eval tag
					Element evalElement = doc.createElement("EVAL");
					evalElement.setAttribute("ID", "" + i++);
					
					// peer root tag
					Element peerRoot = doc.createElement("PEER-ROOT");
					peerRoot.setTextContent("./peers"); // relative to rouge script
					evalElement.appendChild(peerRoot);
					
					// model root tag
					Element modelRoot = doc.createElement("MODEL-ROOT");
					modelRoot.setTextContent("./models"); // relative to rouge script
					evalElement.appendChild(modelRoot);
					
					// input-format tag
					Element inputFormat = doc.createElement("INPUT-FORMAT");
					inputFormat.setAttribute("TYPE", "SPL");
					evalElement.appendChild(inputFormat);
					
					// peers tag
					Element peers = doc.createElement("PEERS");
					evalElement.appendChild(peers);
					
					// peer entry
					// extract id from filename: D30001.P.10.T.100.APW19981016.0240
					File peerFile = files.getPeer().get(document.getAbsolutePath());
					String[] peerSplits = peerFile.getName().split("\\.");
					
					Element peerElement = doc.createElement("P");
					peerElement.setAttribute("ID", peerSplits[4]);
					peerElement.setTextContent("output/"+ peerFile.getName()); // relative to peer directory
					peers.appendChild(peerElement);
					
					
					// models tag
					Element models = doc.createElement("MODELS");
					for(File model : files.getModels().get(document.getAbsolutePath())) {
						// get id of model: D30001.P.10.T.A.APW19981016.0240
						String[] splits = model.getName().split("\\.");
						
						Element modelElement = doc.createElement("M");
						modelElement.setAttribute("ID", splits[4]);
						modelElement.setTextContent("1/" + model.getName()); // relative to model directory
						models.appendChild(modelElement);
					}
					evalElement.appendChild(models);
					
					rootElement.appendChild(evalElement);
					
				}
				
				// write xml file to disk
				try {
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					DOMSource source = new DOMSource(doc);
					File xmlFile = new File(Config.rougeScriptPath + "t1.EVALCLASS.rouge.in");
					StreamResult result = new StreamResult(xmlFile);
					transformer.transform(source, result);
					//TODO: create job array with xmlFile.
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
				
			}
					
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * TODO: for some reason when executing the script via java, it can't open the DB file.
	 */
	public void run() {
		System.out.println("Running ROUGE...");
		
		try {
            String cmd2 = "perl";
            ProcessBuilder processBuilder = new ProcessBuilder(cmd2, "ROUGE-1.5.5.pl", "-e ./data", "-a", "-c 95", "-b 75", "-m", "-n 1", "-w 1.2", "t1.EVALCLASS.rouge.in");            
            processBuilder.directory(new File(Config.rougeScriptPath));
            processBuilder.redirectOutput(new File(Config.rougeOutputPath + "results.txt"));
            processBuilder.redirectError(new File(Config.rougeOutputPath + "errors.txt"));
            
            System.out.println(processBuilder.directory().getAbsolutePath());
            for(String cmd : processBuilder.command()) {
            	System.out.println(cmd);
            }
            System.out.println(processBuilder.redirectOutput());
            
            Process p = processBuilder.start();
            p.waitFor();
            p.getInputStream();
            p.getErrorStream();
            
            
            
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (Exception e) {
		    e.printStackTrace();
	    }
	}

}
