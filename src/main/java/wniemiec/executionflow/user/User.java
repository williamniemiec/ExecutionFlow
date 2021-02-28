package wniemiec.executionflow.user;

import java.io.File;
import java.io.IOException;

import wniemiec.util.data.storage.Session;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.LogView;
import wniemiec.util.logger.Logger;

public class User {
	
	private static Session session;
	
	static {
		session = new Session(
				"session.ef", 
				new File(System.getProperty("user.home")
		));
	}
	
	
	
	
	public static LogLevel askUserForLogLevel() throws IOException {
		LogLevel level = LogView.askLogLevel();
		
		session.destroy();
		session.save("LOG_LEVEL", level); 
		
		return level;
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
