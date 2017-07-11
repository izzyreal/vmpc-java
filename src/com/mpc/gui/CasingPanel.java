package com.mpc.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CasingPanel extends JPanel {

	JLabel casing = null;

	public void init() {
		setLocation(10-17, -90);
		setSize(1300, 1000);
		setVisible(true);
		setOpaque(false);
		addImage();

		}

	public void addImage() {
		BufferedImage img;
		try {
			img = ImageIO.read(new File(Bootstrap.resPath + "bg1.png"));
			casing = new JLabel(new ImageIcon(img));
			casing.setLayout(null);
			casing.setLocation(120, -60);
			casing.setSize(1297, 1021);
			casing.setSize(1298, 1022);
		} catch (IOException e) {
			e.printStackTrace();
		}
		add(casing);
	}

}