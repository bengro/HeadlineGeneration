package main;

public class Config {
	
	public static String ducDirectory = "/home/daspan/workspace/nlpheadlinegeneration/NLP/headlines/DUC2004/";
	public static String exampleDoc = ducDirectory + "docs/d30001t/APW19981016.0240";
	public static String outputDirectory = ducDirectory + "eval/peers/";
	
	public static String rougeScriptPath = ducDirectory + "eval/";
	public static String rougeOutputPath = rougeScriptPath;
	
	public static boolean isDebug = true;
	public static boolean overwriteInvalidXMLFiles = false;
	
}
