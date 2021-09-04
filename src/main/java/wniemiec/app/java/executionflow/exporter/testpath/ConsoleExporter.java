package wniemiec.app.java.executionflow.exporter.testpath;

import java.util.List;
import java.util.Map;

import wniemiec.app.java.executionflow.invoked.TestedInvoked;
import wniemiec.io.java.Consolex;

/**
 * Displays the results on the console.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
	public void export(Map<TestedInvoked, List<List<Integer>>> testPaths) {
		if ((testPaths == null) || testPaths.isEmpty())
			return;
		
		currentInvoked = "";
		currentTestMethodSignature = null;
		
		printHeader();
		printBody(testPaths);
		printFooter();
	}

	private void printBody(Map<TestedInvoked, List<List<Integer>>> classTestPaths) {
		for (Map.Entry<TestedInvoked, List<List<Integer>>> e : classTestPaths.entrySet()) {
			printSignatures(e.getKey());
			printAllTestPaths(e.getValue());
		}
	}
	
	private void printHeader()
	{
		Consolex.writeHeader("EXPORT", "-");
	}

	private static void printFooter() {
		Consolex.writeLine("");
	}

	private void printSignatures(TestedInvoked invokedContainer) {
		String testMethodSignature = invokedContainer.getTestMethod().getInvokedSignature();
		String invokedSignature = invokedContainer.getTestedInvoked().getConcreteSignature();

		if (testMethodSignature == null) { 
			testMethodSignature = ""; 
		}
		
		if (!testMethodSignature.equals(currentTestMethodSignature)) {
			Consolex.writeLine(testMethodSignature);	
			Consolex.writeLine(invokedSignature);
			
			currentTestMethodSignature = testMethodSignature;
			currentInvoked = invokedSignature;
		} 
		else if (!invokedSignature.equals(currentInvoked)) {	
			Consolex.writeLine("");
			Consolex.writeLine(testMethodSignature);
			Consolex.writeLine(invokedSignature);
			
			currentInvoked = invokedSignature;
		}
	}

	private void printAllTestPaths(List<List<Integer>> testPaths) {
		for (List<Integer> testPath : testPaths) {
			Consolex.writeLine(testPath);
		}
	}
}
