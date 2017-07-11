package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

import com.mpc.gui.Bootstrap;

public class Background extends JComponent {

	private static final long serialVersionUID = 1L;
	public String backgroundName;
	public String selectPanel;
	private BufferedImage[] gifFrames;
	private BufferedImage img;
	private Timer timer;
	private int frameCounter = 0;
	private int totalFrames = 0;

	public Background() {
		setName("background");
	}

	public void paintComponent(Graphics g1) {
		this.removeAll();

		if (img == null) {
			try {
				img = ImageIO.read(new File(Bootstrap.resPath + backgroundName + ".gif"));
			} catch (IOException e) {
//				System.out.println(Bootstrap.resPath + backgroundName + "not found");
				return;
			}
		}
		
		if (img == null) return;
		
		Graphics2D g = (Graphics2D) g1;
		BufferedImage background = new BufferedImage(248, 60, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig = background.createGraphics();

		ig.setStroke(new BasicStroke(1));
		ig.setColor(Bootstrap.lcdOn);
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {

				int c = img.getRGB(x, y);
				Color color = new Color(c);
				ig.setColor(Bootstrap.lcdOn);
				if (color.equals(Color.black)) {
					ig.drawLine(x, y, x, y);
				}
				ig.setColor(Bootstrap.lcdOff);
				if (color.equals(Color.white)) {
					ig.drawLine(x, y, x, y);
				}
			}
		}
		g.drawImage(background, AffineTransform.getScaleInstance(2.0, 2.0), this);

	}

	public void setBackgroundName(String s) {
		img = null;
		backgroundName = s;
		repaint();
	}

	// public void startAnimation() {
	// try {
	// InputStream is = new FileInputStream(Bootstrap.resPath
	// + backgroundName + ".gif");
	// GifDecoder gd = new GifDecoder();
	// gd.read(is);
	//
	// totalFrames = gd.getFrameCount();
	// if (totalFrames < 2)
	// return;
	// gifFrames = new BufferedImage[totalFrames];
	// for (int i = 0; i < totalFrames; i++) {
	// gifFrames[i] = gd.getFrame(i);
	// }
	//
	// timer = new Timer(100, this);
	// timer.setInitialDelay(100);
	// timer.start();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// @Override
	// public void actionPerformed(ActionEvent arg0) {
	// img = gifFrames[frameCounter++];
	// repaint();
	// if (frameCounter > totalFrames)
	// frameCounter = 0;
	// }

	public String getBackgroundName() {
		return backgroundName;
	}
}
