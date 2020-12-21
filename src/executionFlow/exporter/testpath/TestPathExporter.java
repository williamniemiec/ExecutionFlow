package executionFlow.exporter.testpath;

import java.util.List;
import java.util.Map;

import executionFlow.ExecutionFlow;
import executionFlow.info.InvokedContainer;

/**
 * Responsible for exporting the results obtained in {@link ExecutionFlow}.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.0
 */
public interface TestPathExporter {
	
	/**
	 * Exports test path.
	 * 
	 * @param		classTestPaths Test paths to be exported
	 */
	public void export(Map<InvokedContainer, List<List<Integer>>> classTestPaths);
}
