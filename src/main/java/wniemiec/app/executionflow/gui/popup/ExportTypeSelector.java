package wniemiec.app.executionflow.gui.popup;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import wniemiec.app.executionflow.exporter.testpath.TestPathExportType;

/**
 * Responsible for creating a test path export type selector.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.2.0
 */
class ExportTypeSelector extends Selector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private TestPathExportType selectedTestPathExportType;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public ExportTypeSelector(JDialog window) {
		super(window);
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public JPanel create() {
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
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public TestPathExportType getSelectedExportType() {
		return selectedTestPathExportType;
	}
}
