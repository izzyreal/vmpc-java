package com.mpc.gui.sampler;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.LayeredScreen;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
//import com.mpc.sequencer.Track;
//import com.mpc.sequencer.Sequence;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class PgmParamsObserver implements Observer {

	private String[] decayModes = { "END", "START" };

	private String[] voiceOverlapModes = { "POLY", "MONO", "NOTE OFF" };

	private Sampler sampler;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;
	private Program program;

	private JTextField pgmField;

	private JTextField noteField;
	private JTextField attackField;
	private JTextField decayField;
	private JTextField decayModeField;
	private JTextField freqField;
	private JTextField resonField;
	private JTextField tuneField;
	private JTextField voiceOverlapField;

	private LayeredScreen slp;

	private SamplerGui samplerGui;

	public PgmParamsObserver(Mpc mpc, MainFrame mainFrame) {

		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();

		samplerGui.addObserver(this);
		sampler = mpc.getSampler();
		slp = mainFrame.getLayeredScreen();
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		program.deleteObservers();
		program.addObserver(this);
		mpcSoundPlayerChannel.deleteObservers();
		mpcSoundPlayerChannel.addObserver(this);
		Sampler.getLastNp(program).deleteObservers();
		Sampler.getLastNp(program).addObserver(this);

		pgmField = mainFrame.lookupTextField("pgm");

		noteField = mainFrame.lookupTextField("note");
		attackField = mainFrame.lookupTextField("attack");
		decayField = mainFrame.lookupTextField("decay");
		decayModeField = mainFrame.lookupTextField("dcymd");
		freqField = mainFrame.lookupTextField("freq");
		resonField = mainFrame.lookupTextField("reson");
		tuneField = mainFrame.lookupTextField("tune");
		voiceOverlapField = mainFrame.lookupTextField("voiceoverlap");

		displayPgm();
		displayNote();
		displayDecayMode();
		displayFreq();
		displayReson();
		displayTune();
		displayVoiceOverlap();
		displayAttackDecay();

	}

	@Override
	public void update(Observable o, Object arg) {

		String parameter = (String) arg;

		program.deleteObservers();
		mpcSoundPlayerChannel.deleteObservers();

		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		Sampler.getLastNp(program).deleteObservers();
		Sampler.getLastNp(program).addObserver(this);
		program.addObserver(this);
		mpcSoundPlayerChannel.addObserver(this);

		switch (parameter) {

		case "pgm":
			displayPgm();
		case "padandnote":
			displayNote();
			displayAttackDecay();
			displayDecayMode();
			displayFreq();
			displayReson();
			displayTune();
			displayVoiceOverlap();
			break;

		case "attack":
		case "decay":
			displayAttackDecay();
			break;

		case "dcymd":
			displayDecayMode();
			break;

		case "freq":
			displayFreq();
			break;

		case "reson":
			displayReson();
			break;

		case "tune":
			displayTune();
			break;

		case "voiceoverlap":
			displayVoiceOverlap();
			break;

		case "note":
			displayNote();
			break;
		}
	}

	private void displayReson() {
		resonField.setText("" + Sampler.getLastNp(program).getFilterResonance());
	}

	private void displayFreq() {
		freqField.setText("" + Sampler.getLastNp(program).getFilterFrequency());
	}

	private void displayAttackDecay() {

		int attack = Sampler.getLastNp(program).getAttack();

		int decay = Sampler.getLastNp(program).getDecay();

		attackField.setText(Util.padLeftSpace("" + attack, 3));
		decayField.setText(Util.padLeftSpace("" + decay, 3));

		slp.redrawEnvGraph(attack, decay);
		slp.repaint();

	}

	private void displayNote() {

		int sampleNumber = Sampler.getLastNp(program).getSndNumber();
		int note = Sampler.getLastNp(program).getNumber();
		String sampleName = sampleNumber != -1 ? sampler.getSoundName(sampleNumber) : "OFF";

		int padNumber = program.getPadNumberFromNote(note);

		if (padNumber != -1) {

			String stereo = program.getPad(padNumber).getMixerChannel().isStereo() && sampleNumber != -1 ? "(ST)" : "";
			String padName = sampler.getPadName(padNumber);
			noteField.setText(
					"" + note + "/" + padName + "-" + Util.padRightSpace(sampleName, 16) + stereo);
		} else {
			noteField.setText("" + note + "/OFF-" + sampleName);

		}
	}

	private void displayPgm() {
		pgmField.setText(Util.padLeftSpace("" + (mpcSoundPlayerChannel.getProgram() + 1), 2));
	}

	private void displayTune() {
		tuneField.setText("" + Sampler.getLastNp(program).getTune());
	}

	private void displayDecayMode() {
		decayModeField.setText("" + decayModes[Sampler.getLastNp(program).getDecayMode()]);
	}

	private void displayVoiceOverlap() {
		voiceOverlapField.setText("" + voiceOverlapModes[Sampler.getLastNp(program).getVoiceOverlap()]);
	}
}