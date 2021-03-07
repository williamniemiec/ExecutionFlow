package wniemiec.executionflow.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class ThemePanel extends JPanel {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 700L;

	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	public ThemePanel(BorderLayout borderLayout) {
		super(borderLayout);
	}
	
	public ThemePanel() {
		super();
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
        		RenderingHints.KEY_RENDERING, 
        		RenderingHints.VALUE_RENDER_QUALITY
		);

        Color color1 = new Color(245,0,86);
        Color color2 = new Color(78,0,142);
        
        GradientPaint gp = new GradientPaint(
        		100, 100, color1, 
        		getWidth() - 100, getHeight()*2/3, color2
		);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
