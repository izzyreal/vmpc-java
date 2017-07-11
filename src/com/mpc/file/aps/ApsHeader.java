package com.mpc.file.aps;

class ApsHeader {

	// For loading
	boolean valid;
	int soundAmount;
	
	// For saving
	byte[] saveBytes;
	
	/*
	 *  Constructor for loading
	 */
	
	ApsHeader(byte[] loadBytes) {
		valid = loadBytes[0] == 0x0A && loadBytes[1] == 0x05 && loadBytes[3] == 0x00;
		soundAmount = loadBytes[2] & 0xFF;
	}
	
	
	/*
	 *  Constructor for saving
	 */

	ApsHeader(int soundCount) {
		saveBytes = new byte[4];
		saveBytes[0] = 0x0A;
		saveBytes[1] = 0x05;
		saveBytes[2] = (byte) soundCount;
		saveBytes[3] = 0x00;
	}
	
	boolean isValid() {
		return valid;
	}
	
	int getSoundAmount() {
		return soundAmount;
	}
	
	byte[] getBytes() {
		return saveBytes;
	}
}
