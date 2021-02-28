package wniemiec.executionflow.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Responsible for generating application buttons.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class ThemeButton extends JButton {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 700L;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ThemeButton(String title) {
        super(title);
        setContentAreaFilled(false);
        setFocusPainted(false); 
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());
    }
    
	
	//-------------------------------------------------------------------------
	//		Factories
	//-------------------------------------------------------------------------
    public static ThemeButton createRegularThemeButton(String title) {
    	ThemeButton btn = new ThemeButton(title);
    	
    	btn.setFont(new Font("Arial", Font.BOLD, 14));
    	btn.setPreferredSize(new Dimension(160, 50));
    	
    	return btn;
    }
    
    public static ThemeButton createBigThemeButton(String title) {
    	ThemeButton btn = new ThemeButton(title);
    	
    	btn.setFont(new Font("Arial", Font.BOLD, 30));
    	
    	return btn;
    }

    
    //-------------------------------------------------------------------------
  	//		Methods
  	//-------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        
        if (getModel().isPressed())
        	paintOnClick(g2);
        else if (getModel().isRollover())
        	paintOnHoover(g2);
        else
            paintDefault(g2);
        
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }

	private void paintDefault(final Graphics2D g2) {
		g2.setPaint(new GradientPaint(
		        new Point(0, 0), 
		        new Color(255,0,144),			// e50081
		        new Point(0, getHeight()), 
		        new Color(78,0,142))			// 4e008e
		);
	}

	private void paintOnHoover(final Graphics2D g2) {
		g2.setPaint(new GradientPaint(
		        new Point(0, 0), 
		        new Color(255,0,144),			// e50081
		        new Point(0, getHeight()), 
		        new Color(160,0,90))			// #a0005a
		);
	}

	private void paintOnClick(final Graphics2D g2) {
		g2.setPaint(new GradientPaint(
		        new Point(0, 0),
		        new Color(78,0,142),			// 4e008e
		        new Point(0, getHeight()), 
		        new Color(46,0,85))				// 2e0055
		);
	}
}
