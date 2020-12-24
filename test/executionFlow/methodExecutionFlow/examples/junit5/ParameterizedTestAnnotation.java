package executionFlow.methodExecutionFlow.examples.junit5;

import java.io.IOException;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.junit.Test;

import executionFlow.ExecutionFlow;
import executionFlow.MethodExecutionFlow;
import executionFlow.methodExecutionFlow.MethodExecutionFlowTest;
import executionFlow.runtime.SkipCollection;

/**
 * Tests test path computation for the tested methods of 
 * {@link examples.junit5.ParameterizedTestAnnotation} test using 
 * {@link MethodExecutionFlow}.
 */
@SkipCollection
public class ParameterizedTestAnnotation extends MethodExecutionFlowTest {

	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(int)} test
	 * method with its first argument.
	 */
	@Test
	public void test1_int_arg1() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(int)");
		withTestMethodParameterValues(-1);
		invokedOnLine(38);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,41);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation#test1(int)} test
	 * method with its second argument.
	 */
	@Test
	public void test1_int_arg2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(int)");
		withTestMethodParameterValues(0);
		invokedOnLine(38);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,41);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #test1(int)} test method with its third argument.
	 */
	@Test
	public void test1_int_arg3() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(int)");
		withTestMethodParameterValues(1);
		invokedOnLine(38);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.factorial(int)");
		
		assertTestPathIs(35,36,37,38,39,37,41);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #nullEmptyAndBlankStrings(String)} test method with its first argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".nullEmptyAndBlankStrings(String)");
		withTestMethodParameterValues(" ");
		invokedOnLine(45);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.trim(String)");
		
		assertTestPathIs(115,118);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #nullEmptyAndBlankStrings(String)} test method with its second argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".nullEmptyAndBlankStrings(String)");
		withTestMethodParameterValues("   ");
		invokedOnLine(45);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.trim(String)");
		
		assertTestPathIs(115,118);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #nullEmptyAndBlankStrings(String)} test method with its third argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg3() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".nullEmptyAndBlankStrings(String)");
		withTestMethodParameterValues("\t");
		invokedOnLine(45);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.trim(String)");
		
		assertTestPathIs(115,118);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #nullEmptyAndBlankStrings(String)} test method with its fourth argument.
	 */
	@Test
	public void nullEmptyAndBlankStrings_arg4() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".nullEmptyAndBlankStrings(String)");
		withTestMethodParameterValues("\n");
		invokedOnLine(45);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.trim(String)");
		
		assertTestPathIs(115,118);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #nullTest(String)} test method.
	 */
	@Test
	public void nullTest() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".nullTest(String)");
		withTestMethodParameterValues((Object[]) null);
		invokedOnLine(52);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass.trim(String)");
		
		assertTestPathIs(115,116);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #test1(String, int)} test method with its first argument.
	 */
	@Test
	public void test1_String_int_arg1() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(String, int)");
		withTestMethodParameterValues("I", -1);
		invokedOnLine(59);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,128,129,127,132);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #test1(String, int)} test method with its second argument.
	 */
	@Test
	public void test1_String_int_arg2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(String, int)");
		withTestMethodParameterValues("II", 0);
		invokedOnLine(59);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,128,129,127,132);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #test1(String, int)} test method with its third argument.
	 */
	@Test
	public void test1_String_int_arg3() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".test1(String, int)");
		withTestMethodParameterValues("III", 1);
		invokedOnLine(59);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,128,129,127,132);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #withMethodSource(String, int)} test method with its first argument.
	 */
	@Test
	public void withMethodSource_arg1() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".withMethodSource(String, int)");
		withTestMethodParameterValues("Hello", 5);
		invokedOnLine(66);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".concatStrNum(String, int)");
		
		assertTestPathIs(121);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #withMethodSource(String, int)} test method with its second argument.
	 */
	@Test
	public void withMethodSource_arg2() throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".withMethodSource(String, int)");
		withTestMethodParameterValues("Hello", 5);
		invokedOnLine(66);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".concatStrNum(String, int)");
		
		assertTestPathIs(121);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #testWithEnumSourceInclude(ChronoUnit)} test method with its first
	 * argument.
	 */
	@Test
	public void testWithEnumSourceInclude_arg1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".testWithEnumSourceInclude(ChronoUnit)");
		withTestMethodParameterValues(ChronoUnit.DAYS);
		invokedOnLine(80);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,132);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #testWithEnumSourceInclude(ChronoUnit)} test method with its second
	 * argument.
	 */
	@Test
	public void testWithEnumSourceInclude_arg2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".testWithEnumSourceInclude(ChronoUnit)");
		withTestMethodParameterValues(ChronoUnit.HOURS);
		invokedOnLine(80);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,132);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #testWithEnumSourceIncludeUsingInterface(TemporalUnit)} test method with
	 * its first argument.
	 */
	@Test
	public void testWithEnumSourceIncludeUsingInterface_arg1() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".testWithEnumSourceIncludeUsingInterface(TemporalUnit)");
		withTestMethodParameterValues(ChronoUnit.DAYS);
		invokedOnLine(87);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,132);
	}
	
	/**
	 * Tests {@link examples.junit5.ParameterizedTestAnnotation
	 * #testWithEnumSourceIncludeUsingInterface(TemporalUnit)} test method with
	 * its second argument.
	 */
	@Test
	public void testWithEnumSourceIncludeUsingInterface_arg2() 
			throws ClassNotFoundException, IOException {
		withTestMethodSignature("examples.junit5.ParameterizedTestAnnotation" + 
								".testWithEnumSourceIncludeUsingInterface(TemporalUnit)");
		withTestMethodParameterValues(ChronoUnit.HOURS);
		invokedOnLine(87);
		initializeTest();
		
		computeTestPathOf("examples.others.auxClasses.AuxClass" + 
						  ".countTotalArguments(Object[])");
		
		assertTestPathIs(124,125,126,127,128,129,127,132);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	protected String getTestMethodPackage() {
		return "examples.junit5";
	}
	
	@Override
	protected Path getTestMethodBinFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("bin", "examples", "junit5", "PolymorphismTest.class")
		);
	}
	
	@Override
	protected Path getTestMethodSrcFile() {
		return ExecutionFlow.getAppRootPath().resolve(
				Path.of("examples", "examples", "junit5", "PolymorphismTest.java")
		);
	}
	
	@Override
	protected Path getBinTestedInvoked() {
		return Path.of("bin", "examples", "others", "auxClasses", 
					   "AuxClass.class");
	}
	
	@Override
	protected Path getSrcTestedInvoked() {
		return Path.of("examples", "examples", "others", "auxClasses", 
					   "AuxClass.java");
	}
}