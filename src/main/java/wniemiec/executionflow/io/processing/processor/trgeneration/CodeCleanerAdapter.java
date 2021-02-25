package wniemiec.executionflow.io.processing.processor.trgeneration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
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
	public List<String> processLines() {
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
	 * 	<li><b>Value:</b> Modified source file line</li>
	 * </ul>
	 */
	public Map<Integer, Integer> getMapping() {
		if (lineMappings.isEmpty())
			return new HashMap<>();
		
		Map<Integer, Integer> mapping = new HashMap<>();
		int totMap = (lineMappings.size() <= 1) ? 0 : lineMappings.size()-1;
		int multiline = 0;
		int currentMultiline = 0;
		
		for (Map.Entry<Integer, List<Integer>> map : lineMappings.get(totMap).entrySet()) {
			List<Integer> originalLines = map.getValue();
			int newLine = map.getKey()+1;
			
			if (originalLines.size() > 1) {
				if (multiline == 0) {
					multiline = originalLines.size();	
					currentMultiline = 0;
				}
				
				mapping.put(originalLines.get(currentMultiline)+1, newLine);
				
				if (multiline > 0) {
					multiline--;
					currentMultiline++;
				}
			}
			else if (originalLines.size() == 1) {
				mapping.put(originalLines.get(0)+1, newLine);
			}
		}
		return mapping;
	}
}
