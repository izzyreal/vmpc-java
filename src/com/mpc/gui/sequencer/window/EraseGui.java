package com.mpc.gui.sequencer.window;

import java.util.Observable;

public class EraseGui extends Observable {

	int track;
	int erase;
	int type;
	int notes0;
	int notes1;
	
	public void setTrack(int i) {
		if (i<-1||i>63) return;
		track = i;
		setChanged();
		notifyObservers("track");
	}
	
	public void setErase(int i) {
		if (i<0||i>2) return;
		erase = i;
		setChanged();
		notifyObservers("erase");
	}
	
	public void setType(int i) {
		if (i<0||i>6) return;
		type = i;
		setChanged();
		notifyObservers("type");
	}
	
	public int getTrack() {
		return track;
	}

	public int getErase() {
		return erase;
	}
	
	public int getType() {
		return type;
	}
}
