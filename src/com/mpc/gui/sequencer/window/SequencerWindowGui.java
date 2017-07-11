package com.mpc.gui.sequencer.window;

import java.util.Observable;

import com.mpc.gui.MainFrame;
import com.mpc.sequencer.TempoChangeEvent;
import com.mpc.sequencer.TimeSignature;

public class SequencerWindowGui extends Observable {

	private int trackNumber;
	private int sq0;
	private int sq1;
	private int tr0;
	private int tr1;
	private int displayStyle;
	private int h;
	private int startTime;
	private int m;
	private int f;
	private int s;
	private int frameRate;
	private MultiRecordingSetupLine[] mrsLines;
	private int mrsYOffset;
	private TempoChangeEvent[] visibleTempoChanges;
	private int tempoChangeOffset;
	private int amount;
	private boolean shiftTimingLater;
	private int drumNote = 34;
	private long time0;
	private long time1;
	private int midiNote0;
	private int midiNote1;
	private int swing = 50;
	private int noteValue = 3;
	private int bar0;
	private int bar1;
	private TimeSignature newTimeSignature;
	private int countIn;
	private boolean inPlay = true;
	private int rate;
	private boolean waitForKey;
	private boolean inRec = true;
	// private int lastBar;
	// private int firstBar;
	// private boolean lastBarEnd;
	private int changeBarsLastBar;
	private int changeBarsFirstBar;
	private int changeBarsNumberOfBars;
	private int changeBarsAfterBar;
	private boolean transmitProgramChangesInThisTrack;
	private int newBars;
	private MultiRecordingSetupLine[] visibleMrsLines;
	private int receiveCh = 0; // 0 = all
	private boolean progChangeSeq;
	private boolean sustainPedalToDuration;
	private boolean midiFilter;
	private boolean pass;
	private int filterType;
	private int softThru;
	private int deviceNumber;
	private int value = 1;
	private int editType;
	private int clickVolume = 100;
	private int clickOutput;
	private int metronomeSound; // 0 = internal click, 1..4 = DRUM1..4
	private int accentVelo = 127;
	private int accentNote = 35;
	private int normalVelo = 64;
	private int normalNote = 35;
	private boolean notePassEnabled;
	private boolean pitchBendPassEnabled;
	private boolean pgmChangePassEnabled;
	private boolean chPressurePassEnabled;
	private boolean polyPressurePassEnabled;
	private boolean exclusivePassEnabled;
	private int tapAvg;
	private boolean pgmChangeToSeqEnabled;

	public SequencerWindowGui(MainFrame mainFrame) {
		mrsLines = new MultiRecordingSetupLine[34];
		for (int i = 0; i < 34; i++) {
			mrsLines[i] = new MultiRecordingSetupLine(i);
			mrsLines[i].setTrack(i);
			mrsLines[i].setOut(0);
		}
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int i) {
		if (i < 0 || i > 63) return;
		trackNumber = i;
		setChanged();
		notifyObservers("tracknumber");
	}

	public int getSq0() {
		return sq0;
	}

	public void setSq0(int i) {
		if (i < 0 || i > 98) return;
		sq0 = i;
		setChanged();
		notifyObservers("sq0");
	}

	public int getSq1() {
		return sq1;
	}

	public void setSq1(int i) {
		if (i < 0 || i > 98) return;
		sq1 = i;
		setChanged();
		notifyObservers("sq1");
	}

	public int getTr0() {
		return tr0;
	}

	public void setTr0(int i) {
		if (i < 0 || i > 63) return;
		tr0 = i;
		setChanged();
		notifyObservers("tr0");
	}

	public int getTr1() {
		return tr1;
	}

	public void setTr1(int i) {
		if (i < 0 || i > 63) return;
		tr1 = i;
		setChanged();
		notifyObservers("tr1");
	}

	public int getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(int i) {
		if (i < 0 || i > 1) return;
		displayStyle = i;
		setChanged();
		notifyObservers("displaystyle");
	}

	public int getH() {
		return h;
	}

	public void setH(int i) {
		if (i < 0 || i > 59) return;
		h = i;
		setChanged();
		notifyObservers("starttime");
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int i) {
		if (i < 0 || i > 23) return;
		startTime = i;
		setChanged();
		notifyObservers("starttime");
	}

	public int getM() {
		return m;
	}

	public void setM(int i) {
		if (i < 0 || i > 59) return;
		m = i;
		setChanged();
		notifyObservers("starttime");
	}

	public int getF() {
		return f;
	}

	public void setF(int i) {
		if (i < 0 || i > 99) return;
		f = i;
		setChanged();
		notifyObservers("starttime");
	}

	public int getS() {
		return s;
	}

	public void setS(int i) {
		if (i < 0 || i > 29) return;
		s = i;
		setChanged();
		notifyObservers("starttime");
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int i) {
		if (i < 0 || i > 3) return;
		frameRate = i;
		setChanged();
		notifyObservers("framerate");
	}

	public TempoChangeEvent[] getVisibleTempoChanges() {
		return visibleTempoChanges;
	}

	public void setVisibleTempoChanges(TempoChangeEvent[] vtc) {
		visibleTempoChanges = vtc;
	}

	public int getTempoChangeOffset() {
		return tempoChangeOffset;
	}

	public void setTempoChangeOffset(int i) {
		if (i < 0) return;
		tempoChangeOffset = i;
		setChanged();
		notifyObservers("offset");
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int i) {
		if (i < 0) return;

		if (noteValue == 0 && i > 0) return;

		if (noteValue == 1 && i > 23) return;

		if (noteValue == 2 && i > 15) return;

		if (noteValue == 3 && i > 11) return;

		if (noteValue == 4 && i > 7) return;

		if (noteValue == 5 && i > 5) return;

		if (noteValue == 6 && i > 3) return;

		amount = i;
		setChanged();
		notifyObservers("amount");
	}

	public boolean isShiftTimingLater() {
		return shiftTimingLater;
	}

	public void setShiftTimingLater(boolean b) {
		shiftTimingLater = b;
		setChanged();
		notifyObservers("shifttiming");
	}

	public int getDrumNote() {
		return drumNote;
	}

	public void setDrumNote(int i) {
		if (i < 34 || i > 98) return;
		drumNote = i;
		setChanged();
		notifyObservers("notes");
	}

	public int getMidiNote0() {
		return midiNote0;
	}

	public void setMidiNote0(int i) {
		if (i < 0 || i > 127) return;
		midiNote0 = i;
		setChanged();
		notifyObservers("notes");
	}

	public int getMidiNote1() {
		return midiNote1;
	}

	public void setMidiNote1(int i) {
		if (i < 0 || i > 127) return;
		midiNote1 = i;
		setChanged();
		notifyObservers("notes");
	}

	public int getSwing() {
		return swing;
	}

	public void setSwing(int i) {
		if (i < 50 || i > 75) return;
		swing = i;
		setChanged();
		notifyObservers("swing");
	}

	public int getNoteValue() {
		return noteValue;
	}

	public void setNoteValue(int i) {
		if (i < 0 || i > 6) return;
		noteValue = i;
		if (noteValue == 0) {
			setAmount(0);
		}

		if (noteValue == 2) {
			if (amount > 15) setAmount(15);
		}
		if (noteValue == 3) {
			if (amount > 11) setAmount(11);
		}
		if (noteValue == 4) {
			if (amount > 7) setAmount(7);
		}
		if (noteValue == 5) {
			if (amount > 5) setAmount(5);
		}
		if (noteValue == 6) {
			if (amount > 3) setAmount(3);
		}
		setChanged();
		notifyObservers("notevalue");
	}

	public long getTime0() {
		return time0;
	}

	public void setTime0(long time0) {
		this.time0 = time0;
		if (time0 > time1) time1 = time0;
		setChanged();
		notifyObservers("time");
	}

	public long getTime1() {
		return time1;
	}

	public void setTime1(long time1) {
		this.time1 = time1;
		if (time1 < time0) time0 = time1;
		setChanged();
		notifyObservers("time");
	}

	public int getBar0() {
		return bar0;
	}

	public void setBar0(int i, int max) {
		if (i < 0 || i > max) return;
		bar0 = i;
		if (bar0 > bar1) bar1 = bar0;
		setChanged();
		notifyObservers("bars");
	}

	public int getBar1() {
		return bar1;
	}

	public void setBar1(int i, int max) {
		if (i < 0 || i > max) return;
		bar1 = i;
		setChanged();
		notifyObservers("bars");
	}

	public TimeSignature getNewTimeSignature() {
		return newTimeSignature;
	}

	public void setNewTimeSignature(TimeSignature ts) {
		newTimeSignature = ts;
	}

	public int getCountInMode() {
		return countIn;
	}

	public void setCountIn(int i) {
		if (i < 0 || i > 2) return;
		countIn = i;
		setChanged();
		notifyObservers("countin");
	}

	public void setInPlay(boolean b) {
		if (inPlay == b) return;
		inPlay = b;
		setChanged();
		notifyObservers("inplay");
	}

	public boolean getInPlay() {
		return inPlay;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int i) {
		if (i < 0 || i > 7) return;
		rate = i;
		setChanged();
		notifyObservers("rate");
	}

	public void setWaitForKey(boolean b) {
		if (waitForKey == b) return;
		waitForKey = b;
		setChanged();
		notifyObservers("waitforkey");
	}

	public boolean isWaitForKeyEnabled() {
		return waitForKey;
	}

	public void setInRec(boolean b) {
		if (inRec == b) return;
		inRec = b;
		setChanged();
		notifyObservers("inrec");
	}

	public boolean getInRec() {
		return inRec;
	}

	public int getChangeBarsLastBar() {
		return changeBarsLastBar;
	}

	public void setChangeBarsLastBar(int i, int max) {
		if (i < 0 || i > max) return;
		changeBarsLastBar = i;
		if (changeBarsLastBar < changeBarsFirstBar) setChangeBarsFirstBar(changeBarsLastBar, max);
		setChanged();
		notifyObservers("changebarslastbar");
	}

	public int getChangeBarsFirstBar() {
		return changeBarsFirstBar;
	}

	public void setChangeBarsFirstBar(int i, int max) {
		if (i < 0 || i > max) return;
		changeBarsFirstBar = i;
		if (changeBarsFirstBar > changeBarsLastBar) setChangeBarsLastBar(changeBarsFirstBar, max);
		setChanged();
		notifyObservers("changebarsfirstbar");
	}

	public int getChangeBarsNumberOfBars() {
		return changeBarsNumberOfBars;
	}

	public void setChangeBarsNumberOfBars(int i, int max) {
		if (i < 0 || i > (999 - (max + 1))) return;
		changeBarsNumberOfBars = i;
		setChanged();
		notifyObservers("changebarsnumberofbars");
	}

	public int getChangeBarsAfterBar() {
		return changeBarsAfterBar;
	}

	public void setChangeBarsAfterBar(int i, int max) {
		if (i < 0 || i > max + 1) return;
		changeBarsAfterBar = i;
		setChanged();
		notifyObservers("changebarsafterbar");
	}

	public boolean getTransmitProgramChangesInThisTrack() {
		return transmitProgramChangesInThisTrack;
	}

	public void setTransmitProgramChangesInThisTrack(boolean b) {
		transmitProgramChangesInThisTrack = b;
		setChanged();
		notifyObservers("transmitprogramchangesinthistrack");
	}

	public int getNewBars() {
		return newBars;
	}

	public void setNewBars(int i) {
		if (i < 0 || i > 998) return;
		newBars = i;
		setChanged();
		notifyObservers("newbars");
	}

	public MultiRecordingSetupLine[] getMrsLines() {
		return mrsLines;
	}

	public void setMrsYOffset(int i) {
		if (i < 0) return;
		if (i + 3 > mrsLines.length) return;
		visibleMrsLines = new MultiRecordingSetupLine[3];
		mrsYOffset = i;
		for (int j = 0; j < 3; j++) {
			visibleMrsLines[j] = mrsLines[mrsYOffset + j];
		}
		setChanged();
		notifyObservers("multirecordingsetup");
	}

	public int getMrsYOffset() {
		return mrsYOffset;
	}

	public void setMrsTrack(int inputNumber, int newTrackNumber) {
		mrsLines[inputNumber].setTrack(newTrackNumber);
		visibleMrsLines = new MultiRecordingSetupLine[3];
		for (int j = 0; j < 3; j++) {
			visibleMrsLines[j] = mrsLines[mrsYOffset + j];
		}
		setChanged();
		notifyObservers("mrsline");
	}

	public void setMrsOut(int inputNumber, int newOutputNumber) {
		mrsLines[inputNumber].setOut(newOutputNumber);
		visibleMrsLines = new MultiRecordingSetupLine[3];
		for (int j = 0; j < 3; j++) {
			visibleMrsLines[j] = mrsLines[mrsYOffset + j];
		}
		setChanged();
		notifyObservers("mrsline");
	}

	public void setVisbleMrsLines(MultiRecordingSetupLine[] newVisibleMrsLines) {
		visibleMrsLines = newVisibleMrsLines;
	}

	public MultiRecordingSetupLine[] getVisibleMrsLines() {
		return visibleMrsLines;
	}

	public void setReceiveCh(int i) {
		if (i < -1 || i > 15) return;
		receiveCh = i;
		setChanged();
		notifyObservers("receivech");
	}

	public int getReceiveCh() {
		return receiveCh;
	}

	public void setProgChangeSeq(boolean b) {
		if (progChangeSeq == b) return;
		progChangeSeq = b;
		setChanged();
		notifyObservers("progchangeseq");
	}

	public boolean getProgChangeSeq() {
		return progChangeSeq;
	}

	public void setSustainPedalToDuration(boolean b) {
		if (sustainPedalToDuration == b) return;
		sustainPedalToDuration = b;
		setChanged();
		notifyObservers("sustainpedaltoduration");
	}

	public boolean isSustainPedalToDurationEnabled() {
		return sustainPedalToDuration;
	}

	public void setMidiFilterEnabled(boolean b) {
		if (midiFilter == b) return;
		midiFilter = b;
		setChanged();
		notifyObservers("midifilter");
	}

	public boolean isMidiFilterEnabled() {
		return midiFilter;
	}

	public void setFilterType(int i) {
		if (i < 0 || i > 134) return;
		filterType = i;
		setChanged();
		notifyObservers("type");
	}

	public int getMidiFilterType() {
		return filterType;
	}

	public void setPass(boolean b) {
		if (pass == b) return;
		pass = b;
		setChanged();
		notifyObservers("pass");
	}

	public boolean getPass() {
		return pass;
	}

	public void setSoftThru(int i) {
		if (i < 0 || i > 4) return;
		softThru = i;
		setChanged();
		notifyObservers("softthru");
	}

	public int getSoftThru() {
		return softThru;
	}

	public void setDeviceNumber(int i) {
		if (i < 0 || i > 31) return;
		deviceNumber = i;
		setChanged();
		notifyObservers("devicenumber");
	}

	public int getDeviceNumber() {
		return deviceNumber;
	}

	public int getEditType() {
		return editType;
	}

	public void setEditType(int i) {
		if (i < 0 || i > 3) return;
		editType = i;
		setChanged();
		notifyObservers("edittype");
	}

	public int getValue() {
		return value;
	}

	public void setValue(int i) {
		if (i < 1 || i > 200) return;
		if (editType != 2 && i > 127) return;
		value = i;
		setChanged();
		notifyObservers("value");
	}

	public int getClickVolume() {
		return clickVolume;
	}

	public void setClickVolume(int i) {
		if (i < 0 || i > 100) return;
		clickVolume = i;
		setChanged();
		notifyObservers("clickvolume");
	}

	public int getClickOutput() {
		return clickOutput;
	}

	public void setClickOutput(int i) {
		if (i < 0 || i > 8) return;
		clickOutput = i;
		setChanged();
		notifyObservers("clickoutput");
	}

	public int getMetronomeSound() {
		return metronomeSound;
	}

	public void setMetronomeSound(int i) {
		if (i < 0 || i > 4) return;
		metronomeSound = i;
		setChanged();
		notifyObservers("metronomesound");
	}

	public int getAccentNote() {
		return accentNote;
	}

	public void setAccentNote(int i) {
		if (i<35||i>98) return;
		accentNote = i;
		setChanged();
		notifyObservers("accentnote");
	}
	
	public int getAccentVelo() {
		return accentVelo;
	}

	public void setAccentVelo(int i) {
		if (i < 0 || i > 127) return;
		accentVelo = i;
		setChanged();
		notifyObservers("accentvelo");
	}

	public int getNormalNote() {
		return normalNote;
	}
	
	public void setNormalNote(int i) {
		if (i<35||i>98) return;
		normalNote = i;
		setChanged();
		notifyObservers("normalnote");
	}
	
	public int getNormalVelo() {
		return normalVelo;
	}

	public void setNormalVelo(int i) {
		if (i < 0 || i > 127) return;
		normalVelo = i;
		setChanged();
		notifyObservers("normalvelo");
	}

	public boolean isNotePassEnabled() {
		return notePassEnabled;
	}

	public void setNotePassEnabled(boolean b) {
		notePassEnabled = b;
	}

	public boolean isPitchBendPassEnabled() {
		return pitchBendPassEnabled;
	}

	public void setPitchBendPassEnabled(boolean b) {
		pitchBendPassEnabled = b;
	}

	public boolean isPgmChangePassEnabled() {
		return pgmChangePassEnabled;
	}

	public void setPgmChangePassEnabled(boolean b) {
		pgmChangePassEnabled = b;
	}

	public boolean isChPressurePassEnabled() {
		return chPressurePassEnabled;
	}

	public void setChPressurePassEnabled(boolean b) {
		chPressurePassEnabled = b;
	}

	public boolean isPolyPressurePassEnabled() {
		return polyPressurePassEnabled;
	}

	public void setPolyPressurePassEnabled(boolean b) {
		polyPressurePassEnabled = b;
	}

	public boolean isExclusivePassEnabled() {
		return exclusivePassEnabled;
	}

	public void setExclusivePassEnabled(boolean b) {
		exclusivePassEnabled = b;
	}

	public int getTapAvg() {
		return tapAvg;
	}

	public void setTapAvg(int i) {
		tapAvg = i;
	}

	public boolean isPgmChangeToSeqEnabled() {
		return pgmChangeToSeqEnabled;
	}

	public void setPgmChangeToSeqEnabled(boolean b) {
		pgmChangeToSeqEnabled = b;
	}
	
}