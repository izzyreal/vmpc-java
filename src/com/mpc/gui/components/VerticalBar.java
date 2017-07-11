package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class VerticalBar extends JComponent {

	private static final long serialVersionUID = 1L;
	private int value;
	private Color color = Bootstrap.lcdOn;


	public void setValue(int value) {
		this.value = value;
		repaint();
	}
	
	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

	public void paintComponent(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		BufferedImage horizontalBar = new BufferedImage(6, 40,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = horizontalBar.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(color);
		int valuePixels = (int) ((value - 2) / 3.0);
		

		if (value > 2) {
			ig.drawRect(0, 32-valuePixels, 3, valuePixels);
			ig.fillRect(0, 32-valuePixels, 3, valuePixels);
		}
		g.drawImage(horizontalBar, AffineTransform.getScaleInstance(2, 2), this);
	}
}
