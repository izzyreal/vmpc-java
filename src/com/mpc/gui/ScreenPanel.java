package com.mpc.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")

public class ScreenPanel extends JPanel {
	
	private BufferedImage getScreenShot(Component component) {
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
				BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = image.getGraphics();
		component.paint(g);
		return image;
	}

	public ByteArrayInputStream getScreenPng() {
		BufferedImage img = getScreenShot(this);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
	  
			ImageIO.write(img, "png", baos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return new ByteArrayInputStream(baos.toByteArray());
	}

	public BufferedImage getScreenImg() {
		return getScreenShot(this);
	}
	
		
	public void init() {
		setLayout(null);
		setLocation (126-17,20);
		setSize (496, 120);
		setVisible(true);
		setOpaque(true);
		setBackground(Bootstrap.lcdOff);
			
	}
	
}
