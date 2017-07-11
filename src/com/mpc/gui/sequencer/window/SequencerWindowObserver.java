package com.mpc.gui.sequencer.window;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.FunctionBox;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.NameGui;
import com.mpc.gui.components.HorizontalBar;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.SeqUtil;
import com.mpc.sequencer.TempoChangeEvent;
import com.mpc.sequencer.TimeSignature;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class SequencerWindowObserver implements Observer {

	private String[] softThruNames = { "OFF", "AS TRACK", "OMNI-A", "OMNI-B", "OMNI-AB" };

	private String[] inNames;

	private String[] editTypeNames = { "ADD VALUE", "SUB VALUE", "MULT VAL%", "SET TO VAL" };

	private String[] typeNames = { "NOTES", "PITCH BEND", "PROG CHANGE", "CH PRESSURE", "POLY PRESS", "EXCLUSIVE",
			"BANK SEL MSB", "MOD WHEEL", "BREATH CONT", "03", "FOOT CONTROL", "PORTA TIME", "DATA ENTRY", "MAIN VOLUME",
			"BALANCE", "09", "PAN", "EXPRESSION", "EFFECT 1", "EFFECT 2", "14", "15", "GEN.PUR. 1", "GEN.PUR. 2",
			"GEN.PUR. 3", "GEN.PUR. 4", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
			"BANK SEL LSB", "MOD WHEL LSB", "BREATH LSB", "35", "FOOT CNT LSB", "PORT TIME LS", "DATA ENT LSB",
			"MAIN VOL LSB", "BALANCE LSB", "41", "PAN LSB", "EXPRESS LSB", "EFFECT 1 LSB", "EFFECT 2 MSB", "46", "47",
			"GEN.PUR.1 LS", "GEN.PUR.2 LS", "GEN.PUR.3 LS", "GEN.PUR.4 LS", "52", "53", "54", "55", "56", "57", "58",
			"59", "60", "61", "62", "63", "SUSTAIN PDL", "PORTA PEDAL", "SOSTENUTO", "SOFT PEDAL", "LEGATO FT SW",
			"HOLD 2", "SOUND VARI", "TIMBER/HARMO", "RELEASE TIME", "ATTACK TIME", "BRIGHTNESS", "SOUND CONT 6",
			"SOUND CONT 7", "SOUND CONT 8", "SOUND CONT 9", "SOUND CONT10", "GEN.PUR. 5", "GEN.PUR. 6", "GEN.PUR. 7",
			"GEN.PUR. 8", "PORTA CNTRL", "85", "86", "87", "88", "89", "90", "EXT EFF DPTH", "TREMOLO DPTH",
			"CHORUS DEPTH", " DETUNE DEPTH", "PHASER DEPTH", "DATA INCRE", "DATA DECRE", "NRPN LSB", "NRPN MSB",
			"RPN LSB", "RPN MSB", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113",
			"114", "115", "116", "117", "118", "119", "ALL SND OFF", "RESET CONTRL", "LOCAL ON/OFF", "ALL NOTE OFF",
			"OMNI OFF", "OMNI ON", "MONO MODE ON", "POLY MODE ON" };
	private TempoChangeEvent[] visibleTempoChangeEvents;
	private String[] noteValueNames = new String[] { "OFF", "1/8", "1/8(3)", "1/16", "1/16(3)", "1/32", "1/32(3)" };

	private MainFrame mainFrame;
	private LayeredScreen slp;
	private String csn;

	private Sequencer sequencer;
	private Sampler sampler;
	private Program program;
	private int trackNum;
	private int seqNum;

	private MpcSequence mpcSequence;
	private MpcTrack track;
	private TimeSignature timeSig;
	private TimeSignature newTimeSignature;

	private JTextField sequenceNameFirstLetterField;
	private JTextField defaultSequenceNameFirstLetterField;

	private JLabel sequenceNameRestLabel;
	private JLabel defaultSequenceNameRestLabel;

	private SequencerWindowGui swGui;
	private NameGui nameGui;

	private JTextField sqField;
	private JTextField trField;
	private JTextField trackNameFirstLetterField;
	private JTextField defaultTrackNameFirstLetterField;
	private JLabel defaultTrackNameRestLabel;
	private JLabel trackNameRestLabel;

	private JTextField sq0Field;
	private JTextField sq1Field;

	private JTextField tr0Field;
	private JTextField tr1Field;

	private JTextField displayStyleField;
	private JTextField startTimeField;
	private JTextField hField;
	private JTextField mField;
	private JTextField sField;
	private JTextField fField;
	private JTextField frameRateField;

	private String[] displayStyleNames = { "BAR,BEAT,CLOCK", "HOUR,MINUTE,SEC" };
	private String[] frameRateNames = { "24", "25", "30D", "30" };

	private JTextField a0tcField;
	private JTextField a1tcField;
	private JTextField a2tcField;
	private JTextField b0tcField;
	private JTextField b1tcField;
	private JTextField b2tcField;
	private JTextField c0tcField;
	private JTextField c1tcField;
	private JTextField c2tcField;
	private JTextField d0tcField;
	private JTextField d1tcField;
	private JTextField d2tcField;
	private JTextField e0tcField;
	private JTextField e1tcField;
	private JTextField e2tcField;
	private JTextField f0tcField;
	private JTextField f1tcField;
	private JTextField f2tcField;

	private JTextField initialTempoField;
	private JTextField tempoChangeField;

	private JLabel b2tcLabel;
	private JLabel c2tcLabel;
	private JLabel e2tcLabel;
	private JLabel d2tcLabel;
	private JLabel f2tcLabel;
	private JLabel b1tcLabel;
	private JLabel c1tcLabel;
	private JLabel d1tcLabel;
	private JLabel e1tcLabel;
	private JLabel f1tcLabel;

	private HorizontalBar[] hBars;

	private JTextField noteValueField;
	private JTextField swingField;
	private JTextField notes0Field;
	private JTextField notes1Field;
	private JTextField time0Field;
	private JTextField time1Field;
	private JTextField time2Field;
	private JTextField time3Field;
	private JTextField time4Field;
	private JTextField time5Field;
	private JTextField shiftTimingField;
	private JTextField amountField;
	private JLabel swingLabel;
	private JLabel notes1Label;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;

	private FunctionBox fb;

	private JTextField bar0Field;
	private JTextField bar1Field;
	private JTextField newTsigField;

	private JTextField countInField;
	private JTextField inPlayField;
	private JTextField rateField;
	private JTextField inRecField;
	private JTextField waitForKeyField;

	private String[] rateNames = { "1/4", "1/4(3)", "1/8", "1/8(3)", "1/16", "1/16(3)", "1/32", "1/32(3)" };
	private String[] countInNames = { "OFF", "REC ONLY", "REC+PLAY" };
	private JTextField numberOfBarsField;
	private JTextField lastBarField;
	private JTextField firstBarField;

	private JTextField changeBarsAfterBarField;
	private JTextField changeBarsNumberOfBarsField;
	private JTextField changeBarsFirstBarField;
	private JTextField changeBarsLastBarField;

	private JTextField inThisTrackField;

	private JTextField newBarsField;

	private JLabel message0Label;
	private JLabel message1Label;
	private JLabel currentLabel;

	private JTextField a0mrsField;
	private JTextField a1mrsField;
	private JTextField a2mrsField;
	private JTextField b0mrsField;
	private JTextField b1mrsField;
	private JTextField b2mrsField;
	private JTextField c0mrsField;
	private JTextField c1mrsField;
	private JTextField c2mrsField;

	private JTextField passField;
	private JTextField typeField;
	private JTextField midiFilterField;
	private JTextField sustainPedalToDurationField;
	private JTextField progChangeSeqField;
	private JTextField receiveChField;

	private JTextField deviceNumberField;
	private JTextField softThruField;

	private JTextField editTypeField;

	private JTextField valueField;

	private JLabel deviceNameLabel;

	private JTextField firstLetterField;

	private SamplerGui samplerGui;

	public SequencerWindowObserver(Mpc mpc, MainFrame mainFrame) throws UnsupportedEncodingException {

		nameGui = Bootstrap.getGui().getNameGui();

		samplerGui = Bootstrap.getGui().getSamplerGui();

		inNames = new String[34];
		for (int i = 0; i < 34; i++) {
			if (i < 16) {
				inNames[i] = "1-" + Util.padLeft2Zeroes(i + 1);
			}
			if (i == 16) {
				inNames[i] = "1-Ex";
			}
			if (i > 16 && i < 33) {
				inNames[i] = "2-" + Util.padLeft2Zeroes(i - 16);
			}
			if (i == 33) {
				inNames[i] = "2-Ex";
			}
		}

		sequencer = mpc.getSequencer();
		sampler = mpc.getSampler();
		this.mainFrame = mainFrame;

		slp = mainFrame.getLayeredScreen();
		csn = slp.getCurrentScreenName();

		fb = slp.getFunctionBoxes();

		hBars = slp.getHorizontalBarsTempoChangeEditor();

		swGui = Bootstrap.getGui().getSequencerWindowGui();

		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();
		newTimeSignature = swGui.getNewTimeSignature();
		mpcSoundPlayerChannel = samplerGui.getTrackDrum() >= 0 ? sampler.getDrum(samplerGui.getTrackDrum()) : null;
		this.program = samplerGui.getTrackDrum() >= 0 ? sampler.getProgram(mpcSoundPlayerChannel.getProgram()) : null;

		sequenceNameFirstLetterField = mainFrame.lookupTextField("sequencenamefirstletter");
		defaultSequenceNameFirstLetterField = mainFrame.lookupTextField("defaultnamefirstletter");

		sequenceNameRestLabel = mainFrame.lookupLabel("sequencenamerest");
		defaultSequenceNameRestLabel = mainFrame.lookupLabel("defaultnamerest");

		trackNameFirstLetterField = mainFrame.lookupTextField("tracknamefirstletter");
		defaultTrackNameFirstLetterField = mainFrame.lookupTextField("defaultnamefirstletter");

		trackNameRestLabel = mainFrame.lookupLabel("tracknamerest");
		defaultTrackNameRestLabel = mainFrame.lookupLabel("defaultnamerest");

		trField = mainFrame.lookupTextField("tr");
		sqField = mainFrame.lookupTextField("sq");

		tr0Field = mainFrame.lookupTextField("tr0");
		tr1Field = mainFrame.lookupTextField("tr1");
		sq0Field = mainFrame.lookupTextField("sq0");
		sq1Field = mainFrame.lookupTextField("sq1");

		displayStyleField = mainFrame.lookupTextField("displaystyle");
		startTimeField = mainFrame.lookupTextField("starttime");
		hField = mainFrame.lookupTextField("h");
		mField = mainFrame.lookupTextField("m");
		sField = mainFrame.lookupTextField("s");
		fField = mainFrame.lookupTextField("f");
		frameRateField = mainFrame.lookupTextField("framerate");

		a0tcField = mainFrame.lookupTextField("a0");
		a1tcField = mainFrame.lookupTextField("a1");
		a2tcField = mainFrame.lookupTextField("a2");
		b0tcField = mainFrame.lookupTextField("b0");
		b1tcField = mainFrame.lookupTextField("b1");
		b2tcField = mainFrame.lookupTextField("b2");
		c0tcField = mainFrame.lookupTextField("c0");
		c1tcField = mainFrame.lookupTextField("c1");
		c2tcField = mainFrame.lookupTextField("c2");
		d0tcField = mainFrame.lookupTextField("d0");
		d1tcField = mainFrame.lookupTextField("d1");
		d2tcField = mainFrame.lookupTextField("d2");
		e0tcField = mainFrame.lookupTextField("e0");
		e1tcField = mainFrame.lookupTextField("e1");
		e2tcField = mainFrame.lookupTextField("e2");
		f0tcField = mainFrame.lookupTextField("f0");
		f1tcField = mainFrame.lookupTextField("f1");
		f2tcField = mainFrame.lookupTextField("f2");

		if (csn.equals("tempochange")) {

			a0tcField.setOpaque(false);
			a1tcField.setOpaque(false);
			a2tcField.setOpaque(false);
			b0tcField.setOpaque(false);
			b1tcField.setOpaque(false);
			b2tcField.setOpaque(false);
			c0tcField.setOpaque(false);
			c1tcField.setOpaque(false);
			c2tcField.setOpaque(false);
			d0tcField.setOpaque(false);
			d1tcField.setOpaque(false);
			d2tcField.setOpaque(false);
			e0tcField.setOpaque(false);
			e1tcField.setOpaque(false);
			e2tcField.setOpaque(false);
			f0tcField.setOpaque(false);
			f1tcField.setOpaque(false);
			f2tcField.setOpaque(false);

			tempoChangeField = mainFrame.lookupTextField("tempochange");
			initialTempoField = mainFrame.lookupTextField("initialtempo");
		}

		b1tcLabel = mainFrame.lookupLabel("b1");
		c1tcLabel = mainFrame.lookupLabel("c1");
		d1tcLabel = mainFrame.lookupLabel("d1");
		e1tcLabel = mainFrame.lookupLabel("e1");
		f1tcLabel = mainFrame.lookupLabel("f1");

		b2tcLabel = mainFrame.lookupLabel("b2");
		c2tcLabel = mainFrame.lookupLabel("c2");
		d2tcLabel = mainFrame.lookupLabel("d2");
		e2tcLabel = mainFrame.lookupLabel("e2");
		f2tcLabel = mainFrame.lookupLabel("f2");

		noteValueField = mainFrame.lookupTextField("notevalue");
		swingField = mainFrame.lookupTextField("swing");
		notes0Field = mainFrame.lookupTextField("notes0");
		notes1Field = mainFrame.lookupTextField("notes1");
		time0Field = mainFrame.lookupTextField("time0");
		time1Field = mainFrame.lookupTextField("time1");
		time2Field = mainFrame.lookupTextField("time2");
		time3Field = mainFrame.lookupTextField("time3");
		time4Field = mainFrame.lookupTextField("time4");
		time5Field = mainFrame.lookupTextField("time5");
		shiftTimingField = mainFrame.lookupTextField("shifttiming");
		amountField = mainFrame.lookupTextField("amount");
		swingLabel = mainFrame.lookupLabel("swing");
		notes1Label = mainFrame.lookupLabel("notes1");

		bar0Field = mainFrame.lookupTextField("bar0");
		bar1Field = mainFrame.lookupTextField("bar1");
		newTsigField = mainFrame.lookupTextField("newtsig");

		countInField = mainFrame.lookupTextField("countin");
		inPlayField = mainFrame.lookupTextField("inplay");
		rateField = mainFrame.lookupTextField("rate");
		inRecField = mainFrame.lookupTextField("inrec");
		waitForKeyField = mainFrame.lookupTextField("waitforkey");

		firstBarField = mainFrame.lookupTextField("firstbar");
		lastBarField = mainFrame.lookupTextField("lastbar");
		numberOfBarsField = mainFrame.lookupTextField("numberofbars");

		changeBarsAfterBarField = mainFrame.lookupTextField("afterbar");
		changeBarsNumberOfBarsField = mainFrame.lookupTextField("numberofbars");
		changeBarsFirstBarField = mainFrame.lookupTextField("firstbar");
		changeBarsLastBarField = mainFrame.lookupTextField("lastbar");

		inThisTrackField = mainFrame.lookupTextField("inthistrack");

		newBarsField = mainFrame.lookupTextField("newbars");

		message0Label = mainFrame.lookupLabel("message0");
		message1Label = mainFrame.lookupLabel("message1");
		currentLabel = mainFrame.lookupLabel("current");

		a0mrsField = mainFrame.lookupTextField("a0");
		a1mrsField = mainFrame.lookupTextField("a1");
		a2mrsField = mainFrame.lookupTextField("a2");
		b0mrsField = mainFrame.lookupTextField("b0");
		b1mrsField = mainFrame.lookupTextField("b1");
		b2mrsField = mainFrame.lookupTextField("b2");
		c0mrsField = mainFrame.lookupTextField("c0");
		c1mrsField = mainFrame.lookupTextField("c1");
		c2mrsField = mainFrame.lookupTextField("c2");

		receiveChField = mainFrame.lookupTextField("receivech");
		progChangeSeqField = mainFrame.lookupTextField("seq");
		sustainPedalToDurationField = mainFrame.lookupTextField("duration");
		midiFilterField = mainFrame.lookupTextField("midifilter");
		typeField = mainFrame.lookupTextField("type");
		passField = mainFrame.lookupTextField("pass");

		softThruField = mainFrame.lookupTextField("softthru");
		deviceNumberField = mainFrame.lookupTextField("devicenumber");
		firstLetterField = mainFrame.lookupTextField("firstletter");
		deviceNameLabel = mainFrame.lookupLabel("devicename");

		editTypeField = mainFrame.lookupTextField("edittype");
		valueField = mainFrame.lookupTextField("value");

		swGui.deleteObservers();
		swGui.addObserver(this);
		nameGui.deleteObservers();
		nameGui.addObserver(this);
		sequencer.deleteObservers();
		sequencer.addObserver(this);
		mpcSequence.deleteObservers();
		mpcSequence.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);
		timeSig.deleteObservers();
		timeSig.addObserver(this);

		for (HorizontalBar h : hBars)
			h.setVisible(false);

		if (csn.equals("sequence")) {
			sequenceNameFirstLetterField.setText(mpcSequence.getName().substring(0, 1));
			defaultSequenceNameFirstLetterField.setText(sequencer.getDefaultSequenceName().substring(0, 1));
			sequenceNameRestLabel.setText(mpcSequence.getName().substring(1, mpcSequence.getName().length()));
			defaultSequenceNameRestLabel.setText(
					sequencer.getDefaultSequenceName().substring(1, sequencer.getDefaultSequenceName().length()));
		}

		if (csn.equals("track")) {
			trackNameFirstLetterField.setText(track.getName().substring(0, 1));
			defaultTrackNameFirstLetterField.setText(sequencer.getDefaultTrackName(trackNum).substring(0, 1));
			trackNameRestLabel.setText(track.getName().substring(1, track.getName().length()));
			defaultTrackNameRestLabel.setText(sequencer.getDefaultTrackName(trackNum).substring(1,
					sequencer.getDefaultTrackName(trackNum).length()));
		}

		if (csn.equals("deletesequence")) displaySequenceNumberName();

		if (csn.equals("deletetrack")) displayTrackNumber();

		if (csn.equals("copysequence")) displaySequenceNumberNames();

		if (csn.equals("copytrack")) displayTrackNumberNames();

		if (csn.equals("timedisplay")) {
			displayDisplayStyle();
			displayStartTime();
			displayFrameRate();
		}

		if (csn.equals("tempochange")) {
			initVisibleEvents();
			displayTempoChange0();
			displayTempoChange1();
			displayTempoChange2();
			displayTempoChangeOn();
			displayInitialTempo();
		}

		if (csn.equals("timingcorrect")) {
			swGui.setTime1(mpcSequence.getLastTick());
			displayNoteValue();
			displaySwing();
			displayShiftTiming();
			displayAmount();
			displayTime();
			displayNotes();
		}

		if (csn.equals("changetsig")) {
			newTimeSignature.deleteObservers();
			newTimeSignature.addObserver(this);
			displayBars();
			displayNewTsig();
		}

		if (csn.equals("countmetronome")) {
			displayCountIn();
			displayInPlay();
			displayRate();
			displayInRec();
			displayWaitForKey();
		}

		if (csn.equals("loopbarswindow")) {
			displayFirstBar();
			displayLastBar();
			displayNumberOfBars();
		}

		if (csn.equals("changebars")) {
			displayChangeBarsAfterBar();
			displayChangeBarsNumberOfBars();
			displayChangeBarsFirstBar();
			displayChangeBarsLastBar();
		}

		if (csn.equals("transmitprogramchanges")) {
			displayTransmitProgramChangesInThisTrack();
		}

		if (csn.equals("changebars2")) {
			currentLabel.setText("" + (mpcSequence.getLastBar() + 1));
			displayNewBars();
		}

		if (csn.equals("multirecordingsetup")) {
			for (MultiRecordingSetupLine mrsLine : swGui.getMrsLines()) {
				mrsLine.setOut(((MpcTrack) mpcSequence.getTrack(mrsLine.getTrack())).getDevice());
			}
			swGui.setMrsYOffset(swGui.getMrsYOffset());
			displayMrsLine(0);
			displayMrsLine(1);
			displayMrsLine(2);
		}

		if (csn.equals("midiinput")) {
			displayReceiveCh();
			displayProgChangeSeq();
			displaySustainPedalToDuration();
			displayMidiFilter();
			displayType();
			displayPass();
		}

		if (csn.equals("midioutput")) {
			displaySoftThru();
			displayDeviceName();
		}

		if (csn.equals("editvelocity")) {
			displayEditType();
			displayValue();
			displayTime();
			displayNotes();
		}

	}

	private void displaySoftThru() {
		softThruField.setText(softThruNames[swGui.getSoftThru()]);
	}

	private void displayPass() {
		passField.setText(swGui.getPass() ? "YES" : "NO");
	}

	private void displayType() {
		typeField.setText(typeNames[swGui.getMidiFilterType()]);

	}

	private void displayMidiFilter() {
		midiFilterField.setText(swGui.isMidiFilterEnabled() ? "ON" : "OFF");
	}

	private void displaySustainPedalToDuration() {
		sustainPedalToDurationField.setText(swGui.isSustainPedalToDurationEnabled() ? "ON" : "OFF");
	}

	private void displayProgChangeSeq() {
		progChangeSeqField.setText(swGui.getProgChangeSeq() ? "ON" : "OFF");
	}

	private void displayReceiveCh() {
		if (swGui.getReceiveCh() == -1) {
			receiveChField.setText("ALL");
		} else {
			receiveChField.setText("" + (swGui.getReceiveCh() + 1));
		}

	}

	private void displayMrsLine(int i) {
		if (i == 0) {
			a0mrsField.setText(inNames[swGui.getVisibleMrsLines()[i].getIn()]);
			if (swGui.getVisibleMrsLines()[i].getTrack() == -1) {
				b0mrsField.setText("---OFF");
			} else {
				b0mrsField.setText(Util.padLeft2Zeroes(swGui.getVisibleMrsLines()[i].getTrack() + 1) + "-"
						+ mpcSequence.getTrack(swGui.getVisibleMrsLines()[i].getTrack()).getName());
			}

			if (swGui.getVisibleMrsLines()[i].getOut() == 0) {
				c0mrsField.setText("OFF");
			} else {
				if (swGui.getVisibleMrsLines()[i].getOut() >= 17) {
					c0mrsField.setText(Util.padLeftSpace("" + (swGui.getVisibleMrsLines()[i].getOut() - 16), 2) + "B");
				} else {
					c0mrsField.setText(Util.padLeftSpace("" + swGui.getVisibleMrsLines()[i].getOut(), 2) + "A");
				}
			}
		}
		if (i == 1) {
			a1mrsField.setText(inNames[swGui.getVisibleMrsLines()[i].getIn()]);
			if (swGui.getVisibleMrsLines()[i].getTrack() == -1) {
				b1mrsField.setText("---OFF");
			} else {
				b1mrsField.setText(Util.padLeft2Zeroes(swGui.getVisibleMrsLines()[i].getTrack() + 1) + "-"
						+ mpcSequence.getTrack(swGui.getVisibleMrsLines()[i].getTrack()).getName());
			}
			if (swGui.getVisibleMrsLines()[i].getOut() == 0) {
				c1mrsField.setText("OFF");
			} else {
				if (swGui.getVisibleMrsLines()[i].getOut() >= 17) {
					c1mrsField.setText(Util.padLeftSpace("" + (swGui.getVisibleMrsLines()[i].getOut() - 16), 2) + "B");
				} else {
					c1mrsField.setText(Util.padLeftSpace("" + swGui.getVisibleMrsLines()[i].getOut(), 2) + "A");
				}
			}

		}
		if (i == 2) {
			a2mrsField.setText(inNames[swGui.getVisibleMrsLines()[i].getIn()]);
			if (swGui.getVisibleMrsLines()[i].getTrack() == -1) {
				b2mrsField.setText("---OFF");
			} else {
				b2mrsField.setText(Util.padLeft2Zeroes(swGui.getVisibleMrsLines()[i].getTrack() + 1) + "-"
						+ mpcSequence.getTrack(swGui.getVisibleMrsLines()[i].getTrack()).getName());
			}
			if (swGui.getVisibleMrsLines()[i].getOut() == 0) {
				c2mrsField.setText("OFF");
			} else {
				if (swGui.getVisibleMrsLines()[i].getOut() >= 17) {
					c2mrsField.setText(Util.padLeftSpace("" + (swGui.getVisibleMrsLines()[i].getOut() - 16), 2) + "B");
				} else {
					c2mrsField.setText(Util.padLeftSpace("" + swGui.getVisibleMrsLines()[i].getOut(), 2) + "A");
				}
			}
		}
	}

	private void displayNewBars() {
		newBarsField.setText(Util.padLeftSpace("" + (swGui.getNewBars() + 1), 3));
		if (swGui.getNewBars() == mpcSequence.getLastBar()) {
			message0Label.setText("");
			message1Label.setText("");
		}

		if (swGui.getNewBars() > mpcSequence.getLastBar()) {
			message0Label.setText("Pressing DO IT will add");
			message1Label.setText("blank bars after last bar.");
		}

		if (swGui.getNewBars() < mpcSequence.getLastBar()) {
			message0Label.setText("Pressing DO IT will truncate");
			message1Label.setText("bars after last bar.");
		}
	}

	private void displayTransmitProgramChangesInThisTrack() {
		inThisTrackField.setText(swGui.getTransmitProgramChangesInThisTrack() ? "YES" : "NO");
	}

	private void displayChangeBarsLastBar() {
		changeBarsLastBarField.setText("" + (swGui.getChangeBarsLastBar() + 1));
	}

	private void displayChangeBarsFirstBar() {
		changeBarsFirstBarField.setText("" + (swGui.getChangeBarsFirstBar() + 1));
	}

	private void displayChangeBarsNumberOfBars() {
		changeBarsNumberOfBarsField.setText("" + swGui.getChangeBarsNumberOfBars());
	}

	private void displayChangeBarsAfterBar() {
		changeBarsAfterBarField.setText("" + swGui.getChangeBarsAfterBar());
	}

	private void displayNumberOfBars() {
		if (csn.equals("deletesequence")) return;
		numberOfBarsField.setText("" + ((mpcSequence.getLastLoopBar() - mpcSequence.getFirstLoopBar()) + 1));
	}

	private void displayLastBar() {
		if (csn.equals("deletesequence")) return;
		if (mpcSequence.isLastLoopBarEnd()) {
			lastBarField.setText("END");
		} else {
			lastBarField.setText("" + (mpcSequence.getLastLoopBar() + 1));
		}
	}

	private void displayFirstBar() {
		firstBarField.setText("" + (mpcSequence.getFirstLoopBar() + 1));
	}

	private void displayWaitForKey() {
		waitForKeyField.setText(swGui.isWaitForKeyEnabled() ? "ON" : "OFF");
	}

	private void displayInRec() {
		inRecField.setText(swGui.getInRec() ? "YES" : "NO");
	}

	private void displayRate() {
		rateField.setText(rateNames[swGui.getRate()]);
	}

	private void displayInPlay() {
		inPlayField.setText(swGui.getInPlay() ? "YES" : "NO");
	}

	private void displayCountIn() {
		countInField.setText(countInNames[swGui.getCountInMode()]);

	}

	private void displayBars() {
		bar0Field.setText("" + (swGui.getBar0() + 1));
		bar1Field.setText("" + (swGui.getBar1() + 1));
	}

	private void displayNewTsig() {
		if (csn.equals("deletesequence")) return;
		newTsigField.setText("" + newTimeSignature.getNumerator() + "/" + newTimeSignature.getDenominator());
	}

	private void displayNoteValue() {
		if (swGui.getNoteValue() != 0) {
			fb.enable(4);
		} else {
			fb.disable(4);
		}
		noteValueField.setText("" + noteValueNames[swGui.getNoteValue()]);
		if (swGui.getNoteValue() == 1 || swGui.getNoteValue() == 3) {
			swingLabel.setVisible(true);
			swingField.setVisible(true);
		} else {
			swingLabel.setVisible(false);
			swingField.setVisible(false);
		}
	}

	private void displaySwing() {
		swingField.setText("" + swGui.getSwing());

	}

	private void displayNotes() {
		if (track.getBusNumber() == 0) {
			notes0Field.setSize(8 * 6 * 2, 18);
			notes1Label.setVisible(true);
			notes1Field.setVisible(true);
			notes0Field.setText(Util.padLeftSpace("" + swGui.getMidiNote0(), 3) + "("
					+ Gui.noteNames[swGui.getMidiNote0()] + "\u00D4");
			notes1Field.setText(Util.padLeftSpace("" + swGui.getMidiNote1(), 3) + "("
					+ Gui.noteNames[swGui.getMidiNote1()] + "\u00D4");
		} else {
			notes0Field.setSize(6 * 6 * 2 + 4, 18);
			if (swGui.getDrumNote() != 34) {
				notes0Field.setText("" + swGui.getDrumNote() + "/"
						+ sampler.getPadName(program.getPadNumberFromNote(swGui.getDrumNote())));
			} else {
				notes0Field.setText("ALL");
			}
			notes1Label.setVisible(false);
			notes1Field.setVisible(false);
		}
	}

	private void displayTime() {
		time0Field.setText(Util.padLeft3Zeroes(SeqUtil.getBarFromTick(mpcSequence, swGui.getTime0()) + 1));
		time1Field.setText(Util.padLeft2Zeroes(SeqUtil.getBeat(mpcSequence, swGui.getTime0()) + 1));
		time2Field.setText(Util.padLeft2Zeroes(SeqUtil.getClockNumber(mpcSequence, swGui.getTime0())));
		time3Field.setText(Util.padLeft3Zeroes(SeqUtil.getBarFromTick(mpcSequence, swGui.getTime1()) + 1));
		time4Field.setText(Util.padLeft2Zeroes(SeqUtil.getBeat(mpcSequence, swGui.getTime1()) + 1));
		time5Field.setText(Util.padLeft2Zeroes(SeqUtil.getClockNumber(mpcSequence, swGui.getTime1())));
	}

	private void displayShiftTiming() {
		shiftTimingField.setText(swGui.isShiftTimingLater() ? "LATER" : "EARLIER");
	}

	private void displayAmount() {
		amountField.setText("" + swGui.getAmount());
	}

	private void initVisibleEvents() {
		visibleTempoChangeEvents = new TempoChangeEvent[3];
		for (int i = 0; i < 3; i++) {
			visibleTempoChangeEvents[i] = mpcSequence.getTempoChangeEvents().get(i + swGui.getTempoChangeOffset());
			visibleTempoChangeEvents[i].deleteObservers();
			visibleTempoChangeEvents[i].addObserver(this);
			if (mpcSequence.getTempoChangeEvents().size() <= i + swGui.getTempoChangeOffset() + 1) {
				for (int j = i + 1; j < 2; j++) {
					visibleTempoChangeEvents[j] = null;
				}
				break;
			}
		}
		swGui.setVisibleTempoChanges(visibleTempoChangeEvents);
	}

	private void displayInitialTempo() {
		if (csn.equals("deletesequence")) return;
		initialTempoField
				.setText(mpcSequence.getTempoChangeEvents().get(0).getInitialTempo().toString().replace(".", "\u00CB"));
	}

	private void displayTempoChangeOn() {
		tempoChangeField.setText(mpcSequence.isTempoChangeOn() ? "YES" : "NO");
	}

	private void displayTempoChange0() {
		hBars[1].setVisible(true);
		a0tcField.setText(" " + (swGui.getVisibleTempoChanges()[0].getStepNumber() + 1));
		b0tcField.setText(Util.padLeft3Zeroes(
				swGui.getVisibleTempoChanges()[0].getBar(timeSig.getNumerator(), timeSig.getDenominator()) + 1));
		c0tcField.setText(Util.padLeft2Zeroes(
				swGui.getVisibleTempoChanges()[0].getBeat(timeSig.getNumerator(), timeSig.getDenominator()) + 1));
		d0tcField.setText(Util.padLeft2Zeroes(
				swGui.getVisibleTempoChanges()[0].getClock(timeSig.getNumerator(), timeSig.getDenominator())));
		e0tcField.setText(Util.padLeftSpace("" + (swGui.getVisibleTempoChanges()[0].getRatio() / 10.0), 4).replace(".",
				"\u00CB"));
		double tempo = mpcSequence.getTempoChangeEvents().get(0).getInitialTempo().doubleValue()
				* (swGui.getVisibleTempoChanges()[0].getRatio() / 1000.0);
		if (tempo < 30) tempo = 30;
		if (tempo > 300) tempo = 300;
		String tempoString = "" + (double) Math.round(tempo * 10) / 10.0;
		tempoString = tempoString.replace(".", "\u00CB");
		f0tcField.setText(Util.padLeftSpace(tempoString, 5));
		hBars[1].setValue((int) ((tempo - 15) * (290 / 925.0)));
	}

	private void displayTempoChange1() {
		if (swGui.getVisibleTempoChanges()[1] == null) {
			a1tcField.setText("END");
			b1tcField.setVisible(false);
			c1tcField.setVisible(false);
			d1tcField.setVisible(false);
			e1tcField.setVisible(false);
			f1tcField.setVisible(false);
			b1tcLabel.setVisible(false);
			c1tcLabel.setVisible(false);
			d1tcLabel.setVisible(false);
			e1tcLabel.setVisible(false);
			f1tcLabel.setVisible(false);
			hBars[2].setVisible(false);
			return;
		}

		b1tcField.setVisible(true);
		c1tcField.setVisible(true);
		d1tcField.setVisible(true);
		e1tcField.setVisible(true);
		f1tcField.setVisible(true);
		b1tcLabel.setVisible(true);
		c1tcLabel.setVisible(true);
		d1tcLabel.setVisible(true);
		e1tcLabel.setVisible(true);
		f1tcLabel.setVisible(true);
		hBars[2].setVisible(true);
		a1tcField.setText(" " + (swGui.getVisibleTempoChanges()[1].getStepNumber() + 1));
		b1tcField.setText(Util.padLeft3Zeroes(
				swGui.getVisibleTempoChanges()[1].getBar(timeSig.getNumerator(), timeSig.getDenominator()) + 1));
		c1tcField.setText(Util.padLeft2Zeroes(
				swGui.getVisibleTempoChanges()[1].getBeat(timeSig.getNumerator(), timeSig.getDenominator()) + 1));
		d1tcField.setText(Util.padLeft2Zeroes(
				swGui.getVisibleTempoChanges()[1].getClock(timeSig.getNumerator(), timeSig.getDenominator())));
		e1tcField.setText(Util.padLeftSpace("" + (swGui.getVisibleTempoChanges()[1].getRatio() / 10.0), 4).replace(".",
				"\u00CB"));
		double tempo = mpcSequence.getTempoChangeEvents().get(0).getInitialTempo().doubleValue()
				* (swGui.getVisibleTempoChanges()[1].getRatio() / 1000.0);
		String tempoString = "" + (double) Math.round(tempo * 10) / 10;
		tempoString = tempoString.replace(".", "\u00CB");
		f1tcField.setText(Util.padLeftSpace(tempoString, 5));
		hBars[2].setValue((int) (swGui.getVisibleTempoChanges()[1].getRatio() * (127 / 4000.0)));
	}

	private void displayTempoChange2() {
		if (swGui.getVisibleTempoChanges()[2] == null) {
			if (swGui.getVisibleTempoChanges()[1] == null) {
				a2tcField.setVisible(false);
			} else {
				a2tcField.setText("END");
			}
			b2tcField.setVisible(false);
			c2tcField.setVisible(false);
			d2tcField.setVisible(false);
			e2tcField.setVisible(false);
			f2tcField.setVisible(false);
			b2tcLabel.setVisible(false);
			c2tcLabel.setVisible(false);
			d2tcLabel.setVisible(false);
			e2tcLabel.setVisible(false);
			f2tcLabel.setVisible(false);
			hBars[3].setVisible(false);
			return;
		}

		b2tcField.setVisible(true);
		c2tcField.setVisible(true);
		d2tcField.setVisible(true);
		e2tcField.setVisible(true);
		f2tcField.setVisible(true);
		b2tcLabel.setVisible(true);
		c2tcLabel.setVisible(true);
		d2tcLabel.setVisible(true);
		e2tcLabel.setVisible(true);
		f2tcLabel.setVisible(true);
		hBars[3].setVisible(true);

		a2tcField.setText(" " + (swGui.getVisibleTempoChanges()[2].getStepNumber() + 1));
		b2tcField.setText(Util.padLeft3Zeroes(
				swGui.getVisibleTempoChanges()[2].getBar(timeSig.getNumerator(), timeSig.getDenominator()) + 1));
		c2tcField.setText(Util.padLeft2Zeroes(
				swGui.getVisibleTempoChanges()[2].getBeat(timeSig.getNumerator(), timeSig.getDenominator()) + 1));
		d2tcField.setText(Util.padLeft2Zeroes(
				swGui.getVisibleTempoChanges()[2].getClock(timeSig.getNumerator(), timeSig.getDenominator())));
		e2tcField.setText(Util.padLeftSpace("" + (swGui.getVisibleTempoChanges()[2].getRatio() / 10.0), 4).replace(".",
				"\u00CB"));
		double tempo = mpcSequence.getTempoChangeEvents().get(0).getInitialTempo().doubleValue()
				* (swGui.getVisibleTempoChanges()[2].getRatio() / 1000.0);
		String tempoString = "" + (double) Math.round(tempo * 10) / 10;
		tempoString = tempoString.replace(".", "\u00CB");
		f2tcField.setText(Util.padLeftSpace(tempoString, 5));
		hBars[3].setValue((int) (swGui.getVisibleTempoChanges()[2].getRatio() * (127 / 4000.0)));
	}

	private void displayDisplayStyle() {
		displayStyleField.setText(displayStyleNames[swGui.getDisplayStyle()]);
	}

	private void displayStartTime() {
		startTimeField.setText(Util.padLeft2Zeroes(swGui.getStartTime()));
		hField.setText(Util.padLeft2Zeroes(swGui.getH()));
		mField.setText(Util.padLeft2Zeroes(swGui.getM()));
		sField.setText(Util.padLeft2Zeroes(swGui.getS()));
		fField.setText(Util.padLeft2Zeroes(swGui.getF()));
	}

	private void displayFrameRate() {
		frameRateField.setText(frameRateNames[swGui.getFrameRate()]);
	}

	@Override
	public void update(Observable o, Object arg) {

		track.deleteObservers();
		mpcSequence.deleteObservers();
		timeSig.deleteObservers();
		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();
		track.addObserver(this);
		mpcSequence.addObserver(this);
		timeSig.addObserver(this);

		switch ((String) arg) {

		case "initialtempo":
			displayInitialTempo();
			initVisibleEvents();
			displayTempoChange0();
			displayTempoChange1();
			displayTempoChange2();
			break;
		
		case "seqnumbername":
			displaySequenceNumberName();
			break;

		case "tracknumber":
			displayTrackNumber();
			break;

		case "tr0":
			displayTrackNumberNames();
			break;

		case "tr1":
			displayTrackNumberNames();
			break;

		case "sq0":
			displaySequenceNumberNames();
			break;

		case "sq1":
			displaySequenceNumberNames();
			break;

		case "starttime":
			displayStartTime();
			break;

		case "displaystyle":
			displayDisplayStyle();
			break;

		case "framerate":
			displayFrameRate();
			break;

		case "tempochange":
			initVisibleEvents();
			if (mainFrame.getFocus(slp.getWindowPanel()).contains("0")) {
				displayTempoChange0();
			}
			if (mainFrame.getFocus(slp.getWindowPanel()).contains("1")) {
				displayTempoChange1();
			}
			if (mainFrame.getFocus(slp.getWindowPanel()).contains("2")) {
				displayTempoChange2();
			}
			break;

		case "offset":
			initVisibleEvents();
			displayTempoChange0();
			displayTempoChange1();
			displayTempoChange2();
			break;

		case "tempochangeon":
			displayTempoChangeOn();
			break;

		case "tempo":
			displayInitialTempo();
			break;

		case "time":
			displayTime();
			break;

		case "notevalue":
			displayNoteValue();
			break;

		case "swing":
			displaySwing();
			break;

		case "shifttiming":
			displayShiftTiming();
			break;

		case "amount":
			displayAmount();
			break;

		case "notes":
			displayNotes();
			break;

		case "bars":
			displayBars();
			break;

		case "timesignature":
			displayNewTsig();
			break;

		case "countin":
			displayCountIn();
			break;

		case "inplay":
			displayInPlay();
			break;

		case "rate":
			displayRate();
			break;

		case "inrec":
			displayInRec();
			break;

		case "waitforkey":
			displayWaitForKey();
			break;

		case "firstloopbar":
			displayFirstBar();
			displayNumberOfBars();
			break;

		case "lastloopbar":
			displayLastBar();
			displayNumberOfBars();
			break;

		case "numberofbars":
			displayNumberOfBars();
			displayLastBar();
			break;

		case "transmitprogramchangesinthistrack":
			displayTransmitProgramChangesInThisTrack();
			break;

		case "changebarsafterbar":
			displayChangeBarsAfterBar();
			break;

		case "changebarsnumberofbars":
			displayChangeBarsNumberOfBars();
			break;

		case "changebarsfirstbar":
			displayChangeBarsFirstBar();
			break;

		case "changebarslastbar":
			displayChangeBarsLastBar();
			break;

		case "newbars":
			displayNewBars();
			break;

		case "mrsline":
			int yPos = Integer.parseInt(mainFrame.getFocus(slp.getWindowPanel()).substring(1, 2));
			displayMrsLine(yPos);
			break;

		case "multirecordingsetup":
			displayMrsLine(0);
			displayMrsLine(1);
			displayMrsLine(2);
			break;

		case "receivech":
			displayReceiveCh();
			break;

		case "progchangeseq":
			displayProgChangeSeq();
			break;

		case "sustainpedaltoduration":
			displaySustainPedalToDuration();
			break;

		case "midifilter":
			displayMidiFilter();
			break;

		case "type":
			displayType();
			break;

		case "pass":
			displayPass();
			break;

		case "softthru":
			displaySoftThru();
			break;

		case "devicenumber":
			// displayDeviceNumber();
			displayDeviceName();
			break;

		case "edittype":
			displayEditType();
			break;

		case "value":
			displayValue();
			break;

		}
	}

	private void displayDeviceName() {
		String devName = mpcSequence.getDeviceName(swGui.getDeviceNumber() + 1);

		firstLetterField.setText(devName.substring(0, 1));
		deviceNameLabel.setText(devName.substring(1, devName.length()));

		String devNumber = null;
		if (swGui.getDeviceNumber() >= 16) {
			devNumber = Util.padLeftSpace("" + (swGui.getDeviceNumber() - 15), 2) + "B";

		} else {
			devNumber = Util.padLeftSpace("" + (swGui.getDeviceNumber() + 1), 2) + "A";
		}
		deviceNumberField.setText(devNumber);
	}

	private void displayValue() {
		valueField.setText("" + swGui.getValue());
	}

	private void displayEditType() {
		editTypeField.setText(editTypeNames[swGui.getEditType()]);
	}

	private void displayTrackNumber() {
		trField.setText(Util.padLeft2Zeroes(swGui.getTrackNumber() + 1) + "-"
				+ mpcSequence.getTrack(swGui.getTrackNumber()).getName());
	}

	private void displaySequenceNumberName() {
		String sequenceName = sequencer.getActiveSequence().getName();
		sqField.setText(Util.padLeft2Zeroes(sequencer.getActiveSequenceIndex() + 1) + "-" + sequenceName);
	}

	private void displaySequenceNumberNames() {
		String sq0 = sequencer.getSequence(swGui.getSq0()).getName();
		sq0Field.setText(Util.padLeft2Zeroes(swGui.getSq0() + 1) + "-" + sq0);
		String sq1 = sequencer.getSequence(swGui.getSq1()).getName();
		sq1Field.setText(Util.padLeft2Zeroes(swGui.getSq1() + 1) + "-" + sq1);
	}

	private void displayTrackNumberNames() {
		String tr0 = mpcSequence.getTrack(swGui.getTr0()).getName();
		tr0Field.setText(Util.padLeft2Zeroes(swGui.getTr0() + 1) + "-" + tr0);

		String tr1 = mpcSequence.getTrack(swGui.getTr1()).getName();
		tr1Field.setText(Util.padLeft2Zeroes(swGui.getTr1() + 1) + "-" + tr1);
	}
}
