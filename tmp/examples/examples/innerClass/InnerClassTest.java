package examples.innerClass;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import examples.innerClass.OuterClass.InnerClass;


/**
 * Tests classes that use inner classes.
 */
public class InnerClassTest 
{
	@Test
	public void test1()
	{
		OuterClass outer = new OuterClass("test");
		InnerClass inner = outer.new InnerClass("test2");
		
		
		assertEquals("test", outer.getText());
		assertEquals("test2", inner.getText());
	}
}
