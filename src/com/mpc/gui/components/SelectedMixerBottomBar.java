package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class SelectedMixerBottomBar extends JComponent {
	
	private static final long serialVersionUID = 1L;

	public void paintComponent(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		BufferedImage horizontalBar = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = horizontalBar.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(Bootstrap.lcdOn);

		ig.drawRect(0, 0, 14, 34);
		ig.fillRect(0, 0, 14, 34);
		g.drawImage(horizontalBar, AffineTransform.getScaleInstance(2, 2), this);
	}
}
