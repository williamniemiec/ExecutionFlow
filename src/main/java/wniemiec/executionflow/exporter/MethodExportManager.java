package wniemiec.executionflow.exporter;

import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.io.processing.manager.TestedInvokedProcessingManager;

public class MethodExportManager extends ExportManager {

	private InvokedCollector collector;
	
	protected MethodExportManager(boolean isDevelopment) {
		super(isDevelopment, false);
		collector = MethodCollector.getInstance();
	}
	
	@Override
	public void exportAll() {
		TestedInvokedProcessingManager collectionProcessor = new TestedInvokedProcessingManager();
		TestedInvokedParser parser = collectionProcessor.parse(
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
