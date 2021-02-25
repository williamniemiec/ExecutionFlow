package wniemiec.executionflow.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MenuView extends JFrame {

	private Color bgColor = new Color(80,80,80);
	
	public static void main(String[] args) {
//		MenuView view = new MenuView();
//		view.open();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuView view = new MenuView();
					view.open();
					view.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void open() {
		
		setTitle("Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 300, 820, 180);
		setResizable(false);
		JPanel central = new JPanel(new BorderLayout(0, 0));
		central.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		central.setBackground(new Color(25,25,25));
		
		getContentPane().add(central);
		
		JPanel panel = new JPanel();
		panel.setBackground(bgColor);
		central.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		JLabel lblLog = new JLabel("Logging level");
		lblLog.setForeground(Color.white);
		
		panel.add(lblLog, BorderLayout.NORTH);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		JPanel panelLog = new JPanel();
		panelLog.setBackground(bgColor);
		panel.add(panelLog, BorderLayout.CENTER);
		panelLog.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		for (JButton btn : initializeOptions()) {
			panelLog.add(btn);
		}
		
		JPanel panel_1 = new JPanel();
		central.add(panel_1, BorderLayout.NORTH);
		panel_1.setBackground(bgColor);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Test path export mode");
		lblNewLabel.setForeground(Color.white);
		panel_1.add(lblNewLabel, BorderLayout.NORTH);
		panel_1.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		
		
		
		
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);
		panel_2.setBackground(bgColor);
		
		ButtonGroup rdoGroup = new ButtonGroup();
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Console");
		rdbtnNewRadioButton.setBackground(bgColor);
		rdbtnNewRadioButton.setForeground(Color.white);
		rdbtnNewRadioButton.setRequestFocusEnabled(false);
		rdbtnNewRadioButton.setBorderPainted(false);
		rdbtnNewRadioButton.setBorder(BorderFactory.createEmptyBorder());
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("File");
		rdbtnNewRadioButton_1.setBackground(bgColor);
		rdbtnNewRadioButton_1.setForeground(Color.white);
		rdbtnNewRadioButton_1.setRequestFocusEnabled(false);
		rdbtnNewRadioButton_1.setBorderPainted(false);
		rdbtnNewRadioButton_1.setBorder(BorderFactory.createEmptyBorder());
		
		rdoGroup.add(rdbtnNewRadioButton);
		rdoGroup.add(rdbtnNewRadioButton_1);
		
		panel_2.add(rdbtnNewRadioButton);
		panel_2.add(rdbtnNewRadioButton_1);
		
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
}
