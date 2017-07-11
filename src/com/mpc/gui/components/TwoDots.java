package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class TwoDots extends JComponent {

	private static final long serialVersionUID = 1L;
	private boolean[] selected;
	private Color color;
	private boolean[] visible;

	private boolean inverted = false;

	public TwoDots() {
		selected = new boolean[4];
		visible = new boolean[4];
	}

	public void setInverted(boolean b) {
		inverted = b;
		repaint();
	}

	public void setSelected(int i, boolean b) {
		this.selected[i] = b;
		repaint();
	}

	public void setVisible(int i, boolean b) {
		this.visible[i] = b;
		repaint();
	}

	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		BufferedImage twoDots = new BufferedImage(248, 60, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = twoDots.createGraphics();
		ig.setStroke(new BasicStroke(1));

		for (int i = 0; i < 4; i++) {
			if (selected[i]) {
				color = inverted ? Bootstrap.lcdOn : Bootstrap.lcdOff;
			} else {
				color = inverted ? Bootstrap.lcdOff : Bootstrap.lcdOn;
			}

			ig.setColor(color);

			if (i == 0 && visible[i]) {
				if (inverted) {
					ig.setColor(Bootstrap.lcdOn);
					ig.drawLine(25, 19, 73, 19);
					ig.setColor(Bootstrap.lcdOff);
				}
				ig.drawLine(37, 19, 37, 19);
				ig.drawLine(55, 19, 55, 19);
			}

			if (i == 1 && visible[i]) {
				if (inverted) {
					ig.setColor(Bootstrap.lcdOn);
					ig.drawLine(96 + 25, 19, 114 + 55, 19);
					ig.setColor(Bootstrap.lcdOff);
				}
				ig.drawLine(96 + 37, 19, 96 + 37, 19);
				ig.drawLine(114 + 37, 19, 114 + 37, 19);
			}

			if (i == 2 && visible[i]) {
				if (inverted) {
					ig.setColor(Bootstrap.lcdOn);
					ig.drawLine(155 + 25, 21, 173 + 57, 21);
					ig.setColor(Bootstrap.lcdOff);
				}
				ig.drawLine(155 + 37, 21, 155 + 37, 21);
				ig.drawLine(173 + 37, 21, 173 + 37, 21);
			}

			if (i == 3 && visible[i]) {
				if (inverted) {
					ig.setColor(Bootstrap.lcdOn);
					ig.drawLine(155 + 25, 30, 173 + 57, 30);
					ig.setColor(Bootstrap.lcdOff);
				}
				ig.drawLine(155 + 37, 30, 155 + 37, 30);
				ig.drawLine(173 + 37, 30, 173 + 37, 30);
			}
		}
		g.drawImage(twoDots, AffineTransform.getScaleInstance(2, 2), this);
	}
}
