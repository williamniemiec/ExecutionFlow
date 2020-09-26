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
public class Control 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private transient static JFrame instance = null;
	private final static int MAIN_FRAME_WIDTH = 325;
	private final static int MAIN_FRAME_HEIGHT = 100;
	private final static int MAIN_FRAME_X = 100;
	private final static int MAIN_FRAME_Y = 100;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private Control()
	{}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays control window.
	 */
	public static void open()
	{
		if (instance == null)
			createWindow();
			
		instance.setVisible(true);
	}
	
	/**
	 * Closes control window.
	 */
	public static void close()
	{
		instance.dispose();
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
		instance = new JFrame("Execution Flow - Control");
		instance.add(stop);
		instance.setBounds(MAIN_FRAME_X, MAIN_FRAME_Y, MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
	}
}
