package com.mpc.file.all;

import com.mpc.Util;

class Bar {

	// Loading
	
	int ticksPerBeat;
	int lastTick;
	int barLength;

	
	// Saving
	
	byte[] saveBytes;
	
	
	/*
	 *  Constructor and methods for loading
	 */
	
	Bar(byte[] bytes, Bar previousBar) {
		ticksPerBeat = bytes[0] & 0xFF;
		int intVal = Util.bytePairToUnsignedInt(new byte[] { bytes[1], bytes[2] });
		lastTick = ((bytes[3] & 0xFF) * 65536) + intVal;
		barLength = lastTick - (previousBar == null ? 0 : previousBar.lastTick);
	}

	int getTicksPerBeat() {
		return ticksPerBeat;
	}

	int getDenominator() {
		int result = 0;
		switch (ticksPerBeat) {
		case 96:
			result = 4;
			break;
		case 48:
			result = 8;
			break;
		case 24:
			result = 16;
			break;
		case 12:
			result = 32;
			break;
		}

		return result;
	}

	int getNumerator() {
		return barLength / ticksPerBeat;
	}

	int getLastTick() {
		return lastTick;
	}

	
	/*
	 *  Constructor and methods for saving
	 */
	
	Bar(int ticksPerBeat, int lastTick) {
		saveBytes = new byte[4];
		saveBytes[0] = (byte) ticksPerBeat;
		
		int intVal = lastTick % 65536;
		byte[] bytePair = Util.unsignedIntToBytePair(intVal);
		saveBytes[1] = bytePair[0];
		saveBytes[2] = bytePair[1];
		saveBytes[3] = (byte) ((lastTick - intVal) / 65536);
	}
	
	byte[] getBytes() {
		return saveBytes;
	}
}