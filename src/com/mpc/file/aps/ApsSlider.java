package com.mpc.file.aps;

import com.mpc.tootextensions.MpcSlider;

class ApsSlider {

	// loading
	int note;
	int tuneLow;
	int tuneHigh;
	int decayLow;
	int decayHigh;
	int attackLow;
	int attackHigh;
	int filterLow;
	int filterHigh;
	int programChange;

	// saving
	byte[] saveBytes;
	final static byte[] PADDING = { 0x00, 0x23, 0x40, 0x00, 0x1A, 0x00 };
	/*
	 * Constructor for reading slider chunk
	 */

	ApsSlider(byte[] loadBytes) {
		note = loadBytes[0] & 0xFF;
		tuneLow = loadBytes[1];
		tuneHigh = loadBytes[2];
		decayLow = loadBytes[3] & 0xFF;
		decayHigh = loadBytes[4] & 0xFF;
		attackLow = loadBytes[5] & 0xFF;
		attackHigh = loadBytes[6] & 0xFF;
		filterLow = loadBytes[7];
		filterHigh = loadBytes[8];
		programChange = loadBytes[9] & 0xFF;
	}

	/*
	 * Constructor for
	 */

	ApsSlider(MpcSlider slider) {
		saveBytes = new byte[ApsProgram.SLIDER_LENGTH];
		saveBytes[0] = (byte) slider.getNote();
		saveBytes[1] = (byte) slider.getTuneLowRange();
		saveBytes[2] = (byte) slider.getTuneHighRange();
		saveBytes[3] = (byte) (slider.getDecayLowRange() & 0xFF);
		saveBytes[4] = (byte) (slider.getDecayHighRange() & 0xFF);
		saveBytes[5] = (byte) (slider.getAttackLowRange() & 0xFF);
		saveBytes[6] = (byte) (slider.getAttackHighRange() & 0xFF);
		saveBytes[7] = (byte) slider.getFilterLowRange();
		saveBytes[8] = (byte) slider.getFilterHighRange();
		saveBytes[9] = (byte) slider.getControlChange();
	}

	int getNote() {
		return note;
	}

	int getTuneLow() {
		return tuneLow;
	}

	int getTuneHigh() {
		return tuneHigh;
	}

	int getDecayLow() {
		return decayLow;
	}

	int getDecayHigh() {
		return decayHigh;
	}

	int getAttackLow() {
		return attackLow;
	}

	int getAttackHigh() {
		return attackHigh;
	}

	int getFilterLow() {
		return filterLow;
	}

	int getFilterHigh() {
		return filterHigh;
	}

	int getProgramChange() {
		return programChange;
	}

	byte[] getBytes() {
		return saveBytes;
	}
}