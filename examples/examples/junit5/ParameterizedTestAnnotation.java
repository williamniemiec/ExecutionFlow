package examples.junit5;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.EnumSet;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import examples.others.auxClasses.AuxClass;
import wniemiec.app.java.executionflow.io.FileEncoding;


/**
 * Tests that use the {@link ParameterizedTest} annotation belonging to 
 * JUnit 5.
 */
public class ParameterizedTestAnnotation 
{
	@ParameterizedTest
	@ValueSource(ints = {-1,0,1})
	public void test1(int num)
	{
		AuxClass tc = new AuxClass(4);


		assertEquals(1, tc.factorial(num));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {" ", "   ", "\t", "\n"})
	public void nullEmptyAndBlankStrings(String text) 
	{
	    assertEquals("", AuxClass.trim(text));
	}
	
	@ParameterizedTest
	@NullSource
	public void nullTest(String text) 
	{
	    assertEquals("", AuxClass.trim(text));
	}
	
	@ParameterizedTest
	@CsvSource({"I, -1", "II, 0", "III, 1"})
	public void test1(String word, int num)
	{
		assertEquals(2, AuxClass.countTotalArguments(word, num));
	}
	
	@ParameterizedTest
	@MethodSource("createWordsWithLength")
	public void withMethodSource(String word, int length) 
	{
		assertEquals(word+length, AuxClass.concatStrNum(word, length));
	}
	 
	private static Stream<Arguments> createWordsWithLength() 
	{
		return Stream.of(
			Arguments.of("Hello", 5),
			Arguments.of("JUnit 5", 7));
	}
	
	@ParameterizedTest
	@EnumSource(names = {"DAYS", "HOURS"}, value = ChronoUnit.class)
	public void testWithEnumSourceInclude(ChronoUnit unit) 
	{
		assertEquals(1, AuxClass.countTotalArguments(unit));
	}
	
	@ParameterizedTest
	@EnumSource(names = {"DAYS", "HOURS"}, value = ChronoUnit.class)
	public void testWithEnumSourceIncludeUsingInterface(TemporalUnit unit) 
	{
		assertEquals(1, AuxClass.countTotalArguments(unit));
	}
	
	@ParameterizedTest
	@EnumSource(FileEncoding.class)
	public void testWithEnumSourceIncludeUsingInterface(FileEncoding encode) 
	{
	    assertTrue(EnumSet.of(FileEncoding.ISO_8859_1, FileEncoding.UTF_8).contains(encode));
	}
	
	@ParameterizedTest
	@EnumSource(ChronoUnit.class)
	public void testWithEnumSource(TemporalUnit unit) 
	{
	    assertNotNull(unit);
	}
}