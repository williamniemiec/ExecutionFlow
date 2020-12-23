package executionFlow.constructorExecutionFlow;

import java.nio.file.Path;
import java.util.List;

import executionFlow.ExecutionFlow;
import executionFlow.ExecutionFlowTest;

public abstract class ConstructorExecutionFlowTest extends ExecutionFlowTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected String testMethodSignature;
	protected Object[] paramValues;
	protected List<List<Integer>> testPaths;
	protected Class<?>[] paramTypes;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected ConstructorExecutionFlowTest(String classSignature, String pkgTestMethod, 
										Path srcTestMethod, Path binTestMethod) {
		super(classSignature, pkgTestMethod, srcTestMethod, binTestMethod);
	}
}
