package wniemiec.app.executionflow.io.processing.processor.trgeneration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 		5.2.0
 */
public class CodeCleanerAdapter extends CodeCleaner {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> sourceCode;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public CodeCleanerAdapter(List<String> sourceCode) {
		super(false);
		this.sourceCode = sourceCode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public List<String> processLines() throws Exception {
		cleanupCode(sourceCode);
		deleteContentBetweenPercentages(processedCode);

		return processedCode;
	}
	
	private void deleteContentBetweenPercentages(List<String> processedCode) {
		for (int i=0; i<processedCode.size(); i++) {
			String line = processedCode.get(i);
			line = line.replaceAll("%[^\\s\\t]+%", "");
			
			processedCode.set(i, line);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the mapping of the original file with the modified file.
	 * 
	 * @return		Mapping with the following format:
	 * <ul>
	 * 	<li><b>Key:</b> Original source file line</li>
	 * 	<li><b>Value:</b> Modified source file lines</li>
	 * </ul>
	 */
	public Map<Integer, List<Integer>> getMapping() {
		if (lineMappings.isEmpty())
			return new HashMap<>();
		
		return getCleanToOriginalCodeMapping();
	}
}
