package com.mpc.gui.sampler;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.disk.AbstractDisk;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Sound;
import com.mpc.sampler.Sampler;

public class SndParamsObserver implements Observer {
	private Gui gui;

	private String[] playXNames = { "ALL", "ZONE", "BEFOR ST", "BEFOR TO", "AFTR END" };

	private Sampler sampler;
	private Sound sound;
	private JTextField sndField;
	private JTextField playXField;
	private JTextField levelField;
	private JTextField tuneField;
	private JTextField beatField;
	private JLabel sampleTempoLabel;
	private JLabel newTempoLabel;

	private JTextField dummyField;
	private SoundGui soundGui;

	public SndParamsObserver(Sampler sampler, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sampler = sampler;

		this.gui = Bootstrap.getGui();

		soundGui = gui.getSoundGui();
		soundGui.deleteObservers();
		soundGui.addObserver(this);

		if (sampler.getSoundCount() != 0) {
			if (sound != null) {
				sound.deleteObservers();
				sound.getMsoc().deleteObservers();
			}
			sound = sampler.getSound(soundGui.getSoundIndex());
			sound.addObserver(this);
			sound.getMsoc().addObserver(this);
		}

		sndField = mainFrame.lookupTextField("snd");
		playXField = mainFrame.lookupTextField("playx");
		levelField = mainFrame.lookupTextField("level");
		tuneField = mainFrame.lookupTextField("tune");
		beatField = mainFrame.lookupTextField("beat");
		sampleTempoLabel = mainFrame.lookupLabel("sampletempo");
		newTempoLabel = mainFrame.lookupLabel("newtempo");
		dummyField = mainFrame.lookupTextField("dummy");
		if (sampler.getSoundCount() != 0) {
			dummyField.setFocusable(false);
		}

		displaySnd();
		displayPlayX();
		displayLevel();
		displayTune();
		displayBeat();
		displaySampleAndNewTempo();
	}

	private void displayLevel() {
		System.out.println(sound == null ? "null" : sound.getName());
		if (sound != null) {
			levelField.setText("" + sound.getSndLevel());
		} else {
			levelField.setText("100");
		}
	}

	private void displayTune() {
		if (sound != null) {
			tuneField.setText("" + sound.getTune());
		} else {
			tuneField.setText("0");
		}
	}

	private void displayBeat() {
		if (sound != null) {
			beatField.setText("" + sound.getBeatCount());
		} else {
			beatField.setText("4");
		}
	}

	private void displaySampleAndNewTempo() {
		if (sound == null) {
			sampleTempoLabel.setText("");
			newTempoLabel.setText("");
			return;
		}

		int length = sound.getEnd() - sound.getLoopTo();
		float lengthMs = (float) (length / (sound.getSampleRate() / 1000.0));
		int bpm = (int) ((600000.0 / lengthMs) * sound.getBeatCount());
		String bpmString = "" + bpm;
		String part1 = bpmString.substring(0, bpmString.length() - 1);
		String part2 = bpmString.substring(bpmString.length() - 1, bpmString.length());

		if (bpm < 300 || bpm > 3000) {
			part1 = "---";
			part2 = "-";
		}

		sampleTempoLabel.setText("Sample tempo=" + Util.padLeftSpace(part1 + "." + part2, 5));

		int newBpm = (int) (Math.pow(2.0, ((float) sound.getTune()) / 120.0) * bpm);
		bpmString = "" + newBpm;
		part1 = bpmString.substring(0, bpmString.length() - 1);
		part2 = bpmString.substring(bpmString.length() - 1, bpmString.length());

		if (newBpm < 300 || newBpm > 3000) {
			part1 = "---";
			part2 = "-";
		}

		newTempoLabel.setText("New tempo=" + Util.padLeftSpace(part1 + "." + part2, 5));
	}

	private void displaySnd() {
		if (sampler.getSoundCount() != 0) {
			sndField.grabFocus();
			// sound.deleteObservers();
			// System.out.println("old sound name " + sound.getName());
			// sound = sampler.getSound(soundGui.getSoundIndex());
			// System.out.println("new sound name " + sound.getName());
			// sound.addObserver(this);
			String sampleName = sound.getName();
			if (!sound.isMono()) {
				sampleName = AbstractDisk.padRightSpace(sampleName, 16) + "(ST)";
			}
			sndField.setText(sampleName);
		} else {
			sndField.setText("(no sound)");
			dummyField.grabFocus();
		}
	}

	private void displayPlayX() {
		playXField.setText(playXNames[soundGui.getPlayX()]);
	}

	@Override
	public void update(Observable o, Object arg) {

		if (sampler.getSoundCount() != 0) {
			sound.deleteObservers();
			sound.getMsoc().deleteObservers();
			sound = sampler.getSound(soundGui.getSoundIndex());
			sound.addObserver(this);
			sound.getMsoc().addObserver(this);
		}

		switch ((String) arg) {

		case "soundnumber":
			displaySnd();
			soundGui.initZones(sound.getLastFrameIndex() + 1);
			displayBeat();
			displaySampleAndNewTempo();
			displayTune();
			displayLevel();
			break;

		case "playx":
			displayPlayX();
			break;

		case "beat":
			displayBeat();
			displaySampleAndNewTempo();
			break;

		case "tune":
			displayTune();
			displaySampleAndNewTempo();
			break;

		case "level":
			displayLevel();
			break;

		}
	}
}