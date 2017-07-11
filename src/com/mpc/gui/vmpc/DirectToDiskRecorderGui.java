package com.mpc.gui.vmpc;

import java.util.Observable;

import com.mpc.gui.Bootstrap;
import com.mpc.sequencer.MpcSequence;

public class DirectToDiskRecorderGui extends Observable {

	private int record;
	private int sq;
	private int song;

	private long time0;
	private long time1;

	private String outputFolder = "DEFAULT";
	private boolean offline;
	private boolean splitLR;

	public long getTime0() {
		return time0;
	}

	public void setTime0(long time0) {
		this.time0 = time0;
		if (time0 > time1) time1 = time0;
		setChanged();
		notifyObservers("time");
	}

	public long getTime1() {
		return time1;
	}

	public void setTime1(long time1) {
		this.time1 = time1;
		if (time1 < time0) time0 = time1;
		setChanged();
		notifyObservers("time");
	}

	public void setRecord(int i) {
		if (i < 0 || i > 4) return;
		record = i;
		setChanged();
		notifyObservers("record");
	}

	public int getRecord() {
		return record;
	}

	public void setSq(int i) {
		if (i < 0 || i > 99) return;
		sq = i;
		setChanged();
		notifyObservers("sq");
		setTime0(0);
		MpcSequence s = Bootstrap.getGui().getMpc().getSequencer().getSequence(sq);
		if (s.isUsed()) {
			setTime1(s.getLastTick());
		} else {
			setTime1(0);
		}
	}

	public int getSq() {
		return sq;
	}

	public void setSong(int i) {
		if (i < 0 || i > 4) return;
		song = i;
		setChanged();
		notifyObservers("song");
	}

	public int getSong() {
		return song;
	}

	public void setOutputFolder(String s) {
		outputFolder = s.toUpperCase();
		setChanged();
		notifyObservers("outputfolder");
	}

	public String getOutputfolder() {
		return outputFolder;
	}

	public void setOffline(boolean b) {
		offline = b;
		setChanged();
		notifyObservers("offline");
	}

	public boolean isOffline() {
		return offline;
	}

	public void setSplitLR(boolean b) {
		splitLR = b;
		setChanged();
		notifyObservers("splitlr");
	}
	
	public boolean isSplitLR() {
		return splitLR;
	}
	
}
