package com.mpc.file.all;

import com.mpc.Util;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.PitchBendEvent;

class AllPitchBendEvent {

	static int AMOUNT_OFFSET = 0x05;

	/*
	 * Constructor for loading
	 */

	Event event;

	byte[] saveBytes;

	AllPitchBendEvent(byte[] ba) {
		PitchBendEvent pbe = new PitchBendEvent();
		pbe.setTick(AllEvent.readTick(ba));
		pbe.setTrack(ba[AllEvent.TRACK_OFFSET]);
		int candidate = Util.bytePairToUnsignedInt(new byte[] { ba[AMOUNT_OFFSET], ba[AMOUNT_OFFSET + 1] }) - 16384;
		if (candidate < -8192) candidate += 8192;
		pbe.setAmount(candidate);
		event = pbe;
	}

	/*
	 * Constructor for saving
	 */

	AllPitchBendEvent(Event e) {
		PitchBendEvent pbe = (PitchBendEvent) e;
		saveBytes = new byte[8];
		saveBytes[AllEvent.EVENT_ID_OFFSET] = AllEvent.PITCH_BEND_ID;
		AllEvent.writeTick(saveBytes, (int) e.getTick());
		saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
		int candidate = pbe.getAmount() + 16384;
		if (pbe.getAmount() < 0) candidate = pbe.getAmount() + 8192;
		byte[] amountBytes = Util.unsignedIntToBytePair(candidate);
		saveBytes[AMOUNT_OFFSET] = amountBytes[0];
		saveBytes[AMOUNT_OFFSET + 1] = amountBytes[1];
	}

}
