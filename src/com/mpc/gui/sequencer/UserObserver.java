package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.UserDefaults;
import com.mpc.sampler.Program;
import com.mpc.sequencer.TimeSignature;

public class UserObserver implements Observer {

	private String[] busNames = new String[] { "MIDI", "DRUM1", "DRUM2",
			"DRUM3", "DRUM4" };

	private Mpc mpc;

	private TimeSignature timeSig;

	private JTextField tempoField;
	private JTextField loopField;
	private JTextField tsigField;
	private JTextField barsField;
	private JTextField pgmField;
	private JTextField recordingModeField;
	private JTextField busField;
	private JTextField deviceNumberField;
	private JLabel deviceNameLabel;
	private JTextField veloField;
	private UserDefaults ud;

	public UserObserver(Mpc mpc, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		this.mpc = mpc;
		this.ud = Bootstrap.getUserDefaults();

		timeSig = ud.getTimeSig();

		timeSig.deleteObservers();
		timeSig.addObserver(this);
		
		ud.deleteObservers();
		ud.addObserver(this);

		tempoField = mainFrame.lookupTextField("tempo");
		loopField = mainFrame.lookupTextField("loop");
		tsigField = mainFrame.lookupTextField("tsig");
		barsField = mainFrame.lookupTextField("bars");
		pgmField = mainFrame.lookupTextField("pgm");
		recordingModeField = mainFrame.lookupTextField("recordingmode");
		busField = mainFrame.lookupTextField("tracktype");
		deviceNumberField = mainFrame.lookupTextField("devicenumber");
		deviceNameLabel = mainFrame.lookupLabel("devicename");
		veloField = mainFrame.lookupTextField("velo");

		displayTempo();
		displayLoop();
		displayTsig();
		displayBars();
		displayPgm();
		displayRecordingMode();
		displayBus();
		displayDeviceNumber();
		displayVelo();
	}

	private void displayTempo() {
		double tempo = ud.getTempo().doubleValue();
		tempo = (double) Math.round(tempo * 10) / 10;
		if (tempo < 30)
			tempo = 30;
		if (tempo > 300)
			tempo = 300;
		String tempoString = ("" + (double) Math.round(tempo * 10) / 10)
				.replace(".", "\u00CB");
		tempoField.setText(Util.padLeftSpace(tempoString, 5));
	}

	private void displayLoop() {
		loopField.setText(ud.isLoopEnabled() ? "ON" : "OFF");
	}

	private void displayTsig() {
		tsigField.setText(ud.getTimeSig().getNumerator() + "/"
				+ ud.getTimeSig().getDenominator());
	}

	private void displayBars() {
		barsField.setText("" + (ud.getLastBarIndex() + 1));
	}

	private void displayPgm() {
		if (ud.getPgm() == 0) {
			pgmField.setText("OFF");
		} else {
			pgmField.setText("" + ud.getPgm());
		}
	}

	private void displayRecordingMode() {
		recordingModeField.setText(ud.isRecordingModeMulti() ? "M" : "S");
	}

	private void displayBus() {
		busField.setText(busNames[ud.getBus()]);
		displayDeviceName();
	}

	private void displayDeviceNumber() {
		if (ud.getDeviceNumber() == 0) {
			deviceNumberField.setText("OFF");
		} else {
			if (ud.getDeviceNumber() >= 17) {
				deviceNumberField.setText(Util.padLeftSpace(
						"" + (ud.getDeviceNumber() - 16), 2)
						+ "B");
			} else {
				deviceNumberField.setText(Util.padLeftSpace(
						"" + ud.getDeviceNumber(), 2)
						+ "A");
			}
		}
	}

	private void displayVelo() {
		veloField.setText("" + ud.getVeloRatio());
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "tempo":
			displayTempo();
			break;

		case "loop":
			displayLoop();
			break;

		case "timesignature":
			displayTsig();
			break;

		case "bars":
			displayBars();
			break;

		case "pgm":
			displayPgm();
			break;

		case "recordingmode":
			displayRecordingMode();
			break;

		case "tracktype":
			displayBus();
			break;

		case "devicenumber":
			displayDeviceNumber();
			displayDeviceName();
			break;

		case "velo":
			displayVelo();
			break;
		}
	}

	private void displayDeviceName() {
		if (ud.getBus() != 0) {
			if (ud.getDeviceNumber() == 0) {
				Program p = mpc.getSampler().getProgram(
						mpc.getSampler().getDrumBusProgramNumber(
								ud.getBus()));
				deviceNameLabel.setText(p.getName());
			} else {
				deviceNameLabel.setText(ud.getDeviceName(ud.getDeviceNumber()));
			}
		}

		if (ud.getBus() == 0) {
			if (ud.getDeviceNumber() != 0) {
				deviceNameLabel.setText(ud.getDeviceName(ud.getDeviceNumber()));
			} else {
				deviceNameLabel.setText("");
			}
		}
	}
}