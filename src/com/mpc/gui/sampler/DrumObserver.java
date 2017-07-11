package com.mpc.gui.sampler;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class DrumObserver implements Observer {

	private SamplerGui samplerGui;

	private JTextField drumField;
	private JTextField padToInternalSoundField;
	private JTextField pgmField;
	private JTextField pgmChangeField;
	private JTextField midiVolumeField;
	private JTextField currentValField;

	private MainFrame mainFrame;
	private Mpc mpc;
	private Sequencer sequencer;
	private Sampler sampler;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;

	private JLabel valueLabel;

	public DrumObserver(Gui gui) {

		mpc = gui.getMpc();
		mainFrame = gui.getMainFrame();

		samplerGui = gui.getSamplerGui();

		sequencer = mpc.getSequencer();
		sampler = mpc.getSampler();
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());

		mpcSoundPlayerChannel.deleteObservers();
		mpcSoundPlayerChannel.addObserver(this);

		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		drumField = mainFrame.lookupTextField("drum");
		padToInternalSoundField = mainFrame.lookupTextField("padtointernalsound");
		pgmField = mainFrame.lookupTextField("pgm");
		pgmChangeField = mainFrame.lookupTextField("programchange");
		midiVolumeField = mainFrame.lookupTextField("midivolume");
		currentValField = mainFrame.lookupTextField("currentval");

		displayDrum();
		displayPadToInternalSound();
		displayPgm();
		displayPgmChange();
		displayMidiVolume();
		displayCurrentVal();
		
	}

	private void displayValue() {
		valueLabel.setText(Util.padLeftSpace("" + sampler.getUnusedSampleAmount(), 3));
	}

	private void displayDrum() {
		drumField.setText("" + (samplerGui.getSelectedDrum() + 1));
	}

	private void displayPadToInternalSound() {
		padToInternalSoundField.setText(samplerGui.isPadToIntSound() ? "ON" : "OFF");
	}

	private void displayPgm() {
		int pn = mpcSoundPlayerChannel.getProgram();
		pgmField.setText(Util.padLeftSpace("" + (pn + 1), 2) + "-" + sampler.getProgram(pn).getName());
	}

	private void displayPgmChange() {
		pgmChangeField.setText(mpcSoundPlayerChannel.receivesPgmChange() ? "RECEIVE" : "IGNORE");
	}

	private void displayMidiVolume() {
		midiVolumeField.setText(mpcSoundPlayerChannel.receivesMidiVolume() ? "RECEIVE" : "IGNORE");
	}

	private void displayCurrentVal() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		mpcSoundPlayerChannel.deleteObservers();
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		mpcSoundPlayerChannel.addObserver(this);

		switch ((String) arg1) {

		case "drum":
			displayDrum();
			displayPgm();
			displayPgmChange();
			displayMidiVolume();
			displayCurrentVal();
			break;

		case "pgm":
			displayPgm();
			break;

		case "padtointsound":
			displayPadToInternalSound();
			break;

		case "receivepgmchange":
			displayPgmChange();
			break;

		case "receivemidivolume":
			displayMidiVolume();
			break;
		}
	}

}
