package com.mpc.gui.misc;

import java.util.Observable;

public class PunchGui extends Observable {

	private int autoPunch;
	private long time0;
	private long time1;

	public void setAutoPunch(int i) {
		if (i < 0 || i > 2) return;
		autoPunch = i;
		setChanged();
		notifyObservers("autopunch");
	}

	public int getAutoPunch() {
		return autoPunch;
	}

	public void setTime0(long l) {
		time0 = l;
		if (time0 > time1) time1 = time0;
		setChanged();
		notifyObservers("time");
	}

	public long getTime0() {
		return time0;
	}

	public void setTime1(long l) {
		time1 = l;
		if (time1 < time0) time0 = time1;
		setChanged();
		notifyObservers("time");
	}

	public long getTime1() {
		return time1;
	}
}
