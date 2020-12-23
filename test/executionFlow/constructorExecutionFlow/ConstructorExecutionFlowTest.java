package executionFlow.constructorExecutionFlow;

import java.nio.file.Path;
import java.util.List;

import executionFlow.ExecutionFlow;
import executionFlow.ExecutionFlowTest;
import executionFlow.info.InvokedInfo;

public abstract class ConstructorExecutionFlowTest extends ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected ConstructorExecutionFlowTest(String pkgTestMethod, Path srcTestMethod, 
			Path binTestMethod) {
		super(pkgTestMethod, srcTestMethod, binTestMethod);
	}
	
	protected boolean isConstructor() {
		return true;
	}
	
	protected abstract Path getBinTestedInvoked();
	protected abstract Path getSrcTestedInvoked();
}
