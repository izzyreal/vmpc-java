package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.TimeSignature;

public class EditSequenceObserver implements Observer {

	private String[] functionNames = { "COPY", "DURATION", "VELOCITY", "TRANSPOSE" };

	private String[] modeNames = { "ADD VALUE", "SUB VALUE", "MULTI VAL%", "SET TO VAL" };

	private Gui gui;
	private EditSequenceGui editSequenceGui;

	private Sequencer sequencer;
	private int trackNum;
	private int seqNum;

	private MpcSequence mpcSequence;
	private MpcTrack track;
	private TimeSignature timeSig;

	private JTextField editFunctionField;
	private JTextField time0Field;
	private JTextField time1Field;
	private JTextField time2Field;
	private JTextField time3Field;
	private JTextField time4Field;
	private JTextField time5Field;
	private JTextField drumNoteField;
	private JTextField fromSqField;
	private JTextField tr0Field;
	private JTextField toSqField;
	private JTextField tr1Field;
	private JTextField modeField;
	private JTextField start0Field;
	private JTextField start1Field;
	private JTextField start2Field;
	private JTextField copiesField;

	private Sampler sampler;

	private JTextField midiNote0Field;

	private JTextField midiNote1Field;

	private JLabel midiNote1Label;
	private JLabel toSqLabel;
	private JLabel tr0Label;
	private JLabel tr1Label;
	private JLabel start0Label;
	private JLabel start1Label;
	private JLabel start2Label;
	private JLabel copiesLabel;
	private JLabel fromSqLabel;
	private JLabel modeLabel;

	public EditSequenceObserver(Sequencer sequencer, Sampler sampler, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		this.sequencer = sequencer;
		this.sampler = sampler;

		this.gui = Bootstrap.getGui();
		editSequenceGui = gui.getEditSequenceGui();
		editSequenceGui.deleteObservers();
		editSequenceGui.addObserver(this);

		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();

		editFunctionField = mainFrame.lookupTextField("editfunction");
		time0Field = mainFrame.lookupTextField("time0");
		time1Field = mainFrame.lookupTextField("time1");
		time2Field = mainFrame.lookupTextField("time2");
		time3Field = mainFrame.lookupTextField("time3");
		time4Field = mainFrame.lookupTextField("time4");
		time5Field = mainFrame.lookupTextField("time5");
		drumNoteField = mainFrame.lookupTextField("drumnote");
		midiNote0Field = mainFrame.lookupTextField("midinote0");
		midiNote1Field = mainFrame.lookupTextField("midinote1");
		midiNote1Label = mainFrame.lookupLabel("midinote1");
		fromSqField = mainFrame.lookupTextField("fromsq");
		tr0Field = mainFrame.lookupTextField("tr0");
		toSqField = mainFrame.lookupTextField("tosq");
		tr1Field = mainFrame.lookupTextField("tr1");
		modeField = mainFrame.lookupTextField("mode");
		start0Field = mainFrame.lookupTextField("start0");
		start1Field = mainFrame.lookupTextField("start1");
		start2Field = mainFrame.lookupTextField("start2");
		copiesField = mainFrame.lookupTextField("copies");

		fromSqLabel = mainFrame.lookupLabel("fromsq");
		toSqLabel = mainFrame.lookupLabel("tosq");
		tr0Label = mainFrame.lookupLabel("tr0");
		tr1Label = mainFrame.lookupLabel("tr1");
		start0Label = mainFrame.lookupLabel("start0");
		start1Label = mainFrame.lookupLabel("start1");
		start2Label = mainFrame.lookupLabel("start2");
		copiesLabel = mainFrame.lookupLabel("copies");
		modeLabel = mainFrame.lookupLabel("mode");
		modeLabel.setSize(200, 14);
		copiesLabel.setSize(250, 14);

		sequencer.deleteObservers();
		sequencer.addObserver(this);
		mpcSequence.deleteObservers();
		mpcSequence.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);
		timeSig.deleteObservers();
		timeSig.addObserver(this);

		setEditFunctionValue();
		displayTime();
		setNoteValues();
		fromSqField.setText("" + (editSequenceGui.getFromSq() + 1));
		tr0Field.setText("" + (editSequenceGui.getTr0() + 1));
		toSqField.setText("" + (editSequenceGui.getToSq() + 1));
		tr1Field.setText("" + (editSequenceGui.getTr1() + 1));
		setModeValue();
		displayStart();
		setCopiesValue();
	}

	private void displayStart() {
		start0Field.setText(
				Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, editSequenceGui.getStartTicks()) + 1));
		start1Field.setText(
				Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, editSequenceGui.getStartTicks()) + 1));
		start2Field.setText(
				Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, editSequenceGui.getStartTicks())));
	}

	private void displayTime() {
		time0Field.setText(
				Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, editSequenceGui.getTime0()) + 1));
		time1Field.setText(
				Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, editSequenceGui.getTime0()) + 1));
		time2Field
				.setText(Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, editSequenceGui.getTime0())));
		time3Field.setText(
				Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, editSequenceGui.getTime1()) + 1));
		time4Field.setText(
				Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, editSequenceGui.getTime1()) + 1));
		time5Field
				.setText(Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, editSequenceGui.getTime1())));
	}

	private void setCopiesValue() {
		if (editSequenceGui.getEditFunctionNumber() == 0) {
			copiesField.setText(Util.padLeftSpace("" + editSequenceGui.getCopies(), 3));
		}
		if (editSequenceGui.getEditFunctionNumber() == 1) {
			copiesField.setText(Util.padLeftSpace("" + editSequenceGui.getDurationValue(), 4));
		}
		if (editSequenceGui.getEditFunctionNumber() == 2) {
			copiesField.setText(Util.padLeftSpace("" + editSequenceGui.getVelocityValue(), 3));
		}
	}

	private void setModeValue() {
		if (editSequenceGui.getEditFunctionNumber() == 0) {
			modeField.setText(editSequenceGui.isModeMerge() ? "MERGE" : "REPLACE");
		}

		if (editSequenceGui.getEditFunctionNumber() == 1) {
			modeField.setText(modeNames[editSequenceGui.getDurationMode()]);
		}

		if (editSequenceGui.getEditFunctionNumber() == 2) {
			modeField.setText(modeNames[editSequenceGui.getVelocityMode()]);
		}

		if (editSequenceGui.getEditFunctionNumber() == 3) {
			if (editSequenceGui.getTransposeAmount() == 0) {
				modeField.setText("  0");
			}
			if (editSequenceGui.getTransposeAmount() < 0) {
				modeField.setText(Util.padLeftSpace("" + editSequenceGui.getTransposeAmount(), 3));
			}
			if (editSequenceGui.getTransposeAmount() > 0) {
				modeField.setText(Util.padLeftSpace("+" + editSequenceGui.getTransposeAmount(), 3));
			}
		}
	}

	private void setEditFunctionValue() {
		editFunctionField.setSize(52 * 2 + 8, 18);
		editFunctionField.setText(functionNames[editSequenceGui.getEditFunctionNumber()]);
		if (editSequenceGui.getEditFunctionNumber() == 0) {

			fromSqLabel.setLocation(131 * 2, 1 * 2);
			fromSqField.setLocation(fromSqField.getLocation().x, 1 * 2);
			tr0Label.setLocation(tr0Label.getLocation().x, 1 * 2);
			tr0Field.setLocation(tr0Field.getLocation().x, 1 * 2);

			modeLabel.setText("Mode:");
			fromSqLabel.setText("From sq:");
			toSqField.setVisible(true);
			tr1Field.setVisible(true);
			start0Field.setVisible(true);
			start1Field.setVisible(true);
			start2Field.setVisible(true);
			copiesField.setVisible(true);
			toSqLabel.setVisible(true);
			tr1Label.setVisible(true);
			start0Label.setVisible(true);
			start1Label.setVisible(true);
			start2Label.setVisible(true);
			copiesLabel.setText("Copies:");
			copiesLabel.setLocation(138 * 2, 39 * 2);
			copiesField.setLocation(copiesField.getLocation().x, 38 * 2);
			modeLabel.setLocation(150 * 2, 21 * 2);
			modeField.setSize(7 * 6 * 2 + 2, 18);
			copiesField.setSize(3 * 6 * 2 + 2, 18);
		}
		if (editSequenceGui.getEditFunctionNumber() == 1 || editSequenceGui.getEditFunctionNumber() == 2) {
			fromSqLabel.setLocation(131 * 2, 3 * 2);
			fromSqField.setLocation(fromSqField.getLocation().x, 3 * 2);
			tr0Label.setLocation(tr0Label.getLocation().x, 3 * 2);
			tr0Field.setLocation(tr0Field.getLocation().x, 3 * 2);

			modeLabel.setText("Mode:");
			fromSqLabel.setText("Edit sq:");
			toSqField.setVisible(false);
			tr1Field.setVisible(false);
			start0Field.setVisible(false);
			start1Field.setVisible(false);
			start2Field.setVisible(false);
			copiesField.setVisible(true);
			toSqLabel.setVisible(false);
			tr1Label.setVisible(false);
			start0Label.setVisible(false);
			start1Label.setVisible(false);
			start2Label.setVisible(false);
			copiesLabel.setText("Value:");
			copiesLabel.setLocation(138 * 2, 35 * 2);
			copiesField.setLocation(copiesField.getLocation().x, 34 * 2);
			copiesField.setSize(4 * 6 * 2 + 2, 18);
			if (editSequenceGui.getEditFunctionNumber() == 2) {
				copiesField.setSize(3 * 6 * 2 + 2, 18);
			}
			modeLabel.setLocation(150 * 2, 21 * 2);
			modeField.setSize(10 * 6 * 2 + 2, 18);
		}
		if (editSequenceGui.getEditFunctionNumber() == 3) {
			fromSqLabel.setLocation(131 * 2, 3 * 2);
			fromSqField.setLocation(fromSqField.getLocation().x, 3 * 2);
			tr0Label.setLocation(tr0Label.getLocation().x, 3 * 2);
			tr0Field.setLocation(tr0Field.getLocation().x, 3 * 2);

			modeLabel.setText("Amount:");
			fromSqLabel.setText("Edit sq:");
			toSqField.setVisible(false);
			tr1Field.setVisible(false);
			start0Field.setVisible(false);
			start1Field.setVisible(false);
			start2Field.setVisible(false);
			copiesField.setVisible(false);
			toSqLabel.setVisible(false);
			tr1Label.setVisible(false);
			start0Label.setVisible(false);
			start1Label.setVisible(false);
			start2Label.setVisible(false);
			copiesLabel.setText("(Except drum track)");
			copiesLabel.setLocation(132 * 2, 38 * 2);
			modeLabel.setLocation(138 * 2, 21 * 2);
			modeField.setSize(3 * 6 * 2 + 2, 18);
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

		case "modevalue":
			setModeValue();
			break;
		case "editfunction":
			setEditFunctionValue();
			setModeValue();
			setCopiesValue();
			break;
		case "time":
			displayTime();
			break;
		case "drumnote":
			setDrumNoteValue();
			break;
		case "midinote0":
			setMidiNoteValues();
			break;
		case "midinote1":
			setMidiNoteValues();
			break;
		case "fromsq":
			fromSqField.setText("" + (editSequenceGui.getFromSq() + 1));
			break;
		case "tr0":
			tr0Field.setText("" + (editSequenceGui.getTr0() + 1));
			break;
		case "tosq":
			toSqField.setText("" + (editSequenceGui.getToSq() + 1));
			break;
		case "tr1":
			tr1Field.setText("" + (editSequenceGui.getTr1() + 1));
			break;
		case "start":
			displayStart();
			break;
		case "copies":
			setCopiesValue();
			break;
		}
	}

	private void setNoteValues() {
		midiNote0Field.setSize(8 * 6 * 2, 18);
		midiNote1Field.setSize(8 * 6 * 2, 18);
		midiNote1Field.setLocation(62 * 2, 39 * 2);
		MpcSequence mpcSequence = sequencer.getSequence(sequencer.getActiveSequenceIndex());
		MpcTrack track = (MpcTrack) mpcSequence.getTrack(sequencer.getActiveTrackIndex());
		if (track.getBusNumber() == 0) {
			drumNoteField.setVisible(false);
			midiNote0Field.setVisible(true);
			midiNote1Field.setVisible(true);
			midiNote1Label.setVisible(true);
			setMidiNoteValues();
		} else {
			drumNoteField.setVisible(true);
			midiNote0Field.setVisible(false);
			midiNote1Field.setVisible(false);
			midiNote1Label.setVisible(false);
			setDrumNoteValue();
		}
	}

	private void setMidiNoteValues() {
		midiNote0Field.setText(Util.padLeftSpace("" + editSequenceGui.getMidiNote0(), 3) + "("
				+ Gui.noteNames[editSequenceGui.getMidiNote0()] + "\u00D4");
		midiNote1Field.setText(Util.padLeftSpace("" + editSequenceGui.getMidiNote1(), 3) + "("
				+ Gui.noteNames[editSequenceGui.getMidiNote1()] + "\u00D4");
	}

	private void setDrumNoteValue() {
		MpcSequence mpcSequence = sequencer.getSequence(sequencer.getActiveSequenceIndex());
		MpcTrack track = (MpcTrack) mpcSequence.getTrack(sequencer.getActiveTrackIndex());
		Program program = sampler.getProgram(sampler.getDrum(track.getBusNumber() - 1).getProgram());
		if (editSequenceGui.getDrumNote() == 34) {
			drumNoteField.setText("ALL");
		} else {
			drumNoteField.setText(Util.padLeftSpace("" + editSequenceGui.getDrumNote(), 2) + "/"
					+ sampler.getPadName(program.getPadNumberFromNote(editSequenceGui.getDrumNote())));

		}
	}
}