package com.mpc.file.aps;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

class ApsName {
	private final static int NAME_STRING_LENGTH = 16;
	// For loading
	String name;

	// For saving
	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	ApsName(byte[] loadBytes) {
		try {
			name = new String(Arrays.copyOfRange(loadBytes, 0, NAME_STRING_LENGTH), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	String get() {
		return name;
	}

	/*
	 * Constructor and methods for saving
	 */

	ApsName(String name) {
		saveBytes = new byte[ApsParser.APS_NAME_LENGTH];
		
		while (name.length() < NAME_STRING_LENGTH)
			name += " ";
		
		for (int i = 0; i < NAME_STRING_LENGTH; i++)
			saveBytes[i] = name.getBytes()[i];
		saveBytes[NAME_STRING_LENGTH] = ApsParser.NAME_TERMINATOR;
	}

	byte[] getBytes() {
		return saveBytes;
	}
}
