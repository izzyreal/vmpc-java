package com.mpc.file.all;

import java.util.Arrays;

import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.SystemExclusiveEvent;

class AllSysExEvent {

	// offsets within total sysex chunk
	static int CHUNK_HEADER_ID_OFFSET = 0x04;
	static int BYTE_COUNT_OFFSET = 0x05; // count of data bytes including
											// initiator/terminator
	static int DATA_OFFSET = 0x08;
	static int MIX_TERMINATOR_ID_OFFSET = 0x1C;

	static byte DATA_HEADER_ID_OFFSET = 0x08;
	static byte HEADER_ID = (byte) 0xF0;
	static byte DATA_TERMINATOR_ID = (byte) 0xF7;
	static byte CHUNK_TERMINATOR_ID = (byte) 0xF8;

	// offsets within data chunk
	static int MIXER_SIGNATURE_OFFSET = 0x00;
	static byte[] MIXER_SIGNATURE = new byte[] { (byte) 0xF0, 0x47, 0x00, 0x44, 0x45 };
	static int MIXER_PARAMETER_OFFSET = 0x05;
	static int MIXER_PAD_OFFSET = 0x06;
	static int MIXER_VALUE_OFFSET = 0x07;

	/*
	 * Attributes for loading
	 */

	byte[] sysexLoadData;
	Event event;

	/*
	 * Attributes for saving
	 */

	byte[] saveBytes;

	/*
	 * Constructor for loading
	 */

	public AllSysExEvent(byte[] ba) {
		int byteCount = ba[BYTE_COUNT_OFFSET];
		sysexLoadData = new byte[byteCount];
		for (int i = 0; i < byteCount; i++)
			sysexLoadData[i] = ba[DATA_OFFSET + i];

		if (Arrays.equals(Arrays.copyOfRange(sysexLoadData, MIXER_SIGNATURE_OFFSET,
				MIXER_SIGNATURE_OFFSET + MIXER_SIGNATURE.length), MIXER_SIGNATURE)) {
			MixerEvent me = new MixerEvent();
			System.out.println("param number in file " + sysexLoadData[MIXER_PARAMETER_OFFSET]);
			int paramCandidate = sysexLoadData[MIXER_PARAMETER_OFFSET] - 1;
			if (paramCandidate == 4) paramCandidate = 3;
			me.setParameter(paramCandidate);
			me.setPadNumber(sysexLoadData[MIXER_PAD_OFFSET]);
			me.setValue(sysexLoadData[MIXER_VALUE_OFFSET]);
			me.setTick(AllEvent.readTick(ba));
			event = me;
		} else {
			SystemExclusiveEvent see = new SystemExclusiveEvent();
			see.setBytes(sysexLoadData);
			see.setTick(AllEvent.readTick(ba));
			event = see;
		}
		event.setTrack(ba[AllEvent.TRACK_OFFSET]);
	}

	/*
	 * Constructor and methods for saving
	 */

	public AllSysExEvent(Event e) {
		if (e instanceof MixerEvent) {
			MixerEvent me = (MixerEvent) e;
			saveBytes = new byte[32];
			AllEvent.writeTick(saveBytes, (int) me.getTick());
			saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
			saveBytes[CHUNK_HEADER_ID_OFFSET] = HEADER_ID;
			saveBytes[BYTE_COUNT_OFFSET] = 0x09;

			saveBytes[DATA_HEADER_ID_OFFSET] = HEADER_ID;

			for (int i = 0; i < MIXER_SIGNATURE.length; i++)
				saveBytes[DATA_OFFSET + i] = MIXER_SIGNATURE[i];

			saveBytes[DATA_OFFSET + MIXER_PAD_OFFSET] = (byte) me.getPad();
			int paramCandidate = me.getParameter();
			if (paramCandidate == 3) paramCandidate = 4;
			paramCandidate++;
			saveBytes[DATA_OFFSET + MIXER_PARAMETER_OFFSET] = (byte) (paramCandidate);
			saveBytes[DATA_OFFSET + MIXER_VALUE_OFFSET] = (byte) me.getValue();
			saveBytes[DATA_OFFSET + MIXER_VALUE_OFFSET + 1] = DATA_TERMINATOR_ID;

			saveBytes[MIX_TERMINATOR_ID_OFFSET] = CHUNK_TERMINATOR_ID;
		} else if (e instanceof SystemExclusiveEvent) {
			SystemExclusiveEvent see = (SystemExclusiveEvent) e;
			AllEvent.writeTick(saveBytes, (int) see.getTick());
			int dataSize = see.getBytes().length;
			int dataSegments = (int) (Math.ceil(dataSize / 8.0));

			saveBytes = new byte[(dataSegments + 2) * Sequence.EVENT_SEG_LENGTH];
			saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
			saveBytes[AllEvent.TRACK_OFFSET + ((dataSegments + 1) * Sequence.EVENT_SEG_LENGTH)] = (byte) e.getTrack();

			saveBytes[CHUNK_HEADER_ID_OFFSET] = HEADER_ID;
			saveBytes[BYTE_COUNT_OFFSET] = (byte) dataSize;

			// saveBytes[DATA_HEADER_ID_OFFSET] = HEADER_ID;

			for (int i = 0; i < dataSize; i++)
				saveBytes[DATA_OFFSET + i] = see.getBytes()[i];

			// saveBytes[DATA_OFFSET + dataSize] = DATA_TERMINATOR_ID;
			saveBytes[saveBytes.length - 4] = CHUNK_TERMINATOR_ID;
		}
	}
}
