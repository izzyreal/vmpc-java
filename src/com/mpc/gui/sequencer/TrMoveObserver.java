package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.disk.AbstractDisk;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.LayeredScreen;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.TimeSignature;
import com.mpc.sequencer.MpcTrack;

public class TrMoveObserver implements Observer {
	private Gui gui;
	private TrMoveGui tmGui;

	private Sequencer sequencer;
	private int trackNum;
	private int seqNum;

	private MpcSequence mpcSequence;
	private MpcTrack track;
	private TimeSignature timeSig;

	private JTextField sqField;

	private JTextField tr0Field;
	private JTextField tr1Field;

	private JLabel tr0Label;
	private JLabel tr1Label;

	private JLabel selectTrackLabel;
	private JLabel toMoveLabel;

	private LayeredScreen slp;

	public TrMoveObserver(Sequencer sequencer, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		this.sequencer = sequencer;

		this.gui = Bootstrap.getGui();
		tmGui = gui.getTrMoveGui();
		tmGui.deleteObservers();
		tmGui.addObserver(this);
		slp = mainFrame.getLayeredScreen();
		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();

		sqField = mainFrame.lookupTextField("sq");
		tr0Field = mainFrame.lookupTextField("tr0");
		tr1Field = mainFrame.lookupTextField("tr1");
		tr0Label = mainFrame.lookupLabel("tr0");
		tr1Label = mainFrame.lookupLabel("tr1");

		selectTrackLabel = mainFrame.lookupLabel("selecttrack");
		toMoveLabel = mainFrame.lookupLabel("tomove");

		selectTrackLabel.setText("Select track");
		toMoveLabel.setText("to move.");

		sequencer.deleteObservers();
		sequencer.addObserver(this);
		mpcSequence.deleteObservers();
		mpcSequence.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);
		timeSig.deleteObservers();
		timeSig.addObserver(this);

		displaySq();
		displayTrFields();
		displayTrLabels();
	}

	private void displayTrLabels() {
		MpcSequence s = sequencer.getSequence(tmGui.getSq());
		String tr0 = "";
		String tr1 = "";
		String tr0Name = "";
		String tr1Name = "";
		int tr0Index = tmGui.getCurrentTrackIndex() - 1;
		int tr1Index = 0;

		if (tmGui.isSelected() && tr0Index >= tmGui.getSelectedTrackIndex())
			tr0Index++;

		tr1Index = tr0Index + 2;

		if (tmGui.isSelected()) {
			tr1Index--;
			if (tr0Index == tmGui.getSelectedTrackIndex()) {
				tr0Index--;
			}
			if (tr1Index == tmGui.getSelectedTrackIndex()) {
				tr1Index++;
			}
		}

		if (tr0Index >= 0) {
			tr0Name = s.getTrack(tr0Index).getName();
			tr0 += "Tr:" + padLeft2Zeroes(tr0Index + 1) + "-" + tr0Name;
		} else {
			tr0 = "";
		}

		if (tr1Index < 64) {
			tr1Name = s.getTrack(tr1Index).getName();
			tr1 += "Tr:" + padLeft2Zeroes(tr1Index + 1) + "-" + tr1Name;
		} else {
			tr1 = "";
		}

		tr0Label.setText(tr0);
		tr1Label.setText(tr1);
	}

	private void displayTrFields() {
		MpcSequence s = sequencer.getSequence(tmGui.getSq());
		if (tmGui.isSelected()) {
			selectTrackLabel.setVisible(false);
			toMoveLabel.setVisible(false);
			tr1Field.setVisible(false);
			tr0Field.setVisible(true);
			String tr0Name = s.getTrack(tmGui.getSelectedTrackIndex())
					.getName();
			if (tr0Name.length() < 10)
				tr0Name = AbstractDisk.padRightSpace(tr0Name, 9) + "\u00CD";
			tr0Field.setText("Tr:"
					+ padLeft2Zeroes(tmGui.getSelectedTrackIndex() + 1) + "-"
					+ tr0Name);
			slp.drawFunctionBoxes("trmove_selected");
		} else {
			selectTrackLabel.setVisible(true);
			toMoveLabel.setVisible(true);
			tr0Field.setVisible(false);
			tr1Field.setVisible(true);
			tr1Field.setText("Tr:"
					+ padLeft2Zeroes(tmGui.getCurrentTrackIndex() + 1) + "-"
					+ s.getTrack(tmGui.getCurrentTrackIndex()).getName());
			if (tr1Field.hasFocus()) {
				slp.drawFunctionBoxes("trmove_notselected");
			} else {
				slp.drawFunctionBoxes("trmove");
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		track.deleteObservers();
		mpcSequence.deleteObservers();
		timeSig.deleteObservers();
		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();
		track.addObserver(this);
		mpcSequence.addObserver(this);
		timeSig.addObserver(this);

		switch ((String) arg) {

		case "sq":
			displaySq();
			break;

		case "trmove":
			displayTrLabels();
			displayTrFields();
			break;

		}
	}

	private void displaySq() {
		sqField.setText("" + padLeft2Zeroes(tmGui.getSq() + 1) + "-"
				+ sequencer.getSequence(tmGui.getSq()).getName());
	}

	private String padLeft2Zeroes(int i) {
		return String.format("%02d", i);
	}
}