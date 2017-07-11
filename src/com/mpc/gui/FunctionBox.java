package com.mpc.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class FunctionBox extends JPanel {

	private static final long serialVersionUID = 1L;
	String name0, name1, name2, name3, name4, name5;
	int box0, box1, box2, box3, box4, box5;
	boolean[] enabled = { true, true, true, true, true, true };
	String[] names;
	
	public FunctionBox(String screenName, String panelName, JSONArray fbLabels, JSONArray fbTypes) throws FileNotFoundException,
			IOException, ParseException {

		setBackground(Bootstrap.lcdOn);
		setVisible(true);
		setSize(244 * 2, 9 * 2);
		setLocation(2 * 2, 51 * 2);

		int[] fbTypesArray = new int[fbTypes.size()];

		for (int i = 0, count = fbTypes.size(); i < count; i++) {
			if (fbTypes.get(i) == null) continue;
			fbTypesArray[i] = ((Long) fbTypes.get(i)).intValue();
		}

		box0 = fbTypesArray[0];
		box1 = fbTypesArray[1];
		box2 = fbTypesArray[2];
		box3 = fbTypesArray[3];
		box4 = fbTypesArray[4];
		box5 = fbTypesArray[5];

		String[] fbLabelsArray = new String[fbLabels.size()];

		for (int i = 0, count = fbLabels.size(); i < count; i++) {
			if (fbLabels.get(i) == null) continue;
			fbLabelsArray[i] = fbLabels.get(i).toString();
		}
		
		int counter = 0;
		for (boolean b : enabled) {
			if (!b) {
				fbLabelsArray[counter] = null;
			}
			counter++;
		}
		
		name0 =fbLabelsArray[0];
		name1 =fbLabelsArray[1];
		name2 =fbLabelsArray[2];
		name3 =fbLabelsArray[3];
		name4 =fbLabelsArray[4];
		name5 =fbLabelsArray[5];

		 String[] temp = { name0, name1, name2, name3, name4, name5 };
		 names = temp;
	}

	public void disable(int i) {
		if (enabled[i] == false) return;
		enabled[i] = false;
		repaint();
	}
	
	public void enable(int i) {
		if (enabled[i] == true) return;
		enabled[i] = true;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g1) {

		Graphics2D g = (Graphics2D) g1;

		BufferedImage functionBox = // Create an off-screen image
		new BufferedImage(244, 9, BufferedImage.TYPE_INT_ARGB);

		Graphics2D ig = functionBox.createGraphics();

		int[] xPos = { 0, 41, 82, 123, 164, 205 };

		int[] types = { box0, box1, box2, box3, box4, box5 };
		
	
		for (int i = 0; i < xPos.length; i++) {

			if (names[i] == null) continue;
			if (!enabled[i]) continue;
			
			int stringSize = names[i].length();
			int lengthInPixels = stringSize*6;
			int offset = ((37 - lengthInPixels)/2)+2;
			Color border = null, bg = null, label = null;
			
			
			if (types[i] == 0) {
				border = Bootstrap.lcdOn;
				bg = Bootstrap.lcdOn;
				label = Bootstrap.lcdOff;				
			}
			
			if (types[i] == 1) {
				border = Bootstrap.lcdOn;
				bg = Bootstrap.lcdOff;
				label = Bootstrap.lcdOn;				
			}
			
			if (types[i] == 2) {
				border = Bootstrap.lcdOff;
				bg = Bootstrap.lcdOff;
				label = Bootstrap.lcdOn;				
			}
			
			ig.setColor(bg);
			ig.fillRect(xPos[i], 0, 38, 8);
			
			ig.setColor(border);
			ig.setStroke(new BasicStroke(1));
			ig.drawRect(xPos[i], 0, 38, 8);
			
			
			ig.setColor(label);
			ig.setFont(Bootstrap.fontsmall);
			ig.drawString(names[i], xPos[i]+offset, 9);
	
		}
		g.drawImage(functionBox, AffineTransform.getScaleInstance(2, 2), this);
	}
}