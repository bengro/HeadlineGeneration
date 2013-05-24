package main;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import filesystem.FileExplorer;
import generators.DependencyBaseline;
import generators.Runner;

public class Generator {
	
	public static FileExplorer fileExplorer;
	public static boolean running = true;
	
	public static void main(String[] args) {
		
		// load all relevant files
		System.out.println("Loading documents, models...");
		fileExplorer = new FileExplorer();
		System.out.println("Done. Console ready, type \"help\" for help.");
		
		// means we invoked this class with the GUI - deactivating console mode
		if(args.length != 0)
			return;
		
		Scanner in = new Scanner(System.in);
		while(running) {
			String input = in.nextLine();
			
			String[] docInput = input.split(" ");
			if(docInput[0].equals("doc")) {
				File article = new File(Config.ducDirectory + "docs" + docInput[1]);
				generateHeadline(article);
			}
			
			if(input.equals("example")) {
				generateHeadline(new File(Config.exampleDoc));
			}
			
			if(input.equals("examples")) {
				ArrayList<File> examples = new ArrayList<File>();
				examples.add(new File(Config.ducDirectory + "docs/d30001t/APW19981016.0240"));
				examples.add(new File(Config.ducDirectory + "docs/d30005t/APW19981129.0665"));
				examples.add(new File(Config.ducDirectory + "docs/d30005t/APW19981112.0551"));
				examples.add(new File(Config.ducDirectory + "docs/d30017t/NYT19981114.0099"));
				examples.add(new File(Config.ducDirectory + "docs/d30047t/APW19981120.0892"));
				examples.add(new File(Config.ducDirectory + "docs/d30047t/APW19981209.1470"));
				examples.add(new File(Config.ducDirectory + "docs/d31022t/APW19981030.0489"));
				examples.add(new File(Config.ducDirectory + "docs/d31033t/NYT19981018.0065"));
			
				for(File doc : examples) {
					generateHeadline(doc);
				}
				
			}
			
			if(input.equals("help")) {
				System.out.println("Following commands are supported: ");
				System.out.println("doc <doc path relative to docs directory> : Generate headline for article.");
				System.out.println("example : Generate headline for example document");
				System.out.println("process : Generate headlines for the whole corpus, generate Rouge XML.");
				System.out.println("exit : quit the amazing experience.");
			}
			
			if(input.equals("process")) {
				processCorpus();
			}
			
			if(input.equals("learn")) {
				System.out.println("Analysing all models.");
				Runner.learnModelPriors();
			}
			
			if(input.equals("exit")) {
				running = false;
			}
			
		}
		
		in.close();
		
	}

	// processes all of the corpus - generates rouge xml.
	public static void processCorpus() {
		
		// create headlines and peer files.
		System.out.println("Start generating headlines...");		

		// change to FirstSentenceBaseline.class
		Runner.generateHeadlines(DependencyBaseline.class); 
		
	}
	
	// generates and outputs the headline of a given article - for debugging.
	public static void generateHeadline(File article) {
		Runner.generateHeadline(DependencyBaseline.class, article);
	}
	
}
