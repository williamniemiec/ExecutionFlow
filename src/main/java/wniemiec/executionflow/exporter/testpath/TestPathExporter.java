package wniemiec.executionflow.exporter.testpath;

import java.util.List;
import java.util.Map;

import wniemiec.executionflow.collector.InvokedCollection;
import wniemiec.executionflow.collector.parser.InvokedCollectorParser;

/**
 * Responsible for exporting the results obtained in {@link InvokedCollectorParser}.
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
	public void export(Map<InvokedCollection, List<List<Integer>>> classTestPaths);
}
