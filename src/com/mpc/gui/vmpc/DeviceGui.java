package com.mpc.gui.vmpc;

import java.util.Observable;

public class DeviceGui extends Observable {

	private int scsi = 0;
	private int[] accessTypes = new int[7];
	private int[] stores = new int[7];

	private int[] accessTypesPlaceHolder = new int[7];
	private int[] storesPlaceHolder = new int[7];
	private int scsiPlaceHolder = 0;

	public static final int OFF = 0;
	public static final int JAVA = 1;
	public static final int RAW = 2;

	private boolean initialized = false;

	public DeviceGui() {
		for (int i = 0; i < 7; i++)
			stores[i] = -1;
		stores[0] = 0;
		accessTypes[0] = RAW;
		saveSettings();
		initialized = true;
	}

	public void setScsi(int i) {
		if (i < 0 || i > 6) return;
		scsi = i;
		setChanged();
		notifyObservers("scsi");
	}

	public void setStore(int i, int store) {
		if (accessTypes[i] == JAVA && store > 0) return;
		if (store < -1) return;
		stores[i] = store;
		setChanged();
		notifyObservers("root");
	}

	public int getStore(int i) {
		return stores[i];
	}

	public int getScsi() {
		return scsi;
	}

	private void notifyAccessType() {
		setChanged();
		notifyObservers("accesstype");
	}

	public boolean isEnabled(int i) {
		return stores[i] != -1 && accessTypes[i] != OFF;
	}

	public boolean isRaw(int i) {
		return accessTypes[i] == RAW;
	}

	public int getAccessType(int i) {
		return accessTypes[i];
	}

	public void setAccessType(int i, int type) {
		if (type < OFF || type > RAW) return;
		if (accessTypes[i] == type) return;
		accessTypes[i] = type;
		stores[i] = -1;
		notifyAccessType();
	}

	public void saveSettings() {
		scsiPlaceHolder = scsi;
		for (int i = 0; i < 7; i++) {
			accessTypesPlaceHolder[i] = accessTypes[i];
			storesPlaceHolder[i] = stores[i];
		}
	}

	public void restoreSettings() {
		if (!initialized) return;
		scsi = scsiPlaceHolder;
		for (int i = 0; i < 7; i++) {
			accessTypes[i] = accessTypesPlaceHolder[i];
			stores[i] = storesPlaceHolder[i];
		}
	}
}