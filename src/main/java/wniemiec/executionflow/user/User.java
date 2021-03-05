package wniemiec.executionflow.user;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import wniemiec.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.executionflow.gui.RemoteControl;
import wniemiec.executionflow.gui.popup.MainSelector;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.data.storage.Session;
import wniemiec.util.logger.LogLevel;
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
	private static MainSelector selector;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		session = new Session(
				"session.ef", 
				new File(System.getProperty("user.home")
		));
		
		selector = new MainSelector();
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private User() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public static void openMainSelector() throws IOException {
		selector.open();
		
		session.destroy();
		session.save("LOG_LEVEL", selector.getSelectedLoggingLevel());
		session.save("TESTPATH_EXPORT_TYPE", selector.getSelectedTestPathExportType());
	}
	
//	public static LogLevel loadLogLevel() throws IOException {
//		LogLevel level;
//		
//		try {
//			level = (LogLevel) session.read("LOG_LEVEL");
//		} 
//		catch (IOException e) {
//			Logger.error("Corrupted session");
//			
//			level = LogView.askLogLevel();
//			session.save("LOG_LEVEL", level);
//		}
//		
//		return level;
//	}
	
	public static LogLevel getSelectedLogLevel() {
		LogLevel logLevel = null;
		
		try {
			logLevel = (LogLevel) session.read("LOG_LEVEL");
		} 
		catch (IOException e) {
			Logger.error("Corrupted session");
			Logger.info("Default logging level selected: INFO");
			session.destroy();
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

	public static TestPathExportType getSelectedTestPathExportType() {
		if (!session.hasKey("TESTPATH_EXPORT_TYPE"))
			return null;
		
		try {
			return (TestPathExportType) session.read("TESTPATH_EXPORT_TYPE");
		} 
		catch (IOException e) {
			return null;
		}
	}

	public static void storeMethodCollector(Map<Integer, List<TestedInvoked>> collector)
			throws IOException {
		session.save("METHOD_COLLECTOR", collector);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Integer, List<TestedInvoked>> getMethodCollector() 
			throws IOException {
		return (Map<Integer, List<TestedInvoked>>) session.read("METHOD_COLLECTOR");
	}

	public static void storeConstructorCollector(Map<Integer, TestedInvoked> collector) 
			throws IOException {
		session.save("CONSTRUCTOR_COLLECTOR", collector);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Integer, TestedInvoked> getConstructorCollector() 
			throws IOException {
		return (Map<Integer, TestedInvoked>) session.read("CONSTRUCTOR_COLLECTOR");
	}
}
