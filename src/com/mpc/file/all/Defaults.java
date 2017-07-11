package com.mpc.file.all;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Util;
import com.mpc.gui.UserDefaults;

public class Defaults {

	// Offsets relative to chunk start

	final static int DEF_SEQ_NAME_OFFSET = 0x0000;
	final static int UNKNOWN1_OFFSET = 0x0010;
	final static byte[] UNKNOWN1 = new byte[] { 0x01, 0x00, 0x00, 0x01, 0x01, 0x00 };
	final static int TEMPO_BYTE1_OFFSET = 0x0016; // uint16le
	final static int TEMPO_BYTE2_OFFSET = TEMPO_BYTE1_OFFSET + 1;
	final static int TIMESIG_NUM_OFFSET = 0x0018;
	final static int TIMESIG_DEN_OFFSET = 0x0019;
	final static int BAR_COUNT_BYTE1_OFFSET = 0x001A; // uint16le
	final static int BAR_COUNT_BYTE2_OFFSET = 0x001B; // uint16le

	final static int LAST_TICK_BYTE1_OFFSET = 0x001C; // TODO
	final static int LAST_TICK_BYTE2_OFFSET = 0x001D; // TODO
	final static int LAST_TICK_BYTE3_OFFSET = 0x001E; // TODO
	final static int UNKNOWN32_BIT_INT_OFFSET = 0x0020; // same 32 bit int
														// repeated 4x

	final static int UNKNOWN2_OFFSET = 0x0030;
	final static byte[] UNKNOWN2 = new byte[] { 0x00, 0x00, (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF), 0x01, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
			0x20, 0x20 };

	final static int DEV_NAMES_OFFSET = 0x0078;
	final static int DEV_NAMES_LENGTH = 0x0108;

	final static int TR_NAMES_OFFSET = 0x0180;
	final static int TR_NAMES_LENGTH = 0x0400;

	final static int DEVICES_OFFSET = 0x0580; // 64x same byte
	final static int DEVICES_LENGTH = 0x0040;

	final static int BUSSES_OFFSET = 0x05C0; // 64x same byte
	final static int BUSSES_LENGTH = 0x0040;

	final static int PGMS_OFFSET = 0x0600; // 64x same byte
	final static int PGMS_LENGTH = 0x0040;

	final static int TR_VELOS_OFFSET = 0x0640; // 64x same byte
	final static int TR_VELOS_LENGTH = 0x0040;

	final static int TR_STATUS_OFFSET = 0x0680;
	final static int TR_STATUS_LENGTH = 0x0040;

	// Loading

	String defaultSeqName;
	int tempo;
	int timeSigNum;
	int timeSigDen;
	int barCount;
	String[] devNames = new String[33];
	String[] trackNames = new String[64];
	int[] devices = new int[64];
	int[] busses = new int[64];
	int[] pgms = new int[64];
	int[] trVelos = new int[64];
	int[] status = new int[64];

	// Saving

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */
	public Defaults(byte[] loadBytes) {
		parseNames(loadBytes);
		byte[] tempoBytes = { loadBytes[TEMPO_BYTE1_OFFSET], loadBytes[TEMPO_BYTE2_OFFSET] };
		tempo = Util.bytePairToUnsignedInt(tempoBytes);
		timeSigNum = loadBytes[TIMESIG_NUM_OFFSET];
		timeSigDen = loadBytes[TIMESIG_DEN_OFFSET];
		byte[] barCountBytes = { loadBytes[BAR_COUNT_BYTE1_OFFSET], loadBytes[BAR_COUNT_BYTE2_OFFSET] };
		barCount = Util.bytePairToUnsignedInt(barCountBytes);
		for (int i = 0; i < 64; i++) {
			devices[i] = loadBytes[DEVICES_OFFSET + i];
			busses[i] = loadBytes[BUSSES_OFFSET + i];
			pgms[i] = loadBytes[PGMS_OFFSET + i];
			trVelos[i] = loadBytes[TR_VELOS_OFFSET + i];
			status[i] = loadBytes[TR_STATUS_OFFSET + i];
		}
	}

	private void parseNames(byte[] loadBytes) {
		byte[] stringBuffer;
		try {
			stringBuffer = Arrays.copyOfRange(loadBytes, DEF_SEQ_NAME_OFFSET,
					DEF_SEQ_NAME_OFFSET + AllParser.NAME_LENGTH);
			defaultSeqName = new String(stringBuffer, "UTF-8");

			int offset = 0;
			for (int i = 0; i < 33; i++) {
				offset = DEV_NAMES_OFFSET + (i * AllParser.DEV_NAME_LENGTH);
				stringBuffer = Arrays.copyOfRange(loadBytes, offset, offset + AllParser.DEV_NAME_LENGTH);
				devNames[i] = new String(stringBuffer, "UTF-8");
			}

			for (int i = 0; i < 64; i++) {
				offset = TR_NAMES_OFFSET + (i * AllParser.NAME_LENGTH);
				stringBuffer = Arrays.copyOfRange(loadBytes, offset, offset + AllParser.NAME_LENGTH);
				trackNames[i] = new String(stringBuffer, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getDefaultSeqName() {
		return defaultSeqName;
	}

	public int getTempo() {
		return tempo;
	}

	public int getTimeSigNum() {
		return timeSigNum;
	}

	public int getTimeSigDen() {
		return timeSigDen;
	}

	public int getBarCount() {
		return barCount;
	}

	public String[] getDefaultDevNames() {
		return devNames;
	}

	public String[] getDefaultTrackNames() {
		return trackNames;
	}

	public int[] getDevices() {
		return devices;
	}

	public int[] getBusses() {
		return busses;
	}

	public int[] getPgms() {
		return pgms;
	}

	public int[] getTrVelos() {
		return trVelos;
	}

	/*
	 * Constructor and methods for saving
	 */

	public Defaults(UserDefaults ud) {
		saveBytes = new byte[AllParser.DEFAULTS_LENGTH];
		setNames(ud);
		for (int i = 0; i < UNKNOWN1.length; i++)
			saveBytes[UNKNOWN1_OFFSET + i] = UNKNOWN1[i];

		setTempo(ud);
		setTimeSig(ud);
		setBarCount(ud);
		setLastTick(ud);

		/*
		 * No logic I could find in the following, but it's what the ALL file
		 * seems to be doing. So when user defaults is set to 2 bars, the
		 * following happens for LAST_TICK as well as UNKNOWN32_BIT_INT.
		 */
		int lastBar = ud.getLastBarIndex();
		if (lastBar == 1) {
			saveBytes[LAST_TICK_BYTE1_OFFSET] = 0;
			saveBytes[LAST_TICK_BYTE2_OFFSET] = 0;
		}

		
		if (lastBar == 1) lastBar = 0;
		byte[] unknownNumberBytes = Util.get32BitIntBytes((lastBar+1) * 2000000);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				saveBytes[UNKNOWN32_BIT_INT_OFFSET + j + (i * 4)] = unknownNumberBytes[j];
			}
		}

		/*
		 * This concludes our lastTick and 32 bit int voodoo ceremony. Hopefully
		 * real MPC2000XL is happy now.
		 */

		for (int i = 0; i < UNKNOWN2.length; i++)
			saveBytes[UNKNOWN2_OFFSET + i] = UNKNOWN2[i];

		setTrackSettings(ud);

	}

	private void setTrackSettings(UserDefaults ud) {
		for (int i = 0; i < 64; i++) {
			saveBytes[DEVICES_OFFSET + i] = (byte) ud.getDeviceNumber();
			saveBytes[BUSSES_OFFSET + i] = (byte) ud.getBus();
			saveBytes[PGMS_OFFSET + i] = (byte) ud.getPgm();
			saveBytes[TR_VELOS_OFFSET + i] = (byte) ud.getVeloRatio();
			saveBytes[TR_STATUS_OFFSET + i] = (byte) ud.getTrackStatus(); // TODO
																			// -
																			// All
																			// 'track
																			// status'
																			// stuff
																			// should
																			// be
																			// bitwise
		}
	}

	private void setLastTick(UserDefaults ud) {
		int lastTick = (ud.getLastBarIndex() + 1) * 384; // TODO - support other
															// than 4/4 and >
															// 65536
		byte[] b = Util.unsignedIntToBytePair(lastTick);
		saveBytes[LAST_TICK_BYTE1_OFFSET] = b[0];
		saveBytes[LAST_TICK_BYTE2_OFFSET] = b[1];
	}

	private void setBarCount(UserDefaults ud) {
		byte[] ba = Util.unsignedIntToBytePair(ud.getLastBarIndex() + 1);
		saveBytes[BAR_COUNT_BYTE1_OFFSET] = ba[0];
		saveBytes[BAR_COUNT_BYTE2_OFFSET] = ba[1];
	}

	private void setTimeSig(UserDefaults ud) {
		saveBytes[TIMESIG_NUM_OFFSET] = (byte) ud.getTimeSig().getNumerator();
		saveBytes[TIMESIG_DEN_OFFSET] = (byte) ud.getTimeSig().getDenominator();
	}

	void setNames(UserDefaults ud) {
		final String defSeqName = StringUtils.rightPad(ud.getSequenceName(), AllParser.NAME_LENGTH);

		for (int i = 0; i < 16; i++)
			saveBytes[DEF_SEQ_NAME_OFFSET + i] = defSeqName.getBytes()[i];

		byte[] stringBuffer;

		for (int i = 0; i < 33; i++) {
			final String defDevName = ud.getDeviceName(i);
			stringBuffer = StringUtils.rightPad(defDevName, AllParser.DEV_NAME_LENGTH).getBytes();
			int offset = DEV_NAMES_OFFSET + (i * AllParser.DEV_NAME_LENGTH);
			for (int j = offset; j < offset + AllParser.DEV_NAME_LENGTH; j++)
				saveBytes[j] = stringBuffer[j - offset];
		}

		for (int i = 0; i < 64; i++) {
			final String defTrackName = ud.getTrackName(i);
			stringBuffer = StringUtils.rightPad(defTrackName, AllParser.NAME_LENGTH).getBytes();
			int offset = TR_NAMES_OFFSET + (i * AllParser.NAME_LENGTH);
			for (int j = offset; j < offset + AllParser.NAME_LENGTH; j++)
				saveBytes[j] = stringBuffer[j - offset];
		}
	}

	void setTempo(UserDefaults ud) {
		int tempo = (int) (ud.getTempo().doubleValue() * 10.0);
		byte[] tempoBytes = Util.unsignedIntToBytePair(tempo);
		saveBytes[TEMPO_BYTE1_OFFSET] = tempoBytes[0];
		saveBytes[TEMPO_BYTE2_OFFSET] = tempoBytes[1];
	}

	public byte[] getBytes() {
		return saveBytes;
	}
}
