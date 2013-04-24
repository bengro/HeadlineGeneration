package main;

public class Config {
	
	public static String ducDirectory = "/Users/bengro/Documents/workspace/nlp/headlines/DUC2004/";	
	public static String exampleDoc = ducDirectory + "docs/d30001t/APW19981016.0240";
	public static String outputDirectory = ducDirectory + "eval/peers/output/";
	
	public static String rougeScriptPath = ducDirectory + "eval/";
	public static String rougeOutputPath = rougeScriptPath;
	
	public static boolean isDebug = true;
	public static boolean overwriteInvalidXMLFiles = false;
	
}
