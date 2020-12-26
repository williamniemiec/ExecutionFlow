package examples.junit5;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import examples.others.auxClasses.AuxClass;
import examples.polymorphism.ClassInterface;


/**
 * Tests that use the {@link RepeatedTest} annotation belonging to JUnit 5.
 */
public class RepeatedTestAnnotation 
{
	@RepeatedTest(2)
	@DisplayName(RepeatedTest.LONG_DISPLAY_NAME)
	public void test1()
	{
		AuxClass ac = new AuxClass(4);
		
		
		assertEquals(24, ac.factorial(4));
	}
	
	@org.junit.jupiter.api.RepeatedTest(2)
	@DisplayName(RepeatedTest.LONG_DISPLAY_NAME)
	public void test2()
	{
		AuxClass ac = new AuxClass(4);
		
		
		assertTrue(ac.anonymousObjectReturn() instanceof ClassInterface);
	}
	
	@RepeatedTest(value = 2, name = RepeatedTest.LONG_DISPLAY_NAME)
	@DisplayName(RepeatedTest.LONG_DISPLAY_NAME)
	public void test3()
	{
		AuxClass ac = new AuxClass(4);
		
		
		assertTrue(ac.anonymousObjectReturn() instanceof ClassInterface);
	}
}
