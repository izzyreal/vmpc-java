package com.mpc.file.all;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

class Song {

	final static int LENGTH = 0x0210;

	final private static int NAME_OFFSET = 0x0000;

	/*
	 * Attributes for loading
	 */

	String name;

	/*
	 * Attributes for saving
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	Song(byte[] b) {
		name = new String(Arrays.copyOfRange(b, NAME_OFFSET, NAME_OFFSET + AllParser.NAME_LENGTH));
	}

	String getName() {
		return name;
	}

	/*
	 * Constructor and methods for saving
	 */

	Song(com.mpc.sequencer.Song mpcSong) {
		saveBytes = new byte[LENGTH];
		for (int i = 0; i < AllParser.NAME_LENGTH; i++)
			saveBytes[NAME_OFFSET + i] = StringUtils.rightPad(mpcSong.getName(), AllParser.NAME_LENGTH).getBytes()[i];

		
		// fill as if songs are unused
		for (int i = AllParser.NAME_LENGTH; i < LENGTH - 0x0A; i++)
			saveBytes[i] = (byte) (0xFF & 0xFF);
		
		for (int i=LENGTH-0x0A;i<LENGTH;i++)
			saveBytes[i] = 0;
	}

	byte[] getBytes() {
		return saveBytes;
	}
}
