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

public class TrimObserver implements Observer {
	private Gui gui;

	private String[] playXNames = { "ALL", "ZONE", "BEFOR ST", "BEFOR TO", "AFTR END" };

	private Sampler sampler;
	private Sound sound;
	private JTextField sndField;
	private JTextField playXField;
	private JTextField stField;
	private JTextField endField;
	private JTextField viewField;

	private JTextField dummyField;

	private TwoDots twoDots;

	private Waveform waveform;
	private SoundGui soundGui;

	private SamplerGui samplerGui;

	public TrimObserver(Sampler sampler, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sampler = sampler;

		this.gui = Bootstrap.getGui();

		soundGui = gui.getSoundGui();
		soundGui.deleteObservers();
		soundGui.addObserver(this);

		samplerGui = gui.getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

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
		stField = mainFrame.lookupTextField("st");
		endField = mainFrame.lookupTextField("end");
		viewField = mainFrame.lookupTextField("view");
		dummyField = mainFrame.lookupTextField("dummy");

		stField.setSize(8 * 6 * 2 + 2, 18);
		endField.setSize(8 * 6 * 2 + 2, 18);

		displaySnd();
		displayPlayX();
		displaySt();
		displayEnd();
		if (sampler.getSoundCount() != 0) {
			dummyField.setFocusable(false);
			waveformLoadData();
			waveform.setSelectionStart(sound.getStart());
			waveform.setSelectionEnd(sound.getEnd());
		} else {
			sndField.setFocusable(false);
			playXField.setFocusable(false);
			stField.setFocusable(false);
			endField.setFocusable(false);
			viewField.setFocusable(false);
		}
		displayView();
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

	private void displaySt() {
		if (sampler.getSoundCount() != 0) {
			stField.setText(Util.padLeftSpace("" + sound.getStart(), 8));
		} else {
			stField.setText("       0");
		}
	}

	private void displayEnd() {
		if (sampler.getSoundCount() != 0) {
			endField.setText(Util.padLeftSpace("" + sound.getEnd(), 8));
		} else {
			endField.setText("       0");
		}
	}

	private void displayView() {
		if (soundGui.getView() == 0) {
			viewField.setText("LEFT");
		} else {
			viewField.setText("RIGHT");
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "soundnumber":
			displaySnd();
			displaySt();
			displayEnd();
			waveformLoadData();
			waveform.setSelectionStart(sound.getStart());
			waveform.setSelectionEnd(sound.getEnd());
			soundGui.initZones(sound.getLastFrameIndex() + 1);
			break;

		case "start":
			displaySt();
			waveform.setSelectionStart(sound.getStart());
			break;

		case "end":
			displayEnd();
			waveform.setSelectionEnd(sound.getEnd());
			break;

		case "view":
			displayView();
			waveformLoadData();
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