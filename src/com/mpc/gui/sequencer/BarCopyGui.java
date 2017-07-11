package com.mpc.gui.sequencer;

import java.util.Observable;

public class BarCopyGui extends Observable {

	private int fromSq;
	private int toSq;
	
	private int lastBar;
	private int firstBar;
	private int afterBar;
	private int copies;

	public BarCopyGui() {
		copies = 1;
	}
	
	public int getLastBar() {
		return lastBar;
	}

	public void setLastBar(int i, int max) {
		if (i<0||i>max) return;
		lastBar = i;
		if (lastBar<firstBar) setFirstBar(lastBar, max);
		setChanged();
		notifyObservers("lastbar");
	}
	
	public int getFromSq() {
		return fromSq;
	}
	
	public void setFromSq(int i) {
		if (i<0||i>98) return;
		fromSq = i;
		setChanged();
		notifyObservers("fromsq");
	}
	
	public int getToSq() {
		return toSq;
	}
	
	public void setToSq(int i) {
		if (i<0||i>98) return;
		toSq = i;
		setChanged();
		notifyObservers("tosq");
	}
	
	public int getFirstBar() {
		return firstBar;
	}
	
	public void setFirstBar(int i, int max) {
		if (i<0||i>max) return;
		firstBar = i;
		if (firstBar>lastBar) setLastBar(firstBar, max);
		setChanged();
		notifyObservers("firstbar");
	}
	
	public int getAfterBar() {
		return afterBar;
	}

	public void setAfterBar(int i, int max) {
		if (i<0||i>max+1) return;
		afterBar = i;
		setChanged();
		notifyObservers("afterbar");
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



}