package com.mpc.gui.sequencer;

import java.util.Observable;

public class SequencerGui extends Observable {
	
	private boolean afterEnabled;
	
	private boolean fullLevelEnabled;
	private boolean sixteenLevelsEnabled;

	private int note = 35;
	private int parameter;
	private int type;
	private int originalKeyPad = 0;
	
	public boolean isAfterEnabled() {
		return afterEnabled;
	}
	
	public void setAfterEnabled(boolean b) {
		afterEnabled = b;
	}
	
	public boolean isFullLevelEnabled() {
		return fullLevelEnabled;
	}
	
	public void setFullLevelEnabled(boolean b) {
		fullLevelEnabled = b;
	}

	public boolean isSixteenLevelsEnabled() {
		return sixteenLevelsEnabled;
	}
	
	public void setSixteenLevelsEnabled(boolean b) {
		sixteenLevelsEnabled = b;
	}
	
	public int getNote() {
		return note;
	}
	
	public void setNote(int i) {
		if (i < 35 || i > 98) return;
		note = i;
		setChanged();
		notifyObservers("note");
	}
	
	public int getParameter() {
		return parameter;
	}
	
	public void setParameter(int i) {
		if (i<0||i>1) return;
		parameter = i;
		setChanged();
		notifyObservers("parameter");
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int i) {
		if (i<0||i>3) return;
		type = i;
		setChanged();
		notifyObservers("type");
	}
	
	public int getOriginalKeyPad() {
		return originalKeyPad;
	}
	
	public void setOriginalKeyPad(int i) {
		if (i<0||i>15) return;
		originalKeyPad = i;
		setChanged();
		notifyObservers("originalkeypad");
	}
}
