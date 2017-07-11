package com.mpc.file.all;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.mpc.gui.Gui;
import com.mpc.gui.midisync.MidiSyncGui;

class MidiSyncMisc {

	/*
	 * Offsets relative to chunk start.
	 */
	final static int LENGTH = 0x20;

	final private static int IN_MODE_OFFSET = 0x00;
	final private static int OUT_MODE_OFFSET = 0x01;
	final private static int SHIFT_EARLY_OFFSET = 0x02;
	final private static int SEND_MMC_OFFSET = 0x03;
	final private static int FRAME_RATE_OFFSET = 0x04;
	final private static int INPUT_OFFSET = 0x05;
	final private static int OUTPUT_OFFSET = 0x06;
	final private static int DEF_SONG_NAME_OFFSET = 0x07;

	/*
	 * Attributes for loading
	 */

	int inMode;
	int outMode;
	int shiftEarly;
	boolean sendMMCEnabled;
	int frameRate;
	int input;
	int output;
	String defSongName;

	/*
	 * Attributes for saving
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	MidiSyncMisc(byte[] b) {
		inMode = b[IN_MODE_OFFSET];
		outMode = b[OUT_MODE_OFFSET];
		shiftEarly = b[SHIFT_EARLY_OFFSET];
		sendMMCEnabled = b[SEND_MMC_OFFSET] > 0;
		frameRate = b[FRAME_RATE_OFFSET];
		System.out.println("frameRate " + frameRate);
		input = b[INPUT_OFFSET];
		output = b[OUTPUT_OFFSET];
		defSongName = new String(
				Arrays.copyOfRange(b, DEF_SONG_NAME_OFFSET, DEF_SONG_NAME_OFFSET + AllParser.NAME_LENGTH));
	}

	int getInMode() {
		return inMode;
	}

	int getOutMode() {
		return outMode;
	}

	int getShiftEarly() {
		return shiftEarly;
	}

	boolean isSendMMCEnabled() {
		return sendMMCEnabled;
	}

	int getFrameRate() {
		return frameRate;
	}

	int getInput() {
		return input;
	}

	int getOutput() {
		return output;
	}

	String getDefSongName() {
		return defSongName;
	}

	/*
	 * Constructor and methods for saving
	 */

	MidiSyncMisc(Gui gui) {
		saveBytes = new byte[LENGTH];
		MidiSyncGui ms = gui.getMidiSyncGui();
		saveBytes[IN_MODE_OFFSET] = (byte) ms.getModeIn();
		saveBytes[OUT_MODE_OFFSET] = (byte) ms.getModeOut();
		saveBytes[SHIFT_EARLY_OFFSET] = (byte) ms.getShiftEarly();
		saveBytes[SEND_MMC_OFFSET] = (byte) (ms.isSendMMCEnabled() ? 1 : 0);
		saveBytes[FRAME_RATE_OFFSET] = (byte) ms.getFrameRate();
		System.out.println("frame rate " + ms.getFrameRate());
		System.out.println("frame rate byte " + saveBytes[FRAME_RATE_OFFSET]);
		saveBytes[INPUT_OFFSET] = (byte) ms.getIn();
		saveBytes[OUTPUT_OFFSET] = (byte) ms.getOut();
		for (int i = 0; i < AllParser.NAME_LENGTH; i++)
			saveBytes[DEF_SONG_NAME_OFFSET + i] = StringUtils.rightPad(gui.getSongGui().getDefaultSongName(), 16)
					.getBytes()[i];
		saveBytes[DEF_SONG_NAME_OFFSET + 16] = 0x01;
	}

	byte[] getBytes() {
		return saveBytes;
	}
}
