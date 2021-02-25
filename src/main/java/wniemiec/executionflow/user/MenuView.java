package wniemiec.executionflow.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
		
		setTitle("Execution Flow");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 300, 820, 180);
		setResizable(false);
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("ef_icon.png"));
		} catch (IOException e) {
		}
//		Icon icon = new ImageIcon("ef_icon.png",
//                "a pretty but meaningless splat");
		setIconImage(img);

		JPanel central = new JPanel(new BorderLayout(0, 0));
		central.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		central.setBackground(new Color(25,25,25));
		central.add(createLoggingLevelSelector(), BorderLayout.CENTER);
		central.add(createExportTypeSelector(), BorderLayout.NORTH);
		
		getContentPane().add(central);
	}

	private JPanel createLoggingLevelSelector() {
		JPanel panel = new JPanel();
		panel.setBackground(bgColor);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel.add(createTitle("Logging level"), BorderLayout.NORTH);
		
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
		
		for (JButton btn : initializeOptions()) {
			panelLog.add(btn);
		}
		
		return panelLog;
	}

	private JPanel createExportTypeSelector() {
		JPanel exportTypeSelector;
		exportTypeSelector = new JPanel();
		exportTypeSelector.setBackground(bgColor);
		exportTypeSelector.setLayout(new BorderLayout(0, 0));
		exportTypeSelector.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		
		exportTypeSelector.add(createTitle("Test path export mode"), BorderLayout.NORTH);
		exportTypeSelector.add(createExportTypeOptions(), BorderLayout.SOUTH);
		
		return exportTypeSelector;
	}

	private JPanel createExportTypeOptions() {				
		JRadioButton rdoConsole = createThemeRadioButton("Console", false);
		JRadioButton rdoFile = createThemeRadioButton("File", true);
		
		ButtonGroup rdoGroup = new ButtonGroup();
		rdoGroup.add(rdoConsole);
		rdoGroup.add(rdoFile);
		
		JPanel exportTypeOptions = new JPanel();
		exportTypeOptions.setBackground(bgColor);
		exportTypeOptions.add(rdoConsole);
		exportTypeOptions.add(rdoFile);
		
		return exportTypeOptions;
	}

	private JRadioButton createThemeRadioButton(String label, boolean selected) {
		JRadioButton rdo = new JRadioButton(label);
		rdo.setBackground(bgColor);
		rdo.setForeground(Color.white);
		rdo.setRequestFocusEnabled(false);
		rdo.setBorderPainted(false);
		rdo.setBorder(BorderFactory.createEmptyBorder());
		rdo.setFocusable(false);
		rdo.setSelected(selected);
		
		return rdo;
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
