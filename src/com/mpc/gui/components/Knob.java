package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class Knob extends JComponent {

	private static final long serialVersionUID = 1L;
	private int value;
	private Color color;

	public void setValue(int value) {
		this.value = value;
		repaint();
	}

	public void setColor(Color color) {
		this.color = color;
		repaint();
	}
	
	public void paintComponent(Graphics g1) {
		if (color == null) color = Bootstrap.lcdOn;
		Graphics2D g = (Graphics2D) g1;

		BufferedImage horizontalBar = new BufferedImage(14, 14,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = horizontalBar.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(color);
		ig.drawLine(0, 3, 0, 7);
		ig.drawLine(0, 7, 3, 10);
		ig.drawLine(3, 10, 7, 10); 
		ig.drawLine(7, 10, 10, 7); 
		ig.drawLine(10, 7, 10, 3);
		ig.drawLine(10, 3, 7, 0); 
		ig.drawLine(7, 0, 3, 0); 
		ig.drawLine(3, 0, 0, 3);

		int angle = (int) ((value *3.1)-245); // -247 - 
		float radius = 4.95f;
		int radiusInt = (int) radius;
		int x = (int) (radius * Math.cos(Math.toRadians(angle)));
		int y = (int) (radius * Math.sin(Math.toRadians(angle)));
		ig.drawLine(5, 5, x+radiusInt+1, y+radiusInt+1);
		
		g.drawImage(horizontalBar, AffineTransform.getScaleInstance(2, 2), this);
	}
}
