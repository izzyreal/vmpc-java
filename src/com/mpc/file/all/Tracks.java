package com.mpc.file.all;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Util;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;

class Tracks {

	/*
	 * Offsets relative to chunk start.
	 */

	final static private int TRACK_NAMES_OFFSET = 0x0000;
	final static private int BUSSES_OFFSET = 0x0440;
	final static private int PGMS_OFFSET = 0x0480;
	final static private int VELO_RATIOS_OFFSET = 0x04C0;
	final static private int STATUS_OFFSET = 0x0500;

	final static private int PADDING1_OFFSET = 0x0580;
	final static private byte[] PADDING1 = new byte[] { (byte) (0xE8 & 0xFF), 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) (0xE8 & 0xFF), 0x03 };

	final static private int LAST_TICK_BYTE1_OFFSET = 0x0590;
	final static private int LAST_TICK_BYTE2_OFFSET = 0x0591;
	final static private int LAST_TICK_BYTE3_OFFSET = 0x0592;

	final static private int UNKNOWN32_BIT_INT_OFFSET = 0x0594;

	/*
	 * Attributes for loading.
	 */

	private int[] busses = new int[64];
	private int[] veloRatios = new int[64];
	private int[] pgms = new int[64];
	private String[] names = new String[64];
	private int[] status = new int[64];

	/*
	 * Attribute for saving.
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	Tracks(byte[] loadBytes) {

		for (int i = 0; i < 64; i++) {
			busses[i] = loadBytes[BUSSES_OFFSET + i];
			pgms[i] = loadBytes[PGMS_OFFSET + i];
			veloRatios[i] = loadBytes[VELO_RATIOS_OFFSET + i];
			try {
				int offset = TRACK_NAMES_OFFSET + (i * AllParser.NAME_LENGTH);
				names[i] = new String(Arrays.copyOfRange(loadBytes, offset, offset + AllParser.NAME_LENGTH), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			status[i] = loadBytes[STATUS_OFFSET + i];
		}

	}

	int getBus(int i) {
		return busses[i];
	}

	int getVelo(int i) {
		return veloRatios[i];
	}

	int getPgm(int i) {
		return pgms[i];
	}

	String getName(int i) {
		return names[i];
	}

	int getStatus(int i) {
		return status[i];
	}

	/*
	 * Constructor and methods for saving
	 */

	Tracks(MpcSequence seq) {
		saveBytes = new byte[Sequence.TRACKS_LENGTH];
		for (int i = 0; i < 64; i++) {
			MpcTrack t = seq.getTrack(i);

			for (int j = 0; j < AllParser.NAME_LENGTH; j++) {
				int offset = TRACK_NAMES_OFFSET + (i * AllParser.NAME_LENGTH);
				saveBytes[offset + j] = StringUtils.rightPad(t.getActualName(), AllParser.NAME_LENGTH).getBytes()[j];
			}

			saveBytes[BUSSES_OFFSET + i] = (byte) t.getBusNumber();
			saveBytes[PGMS_OFFSET + i] = (byte) t.getProgramChange();
			saveBytes[VELO_RATIOS_OFFSET + i] = (byte) t.getVelocityRatio();

			int status = t.isUsed() ? 7 : 6;
			if (t.isUsed() && !t.isOn()) status = 5;
			saveBytes[STATUS_OFFSET + i] = (byte) status;
		}

		for (int i = 0; i < PADDING1.length; i++)
			saveBytes[PADDING1_OFFSET + i] = PADDING1[i];

		int lastTick = (int) seq.getLastTick();
		int remainder = lastTick % 65536;
		int large = (int) (Math.floor(lastTick / 65536.0));
		byte[] lastTickBytes = Util.unsignedIntToBytePair(remainder);
		saveBytes[LAST_TICK_BYTE1_OFFSET] = lastTickBytes[0];
		saveBytes[LAST_TICK_BYTE2_OFFSET] = lastTickBytes[1];
		saveBytes[LAST_TICK_BYTE3_OFFSET] = (byte) large;
		byte[] unknown32BitIntBytes1 = Util.get32BitIntBytes(10000000);
		byte[] unknown32BitIntBytes2 = Util.get32BitIntBytes((int) (seq.getLastTick() * 5208.333333333333));
		for (int j = 0; j < 4; j++) {
			int offset = UNKNOWN32_BIT_INT_OFFSET + j;
			saveBytes[offset] = unknown32BitIntBytes1[j];
		}

		for (int j = 0; j < 4; j++) {
			saveBytes[UNKNOWN32_BIT_INT_OFFSET + j + 4] = unknown32BitIntBytes2[j];
		}

	}

	byte[] getBytes() {
		return saveBytes;
	}

}
