package wniemiec.executionflow.exporter;

import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.parser.TestedInvokedParser;
import wniemiec.executionflow.io.processing.manager.TestedInvokedProcessingManager;

public class ConstructorExportManager extends ExportManager {

	private InvokedCollector collector;
	
	protected ConstructorExportManager(boolean isDevelopment) {
		super(isDevelopment, false);
		collector = ConstructorCollector.getInstance();
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
