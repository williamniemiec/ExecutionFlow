package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.util.Pair;


/**
 * Responsible for exporting the results obtained in {@link ExecutionFlow}.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		1.0
 */
public interface ExporterExecutionFlow 
{
	/**
	 * Exports test path.
	 * 
	 * @param		classTestPaths Test paths to be exported
	 */
	public void export(Map<Pair<String, String>, List<List<Integer>>> classTestPaths);
}
