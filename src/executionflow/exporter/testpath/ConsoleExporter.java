package executionflow.exporter.testpath;

import java.util.List;
import java.util.Map;

import executionflow.info.InvokedContainer;
import util.io.console.ConsoleHeader;

/**
 * Displays the results on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		1.0
 */
public class ConsoleExporter implements TestPathExporter {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String currentTestMethodSignature;
	private String currentInvoked;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<InvokedContainer, List<List<Integer>>> testPaths) {
		if ((testPaths == null) || testPaths.isEmpty())
			return;
		
		currentInvoked = "";
		currentTestMethodSignature = null;
		
		printHeader();
		printBody(testPaths);
		printFooter();
	}

	private void printBody(Map<InvokedContainer, List<List<Integer>>> classTestPaths) {
		for (Map.Entry<InvokedContainer, List<List<Integer>>> e : classTestPaths.entrySet()) {
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

	private void printSignatures(InvokedContainer invokedContainer) {
		String testMethodSignature = invokedContainer.getTestMethodInfo().getInvokedSignature();
		String invokedSignature = invokedContainer.getInvokedInfo().getConcreteInvokedSignature();

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
