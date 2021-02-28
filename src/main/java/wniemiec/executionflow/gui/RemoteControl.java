package wniemiec.executionflow.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;


/**
 * Responsible for application control by the user, allowing the user to send 
 * commands to the application.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		5.1.0
 */
public class RemoteControl {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static JFrame window = null;
	private static final int MAIN_FRAME_WIDTH = 365;
	private static final int MAIN_FRAME_HEIGHT = 100;
	private static final int MAIN_FRAME_X = 100;
	private static final int MAIN_FRAME_Y = 100;
	private static final String TITLE = "Execution Flow";
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private RemoteControl() {
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Displays control window. It will only creates a new window if one has 
	 * not been created.
	 */
	public static void open() {
		if (window == null)
			createWindow();
			
		window.setVisible(true);
	}
	
	/**
	 * Closes control window.
	 */
	public static void close() {
		if (window == null)
			return;
		
		window.dispose();
	}
	
	/**
	 * Creates control window.
	 */
	private static void createWindow() {		
		window = new JFrame(TITLE);
		
		window.add(createStopButton());
		window.setBounds(
				MAIN_FRAME_X, 
				MAIN_FRAME_Y, 
				MAIN_FRAME_WIDTH, 
				MAIN_FRAME_HEIGHT
		);
		window.setResizable(false);
		window.toFront();
		window.setDefaultCloseOperation(
				WindowConstants.DO_NOTHING_ON_CLOSE
		);
	}

	private static JButton createStopButton() {
		JButton stop = new ThemeButton("STOP");
		
		stop.setFocusPainted(false);
		stop.addActionListener(event ->
				Runtime.getRuntime().exit(-1)
		);
		
		return stop;
	}
}
