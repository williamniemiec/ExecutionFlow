package wniemiec.executionflow.analyzer;

import java.io.IOException;

import wniemiec.executionflow.invoked.TestedInvoked;

/**
 * Responsible for creating {@link DebuggerAnalyzer} instances.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class DebuggerAnalyzerFactory {
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------	
	private DebuggerAnalyzerFactory() {
	}
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------	
	public static DebuggerAnalyzer createStandardTestPathAnalyzer(TestedInvoked testedInvoked) 
			throws IOException {
		if (testedInvoked == null)
			throw new IllegalArgumentException("Tested invoked cannot be null");
		
		return new StandardDebuggerAnalyzer(testedInvoked);
	}
}
