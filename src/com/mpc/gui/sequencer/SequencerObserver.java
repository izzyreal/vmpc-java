package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.TempoChangeEvent;

public class SequencerObserver implements Observer {

	public static String[] timingCorrectNames = new String[] { "OFF", "1/8", "1/8(3)", "1/16", "1/16(3)", "1/32",
			"1/32(3)" };

	private String[] busNames = new String[] { "MIDI", "DRUM1", "DRUM2", "DRUM3", "DRUM4" };

	private MainFrame mainFrame;

	private Sequencer sequencer;
	private int trackNum;
	private int seqNum;

	private MpcSequence seq;
	private MpcTrack track;

	private JTextField trField;
	private JTextField onField;
	private JTextField sqField;
	private JTextField timingField;
	private JTextField countField;
	private JTextField loopField;
	private JTextField recordingModeField;
	private JTextField barsField;
	private JTextField now0Field;
	private JTextField now1Field;
	private JTextField now2Field;
	private JTextField tempoField;
	private JTextField tempoSourceField;
	private JTextField tsigField;
	private JTextField pgmField;
	private JTextField veloField;
	private JTextField busField;
	private JTextField deviceNumberField;

	private JTextField nextSqField;
	private JLabel nextSqLabel;

	private JLabel tempoLabel;

	private JLabel deviceNameLabel;

	private Sampler sampler;

	public SequencerObserver(Mpc mpc, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sequencer = mpc.getSequencer();
		this.sampler = mpc.getSampler();
		this.mainFrame = mainFrame;

		seqNum = sequencer.getActiveSequenceIndex();
		seq = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) seq.getTrack(trackNum);

		trField = mainFrame.lookupTextField("tr");
		onField = mainFrame.lookupTextField("on");
		sqField = mainFrame.lookupTextField("sq");
		timingField = mainFrame.lookupTextField("timing");
		countField = mainFrame.lookupTextField("count");
		loopField = mainFrame.lookupTextField("loop");
		recordingModeField = mainFrame.lookupTextField("recordingmode");
		barsField = mainFrame.lookupTextField("bars");
		now0Field = mainFrame.lookupTextField("now0");
		now1Field = mainFrame.lookupTextField("now1");
		now2Field = mainFrame.lookupTextField("now2");
		tempoField = mainFrame.lookupTextField("tempo");
		tempoSourceField = mainFrame.lookupTextField("temposource");
		tsigField = mainFrame.lookupTextField("tsig");
		pgmField = mainFrame.lookupTextField("pgm");
		veloField = mainFrame.lookupTextField("velo");
		busField = mainFrame.lookupTextField("tracktype");
		deviceNumberField = mainFrame.lookupTextField("devicenumber");

		deviceNameLabel = mainFrame.lookupLabel("devicename");

		tempoLabel = mainFrame.lookupLabel("tempo");

		nextSqField = mainFrame.lookupTextField("nextsq");
		nextSqLabel = mainFrame.lookupLabel("nextsq");
		nextSqField.setVisible(false);
		nextSqLabel.setVisible(false);

		sequencer.deleteObservers();
		sequencer.addObserver(this);
		seq.deleteObservers();
		seq.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);

		trField.setText(String.format("%02d-", (trackNum + 1)) + seq.getTrack(trackNum).getName());
		onField.setText(track.isOn() ? "YES" : "NO");

		displaySq();
		timingField.setText(timingCorrectNames[sequencer.getTcIndex()]);
		countField.setText(sequencer.isCountEnabled() ? "ON" : "OFF");
		displayLoop();
		displayRecordingMode();
		displayBars();
		displayNow0();
		displayNow1();
		displayNow2();
		displayTempo();
		displayTempoSource();
		displayTsig();
		displayPgm();
		displayVelo();
		displayBus();
		displayDeviceNumber();
		mainFrame.setBlink("soloblink", mainFrame.getLayeredScreen().getMainPanel(), sequencer.isSoloEnabled());
	}

	private void displayTempoSource() {
		tempoSourceField.setText(sequencer.isTempoSourceSequence() ? "(SEQ)" : "(MAS)");
	}

	private void displaySq() {
		if (sequencer.isPlaying()) {
			sqField.setText(String.format("%02d-", (sequencer.getCurrentlyPlayingSequenceIndex() + 1))
					+ sequencer.getCurrentlyPlayingSequence().getName());
		} else {
			sqField.setText(String.format("%02d-", (sequencer.getActiveSequenceIndex() + 1))
					+ sequencer.getActiveSequence().getName());
		}
	}

	private void displayVelo() {
		veloField.setText("" + track.getVelocityRatio());
	}

	private void displayDeviceNumber() {
		if (track.getDevice() == 0) {
			deviceNumberField.setText("OFF");
		} else {
			if (track.getDevice() >= 17) {
				deviceNumberField.setText(Util.padLeftSpace("" + (track.getDevice() - 16), 2) + "B");
			} else {
				deviceNumberField.setText(Util.padLeftSpace("" + track.getDevice(), 2) + "A");
			}
		}
	}

	private void displayBus() {
		busField.setText(busNames[track.getBusNumber()]);
		displayDeviceName();
	}

	private void displayBars() {
		barsField.setText("" + (seq.getLastBar() + 1));
	}

	private void displayPgm() {
		if (track.getProgramChange() == 0) {
			pgmField.setText("OFF");
		} else {
			pgmField.setText("" + track.getProgramChange());
		}
	}

	private void displayDeviceName() {
		if (track.getBusNumber() != 0) {
			if (track.getDevice() == 0) {
				int pgm = Bootstrap.getGui().getMpc().getAudioMidiServices().isDisabled() ? 0
						: sampler.getDrumBusProgramNumber(track.getBusNumber());
				Program p = sampler.getProgram(pgm);
				deviceNameLabel.setText(p.getName());
			} else {
				deviceNameLabel.setText(seq.getDeviceName(track.getDevice()));
			}
		}

		if (track.getBusNumber() == 0) {
			if (track.getDevice() == 0) {
				deviceNameLabel.setText("");
			} else {
				deviceNameLabel.setText(seq.getDeviceName(track.getDevice()));
			}
		}
	}

	private void displayTempo() {
		tempoField.setText(Util.padLeftSpace(sequencer.getTempo().toString().replace(".", "\u00CB"), 5));
		displayTempoLabel();

	}

	private void displayTempoLabel() {
		int currentRatio = -1;

		for (TempoChangeEvent tce : seq.getTempoChangeEvents()) {
			if (tce.getTick() > sequencer.getTickPosition()) {
				break;
			}
			currentRatio = tce.getRatio();
		}

		if (currentRatio != 1000) {
			tempoLabel.setText("c\u00C0:");
		} else {
			tempoLabel.setText(" \u00C0:");
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		track.deleteObservers();
		seq.deleteObservers();
		seqNum = sequencer.getActiveSequenceIndex();
		seq = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) seq.getTrack(trackNum);
		track.addObserver(this);
		seq.addObserver(this);

		switch ((String) arg) {

		case "nextsqvalue":
			nextSqField.setText(StringUtils.leftPad("" + (sequencer.getNextSq() + 1), 2));
			break;

		case "nextsq":
			mainFrame.getLayeredScreen().drawFunctionBoxes("nextsq");
			if (!nextSqField.isVisible()) {
				nextSqField.setVisible(true);
				nextSqLabel.setVisible(true);
				nextSqField.grabFocus();
			}
			nextSqField.setText(StringUtils.leftPad("" + (sequencer.getNextSq() + 1), 2));
			break;

		case "nextsqoff":
			nextSqField.setVisible(false);
			nextSqLabel.setVisible(false);
			mainFrame.getLayeredScreen().drawFunctionBoxes("sequencer");
			mainFrame.lookupTextField("sq").grabFocus();
			break;
		case "timing":
			timingField.setText(timingCorrectNames[sequencer.getTcIndex()]);
			break;

		case "count":
			countField.setText(sequencer.isCountEnabled() ? "ON" : "OFF");
			break;

		case "tracknumbername":
			trField.setText(String.format("%02d-", (trackNum + 1)) + seq.getTrack(trackNum).getName());
			onField.setText(track.isOn() ? "YES" : "NO");

			break;

		case "seqnumbername":
			displaySq();
			break;

		case "loop":
			displayLoop();
			break;

		case "recordingmode":
			displayRecordingMode();
			break;

		case "numberofbars":
			displayBars();
			break;

		case "trackon":
			onField.setText(track.isOn() ? "YES" : "NO");
			break;

		case "bar":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow0();
				}
			});
			break;

		case "beat":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow1();
				}
			});
			break;

		case "clock":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow2();
				}
			});
			break;

		case "tempo":

		case "playtempo":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayTempo();
				}
			});
			break;

		case "temposource":
			displayTempoSource();
			break;
		case "timesignature":
			displayTsig();
			break;
		case "programchange":
			displayPgm();
			break;
		case "velocityratio":
			displayVelo();
			break;
		case "tracktype":
			displayBus();
			break;
		case "device":
			displayDeviceNumber();
			break;
		case "devicename":
			displayDeviceName();
			break;
		case "soloenabled":
			mainFrame.setBlink("soloblink", mainFrame.getLayeredScreen().getMainPanel(), sequencer.isSoloEnabled());
			break;
		case "now":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow0();
					displayNow1();
					displayNow2();
					displayTempo();
				}
			});
			break;
		}
	}

	private void displayNow0() {
		now0Field.setText(String.format("%03d", sequencer.getCurrentBarNumber() + 1));
	}

	private void displayNow1() {
		now1Field.setText(String.format("%02d", sequencer.getCurrentBeatNumber() + 1));
	}

	private void displayNow2() {
		now2Field.setText(String.format("%02d", sequencer.getCurrentClockNumber()));
	}

	private void displayRecordingMode() {
		recordingModeField.setText(sequencer.isRecordingModeMulti() ? "M" : "S");
	}

	private void displayTsig() {
		tsigField.setText(seq.getTimeSignature().getNumerator() + "/" + seq.getTimeSignature().getDenominator());
	}

	private void displayLoop() {
		loopField.setText(seq.isLoopEnabled() ? "ON" : "OFF");
	}

}