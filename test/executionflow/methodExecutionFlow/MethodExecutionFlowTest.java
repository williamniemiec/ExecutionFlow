package executionflow.methodExecutionFlow;

import java.util.Collection;

import executionflow.ExecutionFlow;
import executionflow.ExecutionFlowTest;
import executionflow.MethodExecutionFlow;
import executionflow.info.InvokedContainer;
import executionflow.io.manager.InvokedManager;

public abstract class MethodExecutionFlowTest extends ExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected ExecutionFlow getExecutionFlow(InvokedManager processingManager, 
											 Collection<InvokedContainer> collector) {
		return new MethodExecutionFlow(processingManager, collector);
	}
}
