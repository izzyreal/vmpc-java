package com.mpc.gui.sampler;

import java.util.Observable;

public class MixerSetupGui extends Observable {
	public final static String[] masterLevelNames = { "-\u00D9\u00DAdB", "-72dB", "-66dB", "-60dB", "-54dB", "-48dB", "-42dB",
			"-36dB", "-30dB", "-24dB", "-18dB", "-12dB", "-6dB", "0dB", "6dB", "12dB" };
	private int masterLevel; // -72dB - + 12dB, -73 = -inf
	private int fxDrum; // 1...4
	private boolean stereoMixSourceDrum; // if false, mix source is pgm
	private boolean indivFxSourceDrum; // if false, mix source is pgm
	private boolean copyPgmMixToDrumEnabled;
	private boolean recordMixChangesEnabled;

	public MixerSetupGui() {
		fxDrum = 0;
	}

	public int getMasterLevel() {
		return masterLevel;
	}

	public void setMasterLevel(int i) {
		if (i < -13 || i > 2) return;
		masterLevel = i;
		setChanged();
		notifyObservers("masterlevel");
	}

	public int getFxDrum() {
		return fxDrum;
	}

	public void setFxDrum(int i) {
		if (i < 0 || i > 3) return;
		fxDrum = i;
		setChanged();
		notifyObservers("fxdrum");
	}

	public boolean isStereoMixSourceDrum() {
		return stereoMixSourceDrum;
	}

	public void setStereoMixSourceDrum(boolean b) {
		stereoMixSourceDrum = b;
		setChanged();
		notifyObservers("stereomixsource");
	}

	public boolean isIndivFxSourceDrum() {
		return indivFxSourceDrum;
	}

	public void setIndivFxSourceDrum(boolean b) {
		indivFxSourceDrum = b;
		setChanged();
		notifyObservers("indivfxsource");
	}

	public boolean isCopyPgmMixToDrumEnabled() {
		return copyPgmMixToDrumEnabled;
	}

	public void setCopyPgmMixToDrumEnabled(boolean b) {
		copyPgmMixToDrumEnabled = b;
		setChanged();
		notifyObservers("copypgmmixtodrum");
	}

	public boolean isRecordMixChangesEnabled() {
		return recordMixChangesEnabled;
	}

	public void setRecordMixChangesEnabled(boolean b) {
		recordMixChangesEnabled = b;
		setChanged();
		notifyObservers("recordmixchanges");
	}

	public String getMasterLevelString() {
		return masterLevelNames[masterLevel];
	}

}
