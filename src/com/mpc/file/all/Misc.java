package com.mpc.file.all;

import com.mpc.gui.Gui;

class Misc {

	final static int LENGTH = 0x80;
	
	final static private int TAP_AVG_OFFSET = 0x21;
	final static private int MIDI_SYNC_IN_RECEIVE_MMC_OFFSET = 0x22;
	final static private int AUTO_STEP_INCREMENT_OFFSET = 0x2E;
	final static private int DURATION_OF_REC_NOTES_OFFSET = 0x2F;
	final static private int DURATION_TC_PERCENTAGE_OFFSET = 0x30;
	final static private int MIDI_PGM_CHANGE_TO_SEQ_OFFSET = 0x31; // bit1
	
	/*
	 *  Attributes for loading
	 */
	
	int tapAvg;
	boolean inReceiveMMCEnabled;
	boolean autoStepInc;
	boolean durationOfRecNotesTcEnabled;
	int durationTcPercentage;
	boolean pgmChToSeqEnabled;

	
	/*
	 *  Attributes for saving
	 */
	
	byte[] saveBytes;
	
	/*
	 *  Constructor and methods for loading
	 */
	
	public Misc(byte[] b) {
		tapAvg = b[TAP_AVG_OFFSET];
		System.out.println("tap avg " + tapAvg);
		inReceiveMMCEnabled = b[MIDI_SYNC_IN_RECEIVE_MMC_OFFSET] > 0;
		autoStepInc = b[AUTO_STEP_INCREMENT_OFFSET] > 0;
		durationOfRecNotesTcEnabled = b[DURATION_OF_REC_NOTES_OFFSET] > 0;
		durationTcPercentage = b[DURATION_TC_PERCENTAGE_OFFSET];
		pgmChToSeqEnabled = b[MIDI_PGM_CHANGE_TO_SEQ_OFFSET] > 0;
	}

	int getTapAvg() {
		return tapAvg;
	}
	
	boolean isInReceiveMMCEnabled() {
		return inReceiveMMCEnabled;
	}
	
	boolean isAutoStepIncEnabled() {
		return autoStepInc;
	}
	
	boolean isDurationOfRecNotesTc() {
		return durationOfRecNotesTcEnabled; // if false, rec notes duration as played
	}
	
	int getDurationTcPercentage() {
		return durationTcPercentage;
	}

	boolean isPgmChToSeqEnabled() {
		return pgmChToSeqEnabled;
	}


	
	/*
	 *  Constructor and methods for saving
	 */
	
	public Misc(Gui gui) {
		saveBytes = new byte[LENGTH];
		saveBytes[TAP_AVG_OFFSET] = (byte) gui.getSequencerWindowGui().getTapAvg();
		System.out.println("tap avg = " + gui.getSequencerWindowGui().getTapAvg());
		saveBytes[MIDI_SYNC_IN_RECEIVE_MMC_OFFSET] = (byte) (gui.getMidiSyncGui().isReceiveMMCEnabled() ? 1 : 0);
		saveBytes[AUTO_STEP_INCREMENT_OFFSET] = (byte) (gui.getStepEditorGui().isAutoStepIncrementEnabled() ? 1 : 0);
		saveBytes[DURATION_OF_REC_NOTES_OFFSET] = (byte) (gui.getStepEditorGui().isDurationTcPercentageEnabled() ? 1 :0);
		saveBytes[DURATION_TC_PERCENTAGE_OFFSET] = (byte) (gui.getStepEditorGui().getTcValueRecordedNotes() & 0xFF);
		saveBytes[MIDI_PGM_CHANGE_TO_SEQ_OFFSET] = (byte) (gui.getSequencerWindowGui().isPgmChangeToSeqEnabled() ? 1 :0);
		
	}
	
	byte[] getBytes() {
		return saveBytes;
	}
}
