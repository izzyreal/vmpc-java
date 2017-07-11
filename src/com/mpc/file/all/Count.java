package com.mpc.file.all;

import com.mpc.gui.Gui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;

class Count {
	
	/*
	 * Offsets relative chunk start.
	 */

	final static int ENABLED_OFFSET = 0x05;
	final static int COUNT_IN_MODE_OFFSET = 0x06;
	final static int CLICK_VOLUME_OFFSET = 0x07;
	final static int RATE_OFFSET = 0x08;
	final static int ENABLED_IN_PLAY_OFFSET = 0x09;
	final static int ENABLED_IN_REC_OFFSET = 0x0A;
	final static int CLICK_OUTPUT_OFFSET = 0x0B;
	final static int WAIT_FOR_KEY_ENABLED_OFFSET = 0x0C;
	final static int SOUND_OFFSET = 0x0D;
	final static int ACCENT_VELO_OFFSET = 0x0E;
	final static int NORMAL_VELO_OFFSET = 0x0F;

	
	/*
	 *  Attributes for loading.
	 */

	boolean enabled;
	int countInMode;
	int clickVolume;
	int rate;
	boolean enabledInPlay;
	boolean enabledInRec;
	int clickOutput;
	boolean waitForKeyEnabled;
	int sound;
	int accentVelo;
	int normalVelo;

	
	/*
	 *  Attribute for saving
	 */

	byte[] saveBytes;

	
	/*
	 * Constructor and methods for loading
	 */

	Count(byte[] b) {
		enabled = b[ENABLED_OFFSET] > 0;
		countInMode = b[COUNT_IN_MODE_OFFSET];
		clickVolume = b[CLICK_VOLUME_OFFSET];
		rate = b[RATE_OFFSET];
		enabledInPlay = b[ENABLED_IN_PLAY_OFFSET] > 0;
		enabledInRec = b[ENABLED_IN_REC_OFFSET] > 0;
		clickOutput = b[CLICK_OUTPUT_OFFSET];
		waitForKeyEnabled = b[WAIT_FOR_KEY_ENABLED_OFFSET] > 0;
		sound = b[SOUND_OFFSET];
		accentVelo = b[ACCENT_VELO_OFFSET];
		normalVelo = b[NORMAL_VELO_OFFSET];
	}

	boolean isEnabled() {
		return enabled;
	}

	int getCountInMode() {
		return countInMode;
	}

	int getClickVolume() {
		return clickVolume;
	}

	int getRate() {
		return rate;
	}

	boolean isEnabledInPlay() {
		return enabledInPlay;
	}

	boolean isEnabledInRec() {
		return enabledInRec;
	}

	int getClickOutput() {
		return clickOutput;
	}

	boolean isWaitForKeyEnabled() {
		return waitForKeyEnabled;
	}

	int getSound() {
		return sound;
	}

	int getAccentVelo() {
		return accentVelo;
	}

	int getNormalVelo() {
		return normalVelo;
	}

	/*
	 * Constructor and methods for saving
	 */

	Count(Gui gui) {
		SequencerWindowGui swgui = gui.getSequencerWindowGui();
		saveBytes = new byte[AllParser.COUNT_LENGTH];
		saveBytes[ENABLED_OFFSET] = (byte) (gui.getMpc().getSequencer().isCountEnabled() ? 1 : 0);
		saveBytes[COUNT_IN_MODE_OFFSET] = (byte) swgui.getCountInMode();
		saveBytes[CLICK_VOLUME_OFFSET] = (byte) swgui.getClickVolume();
		saveBytes[RATE_OFFSET] = (byte) swgui.getRate();
		saveBytes[ENABLED_IN_PLAY_OFFSET] = (byte) (swgui.getInPlay() ? 1 : 0);
		saveBytes[ENABLED_IN_REC_OFFSET] = (byte) (swgui.getInRec() ? 1 : 0);
		saveBytes[CLICK_OUTPUT_OFFSET] = (byte) swgui.getClickOutput();
		saveBytes[WAIT_FOR_KEY_ENABLED_OFFSET] = (byte) (swgui.isWaitForKeyEnabled() ? 1 : 0);
		saveBytes[SOUND_OFFSET] = (byte) swgui.getMetronomeSound();
		saveBytes[ACCENT_VELO_OFFSET] = (byte) swgui.getAccentVelo();
		saveBytes[NORMAL_VELO_OFFSET] = (byte) swgui.getNormalVelo();
	}
	
	byte[] getBytes() {
		return saveBytes;
	}
}
