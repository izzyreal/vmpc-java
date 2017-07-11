package com.mpc.file.all;

import com.mpc.gui.Gui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;

class MidiInput {

	final static private byte[] TEMPLATE = new byte[] { 0x07F, 0x40, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x02,
			0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14,
			0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22, 0x01, 0x00, 0x00, 0x00,
			0x00, 0x00 };
	final static int LENGTH = 0x30;;

	/*
	 * Offsets relative to chunk start.
	 */

	final static int RECEIVE_CH_OFFSET = 0x03;
	final static int SUSTAIN_PEDAL_TO_DURATION_OFFSET = 0x04;
	final static int FILTER_ENABLED_OFFSET = 0x05;
	final static int FILTER_TYPE_OFFSET = 0x06;
	final static int MULTI_REC_ENABLED_OFFSET = 0x07;
	final static int MULTI_REC_TRACK_DESTS_OFFSET = 0x08;
	final static int MULTI_REC_TRACK_DESTS_LENGTH = 0x22; // 2x 16 midi channels
															// + 2x exclusive
	final static int NOTE_PASS_ENABLED_OFFSET = 0x2A;
	final static int PITCH_BEND_PASS_ENABLED_OFFSET = 0x2B;
	final static int PGM_CHANGE_PASS_ENABLED_OFFSET = 0x2C;
	final static int CH_PRESSURE_PASS_ENABLED_OFFSET = 0x2D;
	final static int POLY_PRESSURE_PASS_ENABLED_OFFSET = 0x2E;
	final static int EXCLUSIVE_PASS_ENABLED_OFFSET = 0x2F;

	/*
	 * Attributes for loading.
	 */

	int receiveCh;
	boolean sustainPedalToDuration;
	boolean filterEnabled;
	int filterType;
	boolean multiRecEnabled;
	int[] multiRecTrackDests = new int[34];
	boolean notePassEnabled;
	boolean pitchBendPassEnabled;
	boolean pgmChangePassEnabled;
	boolean chPressurePassEnabled;
	boolean polyPressurePassEnabled;
	boolean exclusivePassEnabled;

	/*
	 * Attribute for saving.
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	MidiInput(byte[] b) {
		receiveCh = b[RECEIVE_CH_OFFSET];
		sustainPedalToDuration = b[SUSTAIN_PEDAL_TO_DURATION_OFFSET] > 0;
		System.out.println("sustainPedalToDuration " + sustainPedalToDuration);
		System.out.println("sustainPedalToDuration byte " + b[SUSTAIN_PEDAL_TO_DURATION_OFFSET]);
		filterEnabled = b[FILTER_ENABLED_OFFSET] > 0;
		filterType = b[FILTER_TYPE_OFFSET];
		multiRecEnabled = b[MULTI_REC_ENABLED_OFFSET] > 0;

		for (int i = 0; i < MULTI_REC_TRACK_DESTS_LENGTH; i++)
			multiRecTrackDests[i] = b[MULTI_REC_TRACK_DESTS_OFFSET + i] - 1;

		notePassEnabled = b[NOTE_PASS_ENABLED_OFFSET] > 0;
		pitchBendPassEnabled = b[PITCH_BEND_PASS_ENABLED_OFFSET] > 0;
		pgmChangePassEnabled = b[PGM_CHANGE_PASS_ENABLED_OFFSET] > 0;
		chPressurePassEnabled = b[CH_PRESSURE_PASS_ENABLED_OFFSET] > 0;
		polyPressurePassEnabled = b[POLY_PRESSURE_PASS_ENABLED_OFFSET] > 0;
		exclusivePassEnabled = b[EXCLUSIVE_PASS_ENABLED_OFFSET] > 0;
	}

	int getReceiveCh() {
		return receiveCh;
	}

	boolean isSustainPedalToDurationEnabled() {
		return sustainPedalToDuration;
	}

	boolean isFilterEnabled() {
		return filterEnabled;
	}

	int getFilterType() {
		return filterType;
	}

	boolean isMultiRecEnabled() {
		return multiRecEnabled;
	}

	int[] getMultiRecTrackDests() {
		return multiRecTrackDests;
	}

	boolean isNotePassEnabled() {
		return notePassEnabled;
	}

	boolean isPitchBendPassEnabled() {
		return pitchBendPassEnabled;
	}

	boolean isPgmChangePassEnabled() {
		return pgmChangePassEnabled;
	}

	boolean isChPressurePassEnabled() {
		return chPressurePassEnabled;
	}

	boolean isPolyPressurePassEnabled() {
		return polyPressurePassEnabled;
	}

	boolean isExclusivePassEnabled() {
		return exclusivePassEnabled;
	}

	/*
	 * Constructor and methods for saving
	 */

	MidiInput(Gui gui) {
		saveBytes = new byte[LENGTH];
		for (int i = 0; i < LENGTH; i++)
			saveBytes[i] = TEMPLATE[i];
		SequencerWindowGui swgui = gui.getSequencerWindowGui();
		saveBytes[RECEIVE_CH_OFFSET] = (byte) swgui.getReceiveCh();
		saveBytes[SUSTAIN_PEDAL_TO_DURATION_OFFSET] = (byte) (swgui.isSustainPedalToDurationEnabled() ? 1 : 0);
		System.out.println("sustainPedalToDuration " + swgui.isSustainPedalToDurationEnabled());
		saveBytes[FILTER_ENABLED_OFFSET] = (byte) (swgui.isMidiFilterEnabled() ? 1 : 0);
		saveBytes[FILTER_TYPE_OFFSET] = (byte) swgui.getMidiFilterType();
		saveBytes[MULTI_REC_ENABLED_OFFSET] = (byte) (gui.getMpc().getSequencer().isRecordingModeMulti() ? 1 : 0);

		for (int i = 0; i < MULTI_REC_TRACK_DESTS_LENGTH; i++)
			saveBytes[MULTI_REC_TRACK_DESTS_OFFSET
					+ i] = (byte) (gui.getSequencerWindowGui().getMrsLines()[i].getTrack() + 1);

		saveBytes[NOTE_PASS_ENABLED_OFFSET] = (byte) (swgui.isNotePassEnabled() ? 1 : 0);
		saveBytes[PITCH_BEND_PASS_ENABLED_OFFSET] = (byte) (swgui.isPitchBendPassEnabled() ? 1 : 0);
		saveBytes[PGM_CHANGE_PASS_ENABLED_OFFSET] = (byte) (swgui.isPgmChangePassEnabled() ? 1 : 0);
		saveBytes[CH_PRESSURE_PASS_ENABLED_OFFSET] = (byte) (swgui.isChPressurePassEnabled() ? 1 : 0);
		saveBytes[POLY_PRESSURE_PASS_ENABLED_OFFSET] = (byte) (swgui.isPolyPressurePassEnabled() ? 1 : 0);
		saveBytes[EXCLUSIVE_PASS_ENABLED_OFFSET] = (byte) (swgui.isExclusivePassEnabled() ? 1 : 0);

	}

	byte[] getBytes() {
		return saveBytes;
	}
}
