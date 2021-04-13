package wniemiec.app.executionflow.exporter.testpath;

import java.util.List;
import java.util.Map;

import wniemiec.app.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.app.executionflow.invoked.TestedInvoked;

/**
 * Responsible for exporting the results obtained in {@link TestedInvokedParser}.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		1.0
 */
public interface TestPathExporter {
	
	/**
	 * Exports test path.
	 * 
	 * @param		classTestPaths Test paths to be exported
	 */
	public void export(Map<TestedInvoked, List<List<Integer>>> classTestPaths);
}
