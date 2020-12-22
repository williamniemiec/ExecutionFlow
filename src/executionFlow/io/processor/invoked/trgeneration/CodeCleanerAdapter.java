package executionFlow.io.processor.invoked.trgeneration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since 		5.2.0
 */
public class CodeCleanerAdapter extends CodeCleaner
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> sourceCode;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public CodeCleanerAdapter(List<String> sourceCode)
	{
		super(false);
		this.sourceCode = sourceCode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public List<String> processLines() 
	{
		cleanupCode(sourceCode);	
		deleteContentBetweenPercentages(processedCode);
		
		return processedCode;
	}
	
	private void deleteContentBetweenPercentages(List<String> processedCode)	
	{
		String line;
		
		
		for (int i=0; i<processedCode.size(); i++) {
			line = processedCode.get(i);
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
	public Map<Integer, Integer> getMapping()
	{
		if (lineMappings.isEmpty())
			return new HashMap<>();
		
		Map<Integer, Integer> mapping = new HashMap<>();
		int totMap = (lineMappings.size() <= 1) ? 0 : lineMappings.size()-1;

		for (Map.Entry<Integer, List<Integer>> map : lineMappings.get(totMap).entrySet()) {
			int originalLine = map.getValue().get(0) + 1;
			int newLine = map.getKey()+1;
			
			
			mapping.put(originalLine, newLine);
		}

		return mapping;
	}
}
