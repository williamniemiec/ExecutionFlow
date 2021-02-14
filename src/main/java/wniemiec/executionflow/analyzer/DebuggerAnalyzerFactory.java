package wniemiec.executionflow.analyzer;

import java.io.IOException;

import wniemiec.executionflow.invoked.InvokedInfo;

public class DebuggerAnalyzerFactory {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	private DebuggerAnalyzerFactory() {
	}
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------	
	public static DebuggerAnalyzer createStandardTestPathAnalyzer(InvokedInfo invokedInfo, 
														  InvokedInfo testMethodInfo) 
			throws IOException {
		return new StandardDebuggerAnalyzer(invokedInfo, testMethodInfo);
	}
}
