package com.mpc.gui.sampler;

import java.util.List;
import java.util.Observable;

import com.mpc.gui.components.MixerStrip;

public class MixerGui extends Observable {

	private int tab = 0;
	private boolean link = false;
	private List<MixerStrip> mixerStrips;
	private int xPos;
	private int yPos;
	private int channelSettingsNote = 35;
	
	public void setLink(boolean b) {
		link = b;
		setChanged();
		notifyObservers("link");
	}
	
	public boolean getLink() {
		return link;
	}
	
	public void setTab(int i) {
		tab = i;
		setChanged();
		notifyObservers("tab");
	}
	
	public int getTab() {
		return tab;
	}

	public void setMixerStrips(List<MixerStrip> mixerStrips) {
		this.mixerStrips = mixerStrips;
	}
	
	public List<MixerStrip> getMixerStrips() {
		return mixerStrips;
	}

	public int getXPos() {
		return xPos;
	}
	
	public void setXPos(int i) {
		if (i<0||i>15) return;
		xPos = i;
		setChanged();
		notifyObservers("position");
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public void setYPos(int i) {
		if (i<0||i>1) return;
		yPos = i;
		setChanged();
		notifyObservers("position");
	}

	public void setChannelSettingsNote(int i) {
		if (i<35||i>98) return;
		channelSettingsNote = i;
		setChanged();
		notifyObservers("note");
	}
	
	public int getChannelSettingsNote() {
		return channelSettingsNote;
	}
}