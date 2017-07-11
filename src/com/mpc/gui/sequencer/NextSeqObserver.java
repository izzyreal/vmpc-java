package com.mpc.gui.sequencer;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.TempoChangeEvent;

public class NextSeqObserver implements Observer {

	private JTextField sqField;
	private JTextField now0Field;
	private JTextField now1Field;
	private JTextField now2Field;

	private JTextField tempoField;
	private JLabel tempoLabel;
	private JTextField tempoSourceField;

	private JTextField timingField;

	private JTextField nextSqField;

	private Sequencer sequencer;
	private MainFrame mainFrame;

	public NextSeqObserver(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		sequencer = Bootstrap.getGui().getMpc().getSequencer();

		sequencer.deleteObservers();
		sequencer.addObserver(this);

		sqField = mainFrame.lookupTextField("sq");

		now0Field = mainFrame.lookupTextField("now0");
		now1Field = mainFrame.lookupTextField("now1");
		now2Field = mainFrame.lookupTextField("now2");

		now0Field.setFocusable(false);
		now1Field.setFocusable(false);
		now2Field.setFocusable(false);

		tempoField = mainFrame.lookupTextField("tempo");
		tempoLabel = mainFrame.lookupLabel("tempo");
		tempoSourceField = mainFrame.lookupTextField("temposource");
		timingField = mainFrame.lookupTextField("timing");
		nextSqField = mainFrame.lookupTextField("nextsq");

		displaySq();
		displayNow0();
		displayNow1();
		displayNow2();
		displayTempo();
		displayTempoSource();
		displayTiming();

	}

	private void displaySq() {
		if (sequencer.isPlaying()) {
			sqField.setText(String.format("%02d-", (sequencer.getCurrentlyPlayingSequenceIndex() + 1))
					+ sequencer.getCurrentlyPlayingSequence().getName());
		} else {
			sqField.setText(String.format("%02d-", (sequencer.getActiveSequenceIndex() + 1))
					+ sequencer.getActiveSequence().getName());
		}

	}

	private void displayNextSq() {
		String seqName = sequencer.getSequence(sequencer.getNextSq()).getName();
		nextSqField.setText(String.format("%02d-", (sequencer.getNextSq() + 1)) + seqName);
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

	private void displayTempo() {
		tempoField.setText(Util.padLeftSpace(sequencer.getTempo().toString().replace(".", "\u00CB"), 5));
		displayTempoLabel();

	}

	private void displayTempoLabel() {
		int currentRatio = -1;
		MpcSequence seq = sequencer.isPlaying() ? sequencer.getCurrentlyPlayingSequence()
				: sequencer.getActiveSequence();
		for (TempoChangeEvent tce : seq.getTempoChangeEvents()) {
			if (tce.getTick() > sequencer.getTickPosition()) {
				break;
			}
			currentRatio = tce.getRatio();
		}
		if (currentRatio != 1000) {
			tempoLabel.setText("c\u00C0:");
		} else {
			tempoLabel.setText(" \u00C0:");
		}
	}

	private void displayTempoSource() {
		tempoSourceField.setText(sequencer.isTempoSourceSequence() ? "(SEQ)" : "(MAS)");
	}

	private void displayTiming() {
		timingField.setText(SequencerObserver.timingCorrectNames[sequencer.getTcIndex()]);
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {
		
		case "seqnumbername":
			displaySq();
			break;
		case "bar":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow0();
				}
			});
			break;

		case "beat":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow1();
				}
			});
			break;

		case "clock":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow2();
				}
			});
			break;

		case "nextsqvalue":
			displayNextSq();
			break;

		case "nextsq":
			nextSqField.setVisible(true);
			nextSqField.grabFocus();
			displayNextSq();
			break;

		case "nextsqoff":
			System.out.println("next sq off");
			nextSqField.setVisible(false);
			// nextSqLabel.setVisible(false);
			// mainFrame.getLayeredScreen().drawFunctionBoxes("sequencer");
			mainFrame.lookupTextField("sq").grabFocus();
			break;
		case "timing":
			displayTiming();
			break;

		}

	}


}
