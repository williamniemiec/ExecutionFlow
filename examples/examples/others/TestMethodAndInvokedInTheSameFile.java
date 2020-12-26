package examples.others;

import org.junit.Assert;
import org.junit.Test;

public class TestMethodAndInvokedInTheSameFile 
{
	/**
	 * Test a method defined within the test class. An error will be generated.
	 * Execution will continue if there are more test methods to be executed.
	 */
	@Test
    public void methodInSameFileOfTestMethod() {
		int a = 2, b = 3;
		
        Assert.assertEquals(5, sum(a, b));
    }
    
	/**
	 * Example method for {@link #methodInSameFileOfTestMethod()} method.
	 * 
	 * @param a A number
	 * @param b Another number
	 * @return a + b
	 */
    private int sum(int a, int b) 
    {
        return a + b;
    }
}
