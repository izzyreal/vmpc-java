package com.mpc.file.all;

import com.mpc.gui.Gui;

class Sequencer {

	final static int LENGTH = 0x10;

	final static byte[] TEMPLATE = new byte[] { 0x00, 0x00, 0x00, 0x00, (byte) (0xB0 & 0xFF), 0x04, 0x01, 0x03, 0x00,
			0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00 };

	final static int SEQ_OFFSET = 0x00;
	final static int TR_OFFSET = 0x02;
	final static int TC_OFFSET = 0x07;
	final static int SECOND_SEQ_ENABLED_OFFSET = 0x09;
	final static int SECOND_SEQ_INDEX_OFFSET = 0x0A;
	
	// Loading

	int sequence;
	int track;
	int tc;
	boolean secondSeqEnabled;
	int secondSeqIndex;

	// Saving

	byte[] saveBytes;

	
	/*
	 * Constructor and methods for loading
	 */

	Sequencer(byte[] loadBytes) {
		sequence = loadBytes[SEQ_OFFSET];
		track = loadBytes[TR_OFFSET];
		tc = loadBytes[TC_OFFSET];
		secondSeqEnabled = loadBytes[SECOND_SEQ_ENABLED_OFFSET] > 0;
		secondSeqIndex = loadBytes[SECOND_SEQ_INDEX_OFFSET];
		
	}


	/*
	 * Constructor and methods for saving
	 */

	Sequencer(Gui gui) {
		saveBytes = new byte[LENGTH];
		for (int i=0;i<LENGTH;i++)
			saveBytes[i] = TEMPLATE[i];
		saveBytes[SEQ_OFFSET] = (byte) gui.getMpc().getSequencer().getActiveSequenceIndex();
		saveBytes[TR_OFFSET] = (byte) gui.getMpc().getSequencer().getActiveTrackIndex();
		saveBytes[TC_OFFSET] = (byte) gui.getMpc().getSequencer().getTcIndex();
		saveBytes[SECOND_SEQ_ENABLED_OFFSET] = (byte) (gui.getMpc().getSequencer().isSecondSequenceEnabled() ? 1 : 0);
		saveBytes[SECOND_SEQ_INDEX_OFFSET] = (byte) gui.getMpc().getSequencer().getSecondSequenceIndex();
	}

	byte[] getBytes() {
		return saveBytes;
	}

}
