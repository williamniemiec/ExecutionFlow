package wniemiec.app.executionflow.gui.popup;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import wniemiec.app.executionflow.exporter.testpath.TestPathExportType;
import wniemiec.app.executionflow.gui.AppIcon;
import wniemiec.app.executionflow.gui.ThemePanel;
import wniemiec.io.consolex.LogLevel;

/**
 * Responsible for getting user preferences.
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
	private final LoggingLevelSelector loggingLevelSelector;
	private final ExportTypeSelector exportTypeSelector;
	private final AssertProcessingSelector assertProcessingSelector;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public MainSelector() {
		loggingLevelSelector = new LoggingLevelSelector(this);
		exportTypeSelector = new ExportTypeSelector(this);
		assertProcessingSelector = new AssertProcessingSelector(this);
	}
	
	
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
		
		JPanel selectorsPanel = new JPanel(new BorderLayout(0, 0));
		
		selectorsPanel.add(assertProcessingSelector.create(), BorderLayout.NORTH);
		selectorsPanel.add(exportTypeSelector.create(), BorderLayout.SOUTH);
		
		body.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		body.add(selectorsPanel, BorderLayout.NORTH);
		body.add(loggingLevelSelector.create(), BorderLayout.CENTER);
		
		return body;
	}
	
	private boolean wasLoggingLevelSelected() {
		return (loggingLevelSelector.getSelectedLogging() != null);
	}

	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public TestPathExportType getSelectedTestPathExportType() {
		return exportTypeSelector.getSelectedExportType();
	}
	
	public LogLevel getSelectedLoggingLevel() {
		return loggingLevelSelector.getSelectedLogging();
	}

	public boolean getShouldComputeTestPathOfFailingAsserts() {
		return assertProcessingSelector.getShouldComputeTestPathOfFailingAsserts();
	}
}
