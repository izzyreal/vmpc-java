package com.mpc.file.pgmwriter;

import com.mpc.sampler.Program;

public class Pads {

	private final char[] padsArray;

	/*
	 * This is not padding. This is supposed to be used for FX board settings
	 * (and maybe other stuff too). But I'm ignoring it for now.
	 */
	private final static String padding = "02004800D0070000630114081DFC323302323C08050A1414320000020F19000541141E01050000056300F4FF0C0000000000000002004F014F0100424F0100424F01004232006328003C0000D0070000630114081DFC323302323C08050A1414320000020F19000541141E01050000056300F4FF0C0000000000000002004F014F0100424F0100424F01004232006328003C000004000C000000320023003E335A3214000000320023003E335A3214000000320023003E335A3214000000320023003E335A321400";

	public Pads(Program program) {
		padsArray = new char[264];

		for (int i = 0; i < 64; i++) {
			int nn = program.getPad(i).getNote();
			if (nn == 34) {
				setPadMidiNote(i, 0);
			} else {
				setPadMidiNote(i, nn);
			}
		}

		byte[] paddingBytes = hexStringToByteArray(padding);

		for (int i = 0; i < 200; i++)
			padsArray[i + 64] = (char) paddingBytes[i];
	}

	char[] getPadsArray() {
		return padsArray;
	}

	private void setPadMidiNote(int pad, int padMidiNote) {
		padsArray[pad] = (char) padMidiNote;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] getPgmPadding() {
		return hexStringToByteArray(padding);
	}
}