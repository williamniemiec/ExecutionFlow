package util.logger;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class OffLoggerTest extends LoggerTest {
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Override
	public void testErrorMessage() {
		Logger.error(message);
		
		assertNotEquals(message, Logger.getLastMessage());
		assertNotEquals("ERROR", Logger.getLastMessageType());
	}
	
	@Override
	public void testNullErrorMessage() {
		Logger.error(null);
		
		fail();
	}
	
	@Override
	public void testWarnMessage() {
		Logger.warning(message);
		
		assertNotEquals(message, Logger.getLastMessage());
		assertNotEquals("WARN", Logger.getLastMessageType());
	}
	
	@Override
	public void testNullWarnMessage() {
		Logger.warning(null);
		
		fail();
	}
	
	@Override
	public void testInfoMessage() {
		Logger.info(message);
		
		assertNotEquals(message, Logger.getLastMessage());
		assertNotEquals("INFO", Logger.getLastMessageType());
	}
	
	@Override
	public void testNullInfoMessage() {
		Logger.info(null);
		
		fail();
	}
	
	@Override
	public void testDebugMessage() {
		Logger.debug(message);
		
		assertNotEquals(message, Logger.getLastMessage());
		assertNotEquals("DEBUG", Logger.getLastMessageType());
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
		return LogLevel.OFF;
	}
}
