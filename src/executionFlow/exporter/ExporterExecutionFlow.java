package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.info.SignaturesInfo;

/**
 * Responsible for exporting the results obtained in {@link ExecutionFlow}.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.4
 * @since		1.0
 */
public interface ExporterExecutionFlow 
{
	/**
	 * Exports test path
	 * 
	 * @param		classTestPaths Test paths to be exported
	 */
	public void export(Map<String, Map<SignaturesInfo, List<Integer>>> classTestPaths);
}
