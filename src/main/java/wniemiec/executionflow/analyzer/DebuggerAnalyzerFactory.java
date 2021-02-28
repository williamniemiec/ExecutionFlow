package wniemiec.executionflow.analyzer;

import java.io.IOException;

import wniemiec.executionflow.invoked.Invoked;

public class DebuggerAnalyzerFactory {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	private DebuggerAnalyzerFactory() {
	}
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------	
	public static DebuggerAnalyzer createStandardTestPathAnalyzer(Invoked testedInvoked, 
														  		  Invoked testMethod) 
			throws IOException {
		if (testedInvoked == null)
			throw new IllegalArgumentException("Tested invoked cannot be null");
		
		if (testMethod == null)
			throw new IllegalArgumentException("Test method cannot be null");
		
		return new StandardDebuggerAnalyzer(testedInvoked, testMethod);
	}
}
