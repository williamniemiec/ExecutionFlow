package executionFlow.io.processor.parser.trgeneration;

public class Regex {
	
	// TODO [methodSignature] not all methods contain a scope, default is protected
	
	public static String classSignature = "^[ \\t]*((public|private|protected)\\s+)?(static\\s+)?(final\\s+)?class\\s.*";
	public static String methodSignature = ".*[a-zA-Z][a-zA-Z0-9]*\\s*\\(.*\\)\\s*\\{$";
	public static String reservedMethods = "(if|while|for|class|switch|try|catch|finally)";
	
	public static String reservedInstructions = "^\\b(do|else(?!\\s+if)|case|default|continue|break|switch)\\b.*";
	public static String nonExecutableLines = "^\\b(do|else(?!\\s+if)|default)\\b.*";
	
	// TODO do not capture escaped quotes
	public static String insideQuoteRestriction = "(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$).*";
}
