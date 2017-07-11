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

public class ZoneObserver implements Observer {
	private Gui gui;
	private SoundGui soundGui;
	private String[] playXNames = { "ALL", "ZONE", "BEFOR ST", "BEFOR TO", "AFTR END" };

	private Sampler sampler;
	private Sound sound;
	private JTextField sndField;
	private JTextField playXField;
	private JTextField stField;
	private JTextField endField;
	private JTextField zoneField;

	private JTextField dummyField;

	private TwoDots twoDots;

	private Waveform waveform;
	private JTextField numberOfZonesField;
	private String csn;

	public ZoneObserver(Sampler sampler, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sampler = sampler;

		this.gui = Bootstrap.getGui();

		soundGui = gui.getSoundGui();
		soundGui.deleteObservers();
		soundGui.addObserver(this);
		csn = mainFrame.getLayeredScreen().getCurrentScreenName();
		if (sampler.getSoundCount() != 0) {
			sound = sampler.getSound(soundGui.getSoundIndex());
			sound.deleteObservers();
			sound.addObserver(this);
			sound.getMsoc().deleteObservers();
			sound.getMsoc().addObserver(this);
		}
		twoDots = mainFrame.getLayeredScreen().getTwoDots();
		twoDots.setVisible(0, true);
		twoDots.setVisible(1, true);
		twoDots.setVisible(2, false);
		twoDots.setVisible(3, false);

		waveform = mainFrame.getLayeredScreen().getWaveform();

		if (csn.equals("zone")) {
			twoDots.setVisible(true);
			sndField = mainFrame.lookupTextField("snd");
			playXField = mainFrame.lookupTextField("playx");
			stField = mainFrame.lookupTextField("st");
			endField = mainFrame.lookupTextField("end");
			zoneField = mainFrame.lookupTextField("zone");
			dummyField = mainFrame.lookupTextField("dummy");

			stField.setSize(8 * 6 * 2 + 2, 18);
			endField.setSize(8 * 6 * 2 + 2, 18);

			displaySnd();
			if (sampler.getSoundCount() != 0) {
				dummyField.setFocusable(false);
				soundGui.initZones(sampler.getSound(soundGui.getSoundIndex()).getLastFrameIndex() + 1);
				waveform.setSelectionStart(soundGui.getZoneStart(soundGui.getZoneNumber()));
				waveform.setSelectionEnd(soundGui.getZoneEnd(soundGui.getZoneNumber()));
			} else {
				sndField.setFocusable(false);
				playXField.setFocusable(false);
				stField.setFocusable(false);
				endField.setFocusable(false);
				zoneField.setFocusable(false);
			}
			displayPlayX();
			displaySt();
			displayEnd();
			displayZone();
		}

		if (csn.equals("numberofzones")) {
			twoDots.setVisible(false);
			numberOfZonesField = mainFrame.lookupTextField("numberofzones");
			displayNumberOfZones();
		}
	}

	private void displaySnd() {
		if (sampler.getSoundCount() != 0) {
			waveformLoadData();
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
			stField.setText(Util.padLeftSpace("" + soundGui.getZoneStart(soundGui.getZoneNumber()), 8));
		} else {
			stField.setText("       0");
		}
	}

	private void displayEnd() {
		if (sampler.getSoundCount() != 0) {
			endField.setText(Util.padLeftSpace("" + soundGui.getZoneEnd(soundGui.getZoneNumber()), 8));
		} else {
			endField.setText("       0");
		}
	}

	private void displayZone() {
		if (sampler.getSoundCount() == 0) {
			zoneField.setText("1");
			return;
		}
		zoneField.setText("" + (soundGui.getZoneNumber() + 1));
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "soundnumber":
			displaySnd();
			soundGui.initZones(sampler.getSound(soundGui.getSoundIndex()).getLastFrameIndex() + 1);
			displaySt();
			displayEnd();
			waveformLoadData();
			waveform.setSelectionStart(soundGui.getZoneStart(soundGui.getZoneNumber()));
			waveform.setSelectionEnd(soundGui.getZoneEnd(soundGui.getZoneNumber()));
			break;

		case "zone":
			displayZone();
			displaySt();
			displayEnd();
			waveform.setSelectionStart(soundGui.getZoneStart(soundGui.getZoneNumber()));
			waveform.setSelectionEnd(soundGui.getZoneEnd(soundGui.getZoneNumber()));
			break;

		case "playx":
			displayPlayX();
			break;

		case "numberofzones":
			displayNumberOfZones();
			break;

		}
	}

	private void displayNumberOfZones() {
		numberOfZonesField.setText("" + soundGui.getNumberOfZones());
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