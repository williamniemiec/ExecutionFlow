package wniemiec.executionflow.user;

import java.io.File;
import java.io.IOException;

import wniemiec.executionflow.gui.RemoteControl;
import wniemiec.executionflow.gui.popup.MainSelector;
import wniemiec.util.data.storage.Session;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.LogView;
import wniemiec.util.logger.Logger;

/**
 * Responsible for handling user interactions as well as managing your session.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class User {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Session session;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		session = new Session(
				"session.ef", 
				new File(System.getProperty("user.home")
		));
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private User() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public static LogLevel askUserForLogLevel() throws IOException {
		MainSelector selector = new MainSelector();
		selector.open();
		
		session.destroy();
		session.save("LOG_LEVEL", Logger.getLevel()); 
		
		return Logger.getLevel();
	}
	
	public static LogLevel loadLogLevel() throws IOException {
		LogLevel level;
		
		try {
			level = (LogLevel) session.read("LOG_LEVEL");
		} 
		catch (IOException e) {
			Logger.error("Corrupted session");
			
			level = LogView.askLogLevel();
			session.save("LOG_LEVEL", level);
		}
		
		return level;
	}
	
	public static LogLevel getSelectedLogLevel() {
		LogLevel logLevel;
		
		try {
			logLevel = (LogLevel)session.read("LOG_LEVEL");
		} 
		catch (IOException e) {
			Logger.error("Corrupted session");
			
			logLevel = LogView.askLogLevel();
			
			try {
				session.save("LOG_LEVEL", logLevel);
			} 
			catch (IOException e1) {
				Logger.error(e1.getMessage());
				
				session.destroy();
			}
		}
		
		if (logLevel == null)
			logLevel = LogLevel.INFO;
		
		return logLevel;
	}

	public static void openRemoteControl() {
		RemoteControl.open();
	}
	
	public static void closeRemoteControl() {
		RemoteControl.close();
	}
}
