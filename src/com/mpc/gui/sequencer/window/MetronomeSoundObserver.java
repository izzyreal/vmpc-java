package com.mpc.gui.sequencer.window;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.Background;
import com.mpc.sampler.Sampler;

public class MetronomeSoundObserver implements Observer {

	private String[] soundNames = { "CLICK", "DRUM1", "DRUM2", "DRUM3", "DRUM4" };

	private JTextField soundField;
	private JTextField volumeField;
	private JLabel volumeLabel;
	private JTextField outputField;
	private JLabel outputLabel;
	private JTextField accentField;
	private JLabel accentLabel;
	private JTextField normalField;
	private JLabel normalLabel;
	private JTextField veloAccentField;
	private JLabel veloAccentLabel;
	private JTextField veloNormalField;
	private JLabel veloNormalLabel;

	private SequencerWindowGui swGui;

	private Background bg;
	
	public MetronomeSoundObserver(MainFrame mainFrame) {
		
		bg = mainFrame.getLayeredScreen().getCurrentBackground();
		
		swGui = Bootstrap.getGui().getSequencerWindowGui();
		swGui.deleteObservers();
		swGui.addObserver(this);

		soundField = mainFrame.lookupTextField("sound");
		volumeField = mainFrame.lookupTextField("volume");
		volumeLabel = mainFrame.lookupLabel("volume");
		outputField = mainFrame.lookupTextField("output");
		outputLabel = mainFrame.lookupLabel("output");
		accentField = mainFrame.lookupTextField("accent");
		accentLabel = mainFrame.lookupLabel("accent");
		normalField = mainFrame.lookupTextField("normal");
		normalLabel = mainFrame.lookupLabel("normal");
		veloAccentField = mainFrame.lookupTextField("velocityaccent");
		veloAccentLabel = mainFrame.lookupLabel("velocityaccent");
		veloNormalField = mainFrame.lookupTextField("velocitynormal");
		veloNormalLabel = mainFrame.lookupLabel("velocitynormal");

		displaySound();
		if (swGui.getMetronomeSound() == 0) {
			displayVolume();
			displayOutput();
		} else {
			displayAccent();
			displayNormal();
			displayVelocityAccent();
			displayVelocityNormal();
			bg.setBackgroundName("metronomesoundempty");
		}
	}

	private void displaySound() {
		int sound = swGui.getMetronomeSound();
		soundField.setText(soundNames[sound]);

		volumeLabel.setVisible(sound == 0);
		volumeField.setVisible(sound == 0);
		outputLabel.setVisible(sound == 0);
		outputField.setVisible(sound == 0);

		normalLabel.setVisible(sound > 0);
		normalField.setVisible(sound > 0);
		veloNormalLabel.setVisible(sound > 0);
		veloNormalField.setVisible(sound > 0);
		accentLabel.setVisible(sound > 0);
		accentField.setVisible(sound > 0);
		veloAccentLabel.setVisible(sound > 0);
		veloAccentField.setVisible(sound > 0);
		
		
	}

	private void displayVolume() {
		volumeField.setText(StringUtils.rightPad("" + swGui.getClickVolume(), 3));
	}

	private void displayOutput() {
		outputField.setText(swGui.getClickOutput() == 0 ? "STEREO" : "" + swGui.getClickOutput());
	}

	private void displayAccent() {
		Sampler sampler = Bootstrap.getGui().getMpc().getSampler();
		int program = sampler.getDrumBusProgramNumber(swGui.getMetronomeSound());
		int pad = sampler.getProgram(program).getPadNumberFromNote(swGui.getAccentNote());
		accentField.setText(swGui.getAccentNote() + "/" + sampler.getPadName(pad));
	}

	private void displayNormal() {
		Sampler sampler = Bootstrap.getGui().getMpc().getSampler();
		int program = sampler.getDrumBusProgramNumber(swGui.getMetronomeSound());
		int pad = sampler.getProgram(program).getPadNumberFromNote(swGui.getNormalNote());
		normalField.setText(swGui.getNormalNote() + "/" + sampler.getPadName(pad));
	}

	private void displayVelocityAccent() {
		veloAccentField.setText(StringUtils.rightPad("" + swGui.getAccentVelo(), 3));
	}

	private void displayVelocityNormal() {
		veloNormalField.setText(StringUtils.rightPad("" + swGui.getNormalVelo(), 3));
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "clickvolume":
			displayVolume();
			break;

		case "clickoutput":
			displayOutput();
			break;

		case "metronomesound":
			displaySound();
			if (swGui.getMetronomeSound() == 0) {
				displayVolume();
				displayOutput();
				bg.setBackgroundName("metronomesound");
			} else {
				displayAccent();
				displayVelocityAccent();
				displayNormal();
				displayVelocityNormal();
				bg.setBackgroundName("metronomesoundempty");
			}
			break;

		case "accentvelo":
			displayVelocityAccent();
			break;

		case "normalvelo":
			displayVelocityNormal();
			break;

		case "accentnote":
			displayAccent();
			break;

		case "normalnote":
			displayNormal();
			break;

		}

	}
}