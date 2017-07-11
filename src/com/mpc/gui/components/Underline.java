package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class Underline extends JComponent {

	private static final long serialVersionUID = 1L;

	private boolean[] states = new boolean[16];

	public void paintComponent(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		BufferedImage underline = new BufferedImage((6 * 16) + 1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = underline.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(Bootstrap.lcdOn);
		for (int i = 0; i < 16; i++) {
			if (!states[i]) {
				continue;
			}
			ig.drawLine(i * 6, 0, (i * 6) + 6, 0);
		}
		g.drawImage(underline, AffineTransform.getScaleInstance(2, 2), this);
	}

	public void setState(int i, boolean b) {
		states[i] = b;
		repaint();
	}
}
