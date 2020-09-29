package executionFlow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;


/**
 * Responsible for application control by the user, allowing the user to send 
 * commands to the application.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		5.1.0
 */
public class RemoteControl 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private volatile static JFrame window = null;
	private final static int MAIN_FRAME_WIDTH = 365;
	private final static int MAIN_FRAME_HEIGHT = 100;
	private final static int MAIN_FRAME_X = 100;
	private final static int MAIN_FRAME_Y = 100;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private RemoteControl()
	{}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays control window. It will only creates a new window if one has 
	 * not been created.
	 */
	public static void open()
	{
		if (window == null)
			createWindow();
			
		window.setVisible(true);
	}
	
	/**
	 * Closes control window.
	 */
	public static void close()
	{
		window.dispose();
	}
	
	/**
	 * Creates control window.
	 */
	private static void createWindow()
	{		
		// Stop button creation
		JButton stop = new JButton("Stop");
		
		stop.setFocusPainted(false);
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runtime.getRuntime().exit(-1);
			}
		});
				
		// Window creation
		window = new JFrame("Execution Flow - Remote control");
		window.add(stop);
		window.setBounds(MAIN_FRAME_X, MAIN_FRAME_Y, MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
		window.setResizable(false);
		window.toFront();
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}
