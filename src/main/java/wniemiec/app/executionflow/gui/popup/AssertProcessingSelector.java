package wniemiec.app.executionflow.gui.popup;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Responsible for creating a selector that decides whether to compute the test
 * path of asserts that fail or not.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.2.0
 */
class AssertProcessingSelector extends Selector {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private boolean shouldComputeTestPathOfFailingAsserts;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public AssertProcessingSelector(JDialog window) {
		super(window);
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public JPanel create() {
		JPanel selectionPanel = new JPanel();
		
		selectionPanel.setBackground(bgColor);
		selectionPanel.setLayout(new BorderLayout(0, 0));
		selectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		selectionPanel.add(createTitle("Do you want to compute test path of failing asserts?"), BorderLayout.NORTH);
		selectionPanel.add(createAssertProcessingOptions(), BorderLayout.SOUTH);
		
		return selectionPanel;
	}

	private JPanel createAssertProcessingOptions() {				
		JRadioButton rdoYes = createYesRdoButton();
		JRadioButton rdoNo = createNoRdoButton();
		
		ButtonGroup rdoGroup = new ButtonGroup();
		rdoGroup.add(rdoYes);
		rdoGroup.add(rdoNo);
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBackground(bgColor);
		optionsPanel.add(rdoYes);
		optionsPanel.add(rdoNo);
		
		return optionsPanel;
	}
	
	private JRadioButton createYesRdoButton() {
		JRadioButton rdoYes = createThemeRadioButton("Yes");
		
		rdoYes.addActionListener(event -> 
			shouldComputeTestPathOfFailingAsserts = true
		);
		
		rdoYes.doClick();
		
		return rdoYes;
	}
	
	private JRadioButton createNoRdoButton() {
		JRadioButton rdoNo = createThemeRadioButton("No");
		
		rdoNo.addActionListener(event -> 
			shouldComputeTestPathOfFailingAsserts = false
		);
		
		return rdoNo;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public boolean getShouldComputeTestPathOfFailingAsserts() {
		return shouldComputeTestPathOfFailingAsserts;
	}
}
