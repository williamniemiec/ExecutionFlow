package executionFlow.exporter.testpath;

import java.util.List;
import java.util.Map;

import executionFlow.util.Pair;
import executionFlow.util.console.style.ConsoleHeader;


/**
 * Displays the results on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.0
 */
public class ConsoleExporter implements TestPathExporter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String currentTestMethodSignature;
	private String currentInvoked;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) 
	{
		if (classTestPaths == null || classTestPaths.isEmpty())
			return;
		
		currentInvoked = "";
		currentTestMethodSignature = null;
		
		printHeader();
		printBody(classTestPaths);
		printFooter();
	}

	private void printBody(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) {
		for (Map.Entry<Pair<String, String>, List<List<Integer>>> e : classTestPaths.entrySet()) {
			printSignatures(e.getKey());
			printAllTestPaths(e.getValue());
		}
	}
	
	private void printHeader()
	{
		ConsoleHeader.printHeader("EXPORT", '-');
	}

	private static void printFooter() {
		System.out.println();
	}

	private void printSignatures(Pair<String, String> signatures) {
		String testMethodSignature = signatures.first;
		String invokedSignature = signatures.second;

		if (testMethodSignature == null) { 
			testMethodSignature = ""; 
		}
		
		if (!testMethodSignature.equals(currentTestMethodSignature)) {
			System.out.println(testMethodSignature);	
			System.out.println(invokedSignature);
			
			currentTestMethodSignature = testMethodSignature;
			currentInvoked = invokedSignature;
		} 
		else if (!invokedSignature.equals(currentInvoked)) {	
			System.out.println();
			System.out.println(testMethodSignature);
			System.out.println(invokedSignature);
			
			currentInvoked = invokedSignature;
		}
	}

	private void printAllTestPaths(List<List<Integer>> testPaths) {
		for (List<Integer> testPath : testPaths) {
			System.out.println(testPath);
		}
	}
}
