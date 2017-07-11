package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class EnvGraph extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private int[][] coordinates;

	public EnvGraph(int[][] ia) {
		coordinates = ia;
		setVisible(true);
	}

	public void setCoordinates(int[][] ia) {
		coordinates = ia;
		repaint();
	}
	
	public void paintComponent(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		BufferedImage envGraph = new BufferedImage(248, 60,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = envGraph.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(Bootstrap.lcdOn);

		for (int j = 0; j < coordinates.length; j++) {
			ig.drawLine(coordinates[j][0], coordinates[j][1],
					coordinates[j][2], coordinates[j][3]);
		}

		g.drawImage(envGraph, AffineTransform.getScaleInstance(2, 2), this);
	}
}
