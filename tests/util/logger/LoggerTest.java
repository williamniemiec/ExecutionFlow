package util.logger;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public abstract class LoggerTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------	
	protected final String message = "Hello World";
	protected static LogLevel logLevel;
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------	
	@Before
	public void setUp() {
		Logger.setLevel(getLogLevel());
		Logger.clearLastMessage();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------	
	@Test
	public void testLevel() {
		assertEquals(getLogLevel(), Logger.getLevel());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullLevel() {
		Logger.setLevel(null);
	}
	
	@Test
	public abstract void testErrorMessage();
	
	@Test(expected=IllegalArgumentException.class)
	public abstract void testNullErrorMessage();
	
	@Test
	public abstract void testWarnMessage();
	
	@Test(expected=IllegalArgumentException.class)
	public abstract void testNullWarnMessage();
	
	@Test
	public abstract void testInfoMessage();
	
	@Test(expected=IllegalArgumentException.class)
	public abstract void testNullInfoMessage();
	
	@Test
	public abstract void testDebugMessage();

	@Test(expected=IllegalArgumentException.class)
	public abstract void testNullDebugMessage();
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------	
	protected abstract LogLevel getLogLevel();
}
