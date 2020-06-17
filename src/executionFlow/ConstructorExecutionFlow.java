package executionFlow;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import executionFlow.core.JDB;
import executionFlow.core.file.FileManager;
import executionFlow.core.file.parser.factory.MethodFileParserFactory;
import executionFlow.core.file.parser.factory.TestMethodFileParserFactory;
import executionFlow.info.CollectorInfo;


/**
 * Computes test path for collected constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class ConstructorExecutionFlow extends ExecutionFlow
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	/**
	 * Stores information about collected constructors.
	 */
	private Collection<CollectorInfo> constructorCollector;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Computes test path for collected constructors.
	 * 
	 * @param		constructorCollector Collected constructors from 
	 * {@link executionFlow.runtime.ConstructorCollector}
	 */
	public ConstructorExecutionFlow(Collection<CollectorInfo> constructorCollector)
	{
		this.constructorCollector = constructorCollector;
		
		classTestPaths = new HashMap<>();
	}
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public ExecutionFlow execute()
	{
		// -----{ DEBUG }-----
		if (DEBUG) {
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
			ConsoleOutput.showDebug(constructorCollector.toString());
			ConsoleOutput.showDebug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		}
		// -----{ END DEBUG }-----
		
		List<List<Integer>> tp_jdb;
		
		// Generates test path for each collected method
		for (CollectorInfo collector : constructorCollector) {
			// Checks if collected constructor is within test method
			if (collector.getConstructorInfo().getClassPath().equals(collector.getTestMethodInfo().getClassPath())) {
				ConsoleOutput.showError("The constructor to be tested cannot be within the test class");
				ConsoleOutput.showError("This test path will be skipped");
				continue;
			}
			
			// Gets FileManager for method file
			FileManager constructorFileManager = new FileManager(
				collector.getConstructorInfo().getSrcPath(), 
				collector.getConstructorInfo().getClassDirectory(),
				collector.getConstructorInfo().getPackage(),
				new MethodFileParserFactory()
			);
			
			// Gets FileManager for test method file
			FileManager testMethodFileManager = new FileManager(
				collector.getTestMethodInfo().getSrcPath(), 
				collector.getTestMethodInfo().getClassDirectory(),
				collector.getTestMethodInfo().getPackage(),
				new TestMethodFileParserFactory()
			);
			
			try {
				// Processes the source file of the test method if it has
				// not been processed yet
				if (!testMethodManager.wasParsed(testMethodFileManager)) {
					ConsoleOutput.showInfo("Processing source file of test method "
						+ collector.getTestMethodInfo().getInvokerSignature()+"...");
					
					testMethodManager.parse(testMethodFileManager).compile(testMethodFileManager);
					ConsoleOutput.showInfo("Processing completed");	
				}

				// Processes the source file of the method if it has not 
				// been processed yet
				if (!invokerManager.wasParsed(constructorFileManager)) {
					ConsoleOutput.showInfo("Processing source file of constructor " 
						+ collector.getConstructorInfo().getInvokerSignature()+"...");
					
					invokerManager.parse(constructorFileManager).compile(constructorFileManager);
					ConsoleOutput.showInfo("Processing completed");
				}
				
				// Computes test path from JDB
				ConsoleOutput.showInfo("Computing test path of constructor "
					+ collector.getConstructorInfo().getInvokerSignature()+"...");

				JDB jdb = new JDB(collector.getOrder());					
				tp_jdb = jdb.getTestPaths(collector.getConstructorInfo(), collector.getTestMethodInfo());
				
				if (tp_jdb.isEmpty() || tp_jdb.get(0).isEmpty())
					ConsoleOutput.showWarning("Test path is empty");
				else
					ConsoleOutput.showInfo("Test path has been successfully computed");
				
				// Stores each computed test path
				storeTestPath(tp_jdb, collector);
			} catch (Exception e) {
				ConsoleOutput.showError(e.getMessage());
				e.printStackTrace();
			}
		}
		
		return this;
	}
}
