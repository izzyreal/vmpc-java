package com.mpc.gui.midisync;

import java.util.Observable;

public class MidiSyncGui extends Observable {

	private int shiftEarly;
	private boolean sendMMCEnabled;
	private int frameRate;
	private int modeIn;
	private int modeOut;
	private boolean receiveMMCEnabled;
	private int out;
	private int in;
	
	public void setIn(int i) {
		if (i<0||i>1) return;
		in = i;
		setChanged();
		notifyObservers("in");
	}
	
	public int getIn() {
		return in;
	}
	
	public void setOut(int i) {
		if (i<0||i>2) return;
		out = i;
		setChanged();
		notifyObservers("out");
	}
	
	public int getOut() {
		return out;
	}


	public int getShiftEarly() {
		return shiftEarly;
	}

	public void setShiftEarly(int i) {
		shiftEarly = i;
	}
	
	public boolean isSendMMCEnabled() {
		return sendMMCEnabled;
	}

	public void setSendMMCEnabled(boolean b) {
		sendMMCEnabled = b;
		setChanged();
		notifyObservers("sendmmc");
	}
	
	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int i) {
		frameRate = i;
	}
	
	public int getModeIn() {
		return modeIn;
	}
	
	public void setModeIn(int i) {
		if (i<0||i>2) return;
		modeIn = i;
		setChanged();
		notifyObservers("modein");
		
	}
	
	public int getModeOut() {
		return modeOut;
	}

	public void setModeOut(int i) {
		if (i<0||i>2)return;
		modeOut = i;
		setChanged();
		notifyObservers("modeout");
	}
	
	public boolean isReceiveMMCEnabled() {
		return receiveMMCEnabled;
	}

	public void setReceiveMMCEnabled(boolean b) {
		receiveMMCEnabled = b;
		setChanged();
		notifyObservers("receivemmc");
	}
}