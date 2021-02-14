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
	public static DebuggerAnalyzer createStandardTestPathAnalyzer(Invoked invokedInfo, 
														  Invoked testMethodInfo) 
			throws IOException {
		return new StandardDebuggerAnalyzer(invokedInfo, testMethodInfo);
	}
}
