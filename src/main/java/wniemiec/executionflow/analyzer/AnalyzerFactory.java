package wniemiec.executionflow.analyzer;

import java.io.IOException;

import wniemiec.executionflow.info.InvokedInfo;

public class AnalyzerFactory {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	private AnalyzerFactory() {
	}
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------	
	public static Analyzer createStandardTestPathAnalyzer(InvokedInfo invokedInfo, 
														  InvokedInfo testMethodInfo) 
			throws IOException {
		return new StandardTestPathAnalyzer(invokedInfo, testMethodInfo);
	}
}
