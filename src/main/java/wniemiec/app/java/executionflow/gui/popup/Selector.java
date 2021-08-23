package wniemiec.app.java.executionflow.gui.popup;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import wniemiec.app.java.executionflow.gui.ThemeButton;

/**
 * Responsible for creating a selector.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		7.2.0
 */
public abstract class Selector {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected Color bgColor = new Color(20,20,20);
	private JDialog window;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	protected Selector(JDialog window) {
		this.window = window;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public abstract JPanel create();
	
	protected JLabel createTitle(String title) {
		JLabel lblTitle = new JLabel(title);
		lblTitle.setForeground(Color.white);
		
		return lblTitle;
	}
	
	protected JRadioButton createThemeRadioButton(String label) {
		JRadioButton rdo = new JRadioButton(label);
		
		rdo.setBackground(bgColor);
		rdo.setForeground(Color.white);
		rdo.setRequestFocusEnabled(false);
		rdo.setBorderPainted(false);
		rdo.setBorder(BorderFactory.createEmptyBorder());
		rdo.setFocusable(false);
		
		return rdo;
	}
	
	protected JButton createHTMLButton(String content) {
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
	
	protected void closeWindow() {
		window.dispose();
	}
}
