package com.mpc.gui.other;

import java.awt.Color;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;

public class OthersGui extends Observable {

	private int tapAveraging = 2;
	private int contrast = 10; // 0..20, made up range

	public void setTapAveraging(int i) {
		if (i < 0 || i > 8) return;
		tapAveraging = i;
		setChanged();
		notifyObservers("tapaveraging");
	}

	public int getTapAveraging() {
		return tapAveraging;
	}

	public void setContrast(int i) {
		if (i < 0 || i > 50) return;
		contrast = i;
		Color orig = Bootstrap.lcdOnOriginal;
		float f = (float) (1f - (contrast / 50.0));
		int red = orig.getRed() + (int) ((255 - orig.getRed()) * f);
		int green = orig.getGreen() + (int) ((255 - orig.getGreen()) * f);
		int blue = orig.getBlue() + (int) ((255 - orig.getBlue()) * f);
		Bootstrap.lcdOn = new Color(red, green, blue);
		Bootstrap.getGui().getMainFrame().repaint();
		String focus = Util.getFocus();
		JTextField tf = Bootstrap.getGui().getMainFrame().lookupTextField(focus);
		tf.setBackground(Bootstrap.lcdOn);
		JLabel label = Bootstrap.getGui().getMainFrame().lookupLabel(focus);
		label.setForeground(Bootstrap.lcdOn);
		tf.repaint();
		label.repaint();

		setChanged();
		notifyObservers("contrast");
	}

	public int getContrast() {
		return contrast;
	}

}
