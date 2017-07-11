package com.mpc.gui.sequencer;

import java.util.Observable;

public class SongGui extends Observable {

	private int offset = -1;
	private int selectedSongIndex;
	private String defaultSongName = "Song";
	private boolean loop;

	public void setOffset(int i) {
		if (i < -1) return;
		offset = i;
		setChanged();
		notifyObservers("offset");
	}

	public int getOffset() {
		return offset;
	}

	public int getSelectedSongIndex() {
		return selectedSongIndex;
	}

	public void setSelectedSongIndex(int i) {
		System.out.println("setting song to " + i);
		if (i < 0 || i > 19) return;
		selectedSongIndex = i;
		setChanged();
		notifyObservers("selectedsongindex");
	}

	public void setDefaultSongName(String s) {
		defaultSongName = s;
	}

	public String getDefaultSongName() {
		return defaultSongName;
	}

	public void setLoop(boolean b) {
		loop = b;
		setChanged();
		notifyObservers("loop");
	}
	
	public boolean isLoopEnabled() {
		return loop;
	}
}