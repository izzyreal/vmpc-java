package com.mpc.gui.sampler.window;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.FineWaveform;
import com.mpc.gui.components.TwoDots;
import com.mpc.gui.sampler.SoundGui;
import com.mpc.sampler.Sound;
import com.mpc.sampler.Sampler;

public class ZoomObserver implements Observer {
	private Gui gui;
	private ZoomGui zoomGui;
	private SoundGui soundGui;
	private TwoDots twoDots;

	private JTextField startField;
	private JTextField endField;
	private JTextField toField;

	private JTextField smplLngthField;
	private JTextField loopLngthField;
	private JTextField playXField;

	private JLabel lngthLabel;

	private JTextField lngthField;

	private Sound sound;

	private String[] playXNames = { "ALL", "ZONE", "BEFOR ST", "BEFOR TO",
			"AFTR END" };
	private FineWaveform fineWaveform;
	private int[] zoomLevels;

	private String csn;

	public ZoomObserver(Sampler sampler, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		this.gui = Bootstrap.getGui();

		zoomGui = gui.getZoomGui();
		zoomGui.deleteObservers();
		zoomGui.addObserver(this);

		soundGui = gui.getSoundGui();
		soundGui.deleteObservers();
		soundGui.addObserver(this);

		csn = gui.getMainFrame().getLayeredScreen().getCurrentScreenName();

		sound = sampler.getSound(soundGui.getSoundIndex());
		sound.deleteObservers();
		sound.getMsoc().deleteObservers();
		sound.addObserver(this);
		sound.getMsoc().addObserver(this);
		twoDots = mainFrame.getLayeredScreen().getTwoDots();
		twoDots.setVisible(true);
		twoDots.setVisible(0, false);
		twoDots.setVisible(1, false);
		twoDots.setVisible(2, true);
		twoDots.setVisible(3, true);
		twoDots.setSelected(3, false);

		fineWaveform = mainFrame.getLayeredScreen().getFineWaveform();

		zoomLevels = new int[7];
		zoomLevels[0] = 130;
		for (int i = 1; i < 7; i++) {
			zoomLevels[i] = zoomLevels[i - 1] * 2;
		}

		startField = mainFrame.lookupTextField("start");
		endField = mainFrame.lookupTextField("end");
		lngthLabel = mainFrame.lookupLabel("lngth");
		smplLngthField = mainFrame.lookupTextField("smpllngth");
		playXField = mainFrame.lookupTextField("playx");
		lngthField = mainFrame.lookupTextField("lngth");
		loopLngthField = mainFrame.lookupTextField("looplngth");
		toField = mainFrame.lookupTextField("to");
		lngthField = mainFrame.lookupTextField("lngth");

		if (csn.equals("startfine")) {
			startField.setSize(8 * 6 * 2 + 2, 18);
			displayStart();
			displayLngthLabel();
			displaySmplLngth();
		}

		if (csn.equals("endfine")) {
			endField.setSize(8 * 6 * 2 + 2, 18);
			displayEnd();
			displayLngthLabel();
			displaySmplLngth();
		}

		if (csn.equals("looptofine")) {
			toField.setSize(8 * 6 * 2 + 2, 18);
			lngthField.setSize(8 * 6 * 2 + 2, 18);
			displayTo();
			displayLngthField();
			displayLoopLngth();
		}

		if (csn.equals("loopendfine")) {
			endField.setSize(8 * 6 * 2 + 2, 18);
			lngthField.setSize(8 * 6 * 2 + 2, 18);
			displayEnd();
			displayLngthField();
			displayLoopLngth();
		}

		if (csn.equals("zonestartfine")) {
			startField.setSize(8 * 6 * 2 + 2, 18);
			displayStart();
			displayLngthLabel();
		}

		if (csn.equals("zoneendfine")) {
			endField.setSize(8 * 6 * 2 + 2, 18);
			displayEnd();
			displayLngthLabel();
		}

		displayPlayX();
		displayFineWaveform();
	}

	private void displayLoopLngth() {
		loopLngthField.setText(zoomGui.isLoopLngthFix() ? "FIX" : "VARI");
	}

	private void displayLngthField() {
		lngthField.setText(Util.padLeftSpace(
				"" + (sound.getEnd() - sound.getLoopTo()), 8));
	}

	private void displayFineWaveform() {
		float[] sampleData = sound.getSampleData();

		if (!sound.isMono()) {
			sampleData = sound.getSampleDataLeft();
		}

		int start = 0;
		if (csn.equals("startfine")) {
			start = sound.getStart()
					- ((zoomLevels[zoomGui.getZoomLevel()]) / 2);
		}

		if (csn.equals("zonestartfine")) {
			start = soundGui.getZoneStart(soundGui.getZoneNumber())- ((zoomLevels[zoomGui.getZoomLevel()]) / 2);
		}
		
		if (csn.equals("endfine") || csn.equals("loopendfine")) {
			start = sound.getEnd()
					- ((zoomLevels[zoomGui.getZoomLevel()]) / 2);
		}

		if (csn.equals("zoneendfine")) {
			start = soundGui.getZoneEnd(soundGui.getZoneNumber()) - ((zoomLevels[zoomGui.getZoomLevel()]) /2);
		}
		
		if (csn.equals("looptofine")) {
			start = sound.getLoopTo()
					- ((zoomLevels[zoomGui.getZoomLevel()]) / 2);
		}

		int numberOfSamples = zoomLevels[zoomGui.getZoomLevel()];
		float[] newSampleData = new float[numberOfSamples];

		int startPositive = Math.abs(start);

		if (start < 0) {
			for (int i = 0; i < startPositive; i++) {
				newSampleData[i] = 0f;
			}

			for (int i = 0; i < numberOfSamples - startPositive; i++) {
				if (i < sampleData.length) {
					newSampleData[i + startPositive] = sampleData[i];
				}
			}

		} else {
			for (int i = start; i < numberOfSamples + start; i++) {
				if (i < sampleData.length) {
					newSampleData[i - start] = sampleData[i];
				} else {
					newSampleData[i - start] = 0f;
				}
			}
		}

		fineWaveform.setSampleData(newSampleData);
	}

	private void displayStart() {
		if (csn.equals("startfine")) {
			startField.setText(Util.padLeftSpace("" + sound.getStart(), 8));
		}
		
		if (csn.equals("zonestartfine")) {
			startField.setText(Util.padLeftSpace("" + soundGui.getZoneStart(soundGui.getZoneNumber()), 8));
		}
	}

	private void displayEnd() {
		if (csn.equals("endfine") || csn.equals("loopendfine")) {
			endField.setText(Util.padLeftSpace("" + sound.getEnd(), 8));
		}
		
		if (csn.equals("zoneendfine")) {
			endField.setText(Util.padLeftSpace("" + soundGui.getZoneEnd(soundGui.getZoneNumber()), 8));
		}
	}

	private void displayLngthLabel() {
		if (csn.equals("startfine") || csn.equals("endfine")) {
			lngthLabel.setText(Util.padLeftSpace(
					"" + (sound.getEnd() - sound.getStart()), 8));
		}

		if (csn.equals("zonestartfine") || csn.equals("zoneendfine")) {
			lngthLabel.setText(Util.padLeftSpace("" + (soundGui.getZoneEnd(soundGui.getZoneNumber()) - soundGui.getZoneStart(soundGui.getZoneNumber())), 8));
		}
	}

	private void displaySmplLngth() {
		smplLngthField.setText(zoomGui.isSmplLngthFix() ? "FIX" : "VARI");
	}

	private void displayPlayX() {
		playXField.setText(playXNames[soundGui.getPlayX()]);
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "end":
			if (csn.equals("startfine") || csn.equals("endfine")) {
				displayLngthLabel();
			}
			if (csn.equals("looptofine") || csn.equals("loopendfine")) {
				displayLngthField();
			}
			if (csn.equals("endfine") || csn.equals("loopendfine")) {
				displayEnd();
			}
			displayFineWaveform();
			break;

		case "start":
			if (csn.equals("startfine")) {
				displayStart();
			}
			displayLngthLabel();
			displayFineWaveform();
			break;

		case "smpllngthfix":
			displaySmplLngth();
			break;

		case "looplngthfix":
			displayLoopLngth();
			break;

		case "playx":
			displayPlayX();
			break;

		case "zoomlevel":
			displayFineWaveform();
			break;

		case "loopto":
			if (csn.equals("looptofine")) {
				displayTo();
				displayLngthField();
				displayFineWaveform();
			}
			break;

		case "lngth":
			displayLngthField();
			displayEnd();
			displayFineWaveform();
			break;
			
		case "zone":
			displayStart();
			displayEnd();
			displayLngthLabel();
			displayFineWaveform();
			break;
		}
	}

	private void displayTo() {
		toField.setText(Util.padLeftSpace("" + sound.getLoopTo(), 8));
	}

}