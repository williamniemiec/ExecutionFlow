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

public class ThemeButton extends JButton {
	
    public ThemeButton(String title) {
        super(title);
        setContentAreaFilled(false);
        setFocusPainted(false); 
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 30));
        setBorder(BorderFactory.createEmptyBorder());
    }
    
    public ThemeButton(String title, boolean small) {
    	this(title);
    	//setBounds(0, 0, 100, 30);
    	setFont(new Font("Arial", Font.BOLD, 14));
    	setPreferredSize(new Dimension(160, 50));
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
