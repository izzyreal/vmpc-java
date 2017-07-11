package com.mpc.sequencer;

public class NoteEvent extends DurationEvent {
	private int number = 60;
	private int variationTypeNumber = 0;
	private int variationValue = 64;
	private int velocity = 0;

	private NoteEvent noteOff = null;

	public NoteEvent() {
		noteOff = new NoteEvent(false);
//		System.out.println("Note on event created, noteOff = " + noteOff);
	}

	public NoteEvent(int i) {
		noteOff = new NoteEvent(false);
		number = i;
	}

	private NoteEvent(boolean noteOffTrue) {
//		System.out.println("Note off event created, noteOff = " + noteOff);
	}

	public NoteEvent getNoteOff() {
		return noteOff;
	}

	public void setNote(int i) {
		if (i < 0) return;
		if (i > 127 && i != 256) return;
		if (number == i) return;
		number = i;
		setChanged();
		notifyObservers("stepeditor");
	}

	public int getNote() {
		return number;
	}

	public int getVariationTypeNumber() {
		return variationTypeNumber;
	}

	public void setVariationTypeNumber(int i) {
		if (i < 0 || i > 3) return;
		variationTypeNumber = i;
		setChanged();
		notifyObservers("stepeditor");
	}

	public void setVariationValue(int i) {
		if (i < 0 || i > 128) return;
		if (variationTypeNumber != 0 && i > 100) i = 100;
		variationValue = i;
		setChanged();
		notifyObservers("stepeditor");
	}

	public int getVariationValue() {
		return variationValue;
	}

	public void setVelocity(int i) {
		if (i < 1 || i > 127) return;
		velocity = i;
		setChanged();
		notifyObservers("stepeditor");
	}

	public void setVelocityZero() {
		velocity = 0;
	}

	public int getVelocity() {
		return velocity;
	}
}