package wniemiec.executionflow.user;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
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
	private static final String TITLE = "Execution Flow - Remote control";
	
	
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
		JButton stop = new ThemeButton("Stop");
		
		stop.setFocusPainted(false);
		stop.addActionListener(event ->
				Runtime.getRuntime().exit(-1)
		);
		
		return stop;
	}
	
	private static class ThemeButton extends JButton {
        private ThemeButton(String title) {
            super(title);
            setContentAreaFilled(false);
            setFocusPainted(false); 
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 30));
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g.create();
            if (getModel().isPressed()) {
            	g2.setPaint(new GradientPaint(
                        new Point(0, 0), 
                        new Color(78,0,142),
                        new Point(0, getHeight()), 
                        new Color(78,0,142))
    			);
            } 
            else if (getModel().isRollover()) {
            	g2.setPaint(new GradientPaint(
                        new Point(0, 0), 
                        new Color(255,0,144),
                        new Point(0, getHeight()), 
                        new Color(255,0,144))
    			);
          
            }
            else {
	            g2.setPaint(new GradientPaint(
	                    new Point(0, 0), 
	                    new Color(255,0,144),
	                    new Point(0, getHeight()), 
	                    new Color(78,0,142))
	    		);
            }
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
    }
	
	public static void main(String[] args) {
		RemoteControl.open();
	}
}
