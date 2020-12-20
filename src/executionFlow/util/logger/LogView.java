package executionFlow.util.logger;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class LogView {
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Map<Integer, LogLevel> options;
	private static final int DEFAULT_OPTION = 3;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private LogView() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		options = new HashMap<>();
		
		options.put(0, LogLevel.OFF);
		options.put(1, LogLevel.ERROR);
		options.put(2, LogLevel.WARNING);
		options.put(3, LogLevel.INFO);
		options.put(4, LogLevel.DEBUG);
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Asks the user what level of logging will be used.
	 * 
	 * @return		Selected log level
	 */
	public static LogLevel askLogLevel()
	{
		int option = ask();
		
		return getLogLevel(option);
	}

	private static int ask() {
		String[] btnOptions = initializeOptions();
		
		return JOptionPane.showOptionDialog(
				null, "Choose log level", "Log option", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
				null, btnOptions, btnOptions[DEFAULT_OPTION]
		);
	}

	private static String[] initializeOptions() {
		return new String[] {
				createHTMLButton("None<br>(not recommended \u274C)"),
				createHTMLButton("Error"),
				createHTMLButton("Warning"),
				createHTMLButton("Info<br>(recommended \u2714)"),
				createHTMLButton("Debug")
		};
	}
	
	private static String createHTMLButton(String content) {
		StringBuilder btn = new StringBuilder();
		
		btn.append("<html>");
		btn.append("<body>");
		btn.append("<div align='center'>");
		btn.append(content);
		btn.append("</div>");
		btn.append("</body>");
		btn.append("</html>");
		
		return btn.toString();
	}
	
	private static LogLevel getLogLevel(int option) {
		if (!options.containsKey(option))
			return options.get(DEFAULT_OPTION);
		
		return options.get(option);
	}
}
