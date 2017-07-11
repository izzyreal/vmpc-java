package com.mpc.gui.sequencer;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class SelectedEventBar extends JComponent {
	private static final long serialVersionUID = 1L;

	public SelectedEventBar() {
	}

	public void paintComponent(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		BufferedImage selectedEventBar = new BufferedImage(248, 9,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = selectedEventBar.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(Bootstrap.lcdOn);

		ig.drawRect(0, 0, 193, 8);
		ig.fillRect(0, 0, 193, 8);
		g.drawImage(selectedEventBar, AffineTransform.getScaleInstance(2, 2),
				this);
	}
}
