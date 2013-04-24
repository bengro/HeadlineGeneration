package main;
import output.Rouge;
import filesystem.FileExplorer;
import generators.Runner;

public class Start {
	
	public static FileExplorer fileExplorer;
	
	public static void main(String[] args) {
		
		// load documents, models.
		System.out.println("Start indexing documents, models.");
		fileExplorer = new FileExplorer();
		fileExplorer.process();
		
		// create headlines and peer files.
		Runner.generateHeadlines();
		
		// generate rouge xml
		System.out.println("ROUGE XML file created.");
		Rouge rouge = new Rouge(fileExplorer);
		rouge.generateRougeXML();
		
	}

}
