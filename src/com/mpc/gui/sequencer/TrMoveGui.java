package com.mpc.gui.sequencer;

import java.util.Observable;

import com.mpc.sequencer.MpcSequence;

public class TrMoveGui extends Observable {

	private int sq;
	private int selectedTrackIndex = -1;
	private int currentTrackIndex = 0;

	public TrMoveGui() {
	}

	public boolean isSelected() {
		if (selectedTrackIndex != -1)
			return true;
		return false;
	}

	public int getSq() {
		return sq;
	}

	public void goUp() {
		if (currentTrackIndex == 0)
			return;
		currentTrackIndex--;
		setChanged();
		notifyObservers("trmove");
	}

	public void goDown() {
		if (currentTrackIndex == 63)
			return;
		currentTrackIndex++;
		setChanged();
		notifyObservers("trmove");
	}

	public int getCurrentTrackIndex() {
		return currentTrackIndex;
	}

	public void setSq(int i) {
		if (i < 0 || i > 98)
			return;
		sq = i;
		setChanged();
		notifyObservers("sq");
	}

	public int getSelectedTrackIndex() {
		return selectedTrackIndex;
	}

	public void select() {
		selectedTrackIndex = currentTrackIndex;
		setChanged();
		notifyObservers("trmove");
	}

	public void cancel() {
		selectedTrackIndex = -1;
		setChanged();
		notifyObservers("trmove");
	}

	public void insert(MpcSequence s) {
		s.moveTrack(selectedTrackIndex, currentTrackIndex);
		selectedTrackIndex = -1;
		setChanged();
		notifyObservers("trmove");
	}
}