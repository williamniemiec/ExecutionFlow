package wniemiec.executionflow.collector.parser;

import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.analyzer.DebuggerAnalyzer;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.logger.Logger;

public class TestedInvokedParser {
	
	@Override
	public String toString() {
		return "TestedInvokedParser ["
					+ "testPaths=" + computedTestPaths 
					+ ", debuggerAnalyzer=" + debuggerAnalyzer
					+ ", processedSourceFiles=" + processedSourceFiles 
					+ ", testedInvoked=" + testedInvoked 
				+ "]";
	}

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores computed test paths from a class.<br />
	 * <ul>
	 * 	<li><b>Key:</b> Test method and tested invoked</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 */
	protected Map<TestedInvoked, List<List<Integer>>> computedTestPaths;
	
	private DebuggerAnalyzer debuggerAnalyzer;
	private Map<String, Path> processedSourceFiles;
	private TestedInvoked testedInvoked;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	public TestedInvokedParser() {
		this.computedTestPaths = new HashMap<>();
		this.processedSourceFiles = new HashMap<>();
	}
	

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void parse(TestedInvoked testedInvoked, DebuggerAnalyzer debuggerAnalyzer) 
			throws InterruptedByTimeoutException, IOException {
		initParser(debuggerAnalyzer, testedInvoked);
		runDebugger();
		storeResults();
	}

	private void initParser(DebuggerAnalyzer debuggerAnalyzer, TestedInvoked testedInvoked) {
		this.debuggerAnalyzer = debuggerAnalyzer;
		this.testedInvoked = testedInvoked;
	}

	private void storeResults() {
		if (!debuggerAnalyzer.hasTestPaths())
			return;
		
		if (isConstructor())
			fixAnonymousClassSignature(testedInvoked.getTestedInvoked());
		
		storeTestPath(
				new TestedInvoked(
						testedInvoked.getTestedInvoked(), 
						testedInvoked.getTestMethod()
		));
		
		processedSourceFiles.put(
				testedInvoked.getTestedInvoked().getConcreteSignature(),
				testedInvoked.getTestedInvoked().getSrcPath()
		);
	}

	private void fixAnonymousClassSignature(Invoked invokedInfo) {
		if (debuggerAnalyzer.getAnalyzedInvokedSignature().isBlank())
			return;
		
		if (!invokedInfo.getInvokedSignature().equals(debuggerAnalyzer.getAnalyzedInvokedSignature())) {
			invokedInfo.setSignature(debuggerAnalyzer.getAnalyzedInvokedSignature());
		}
	}

	private void runDebugger() 
			throws IOException, InterruptedByTimeoutException {
		Logger.info(
				"Computing test path of invoked " 
				+ testedInvoked.getTestedInvoked().getConcreteSignature() 
				+ "..."
		);
		
		debuggerAnalyzer.analyze();

		checkDebuggerTimeout();
	}

	private void checkDebuggerTimeout() throws InterruptedByTimeoutException {
		if (!DebuggerAnalyzer.checkTimeout())
			return;
		
		try {
			Thread.sleep(2000);
		} 
		catch (InterruptedException e) {
		}
		
		throw new InterruptedByTimeoutException();
	}

	protected boolean isConstructor() {
		return false;
	}
	
	protected void storeTestPath(TestedInvoked invokedContainer) {
		if (!debuggerAnalyzer.hasTestPaths())
			return;
			
		for (List<Integer> testPath : debuggerAnalyzer.getTestPaths()) {	
			if (testPath.isEmpty())
				continue;
			
			if (computedTestPaths.containsKey(invokedContainer))
				storeExistingTestPath(invokedContainer, testPath);
			else	
				storeNewTestPath(invokedContainer, testPath);
		}
	}

	private void storeNewTestPath(TestedInvoked invokedContainer, 
								  List<Integer> testPath) {
		List<List<Integer>> testPaths = new ArrayList<>();
		testPaths.add(testPath);
	
		computedTestPaths.put(invokedContainer, testPaths);
	}

	private void storeExistingTestPath(TestedInvoked invokedContainer, 
									   List<Integer> testPath) {
		List<List<Integer>> testPaths = computedTestPaths.get(invokedContainer);
		testPaths.add(testPath);
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets a specific computed test path.
	 * 
	 * @param		testMethodSignature Test method signature
	 * @param		invokedSignature Invoked signature
	 * 
	 * @return		List of test paths for the specified invoked or empty list
	 * if specified invoked has not a test path
	 * 
	 * @implNote	It must only be called after method {@link #run()} has 
	 * been executed
	 */
	public List<List<Integer>> getTestPathsOf(TestedInvoked container) {
		if (computedTestPaths.isEmpty())
			return List.of(new ArrayList<Integer>(0));
		
		return computedTestPaths.get(container);
	}
	
	/**
	 * Gets computed test path.It will return the following map:
	 * <ul>
	 * 	<li><b>Key:</b> Test method signature and invoked signature</li>
	 * 	<li><b>Value:</b> List of test paths</li>
	 * </ul>
	 * 
	 * @return		Computed test path
	 * 
	 * @implNote	It must only be called after method {@link #run()} has 
	 * been executed
	 */
	public Map<TestedInvoked, List<List<Integer>>> getTestPaths() {
		return computedTestPaths;
	}
	
	public Map<String, Path> getProcessedSourceFiles() {
		return processedSourceFiles;
	}
	
	public Map<Invoked, Set<String>> getMethodsCalledByTestedInvoked() {
		return debuggerAnalyzer.getMethodsCalledByTestedInvoked();
	}
	
	public Set<TestedInvoked> getMethodsAndConstructorsUsedInTestMethod() {
		return computedTestPaths.keySet();
	}
}
