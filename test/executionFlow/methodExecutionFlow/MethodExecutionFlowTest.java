package executionFlow.methodExecutionFlow;

import java.util.Collection;

import executionFlow.ExecutionFlow;
import executionFlow.ExecutionFlowTest;
import executionFlow.MethodExecutionFlow;
import executionFlow.info.InvokedContainer;
import executionFlow.io.manager.InvokedManager;

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
