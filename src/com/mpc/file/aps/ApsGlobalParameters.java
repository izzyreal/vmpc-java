package com.mpc.file.aps;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.sampler.SamplerGui;

class ApsGlobalParameters {

	// For reading
	boolean padToInternalSound;
	boolean padAssignMaster;
	boolean stereoMixSourceDrum;
	boolean indivFxSourceDrum;
	boolean copyPgmMixToDrum;
	boolean recordMixChanges;
	int masterLevel;
	int fxDrum;

	// For writing
	final static byte[] TEMPLATE = { 0x7F, (byte) (0xFE & 0xFF), 0x7C,  (byte) (0xEF & 0xFF),  0x00, 0x00, 0x00, 0x40 };
	
	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	ApsGlobalParameters(byte[] loadBytes) {

		// charAt bit positions in reverse
		padToInternalSound = getBits(loadBytes[0]).charAt(7) == '1';
		padAssignMaster = getBits(loadBytes[1]).charAt(7) == '1';
		stereoMixSourceDrum = getBits(loadBytes[2]).charAt(7) == '1';
		indivFxSourceDrum = getBits(loadBytes[2]).charAt(6) == '1';
		copyPgmMixToDrum = getBits(loadBytes[3]).charAt(7) == '1';
		recordMixChanges = getBits(loadBytes[3]).charAt(3) == '1';
		fxDrum = readFxDrum(loadBytes[4]);
		masterLevel = loadBytes[6];
		
	}

	private int readFxDrum(byte b) {
		for (int i = 2; i < 8; i++)
			b &= ~(1 << i);
		return b & 0xFF;
	}

	int getFxDrum() {
		return fxDrum;
	}

	boolean isPadToIntSoundEnabled() {
		return padToInternalSound;
	}
	
	boolean isPadAssignMaster() {
		return padAssignMaster;
	}

	boolean isStereoMixSourceDrum() {
		return stereoMixSourceDrum;
	}

	boolean isIndivFxSourceDrum() {
		return indivFxSourceDrum;
	}
	
	boolean copyPgmMixToDrum() {
		return copyPgmMixToDrum;
	}

	boolean recordMixChanges() {
		return recordMixChanges;
	}

	int getMasterLevel() { // -13 to 2 reflecting -infinity, -72dB, -66dB ..
							// +12dB
		return masterLevel;
	}

	// Constructor for saving
	ApsGlobalParameters(Mpc mpc) {
		saveBytes = new byte[ApsParser.PARAMETERS_LENGTH];
		for (int i=0;i<saveBytes.length;i++)
			saveBytes[i] = TEMPLATE[i];
		Bootstrap.getGui().getSamplerGui();
		final boolean padToInternalSound = Bootstrap.getGui().getSamplerGui().isPadToIntSound();
		final boolean padAssignMaster = SamplerGui.isPadAssignMaster();
		final boolean stereoMixSourceDrum = Bootstrap.getGui().getMixerSetupGui().isStereoMixSourceDrum();
		final boolean indivFxSourceDrum = Bootstrap.getGui().getMixerSetupGui().isIndivFxSourceDrum();
		final boolean copyPgmMixToDrum = Bootstrap.getGui().getMixerSetupGui().isCopyPgmMixToDrumEnabled();
		final boolean recordMixChanges = Bootstrap.getGui().getMixerSetupGui().isRecordMixChangesEnabled();
		final int masterLevel = Bootstrap.getGui().getMixerSetupGui().getMasterLevel();
		final int fxDrum = Bootstrap.getGui().getMixerSetupGui().getFxDrum();
		saveBytes[0] = setBits((byte) 0b10000000, saveBytes[0], padToInternalSound);
		saveBytes[1] = setBits((byte) 0b00000001, saveBytes[1], padAssignMaster);
		saveBytes[2] = setBits((byte) 0b00000001, saveBytes[2], stereoMixSourceDrum);
		saveBytes[2] = setBits((byte) 0b00000010, saveBytes[2], indivFxSourceDrum);
		saveBytes[3] = setBits((byte) 0b00000001, saveBytes[3], copyPgmMixToDrum);
		saveBytes[3] = setBits((byte) 0b00010000, saveBytes[3], recordMixChanges);
		saveBytes[4] = (byte) fxDrum;
		saveBytes[6] = (byte) masterLevel;
	}
	
	byte setBits(byte bitsToManipulate, byte b, boolean bool)
	{
	  if (!bool) {
		  b &= ~bitsToManipulate;
	  } else {
		  b |= ~bitsToManipulate;
	  }
	  return (byte) (b & 0xFF);
	}
		
	byte[] getBytes() {
		return saveBytes;
	}
	
	// general
	private String getBits(byte b) { // in reverse!
		String result = Integer.toBinaryString((b + 256) % 256);
		while (result.length() < 8)
			result = "0" + result;
		return result;
	}
}
