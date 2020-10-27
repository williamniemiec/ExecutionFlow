package executionFlow.io.processor.parser.trgeneration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.util.DataUtil;

/**
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since 		5.2.0
 */
public class CodeCleanerAdapter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> sourceCode;
	private CodeCleaner cc = new CodeCleaner(false);

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public CodeCleanerAdapter(List<String> sourceCode)
	{
		this.sourceCode = sourceCode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public List<String> parse() 
	{
		List<String> processedCode;
		
		
		cc.cleanupCode(sourceCode);
		processedCode = cc.getProcessedCode();
		
		deleteContentBetweenPercentages(processedCode);
		
		return processedCode;
	}
	
	
	private void deleteContentBetweenPercentages(List<String> processedCode)	
	{
		String line;
		
		
		for (int i=0; i<processedCode.size(); i++) {
			line = processedCode.get(i);
			line = line.replaceAll("%.+%", "");
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
		Map<Integer, Integer> mapping = new HashMap<>();
		Set<Integer> updated = new HashSet<>();
		List<Map<Integer, List<Integer>>> lineMappings = cc.getLineMappings();
		

		// Gets first line change
		for (Map.Entry<Integer, List<Integer>> lm : lineMappings.get(0).entrySet()) {
			mapping.put(lm.getKey(), lm.getValue().get(0));
		}
		
		// Updates line changes from the second
		for (int i=1; i<lineMappings.size(); i++) {
			// For each mapping
			for (Map.Entry<Integer, List<Integer>> lm : lineMappings.get(i).entrySet()) {
				// If there is a value in the mapping with the current key
				if (mapping.containsValue(lm.getKey())) {
					// Updates this value with the value contained in the current key
					for (Integer key : DataUtil.<Integer, Integer>findKeyFromValue(mapping, lm.getKey())) {
						if (!updated.contains(key)) {
							mapping.put(key, lm.getValue().get(0));
							updated.add(key);
						}
					}
				}
			}
			
			// Resets update set
			updated.clear();
		}
		
		return mapping;
	}
}
