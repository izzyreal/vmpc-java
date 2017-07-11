package com.mpc.gui.disk.window;

import java.util.Observable;

import com.mpc.sequencer.MpcSequence;

public class DiskWindowGui extends Observable {
	
	private int delete;
	private int loadInto;
	
	private MpcSequence sequence;
	
	public void setSequence(MpcSequence s) {
		sequence = s;
	}
	
	public MpcSequence getSequence() {
		return sequence;
	}
		
	public void setLoadInto(int i) {
		if (i<0||i>98) return;
		loadInto = i;
		setChanged();
		notifyObservers("loadinto");
	}
	
	public int getLoadInto() {
		return loadInto;
	}
	
	public void setDelete(int i) {
		if (i<0||i>8) return;
		delete = i;
		setChanged();
		notifyObservers("delete");
	}
	
	public int getDelete() {
		return delete;
	}
}
