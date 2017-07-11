package com.mpc.gui.vmpc;

import java.util.Observable;

public class AudioGui extends Observable {

//	private int server;
	private int in;
	private int out;

//	public void setServer(int i) {
//		if (i < 0) return;
//		server = i;
//		setChanged();
//		notifyObservers("server");
//	}
//
//	public int getServer() {
//		return server;
//	}

	public void setIn(int i) {
		if (i < 0 || i > 1) return;
		in = i;
		setChanged();
		notifyObservers("in");
	}

	public int getIn() {
		return in;
	}

	public void setOut(int i) {
		if (i < 0 || i > 4) return;
		out = i;
		setChanged();
		notifyObservers("out");
	}
	
	public int getOut() {
		return out;
	}
}
