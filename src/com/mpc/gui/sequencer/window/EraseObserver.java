package com.mpc.gui.sequencer.window;

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
import com.mpc.sequencer.SeqUtil;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class EraseObserver implements Observer {

	private String[] typeNames = { "NOTES", "PITCH BEND", "CONTROL", "PROG CHANGE", "CH PRESSURE", "POLY PRESS",
			"EXCLUSIVE" };

	private String[] eraseNames = { "ALL EVENTS", "ALL EXCEPT", "ONLY ERASE" };

	private Sampler sampler;
	private Program program;

	private EraseGui eraseGui;
	private SequencerWindowGui swGui;

	private JTextField trackField;
	private JLabel trackNameLabel;

	private JTextField time0Field;
	private JTextField time1Field;
	private JTextField time2Field;
	private JTextField time3Field;
	private JTextField time4Field;
	private JTextField time5Field;

	private JTextField eraseField;

	private JTextField typeField;

	private JTextField notes0Field;
	private JLabel notes0Label;
	private JTextField notes1Field;
	private JLabel notes1Label;

	private MpcSequence mpcSequence;

	private int bus = 0;

	public EraseObserver(MainFrame mainFrame) {

		eraseGui = Bootstrap.getGui().getEraseGui();
		swGui = Bootstrap.getGui().getSequencerWindowGui();

		eraseGui.deleteObservers();
		eraseGui.addObserver(this);

		swGui.deleteObservers();
		swGui.addObserver(this);

		trackField = mainFrame.lookupTextField("track");
		trackNameLabel = mainFrame.lookupLabel("trackname");
		time0Field = mainFrame.lookupTextField("time0");
		time1Field = mainFrame.lookupTextField("time1");
		time2Field = mainFrame.lookupTextField("time2");
		time3Field = mainFrame.lookupTextField("time3");
		time4Field = mainFrame.lookupTextField("time4");
		time5Field = mainFrame.lookupTextField("time5");
		eraseField = mainFrame.lookupTextField("erase");
		typeField = mainFrame.lookupTextField("type");
		notes0Field = mainFrame.lookupTextField("notes0");
		notes0Label = mainFrame.lookupLabel("notes0");
		notes1Field = mainFrame.lookupTextField("notes1");
		notes1Label = mainFrame.lookupLabel("notes1");

		mpcSequence = Bootstrap.getGui().getMpc().getSequencer().getActiveSequence();
		sampler = Bootstrap.getGui().getMpc().getSampler();
		bus = mpcSequence.getTrack(Bootstrap.getGui().getMpc().getSequencer().getActiveTrackIndex()).getBusNumber();
		MpcSoundPlayerChannel mpcSoundPlayerChannel = Bootstrap.getGui().getSamplerGui().getTrackDrum() >= 0
				? sampler.getDrum(Bootstrap.getGui().getSamplerGui().getTrackDrum()) : null;

		program = Bootstrap.getGui().getSamplerGui().getTrackDrum() >= 0
				? sampler.getProgram(mpcSoundPlayerChannel.getProgram()) : null;

		displayTrack();
		displayTime();
		displayErase();
		displayType();
		displayNotes();
	}

	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "track":
			displayTrack();
			break;

		case "time":
			displayTime();
			break;

		case "erase":
			displayErase();
			displayType();
			displayNotes();
			break;

		case "type":
			displayType();
			displayNotes();
			break;

		case "notes":
			displayNotes();
			break;
		}

	}

	private void displayTrack() {
		String trackName = "";

		if (eraseGui.track == -1) {
			trackName = "ALL";
		} else {
			trackName = mpcSequence.getTrack(eraseGui.track).getActualName();
		}
		trackField.setText(Util.padLeftSpace("" + (eraseGui.track + 1), 2));
		trackNameLabel.setText("-" + trackName);
	}

	private void displayTime() {
		time0Field.setText(Util.padLeft3Zeroes(SeqUtil.getBarFromTick(mpcSequence, swGui.getTime0()) + 1));
		time1Field.setText(Util.padLeft2Zeroes(SeqUtil.getBeat(mpcSequence, swGui.getTime0()) + 1));
		time2Field.setText(Util.padLeft2Zeroes(SeqUtil.getClockNumber(mpcSequence, swGui.getTime0())));
		time3Field.setText(Util.padLeft3Zeroes(SeqUtil.getBarFromTick(mpcSequence, swGui.getTime1()) + 1));
		time4Field.setText(Util.padLeft2Zeroes(SeqUtil.getBeat(mpcSequence, swGui.getTime1()) + 1));
		time5Field.setText(Util.padLeft2Zeroes(SeqUtil.getClockNumber(mpcSequence, swGui.getTime1())));
	}

	private void displayErase() {
		eraseField.setText(eraseNames[eraseGui.erase]);
	}

	private void displayType() {
		typeField.setVisible(eraseGui.erase > 0);
		if (eraseGui.erase > 0) typeField.setText(typeNames[eraseGui.type]);
	}

	private void displayNotes() {
		if (eraseGui.erase != 0 && ((eraseGui.erase == 1 && eraseGui.type != 0) || (eraseGui.erase == 2 && eraseGui.type != 0))) {
			notes0Field.setVisible(false);
			notes0Label.setVisible(false);
			notes1Field.setVisible(false);
			notes1Label.setVisible(false);
			return;
		}
		notes0Field.setVisible(true);
		notes0Label.setVisible(true);
		notes1Field.setVisible(bus == 0);
		notes1Label.setVisible(bus == 0);

		if (bus > 0) {
			notes0Field.setSize(6 * 6 * 2 + 4, 18);
			if (swGui.getDrumNote() != 34) {
				notes0Field.setText("" + swGui.getDrumNote() + "/"
						+ sampler.getPadName(program.getPadNumberFromNote(swGui.getDrumNote())));
			} else {
				notes0Field.setText("ALL");
			}
		} else {
			notes0Field.setSize(8 * 6 * 2, 18);
			notes0Field.setText(Util.padLeftSpace("" + swGui.getMidiNote0(), 3) + "("
					+ Gui.noteNames[swGui.getMidiNote0()] + "\u00D4");
			notes1Field.setText(Util.padLeftSpace("" + swGui.getMidiNote1(), 3) + "("
					+ Gui.noteNames[swGui.getMidiNote1()] + "\u00D4");
		}
	}

}
