package com.mpc.file.aps;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mpc.tootextensions.MpcSampler;

class ApsSoundNames {

	final static int NAME_STRING_LENGTH = 16;

	// For loading
	List<String> names;

	// For saving
	byte[] saveBytes;

	/*
	 * Constructor for loading
	 */

	ApsSoundNames(byte[] loadBytes) {
		names = new ArrayList<String>();
		int sound = 0;
		while (sound < loadBytes.length / ApsParser.SOUND_NAME_LENGTH) {
			try {
				names.add(new String(Arrays.copyOfRange(loadBytes, sound * ApsParser.SOUND_NAME_LENGTH,
						(sound * ApsParser.SOUND_NAME_LENGTH) + NAME_STRING_LENGTH), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sound++;
		}
	}

	/*
	 * Constructor for saving
	 */

	ApsSoundNames(MpcSampler sampler) {
		saveBytes = new byte[sampler.getSoundCount() * ApsParser.SOUND_NAME_LENGTH];
		for (int i = 0; i < sampler.getSoundCount(); i++) {
			int offset = i * ApsParser.SOUND_NAME_LENGTH;
			for (int j = 0; j < NAME_STRING_LENGTH; j++)
				saveBytes[offset + j] = (byte) (StringUtils.rightPad(sampler.getSound(i).getName(), NAME_STRING_LENGTH).getBytes()[j]);
			saveBytes[offset + NAME_STRING_LENGTH] = ApsParser.NAME_TERMINATOR;
		}
	}

	List<String> get() {
		return names;
	}

	byte[] getBytes() {
		return saveBytes;
	}
}
