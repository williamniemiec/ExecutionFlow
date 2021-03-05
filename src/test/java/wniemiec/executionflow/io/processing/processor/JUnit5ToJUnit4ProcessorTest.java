package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.executionflow.io.FileEncoding;

class JUnit5ToJUnit4ProcessorTest extends SourceCodeProcessorTest {

	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private List<String> testMethodArgs;
	
	
	//-----------------------------------------------------------------------
	//		Test hooks
	//-----------------------------------------------------------------------
	@BeforeEach
	void beforeEachTest() {
		testMethodArgs = null;
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-test-annotation"
	})
	void testTestAnnotation(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-repeated-annotation"
	})
	void testRepeatedAnnotation(String filename) throws IOException {
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-valuesource1"
	})
	void testParameterizedAnnotationValueSource1(String filename) throws IOException {
		withTestMethodArgs(" ");
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-valuesource2"
	})
	void testParameterizedAnnotationValueSource2(String filename) throws IOException {
		withTestMethodArgs("\t");
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-csvsource"
	})
	void testParameterizedAnnotationCSVSource(String filename) throws IOException {
		withTestMethodArgs("I", -1);
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-enumsourceinterface"
	})
	void testParameterizedAnnotationEnumSourceInterface(String filename) throws IOException {
		withTestMethodArgs(ChronoUnit.DAYS);
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-enumsourcewithclass"
	})
	void testParameterizedAnnotationEnumSourceWithClass(String filename) throws IOException {
		withTestMethodArgs(FileEncoding.ISO_8859_1);
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-methodsource"
	})
	void testParameterizedAnnotationMethodSource(String filename) throws IOException {
		withTestMethodArgs("Hello", 5);
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-nullsource"
	})
	void testParameterizedAnnotationNullSource(String filename) throws IOException {
		withTestMethodArgs(new Object[] {null});
		testProcessorOnFile(filename);
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void withTestMethodArgs(Object... args) {
		testMethodArgs = argsToStringList(normalizeArgs(args));
	}
	
	@SuppressWarnings("rawtypes")
	private Object[] normalizeArgs(Object[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Enum)
				args[i] = ((Enum) args[i]).name();
		}
		
		return args;
	}

	private List<String> argsToStringList(Object[] args) {
		List<String> stringArgsList = new ArrayList<>();
		
		for (String arg : convertArrayToString(args)) {
			if (arg == null)
				stringArgsList.add(null);
			else
				stringArgsList.add(arg);
		}
		
		return stringArgsList;
	}
	
	private String[] convertArrayToString(Object[] args) {
		String toStringArray = Arrays.toString(args);
		String individualArgs = toStringArray.substring(1, toStringArray.length()-1);
		individualArgs = individualArgs.replaceAll(", ", ",");
		
		return individualArgs.split(",");
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return	(testMethodArgs == null) 
				? new JUnit5ToJUnit4Processor(sourceCode)
				: new JUnit5ToJUnit4Processor(sourceCode, testMethodArgs);
	}
}
