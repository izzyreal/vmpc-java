package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sequencer.Sequencer;

public class TrMuteObserver implements Observer {

	private Sequencer sequencer;
	private SamplerGui samplerGui;
	private JTextField sqField;
	private JTextField now0Field;
	private JTextField now1Field;
	private JTextField now2Field;

	private JLabel[] tracks = new JLabel[16];

	private JLabel tr1;
	private JLabel tr2;
	private JLabel tr3;
	private JLabel tr4;
	private JLabel tr5;
	private JLabel tr6;
	private JLabel tr7;
	private JLabel tr8;
	private JLabel tr9;
	private JLabel tr10;
	private JLabel tr11;
	private JLabel tr12;
	private JLabel tr13;
	private JLabel tr14;
	private JLabel tr15;
	private JLabel tr16;

	private JLabel trackNumbers;
	private JLabel bank;

	public TrMuteObserver(Sequencer sequencer, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sequencer = sequencer;
		sequencer.deleteObservers();
		sequencer.addObserver(this);

		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		sqField = mainFrame.lookupTextField("sq");
		now0Field = mainFrame.lookupTextField("now0");
		now1Field = mainFrame.lookupTextField("now1");
		now2Field = mainFrame.lookupTextField("now2");

		tr1 = mainFrame.lookupLabel("1");
		tr2 = mainFrame.lookupLabel("2");
		tr3 = mainFrame.lookupLabel("3");
		tr4 = mainFrame.lookupLabel("4");
		tr5 = mainFrame.lookupLabel("5");
		tr6 = mainFrame.lookupLabel("6");
		tr7 = mainFrame.lookupLabel("7");
		tr8 = mainFrame.lookupLabel("8");
		tr9 = mainFrame.lookupLabel("9");
		tr10 = mainFrame.lookupLabel("10");
		tr11 = mainFrame.lookupLabel("11");
		tr12 = mainFrame.lookupLabel("12");
		tr13 = mainFrame.lookupLabel("13");
		tr14 = mainFrame.lookupLabel("14");
		tr15 = mainFrame.lookupLabel("15");
		tr16 = mainFrame.lookupLabel("16");

		trackNumbers = mainFrame.lookupLabel("tracknumbers");
		bank = mainFrame.lookupLabel("bank");

		tracks[0] = tr1;
		tracks[1] = tr2;
		tracks[2] = tr3;
		tracks[3] = tr4;
		tracks[4] = tr5;
		tracks[5] = tr6;
		tracks[6] = tr7;
		tracks[7] = tr8;
		tracks[8] = tr9;
		tracks[9] = tr10;
		tracks[10] = tr11;
		tracks[11] = tr12;
		tracks[12] = tr13;
		tracks[13] = tr14;
		tracks[14] = tr15;
		tracks[15] = tr16;

		sequencer.deleteObservers();
		sequencer.addObserver(this);

		for (int i = 0; i < 16; i++) {
			displayTrack(i);
			setOpaque(i);
			setTrackColor(i);
			sequencer.getActiveSequence().getTrack(i).deleteObservers();
			sequencer.getActiveSequence().getTrack(i).addObserver(this);
		}

		displaySq();
		displayNow0();
		displayNow1();
		displayNow2();
		displayBank();
		displayTrackNumbers();
	}

	private int bankoffset() {
		return samplerGui.getBank() * 16;
	}

	private void displayBank() {
		String[] letters = { "A", "B", "C", "D" };
		bank.setText(letters[samplerGui.getBank()]);
	}

	private void displayTrackNumbers() {
		String[] trn = { "01-16", "17-32", "33-48", "49-64" };
		trackNumbers.setText(trn[samplerGui.getBank()]);
	}

	private void displaySq() {
		sqField.setText(String.format("%02d-", (sequencer.getActiveSequenceIndex() + 1))
				+ sequencer.getActiveSequence().getName());
	}

	private void setOpaque(int i) {
		tracks[i].setOpaque(true);
	}

	private void displayTrack(int i) {
		tracks[i].setText(sequencer.getActiveSequence().getTrack(i + bankoffset()).getName().substring(0, 8));
	}

	private void setTrackColor(int i) {
		if (sequencer.isSoloEnabled()) {
			if (i + bankoffset() == sequencer.getActiveTrackIndex()) {
				tracks[i].setForeground(Bootstrap.lcdOff);
				tracks[i].setBackground(Bootstrap.lcdOn);
			} else {
				tracks[i].setForeground(Bootstrap.lcdOn);
				tracks[i].setBackground(Bootstrap.lcdOff);
			}
		} else {
			if (sequencer.getActiveSequence().getTrack(i).isOn()) {
				tracks[i].setForeground(Bootstrap.lcdOff);
				tracks[i].setBackground(Bootstrap.lcdOn);
			} else {
				tracks[i].setForeground(Bootstrap.lcdOn);
				tracks[i].setBackground(Bootstrap.lcdOff);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "soloenabled":
		case "selectedtrackindex":
			refreshTracks();
			break;

		case "bank":
			displayBank();
			displayTrackNumbers();
			refreshTracks();
			break;

		case "seqnumbername":
			displaySq();
			refreshTracks();
			break;

		case "trackon":
			for (int i = 0; i < 16; i++)
				setTrackColor(i);
			break;

		case "clock":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow0();
					displayNow1();
					displayNow2();
				}
			});
			break;

		}
	}

	private void displayNow0() {
		now0Field.setText(String.format("%03d", sequencer.getCurrentBarNumber() + 1));
	}

	private void displayNow1() {
		now1Field.setText(String.format("%02d", sequencer.getCurrentBeatNumber() + 1));
	}

	private void displayNow2() {
		now2Field.setText(String.format("%02d", sequencer.getCurrentClockNumber()));
	}

	private void refreshTracks() {
		for (int i = 0; i < 16; i++) {
			displayTrack(i);
			setTrackColor(i);
			sequencer.getActiveSequence().getTrack(i + bankoffset()).deleteObservers();
			sequencer.getActiveSequence().getTrack(i + bankoffset()).addObserver(this);
		}
	}
}