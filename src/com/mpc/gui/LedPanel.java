package com.mpc.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LedPanel extends JPanel {

	private JLabel fullLevel;
	private JLabel sixteenLevels;
	private JLabel nextSeq;
	private JLabel trackMute;

	private JLabel padBankA;
	private JLabel padBankB;
	private JLabel padBankC;
	private JLabel padBankD;

	private JLabel after;

	private JLabel undoSeq;
	private JLabel rec;
	private JLabel overDub;
	private JLabel play;

	public void init() {
		setLayout(null);
		ImageIcon ledGreen = new ImageIcon(Bootstrap.resPath + "led_green.png");
		ImageIcon ledRed = new ImageIcon(Bootstrap.resPath + "led_red.png");

		fullLevel = new JLabel();
		fullLevel.setIcon(ledGreen);
		fullLevel.setSize(10, 10);
		fullLevel.setVisible(false);

		sixteenLevels = new JLabel();
		sixteenLevels.setIcon(ledGreen);
		sixteenLevels.setSize(10, 10);
		sixteenLevels.setVisible(false);

		nextSeq = new JLabel();
		nextSeq.setIcon(ledGreen);
		nextSeq.setSize(10, 10);
		nextSeq.setVisible(false);

		trackMute = new JLabel();
		trackMute.setIcon(ledGreen);
		trackMute.setSize(10, 10);
		trackMute.setVisible(false);

		padBankA = new JLabel();
		padBankA.setIcon(ledGreen);
		padBankA.setSize(10, 10);
		// padBankA.setVisible(false);

		padBankB = new JLabel();
		padBankB.setIcon(ledGreen);
		padBankB.setSize(10, 10);
		padBankB.setVisible(false);

		padBankC = new JLabel();
		padBankC.setIcon(ledGreen);
		padBankC.setSize(10, 10);
		padBankC.setVisible(false);

		padBankD = new JLabel();
		padBankD.setIcon(ledGreen);
		padBankD.setSize(10, 10);
		padBankD.setVisible(false);

		after = new JLabel();
		after.setIcon(ledGreen);
		after.setSize(10, 10);
		after.setVisible(false);

		play = new JLabel();
		play.setIcon(ledGreen);
		play.setSize(10, 10);
		play.setVisible(false);

		undoSeq = new JLabel();
		undoSeq.setIcon(ledRed);
		undoSeq.setSize(10, 10);
		undoSeq.setVisible(false);

		rec = new JLabel();
		rec.setIcon(ledRed);
		rec.setSize(10, 10);
		rec.setVisible(false);

		overDub = new JLabel();
		overDub.setIcon(ledRed);
		overDub.setSize(10, 10);
		overDub.setVisible(false);

		padBankA.setLocation(958 + 10, 298 + 14);
		padBankB.setLocation(1041 + 10, 298 + 14);
		padBankC.setLocation(1124 + 10, 297 + 14);
		padBankD.setLocation(1206 + 10, 296 + 14);

		fullLevel.setLocation(791 + 10, 217 + 14);
		sixteenLevels.setLocation(874 + 10, 216 + 14);
		nextSeq.setLocation(791 + 10, 298 + 14);
		trackMute.setLocation(875 + 10, 298 + 14);

		after.setLocation(103 + 10, 601 + 14);

		undoSeq.setLocation(226 + 10, 686 + 14);
		rec.setLocation(214 + 10, 833 + 14);
		overDub.setLocation(294 + 10, 833 + 14);
		play.setLocation(451 + 10, 830 + 14);

		this.add(padBankA);
		this.add(padBankB);
		this.add(padBankC);
		this.add(padBankD);

		this.add(fullLevel);
		this.add(sixteenLevels);
		this.add(nextSeq);
		this.add(trackMute);

		this.add(after);

		this.add(undoSeq);
		this.add(rec);
		this.add(overDub);
		this.add(play);
		this.setLocation(0 - 17, -100);
		setSize(1300, 1000);
		setVisible(true);
		setOpaque(false);

	}

	public void setPadBankA(boolean b) {
		padBankA.setVisible(b);
	}

	public void setPadBankB(boolean b) {
		padBankB.setVisible(b);
	}

	public void setPadBankC(boolean b) {
		padBankC.setVisible(b);
	}

	public void setPadBankD(boolean b) {
		padBankD.setVisible(b);
	}

	public void setFullLevel(boolean b) {
		fullLevel.setVisible(b);
	}

	public void setSixteenLevels(boolean b) {
		sixteenLevels.setVisible(b);
	}

	public void setNextSeq(boolean b) {
		nextSeq.setVisible(b);
	}

	public void setTrackMute(boolean b) {
		trackMute.setVisible(b);
	}

	public void setAfter(boolean b) {
		after.setVisible(b);
	}

	public void setRec(boolean b) {
		rec.setVisible(b);
	}

	public void setOverDub(boolean b) {
		overDub.setVisible(b);
	}

	public void setPlay(boolean b) {
		play.setVisible(b);
	}

	public void setUndoSeq(boolean b) {
		undoSeq.setVisible(b);
	}

}
