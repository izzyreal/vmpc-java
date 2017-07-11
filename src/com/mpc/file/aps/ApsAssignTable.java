package com.mpc.file.aps;

class ApsAssignTable {

	// loading
	int[] assignTable;
	
	
	// saving
	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	ApsAssignTable(byte[] loadBytes) {
		assignTable = new int[64];
		for (int i = 0; i < 64; i++)
			assignTable[i] = loadBytes[i] & 0xFF;
	}

	int[] get() {
		return assignTable;
	}

	
	/*
	 * Constructor and methods for saving
	 */

	ApsAssignTable(int[] assignTable) {
		saveBytes = new byte[64];
		for (int i = 0; i < 64; i++)
			saveBytes[i] = (byte) assignTable[i];
	}

	protected byte[] getBytes() {
		return saveBytes;
	}
}
