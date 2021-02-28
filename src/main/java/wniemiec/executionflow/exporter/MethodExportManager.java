package wniemiec.executionflow.exporter;

import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.io.processing.manager.TestedInvokedProcessingManager;

/**
 * Responsible for exporting the following from collected methods:
 * <ul>
 * 	<li>Test paths</li>
 * 	<li>Processed source files</li>
 * 	<li>Methods called by tested invoked</li>
 * 	<li>Test methods that use the tested invoked</li>
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class MethodExportManager extends ExportManager {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private InvokedCollector collector;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	protected MethodExportManager(boolean isDevelopment) {
		super(isDevelopment, false);
		collector = MethodCollector.getInstance();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void exportAll() {
		TestedInvokedProcessingManager collectionProcessor = 
				new TestedInvokedProcessingManager();
		TestedInvokedParser parser = collectionProcessor.processAndParse(
				collector.getAllCollectedInvoked()
		);
		
		exportResultsFromParser(parser);
	}
	
	@Override
	public void exportAllInvokedUsedInTestMethods() {
		exportAllMethodsAndConstructorsUsedInTestMethods(
				collector.getAllCollectedInvoked()
		);
	}
}
