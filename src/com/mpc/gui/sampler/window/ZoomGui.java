package com.mpc.gui.sampler.window;

import java.util.Observable;

public class ZoomGui extends Observable {

	private boolean smplLngthFix;
	private boolean loopLngthFix;
	private int zoomLevel = 6;

	public void setZoomLevel(int i) {
		if (i < 0 || i > 6)
			return;
		zoomLevel = i;
		setChanged();
		notifyObservers("zoomlevel");
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setSmplLngthFix(boolean b) {
		smplLngthFix = b;
		setChanged();
		notifyObservers("smpllngthfix");
	}

	public boolean isSmplLngthFix() {
		return smplLngthFix;
	}

	public void setLoopLngthFix(boolean b) {
		loopLngthFix = b;
		setChanged();
		notifyObservers("looplngthfix");
	}

	public boolean isLoopLngthFix() {
		return loopLngthFix;
	}
}