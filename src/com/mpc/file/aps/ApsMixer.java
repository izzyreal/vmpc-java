package com.mpc.file.aps;

import com.mpc.tootextensions.ConcreteMixParameters;
import com.mpc.tootextensions.MpcMixParameters;

class ApsMixer {

	int[] fxPaths = new int[64];
	int[] levels = new int[64];
	int[] pannings = new int[64];
	int[] iLevels = new int[64];
	int[] iOutputs = new int[64];
	int[] sendLevels = new int[64];

	
	final byte[] saveBytes = new byte[384];

	ApsMixer(byte[] loadBytes) {
		for (int i = 0; i < 64; i++) {
			fxPaths[i] = loadBytes[(i * 6) + 0] & 0xFF;
			levels[i] = loadBytes[(i * 6) + 1] & 0xFF;
			pannings[i] = (loadBytes[(i * 6) + 2] & 0xFF);
			iLevels[i] = loadBytes[(i * 6) + 3] & 0xFF;
			iOutputs[i] = loadBytes[(i * 6) + 4] & 0xFF;
			sendLevels[i] = loadBytes[(i * 6) + 5] & 0xFF;
		}
	}

	ApsMixer(MpcMixParameters[] mixer) {
		for (int i = 0; i < 64; i++) {
			saveBytes[(i * 6) + 0] = (byte) mixer[i].getFxPath();
			saveBytes[(i * 6) + 1] = (byte) mixer[i].getLevel();
			saveBytes[(i * 6) + 2] = (byte) (mixer[i].getPanning());
			saveBytes[(i * 6) + 3] = (byte) mixer[i].getVolumeIndividualOut();
			saveBytes[(i * 6) + 4] = (byte) mixer[i].getOutput();
			saveBytes[(i * 6) + 5] = (byte) mixer[i].getFxSendLevel();
		}
	}

	ConcreteMixParameters getMixVariables(int note) {
		ConcreteMixParameters params = new ConcreteMixParameters();
		params.setFxPath(getFxPath(note));
		params.setLevel(getLevel(note));
		params.setPanning(getPanning(note));
		params.setVolumeIndividualOut(getIndividualLevel(note));
		params.setOutput(getIndividualOutput(note));
		params.setFxSendLevel(getSendLevel(note));
		return params;
	}

	int getFxPath(int note) {
		return fxPaths[note - 35];
	}

	int getLevel(int note) {
		return levels[note - 35];
	}

	int getPanning(int note) {
		return pannings[note - 35];
	}

	int getIndividualLevel(int note) {
		return iLevels[note - 35];
	}

	int getIndividualOutput(int note) {
		return iOutputs[note - 35];
	}

	int getSendLevel(int note) {
		return sendLevels[note - 35];
	}

	byte[] getBytes() {
		return saveBytes;
	}
}