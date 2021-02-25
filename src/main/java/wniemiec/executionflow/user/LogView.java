package wniemiec.executionflow.user;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import wniemiec.util.logger.LogLevel;

/**
 * Responsible for asking the user the desired log level. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class LogView {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Map<Integer, LogLevel> options;
	private static final int DEFAULT_OPTION = 3;
	
	
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
	//		Constructor
	//-------------------------------------------------------------------------
	private LogView() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Asks the user what level of logging will be used.
	 * 
	 * @return		Selected log level
	 */
	public static LogLevel askLogLevel() {
		int option = ask();
		
		return getLogLevel(option);
	}

	private static int ask() {
		JButton[] btnOptions = initializeOptions();
		
		 UIManager UI=new UIManager();
		 UI.put("OptionPane.background", new Color(25,25,25));
		 UI.put("Panel.background", new Color(100,100,100));
		 UI.put("OptionPane.messageForeground", Color.white);
		 
		return JOptionPane.showOptionDialog(
				null, "Choose log level", "Log option", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
				null, btnOptions, btnOptions[DEFAULT_OPTION]
		);
	}

	private static JButton[] initializeOptions() {
		return new JButton[] {
				createHTMLButton("None<br>(not recommended \u274C)"),
				createHTMLButton("Error"),
				createHTMLButton("Warning"),
				createHTMLButton("Info<br>(recommended \u2714)"),
				createHTMLButton("Debug")
		};
	}
	
	private static JButton createHTMLButton(String content) {
		StringBuilder btn = new StringBuilder();
		
		btn.append("<html>");
		btn.append("<body>");
		btn.append("<div align='center'>");
		btn.append(content);
		btn.append("</div>");
		btn.append("</body>");
		btn.append("</html>");
		
		return new ThemeButton(btn.toString(), true);
	}
	
	private static LogLevel getLogLevel(int option) {
		if (!options.containsKey(option))
			return options.get(DEFAULT_OPTION);
		
		return options.get(option);
	}
}
