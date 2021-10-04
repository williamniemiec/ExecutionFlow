package wniemiec.app.java.executionflow.user;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wniemiec.app.java.ExecutionFlow;
import wniemiec.app.java.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.app.java.executionflow.gui.RemoteControl;
import wniemiec.app.java.executionflow.gui.popup.MainSelector;
import wniemiec.app.java.executionflow.invoked.TestedInvoked;
import wniemiec.io.java.Consolex;
import wniemiec.io.java.LogLevel;
import wniemiec.data.java.Session;

/**
 * Responsible for handling user interactions as well as managing your session.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
		initializeSession();
	}
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private User() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private static void initializeSession() {
		File sessionLocation = ExecutionFlow.getAppRootPath().toFile();
		
		if (ExecutionFlow.isDevelopment())
			sessionLocation = new File(sessionLocation, "examples");
		
		session = new Session("session.ef", sessionLocation);
	}
	
	public static void openMainSelector() throws IOException {
		initializeMainSelector();
		storeUserSelections();
	}

	private static void initializeMainSelector() {
		if (selector == null)
			selector = new MainSelector();
		
		session.destroy();
		selector.open();
	}
	
	private static void storeUserSelections() throws IOException {
		session.save(UserInfo.LOG_LEVEL.name(), selector.getSelectedLoggingLevel());
		session.save(UserInfo.TESTPATH_EXPORT_TYPE.name(), selector.getSelectedTestPathExportType());
		session.save(UserInfo.SURROUND_ASSERTS_WITH_TRY_CATCH.name(), selector.getShouldComputeTestPathOfFailingAsserts());
	}
	
	public static LogLevel getSelectedLogLevel() {
		LogLevel logLevel = null;
		
		try {
			logLevel = (LogLevel) session.read(UserInfo.LOG_LEVEL.name());
		} 
		catch (IOException e) {
			Consolex.writeError("Corrupted session");
			session.destroy();
			
			Consolex.writeInfo("Default logging level selected: INFO");
			logLevel = LogLevel.INFO;
			
			tryStore(UserInfo.LOG_LEVEL.name(), logLevel);			
		}
		
		return (logLevel == null) ? LogLevel.INFO : logLevel;
	}
	
	private static void tryStore(String key, Object value) {
		try {
			session.save(key, value);
		} 
		catch (IOException e) {
			Consolex.writeError("Session cannot be stored");
		}
	}

	public static void openRemoteControl() {
		RemoteControl.open();
	}
	
	public static void closeRemoteControl() {
		RemoteControl.close();
	}

	public static TestPathExportType getSelectedTestPathExportType() {
		try {
			Object exportType = session.read(UserInfo.TESTPATH_EXPORT_TYPE.name());
			
			return (exportType == null) 
						? TestPathExportType.FILE 
						: (TestPathExportType) exportType;
		} 
		catch (IOException e) {
			Consolex.writeError("Corrupted session");
			session.destroy();
			
			Consolex.writeInfo("Default test path export type selected: FILE");
			tryStore(UserInfo.TESTPATH_EXPORT_TYPE.name(), TestPathExportType.FILE);
			
			return TestPathExportType.FILE;
		}
	}

	public static void storeMethodCollector(Map<Integer, List<TestedInvoked>> collector)
			throws IOException {
		if (collector.isEmpty())
			return;
		
		session.save(UserInfo.METHOD_COLLECTOR.name(), collector);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Integer, List<TestedInvoked>> getMethodCollector() 
			throws IOException {
		if (!session.exists())
			return new HashMap<>();
		
		return (Map<Integer, List<TestedInvoked>>) session.read(UserInfo.METHOD_COLLECTOR.name());
	}

	public static void storeConstructorCollector(Map<Integer, TestedInvoked> collector) 
			throws IOException {
		if (collector.isEmpty())
			return;
		
		session.save(UserInfo.CONSTRUCTOR_COLLECTOR.name(), collector);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<Integer, TestedInvoked> getConstructorCollector() 
			throws IOException {
		if (!session.exists())
			return new HashMap<>();
		
		return (Map<Integer, TestedInvoked>) session.read(UserInfo.CONSTRUCTOR_COLLECTOR.name());
	}
	
	public static void resetMethodCollector() {
		if (!session.exists())
			return;
		
		try {
			session.remove(UserInfo.METHOD_COLLECTOR.name());
		}
		catch (IOException e2) {
			Consolex.writeError(e2.toString());
			Consolex.writeError("Reset constructor collector - failed");
		}
	}
	
	public static void resetConstructorCollector() {
		if (!session.exists())
			return;
		
		try {
			session.remove(UserInfo.CONSTRUCTOR_COLLECTOR.name());
		}
		catch (IOException e) {
			Consolex.writeError(e.toString());
			Consolex.writeError("Reset method collector - failed");
		}
	}

	public static void unlinkSession() {
		session.destroy();
	}
	
	public static boolean hasLinkedSession() {
		return session.exists();
	}


	public static boolean hasSelectedSurroundAssertsWithTryCatch() {
		if (!session.exists() || !session.hasKey(UserInfo.SURROUND_ASSERTS_WITH_TRY_CATCH.name()))
			return false;
		
		try {
			return ((boolean) session.read(UserInfo.SURROUND_ASSERTS_WITH_TRY_CATCH.name()));
		} 
		catch (IOException e) {
			return false;
		}
	}
}