package com.mpc.gui.sampler;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.disk.AbstractDisk;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.TwoDots;
import com.mpc.gui.components.Waveform;
import com.mpc.sampler.Sound;
import com.mpc.sampler.Sampler;

public class LoopObserver implements Observer {
	private Gui gui;
	private SoundGui soundGui;
	private String[] playXNames = { "ALL", "ZONE", "BEFOR ST", "BEFOR TO", "AFTR END" };

	private Sampler sampler;
	private Sound sound;
	private JTextField sndField;
	private JTextField playXField;
	private JTextField toField;
	private JTextField endLengthField;
	private JTextField endLengthValueField;
	private JTextField loopField;

	private JTextField dummyField;

	private TwoDots twoDots;

	private Waveform waveform;

	public LoopObserver(Sampler sampler, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sampler = sampler;

		this.gui = Bootstrap.getGui();

		soundGui = gui.getSoundGui();
		soundGui.deleteObservers();
		soundGui.addObserver(this);

		if (sampler.getSoundCount() != 0) {
			sound = sampler.getSound(soundGui.getSoundIndex());
			sound.deleteObservers();
			sound.addObserver(this);
			sound.getMsoc().deleteObservers();
			sound.getMsoc().addObserver(this);
		}
		twoDots = mainFrame.getLayeredScreen().getTwoDots();
		twoDots.setVisible(true);
		twoDots.setVisible(0, true);
		twoDots.setVisible(1, true);
		twoDots.setVisible(2, false);
		twoDots.setVisible(3, false);

		waveform = mainFrame.getLayeredScreen().getWaveform();

		sndField = mainFrame.lookupTextField("snd");
		playXField = mainFrame.lookupTextField("playx");
		toField = mainFrame.lookupTextField("to");
		endLengthField = mainFrame.lookupTextField("endlength");
		endLengthValueField = mainFrame.lookupTextField("endlengthvalue");
		loopField = mainFrame.lookupTextField("loop");
		dummyField = mainFrame.lookupTextField("dummy");

		toField.setSize(8 * 6 * 2 + 2, 18);
		endLengthValueField.setSize(8 * 6 * 2 + 2, 18);

		displaySnd();
		displayPlayX();
		displayTo();
		displayEndLength();
		displayEndLengthValue();
		displayLoop();

		if (sampler.getSoundCount() != 0) {
			dummyField.setFocusable(false);
			waveformLoadData();
			waveform.setSelectionStart(sound.getLoopTo());
			waveform.setSelectionEnd(sound.getEnd());
		} else {
			sndField.setFocusable(false);
			playXField.setFocusable(false);
			toField.setFocusable(false);
			endLengthField.setFocusable(false);
			endLengthValueField.setFocusable(false);
			loopField.setFocusable(false);
		}

	}

	private void displaySnd() {
		if (sampler.getSoundCount() != 0) {
			sndField.grabFocus();
			sound.deleteObservers();
			sound.getMsoc().deleteObservers();
			sound = sampler.getSound(soundGui.getSoundIndex());
			sound.addObserver(this);
			sound.getMsoc().addObserver(this);

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

	private void displayTo() {
		if (sampler.getSoundCount() != 0) {
			toField.setText(Util.padLeftSpace("" + sound.getLoopTo(), 8));
		} else {
			toField.setText("       0");
		}
		if (!soundGui.isEndSelected()) displayEndLengthValue();
	}

	private void displayEndLength() {
		endLengthField.setSize(62, 18);
		endLengthField.setText(soundGui.isEndSelected() ? "  End" : "Lngth");
		displayEndLengthValue();
	}

	private void displayEndLengthValue() {
		if (sampler.getSoundCount() != 0) {
			if (soundGui.isEndSelected()) {
				endLengthValueField.setText(Util.padLeftSpace("" + sound.getEnd(), 8));
			} else {
				endLengthValueField.setText(Util.padLeftSpace("" + (sound.getEnd() - sound.getLoopTo()), 8));
			}
		} else {
			endLengthValueField.setText("       0");
		}
	}

	private void displayLoop() {
		if (sampler.getSoundCount() == 0) {
			loopField.setText("OFF");
			return;
		}
		loopField.setText(sound.isLoopEnabled() ? "ON" : "OFF");
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "soundnumber":
			displaySnd();
			displayTo();
			displayEndLength();
			waveformLoadData();
			waveform.setSelectionStart(sound.getLoopTo());
			waveform.setSelectionEnd(sound.getEnd());
			soundGui.initZones(sound.getLastFrameIndex() + 1);
			break;

		case "loopto":
			displayTo();
			waveform.setSelectionStart(sound.getLoopTo());
			break;

		case "endlength":
			displayEndLength();
			break;

		case "end":
			displayEndLengthValue();
			waveform.setSelectionEnd(sound.getEnd());
			break;

		case "loopenabled":
			displayLoop();
			break;

		case "playx":
			displayPlayX();
			break;

		}
	}

	private void waveformLoadData() {
		float[] sampleData = sound.getSampleData();

		if (!sound.isMono()) {
			if (soundGui.getView() == 0) {
				sampleData = sound.getSampleDataLeft();
			} else {
				sampleData = sound.getSampleDataRight();
			}
		}
		waveform.setSampleData(sampleData);
	}
}