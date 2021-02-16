package wniemiec.executionflow.io.processing.processor;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import wniemiec.executionflow.io.FileEncoding;

class JUnit5ToJUnit4ProcessorTest extends SourceCodeProcessorTest {

	private Object[] testMethodArgs;
	
	@BeforeEach
	void beforeEachTest() {
		testMethodArgs = null;
	}
	
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
		testMethodArgs = new Object[] {" "};
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-valuesource2"
	})
	void testParameterizedAnnotationValueSource2(String filename) throws IOException {
		testMethodArgs = new Object[] {"\t"};
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-csvsource"
	})
	void testParameterizedAnnotationCSVSource(String filename) throws IOException {
		testMethodArgs = new Object[] {"I", -1};
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-enumsourceinterface"
	})
	void testParameterizedAnnotationEnumSourceInterface(String filename) throws IOException {
		testMethodArgs = new Object[] {ChronoUnit.DAYS};
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-enumsourcewithclass"
	})
	void testParameterizedAnnotationEnumSourceWithClass(String filename) throws IOException {
		testMethodArgs = new Object[] {FileEncoding.ISO_8859_1};
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-methodsource"
	})
	void testParameterizedAnnotationMethodSource(String filename) throws IOException {
		testMethodArgs = new Object[] {"Hello", 5};
		testProcessorOnFile(filename);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"junit5-parameterized-annotation-nullsource"
	})
	void testParameterizedAnnotationNullSource(String filename) throws IOException {
		testMethodArgs = new Object[] {null};
		testProcessorOnFile(filename);
	}
	
	@Override
	protected SourceCodeProcessor getProcessorFor(List<String> sourceCode) {
		return	(testMethodArgs == null) 
				? new JUnit5ToJUnit4Processor(sourceCode)
				: new JUnit5ToJUnit4Processor(sourceCode, testMethodArgs);
	}
}
