package com.mpc.file.aps;

class ApsDrumConfiguration {

	// For reading
	int program;
	boolean receivePgmChange;
	boolean receiveMidiVolume;

	// For writing
	byte[] saveBytes;
	final static char[] TEMPLATE = { 0x00, 0x00, 0x01, 0x01, 0x7F, 0x00, 0x01, 0x01, 0x7F }; // byte
																								// 1
																								// and
																								// 5
																								// both
																								// store
																								// selected
																								// program(?)
	final static char[] PADDING = { 0x40, 0x00, 0x06 };
	/*
	 * Constructor and methods for loading
	 */

	ApsDrumConfiguration(byte[] loadBytes) {
		program = loadBytes[5];
		receivePgmChange = getBits(loadBytes[2]).charAt(7) == '1';
		receiveMidiVolume = getBits(loadBytes[3]).charAt(7) == '1';
	}

	int getProgram() {
		return program;
	}

	boolean getReceivePgmChange() {
		return receivePgmChange;
	}

	boolean getReceiveMidiVolume() {
		return receiveMidiVolume;
	}

	/*
	 * Constructor and methods for saving
	 */

	ApsDrumConfiguration(int program, boolean recPgmChange, boolean recMidiVolume) {
		saveBytes = new byte[12];
		for (int i = 0; i < 9; i++)
			saveBytes[i] = (byte) TEMPLATE[i];
		for (int i = 0; i < 3; i++)
			saveBytes[i + 9] = (byte) PADDING[i];

		saveBytes[1] = (byte) program;
		saveBytes[2] = setBits((byte) 0b10000000, saveBytes[2], recPgmChange); // set
																		// bit 1
																		// to
																		// receive
																		// pgm
																		// change
		saveBytes[3] = setBits((byte) 0b10000000, saveBytes[3], recMidiVolume);
		saveBytes[5] = (byte) program;
	}

	byte[] getBytes() {
		return saveBytes;
	}

	// general
	byte setBits(byte bitsToManipulate, byte b, boolean bool) {
		if (bool) { // Strangely I'm doing the opposite here than in
					// ApsGlobalParameters. I'm not sure what's happening here.
			b &= ~bitsToManipulate;
		} else {
			b |= ~bitsToManipulate;
		}
		return b;
	}

	private String getBits(byte b) { // in reverse!
		String result = Integer.toBinaryString((b + 256) % 256);
		while (result.length() < 8)
			result = "0" + result;
		return result;
	}

}
