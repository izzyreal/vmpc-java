package com.mpc.gui.sequencer;

import java.util.Observable;

import com.mpc.sequencer.MpcSequence;

public class EditSequenceGui extends Observable {
	private long time0;
	private long time1;

	private int editFunctionNumber;

	private int drumNote;
	
	private int fromSq;
	private int tr0;

	private int toSq;
	private int tr1;
	
	private boolean modeMerge;
	private long startTicks;
	
	private int copies;
	private int midiNote0;
	private int midiNote1;
	private int durationMode;
	private int velocityMode;
	private int transposeAmount;
	private int durationValue;
	private int velocityValue;
	
	public EditSequenceGui() {
	copies = 1;	
	drumNote = 34;
	midiNote1 = 127;
	durationValue = 1;
	velocityValue = 1;
	}
	
	public void setEditFunctionNumber(int i) {
		if (i<0||i>3) return;
		editFunctionNumber = i;
		setChanged();
		notifyObservers("editfunction");
	}
	
	public int getEditFunctionNumber() {
		return editFunctionNumber;
	}

	public int getDrumNote() {
		return drumNote;
	}

	public void setDrumNote(int i) {
		if (i<34||i>98) return;
		drumNote = i;
		setChanged();
		notifyObservers("drumnote");
	}

	public int getMidiNote0() {
		return midiNote0;
	}

	public void setMidiNote0(int i) {
		if (i<0||i>127) return;
		midiNote0 = i;
		setChanged();
		notifyObservers("midinote0");
	}
	
	public int getMidiNote1() {
		return midiNote1;
	}

	public void setMidiNote1(int i) {
		if (i<0||i>127) return;
		midiNote1 = i;
		setChanged();
		notifyObservers("midinote1");
	}
	
	public int getFromSq() {
		return fromSq;
	}

	public void setFromSq(int i) {
		if (i<0||i>99) return;
		fromSq = i;
		setChanged();
		notifyObservers("fromsq");
	}

	public int getTr0() {
		return tr0;
	}

	public void setTr0(int i) {
		if (i<0||i>63) return;
		tr0 = i;
		setChanged();
		notifyObservers("tr0");
	}

	public int getToSq() {
		return toSq;
	}

	public void setToSq(int i) {
		if (i<0||i>99) return;
		toSq = i;
		setChanged();
		notifyObservers("tosq");
	}

	public int getTr1() {
		return tr1;
	}

	public void setTr1(int i) {
		if (i<0||i>63) return;
		tr1 = i;
		setChanged();
		notifyObservers("tr1");
	}

	public boolean isModeMerge() {
		return modeMerge;
	}

	public void setModeMerge(boolean b) {
		modeMerge = b;
		setChanged();
		notifyObservers("modevalue");
	}

	public int getCopies() {
		return copies;
	}

	public void setCopies(int i) {
		if (i<1||i>999) return;
		copies = i;
		setChanged();
		notifyObservers("copies");
	}
	
	public void setDurationMode(int i) {
		if (i<0||i>3) return;
		durationMode = i;
		if (durationMode == 2 && durationValue > 200) setDurationValue(200);
		setChanged();
		notifyObservers("modevalue");
	}

	public int getDurationMode() {
		return durationMode;
	}

	public void setVelocityMode(int i) {
		if (i<0||i>3) return;
		velocityMode = i;
		if (velocityMode != 2 && velocityValue > 127) setVelocityValue(127);
		setChanged();
		notifyObservers("modevalue");
	}

	public int getVelocityMode() {
		return velocityMode;
	}

	public void setTransposeAmount(int i) {
		if (i<-12||i>12) return;
		transposeAmount = i;
		setChanged();
		notifyObservers("modevalue");
	}
	
	public int getTransposeAmount() {
		return transposeAmount;
	}

	public int getDurationValue() {
		return durationValue;
	}
	
	public void setDurationValue(int i) {
		if (i<1||i>9999) return;
		if (durationMode == 2 && i>200) return;
		durationValue = i;
		setChanged();
		notifyObservers("copies");
	}
	
	public int getVelocityValue() {
		return velocityValue;
	}
	
	public void setVelocityValue(int i) {
		if (i<1||i>200) return;
		if (velocityMode != 2 && i>127) return;
		velocityValue = i;
		setChanged();
		notifyObservers("copies");
	}
	
	public long getTime0() {
		return time0;
	}

	public void setTime0(long time0) {
		this.time0 = time0;
		if (time0>time1) time1 = time0;
		setChanged();
		notifyObservers("time");
	}

	public long getTime1() {
		return time1;
	}

	public void setTime1(long time1) {
		this.time1 = time1;
		if (time1<time0) time0 = time1;
		setChanged();
		notifyObservers("time");
	}

	public long getStartTicks() {
		return startTicks;
	}

	public void setStartTicks(long startTicks) {
		this.startTicks = startTicks;
		setChanged();
		notifyObservers("start");
	}
	
	public long setBarNumber(int i, MpcSequence s, long position) {
		if (i < 0)
			return 0;
		int difference = i - getBarNumber(s, position);
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position + (difference * denTicks * 4) > s.getLastTick()) {
			position = s.getLastTick();
		} else {
			position = position + (difference * denTicks * 4);
		}
		return position;
	}

	public long setBeatNumber(int i, MpcSequence s, long position) {
		if (i < 0) i = 0;
		int difference = i - getBeatNumber(s, position);
		int num = s.getTimeSignature().getNumerator();
		if (i>=num) return position;
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position + (difference * denTicks) > s.getLastTick()) {
			position = s.getLastTick();
		} else {
			position = position + (difference * denTicks);
		}
		return position;
	}

	public long setClockNumber(int i, MpcSequence s, long position) {
		if (i < 0) i = 0;
		int difference = i - getClockNumber(s, position);
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (i>denTicks-1) return position;
		if (position + difference > s.getLastTick()) {
			position = s.getLastTick();
		} else {
			position = position + difference;
		}
		return position;
	}

	public static int getBarNumber(MpcSequence s, long position) {
		int num = s.getTimeSignature().getNumerator();
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position == 0)
			return 0;
		int bar = (int) Math.floor(position / (denTicks * num));
		return bar;
	}

	public static int getBeatNumber(MpcSequence s, long position) {
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position == 0)
			return 0;
		int beat = (int) Math.floor(position / (denTicks));
		beat = beat % den;
		return beat;
	}

	public static int getClockNumber(MpcSequence s, long position) {
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position == 0)
			return 0;
		int clock = (int) (position % (denTicks));
		return clock;
	}
}