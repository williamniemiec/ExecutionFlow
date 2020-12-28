package executionflow.exporter.testpath;

import java.util.List;
import java.util.Map;

import executionflow.ExecutionFlow;
import executionflow.info.InvokedContainer;

/**
 * Responsible for exporting the results obtained in {@link ExecutionFlow}.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
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