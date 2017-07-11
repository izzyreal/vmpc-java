package com.mpc.gui.sequencer.window;

import java.util.Observable;

public class MultiRecordingSetupLine extends Observable {

	private int in;
	
	private int track;
	
	private int out;
	
	public MultiRecordingSetupLine(int inputNumber) {
		in = inputNumber;
	}
	
	public int getIn() {
		return in;
	}
	
	public void setTrack(int i) {
		if (i<-1||i>63) return;
		track = i;
	}
	
	public int getTrack() {
		return track;
	}
	
	public void setOut(int i) {
		if (i<0||i>32) return;
		out = i;
	}
	
	public int getOut() {
		return out;
	}	
}
