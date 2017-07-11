package com.mpc.gui.sampler;

import java.util.Observable;

import com.mpc.gui.Bootstrap;
import com.mpc.sampler.Sampler;

public class SoundGui extends Observable {

	private int soundIndex = 0;
	private String previousScreenName;
	private int convert;
	private int newFs;
	private int quality;
	private int newBit;
	private String newName;
	private String newLName;
	private String newRName;
	private int rSource;
	private String newStName;
	private int view;
	private int playX;
	private boolean endSelected = true;
	private int numberOfZones = 16;
	private int previousNumberOfZones = 0;
	private int[][] zones;
	private int zone;
	private int totalLength;

	public boolean isEndSelected() {
		return endSelected;
	}

	public void setEndSelected(boolean b) {
		endSelected = b;
		setChanged();
		notifyObservers("endlength");
	}

	public void setSoundIndex(int i) {
		if (i < 0 || i > Bootstrap.getGui().getMpc().getSampler().getSoundCount() - 1) return;
		soundIndex = i;
		setChanged();
		notifyObservers("soundnumber");
	}

	public int getSoundIndex() {
		return soundIndex;
	}

	public String getPreviousScreenName() {
		return previousScreenName;
	}

	public void setPreviousScreenName(String s) {
		previousScreenName = s;
	}

	public void setConvert(int i) {
		convert = i;
		setChanged();
		notifyObservers("convert");
	}

	public int getConvert() {
		return convert;
	}

	public void setNewFs(int i) {
		if (i < 4000 || i > 65000) return;
		newFs = i;
		setChanged();
		notifyObservers("newfs");
	}

	public int getNewFs() {
		return newFs;
	}

	public void setQuality(int i) {
		if (i < 0 || i > 2) return;
		quality = i;
		setChanged();
		notifyObservers("quality");
	}

	public int getQuality() {
		return quality;
	}

	public void setNewBit(int i) {
		if (i < 0 || i > 2) return;
		newBit = i;
		setChanged();
		notifyObservers("newbit");
	}

	public int getNewBit() {
		return newBit;
	}

	public void setNewName(String s) {
		newName = s;
		setChanged();
		notifyObservers("newname");
	}

	public String getNewName() {
		return newName;
	}

	public void setNewLName(String s) {
		newLName = s;
	}

	public String getNewLName() {
		return newLName;
	}

	public void setNewRName(String s) {
		newRName = s;
	}

	public String getNewRName() {
		return newRName;
	}

	public int getRSource() {
		return rSource;
	}

	public String getNewStName() {
		return newStName;
	}

	public void setRSource(int i) {
		if (i < 0 || i > Bootstrap.getGui().getMpc().getSampler().getSoundCount() - 1) return;
		rSource = i;
		setChanged();
		notifyObservers("rsource");
	}

	public void setNewStName(String s) {
		newStName = s;
	}

	public void setPlayX(int i) {
		if (i < 0 || i > 4) return;
		playX = i;
		setChanged();
		notifyObservers("playx");
	}

	public int getPlayX() {
		return playX;
	}

	public void setView(int i) {
		if (i < 0 || i > 1) return;
		view = i;
		setChanged();
		notifyObservers("view");
	}

	public int getView() {
		return view;
	}

	public void initZones(int length) {
		if (zone > numberOfZones - 1) zone = 0;
		this.totalLength = length;
		int zoneLength = (int) Math.floor(length / numberOfZones);
		int zoneStart = 0;
		zones = new int[numberOfZones][];
		for (int i = 0; i < numberOfZones - 1; i++) {
			zones[i] = new int[2];
			zones[i][0] = zoneStart;
			zones[i][1] = zoneStart + zoneLength - 1;
			zoneStart += zoneLength;
		}
		zones[numberOfZones - 1] = new int[2];
		zones[numberOfZones - 1][0] = zoneStart;
		zones[numberOfZones - 1][1] = length;
	}

	public void setZoneStart(int zoneIndex, int start) {
		if (start > zones[zoneIndex][1]) {
			start = zones[zoneIndex][1];
		}
		if (zoneIndex == 0 && start < 0) {
			start = 0;
		}
		if (zoneIndex > 0 && start < zones[zoneIndex - 1][0]){
			start = zones[zoneIndex-1][0];
		}
		zones[zoneIndex][0] = start;
		if (zoneIndex != 0) {
			zones[zoneIndex - 1][1] = start;
		}
		setChanged();
		notifyObservers("zone");
	}

	public int getZoneStart(int zoneIndex) {
		return zones[zoneIndex][0];
	}

	public void setZoneEnd(int zoneIndex, int end) {
		if (end < zones[zoneIndex][0]) {
			end = zones[zoneIndex][0];
		}
		if (zoneIndex < numberOfZones - 1 && end > zones[zoneIndex + 1][1]) {
			end = zones[zoneIndex + 1][1];
		}
		if (zoneIndex == numberOfZones - 1 && end > totalLength) {
			end = totalLength;
		}
		zones[zoneIndex][1] = end;
		if (zoneIndex != numberOfZones - 1) {
			zones[zoneIndex + 1][0] = end;
		}
		setChanged();
		notifyObservers("zone");
	}

	public int getZoneEnd(int zoneIndex) {
		return zones[zoneIndex][1];
	}

	public void setZone(int i) {
		if (i < 0 || i > numberOfZones - 1) return;
		zone = i;
		setChanged();
		notifyObservers("zone");
	}

	public int getZoneNumber() {
		return zone;
	}

	public void setNumberOfZones(int i) {
		if (i < 1 || i > 16) return;
		numberOfZones = i;
		setChanged();
		notifyObservers("numberofzones");
	}

	public int getNumberOfZones() {
		return numberOfZones;
	}

	public void setPreviousNumberOfZones(int i) {
		previousNumberOfZones = i;
	}

	public int getPreviousNumberOfzones() {
		return previousNumberOfZones;
	}

}