package util.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DebugLoggerTest extends LoggerTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Override
	public void testNullLevel() {
		Logger.setLevel(null);
	}
	
	@Override
	public void testErrorMessage() {
		Logger.error(message);
		
		assertEquals(message, Logger.getLastMessage());
		assertEquals("ERROR", Logger.getLastMessageType());
	}
	
	@Override
	public void testNullErrorMessage() {
		Logger.error(null);
		
		fail();
	}
	
	@Override
	public void testWarnMessage() {
		Logger.warning(message);
		
		assertEquals(message, Logger.getLastMessage());
		assertEquals("WARN", Logger.getLastMessageType());
	}
	
	@Override
	public void testNullWarnMessage() {
		Logger.warning(null);
		
		fail();
	}
	
	@Override
	public void testInfoMessage() {
		Logger.info(message);
		
		assertEquals(message, Logger.getLastMessage());
		assertEquals("INFO", Logger.getLastMessageType());
	}
	
	@Override
	public void testNullInfoMessage() {
		Logger.info(null);
		
		fail();
	}
	
	@Override
	public void testDebugMessage() {
		Logger.debug(message);
		
		assertEquals(message, Logger.getLastMessage());
		assertEquals("DEBUG", Logger.getLastMessageType());
	}

	@Override
	public void testNullDebugMessage() {
		Logger.debug(null);
		
		fail();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	protected LogLevel getLogLevel() {
		return LogLevel.DEBUG;
	}
}
