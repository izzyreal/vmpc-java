package com.mpc.gui.misc;

import java.util.Observable;

public class TransGui extends Observable {

	private int tr = -1;

	private int amount;

	private int bar0;
	private int bar1;

	public void setAmount(int i) {
		if (i < -12 || i > 12) return;
		amount = i;
		setChanged();
		notifyObservers("amount");
	}
	
	public int getAmount() {
		return amount;
	}

	public void setTr(int i) {
		if (i < -1 || i > 63) return;
		tr = i;
		setChanged();
		notifyObservers("tr");
	}

	public int getTr() {
		return tr;
	}

	public void setBar0(int i) {
		if (i < 0) return;
		bar0 = i;
		if (bar0 > bar1) bar1 = bar0;
		setChanged();
		notifyObservers("bars");
	}

	public int getBar0() {
		return bar0;
	}

	public void setBar1(int i) {
		if (i < 0) return;
		bar1 = i;
		if (bar1 < bar0) bar0 = bar1;
		setChanged();
		notifyObservers("bars");
	}

	public int getBar1() {
		return bar1;
	}
	
}
