package com.mpc.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import com.mpc.Util;
import com.mpc.controls.KbMapping;

import imagemap.Shape;
import imagemap.ShapeList;

public class KeyLabels {

	private List<JLabel> keyLabels;
	private List<JLabel> keyLabelOutlines;

	private boolean fadingIn = false;
	private boolean keysVisible = false;
	private final static int FADE_INTERVAL = 20;
	
	public KeyLabels() {
		keyLabels = new ArrayList<JLabel>();
		keyLabelOutlines = new ArrayList<JLabel>();
	}
	
	public void addKeyLabels(MainFrame destination) {
		ShapeList sl = destination.getControlPanel().getControlShapes();
		for (int i = 0; i < sl.size(); i++) {
			String s = "";
			Shape shape = sl.get_shape(i);
			if (shape.get_type() != Shape.TYPE_RECT) continue;
			JLabel label = new JLabel() {

				@Override
				public void paintComponent(Graphics g) {
					Graphics2D graphics2d = (Graphics2D) g;
					graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					super.paintComponent(g);
				}
			};
			JLabel2D labelOutline = new JLabel2D();
			label.setFont(Bootstrap.keyFont);
			Stroke outlineStroke = new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

			int x = (int) shape.getRect().getCenterX() - 45;
			int y = (int) shape.getRect().getCenterY() - 102;

			switch (shape.get_href()) {

			case "datawheel":
				s = getKeyString(KbMapping.dataWheelBack) + " / " + getKeyString(KbMapping.dataWheelForward);
				break;

			case "f1":
				s = KeyEvent.getKeyText(KbMapping.f1);
				break;

			case "f2":
				s = KeyEvent.getKeyText(KbMapping.f2);
				break;

			case "f3":
				s = KeyEvent.getKeyText(KbMapping.f3);
				break;

			case "f4":
				s = KeyEvent.getKeyText(KbMapping.f4);
				break;

			case "f5":
				s = KeyEvent.getKeyText(KbMapping.f5);
				break;

			case "f6":
				s = KeyEvent.getKeyText(KbMapping.f6);
				break;

			case "rec":
				s = getKeyString(KbMapping.rec);
				break;

			case "overdub":
				s = getKeyString(KbMapping.overdub);
				break;

			case "stop":
				s = KeyEvent.getKeyText(KbMapping.stop);
				break;

			case "play":
				s = KeyEvent.getKeyText(KbMapping.play);
				break;

			case "playstart":
				s = getKeyString(KbMapping.playstart);
				break;

			case "shift":
				s = KeyEvent.getKeyText(KbMapping.numPadShift);
				break;

			case "enter":
				s = KeyEvent.getKeyText(KbMapping.numPadEnter);
				break;

			case "banka":
				s = KeyEvent.getKeyText(KbMapping.bankKeys[0]);
				break;

			case "bankb":
				s = KeyEvent.getKeyText(KbMapping.bankKeys[1]);
				break;

			case "bankc":
				s = KeyEvent.getKeyText(KbMapping.bankKeys[2]);
				break;

			case "bankd":
				s = KeyEvent.getKeyText(KbMapping.bankKeys[3]);
				break;

			case "fulllevel":
				s = getKeyString(KbMapping.fullLevel);
				break;

			case "16levels":
				s = getKeyString(KbMapping.sixteenLevels);
				break;

			case "nextseq":
				s = getKeyString(KbMapping.nextSeq);
				break;

			case "trackmute":
				s = getKeyString(KbMapping.trackMute);
				break;

			case "prevstepevent":
				s = getKeyString(KbMapping.prevStepEvent);
				break;

			case "nextstepevent":
				s = getKeyString(KbMapping.nextStepEvent);
				break;

			case "goto":
				s = getKeyString(KbMapping.goTo);
				break;

			case "prevbarstart":
				s = getKeyString(KbMapping.prevBarStart);
				break;

			case "nextbarend":
				s = getKeyString(KbMapping.nextBarEnd);
				break;

			case "taptemponoterepeat":
				s = getKeyString(KbMapping.tap);
				break;

			case "mainscreen":
				s = KeyEvent.getKeyText(KbMapping.mainscreen);
				break;

			case "openwindow":
				s = getKeyString(KbMapping.openWindow);
				break;

			case "undoseq":
				s = KeyEvent.getKeyText(KbMapping.undoSeq);
				break;

			case "erase":
				s = KeyEvent.getKeyText(KbMapping.erase);
				break;

			case "notevariationafter":
				s = KeyEvent.getKeyText(KbMapping.after);
				break;
			}

			for (int num = 0; num < 10; num++)
				if (shape.get_href().equals("" + num)) s = getKeyString(KbMapping.numPad[num]);

			for (int pad = 0; pad < 16; pad++)
				if (shape.get_href().equals("pad" + (pad + 1))) s = getKeyString(KbMapping.padKeys[pad]);

			if (s.length() == 0) continue;
			label.setText(s);
			labelOutline.setText(s);
			labelOutline.setStroke(outlineStroke);
			labelOutline.setBorder(null);
			label.setBorder(null);
			labelOutline.setBorder(new EmptyBorder(0, 10, 1, -10));
			label.setForeground(new Color(0, 0, 0, 0));
			labelOutline.setForeground(new Color(255, 255, 255, 0));
			labelOutline.setOutlineColor(new Color(255, 255, 255, 0));
			int x1 = x - (label.getText().length() * (Bootstrap.keyFontSize / 3)) - 3;
			label.setLocation(x1, y + 30);
			labelOutline.setLocation(x1 + 20, y - 55);
			label.setSize(label.getText().length() * 14, Bootstrap.keyFontSize);
			labelOutline.setSize((label.getText().length() * 10) + 16, Bootstrap.keyFontSize + 16);
			labelOutline.setFont(Bootstrap.keyFont);
			label.setBounds(label.getBounds().x + 40, label.getBounds().y + 20, label.getBounds().width + 30,
					label.getBounds().height + 30);
			labelOutline.setBounds(label.getBounds().x - 9, label.getBounds().y - 13, label.getBounds().width + 30,
					label.getBounds().height + 30);
			destination.add(label);
			destination.add(labelOutline);
			keyLabels.add(label);
			keyLabelOutlines.add(labelOutline);

		}

	}

	private String getKeyString(int keyCode) {
		return String.copyValueOf(new char[] { (char) keyCode });
	}

	public void displayKeys() {
		if (keysVisible) return;
		fadingIn = true;
		keysVisible = true;
		new Thread() {

			@Override
			public void run() {
				JLabel label = null;
				JLabel2D labelOutline = null;
				for (int j = 0; j < 256; j += 16) {
					for (int i = 0; i < keyLabels.size(); i++) {
						label = keyLabels.get(i);
						labelOutline = (JLabel2D) keyLabelOutlines.get(i);

						if (j == 0) {
							label.setVisible(true);
							labelOutline.setVisible(true);
						}

						if (j >= 255) {
							label.setForeground(new Color(0, 0, 0, 255));
							labelOutline.setOutlineColor(new Color(255, 255, 255, (int) (255 / 1.3)));
							continue;
						}

						label.setForeground(new Color(0, 0, 0, j));
						labelOutline.setOutlineColor(new Color(255, 255, 255, (int) (j / 1.3)));
					}
					Util.sleep(FADE_INTERVAL);
				}
				fadingIn = false;
			}
		}.start();

	}

	public void removeKeys() {
		if (!keysVisible) return;
		new Thread() {

			@Override
			public void run() {

				while (fadingIn)
					Util.sleep(FADE_INTERVAL);

				JLabel label = null;
				JLabel2D labelOutline = null;
				for (int j = 255; j > -16; j -= 16) {
					int newValue = j;
					for (int i = 0; i < keyLabels.size(); i++) {
						label = keyLabels.get(i);
						labelOutline = (JLabel2D) keyLabelOutlines.get(i);
						if (newValue < 0) {
							label.setVisible(false);
							labelOutline.setVisible(false);
							continue;
						}
						label.setForeground(new Color(0, 0, 0, newValue));
						labelOutline.setOutlineColor(new Color(255, 255, 255, (int) (newValue / 1.3)));
					}
					Util.sleep(FADE_INTERVAL);
				}

				keysVisible = false;
			}
		}.start();
	}

}
