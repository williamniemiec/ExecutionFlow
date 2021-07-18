package wniemiec.app.executionflow.gui.popup;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import wniemiec.io.consolex.LogLevel;

/**
 * Responsible for creating a logging selector.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.2.0
 */
class LoggingLevelSelector extends Selector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private LogLevel selectedLoggingLevel;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public LoggingLevelSelector(JDialog window) {
		super(window);
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public JPanel create() {
		JPanel panel = new JPanel();
		
		panel.setBackground(bgColor);
		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		panel.add(createTitle("Logging level"), BorderLayout.NORTH);
		panel.add(createLoggingLevelOptions(), BorderLayout.CENTER);
		
		return panel;
	}
	
	private JPanel createLoggingLevelOptions() {
		JPanel panelLog = new JPanel();
		
		panelLog.setBackground(bgColor);
		panelLog.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		for (JButton btn : generateLoggingLevelButtons()) {
			panelLog.add(btn);
		}
		
		return panelLog;
	}
	
	private JButton[] generateLoggingLevelButtons() {		
		return new JButton[] {
				createNoneButton(),
				createErrorButton(),
				createWarningButton(),
				createInfoButton(),
				createDebugButton()
		};
	}
	
	private JButton createNoneButton() {
		JButton btn = createHTMLButton("None<br>(not recommended \u274C)");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.OFF;
			closeWindow();
		});
		
		return btn;
	}
	
	private JButton createErrorButton() {
		JButton btn = createHTMLButton("Error");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.ERROR;
			closeWindow();
		});
		
		return btn;
	}
	
	private JButton createWarningButton() {
		JButton btn = createHTMLButton("Warning");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.WARNING;
			closeWindow();
		});
		
		return btn;
	}
	
	private JButton createInfoButton() {
		JButton btn = createHTMLButton("Info<br>(recommended \u2714)");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.INFO;
			closeWindow();
		});
		
		return btn;
	}
	
	private JButton createDebugButton() {
		JButton btn = createHTMLButton("Debug");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.DEBUG;
			closeWindow();
		});
		
		return btn;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public LogLevel getSelectedLogging() {
		return selectedLoggingLevel;
	}
}
