package wniemiec.app.executionflow.gui.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import wniemiec.app.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.app.executionflow.gui.AppIcon;
import wniemiec.app.executionflow.gui.ThemeButton;
import wniemiec.app.executionflow.gui.ThemePanel;
import wniemiec.io.consolex.*;

/**
 * Responsible for asking the user:
 * <ul>
 * 	<li>Test path export type</li>
 * 	<li>Logging level</li>
 * </ul> 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.0.0
 */
public class MainSelector extends JDialog {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 700L;
	private static final int WIDTH = 900;
	private static final int HEIGHT = 180;
	private static final int X0 = 250;
	private static final int Y0 = 280;
	private Color bgColor = new Color(20,20,20);
	private TestPathExportType selectedTestPathExportType;
	private LogLevel selectedLoggingLevel;

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void close() {
		dispose();
	}


	public void open() {
		setTitle("Execution Flow");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(X0, Y0, WIDTH, HEIGHT);
		setResizable(false);
		setIconImage(AppIcon.getIcon());
		getContentPane().add(createBody());
		pack();
		setModal(true);
		setVisible(true);
		
		if (!wasLoggingLevelSelected())
			System.exit(-1);
	}

	private JPanel createBody() {
		JPanel body = new ThemePanel(new BorderLayout(0, 0));
		
		body.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		body.add(createExportTypeSelector(), BorderLayout.NORTH);
		body.add(createLoggingLevelSelector(), BorderLayout.CENTER);
		
		return body;
	}
	
	private JPanel createExportTypeSelector() {
		JPanel exportTypeSelector = new JPanel();
		
		exportTypeSelector.setBackground(bgColor);
		exportTypeSelector.setLayout(new BorderLayout(0, 0));
		exportTypeSelector.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		exportTypeSelector.add(createTitle("Test path export mode"), BorderLayout.NORTH);
		exportTypeSelector.add(createExportTypeOptions(), BorderLayout.SOUTH);
		
		return exportTypeSelector;
	}

	private JPanel createExportTypeOptions() {				
		JRadioButton rdoConsole = createConsoleRdoButton();
		JRadioButton rdoFile = createFileRdoButton();
		
		ButtonGroup rdoGroup = new ButtonGroup();
		rdoGroup.add(rdoConsole);
		rdoGroup.add(rdoFile);
		
		JPanel exportTypeOptions = new JPanel();
		exportTypeOptions.setBackground(bgColor);
		exportTypeOptions.add(rdoConsole);
		exportTypeOptions.add(rdoFile);
		
		return exportTypeOptions;
	}
	
	private JRadioButton createConsoleRdoButton() {
		JRadioButton rdoConsole = createThemeRadioButton("Console");
		
		rdoConsole.addActionListener(event -> 
			selectedTestPathExportType = TestPathExportType.CONSOLE
		);
		
		return rdoConsole;
	}
	
	private JRadioButton createFileRdoButton() {
		JRadioButton rdoFile = createThemeRadioButton("File");
		
		rdoFile.addActionListener(event -> 
			selectedTestPathExportType = TestPathExportType.FILE
		);
		
		rdoFile.doClick();
		
		return rdoFile;
	}

	private JRadioButton createThemeRadioButton(String label) {
		JRadioButton rdo = new JRadioButton(label);
		
		rdo.setBackground(bgColor);
		rdo.setForeground(Color.white);
		rdo.setRequestFocusEnabled(false);
		rdo.setBorderPainted(false);
		rdo.setBorder(BorderFactory.createEmptyBorder());
		rdo.setFocusable(false);
		
		return rdo;
	}

	private JPanel createLoggingLevelSelector() {
		JPanel panel = new JPanel();
		
		panel.setBackground(bgColor);
		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		panel.add(createTitle("Logging level"), BorderLayout.NORTH);
		panel.add(createLoggingLevelOptions(), BorderLayout.CENTER);
		
		return panel;
	}
	
	private JLabel createTitle(String title) {
		JLabel lblTitle = new JLabel(title);
		lblTitle.setForeground(Color.white);
		
		return lblTitle;
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
			close();
		});
		
		return btn;
	}
	
	private JButton createErrorButton() {
		JButton btn = createHTMLButton("Error");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.ERROR;
			close();
		});
		
		return btn;
	}
	
	private JButton createWarningButton() {
		JButton btn = createHTMLButton("Warning");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.WARNING;
			close();
		});
		
		return btn;
	}
	
	private JButton createInfoButton() {
		JButton btn = createHTMLButton("Info<br>(recommended \u2714)");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.INFO;
			close();
		});
		
		return btn;
	}
	
	private JButton createDebugButton() {
		JButton btn = createHTMLButton("Debug");
		
		btn.addActionListener(event -> {
			selectedLoggingLevel = LogLevel.DEBUG;
			close();
		});
		
		return btn;
	}
	
	private JButton createHTMLButton(String content) {
		StringBuilder btn = new StringBuilder();
		
		btn.append("<html>");
		btn.append("<body>");
		btn.append("<div align='center'>");
		btn.append(content);
		btn.append("</div>");
		btn.append("</body>");
		btn.append("</html>");
		
		return ThemeButton.createRegularThemeButton(btn.toString());
	}
	
	private boolean wasLoggingLevelSelected() {
		return (selectedLoggingLevel != null);
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public TestPathExportType getSelectedTestPathExportType() {
		return selectedTestPathExportType;
	}
	
	public LogLevel getSelectedLoggingLevel() {
		return selectedLoggingLevel;
	}
}
