package com.mpc.gui.misc;

import java.util.Observable;

public class SecondSeqGui extends Observable {

	private int sq = 0;

	public void setSq(int i) {
		if (i < 0 || i > 99) return;
		sq = i;
		setChanged();
		notifyObservers("sq");
	}

	public int getSq() {
		return sq;
	}

}
