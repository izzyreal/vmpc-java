package com.mpc.file.all;

import com.mpc.sequencer.Event;
import com.mpc.sequencer.PolyPressureEvent;

class AllPolyPressureEvent {

	static int NOTE_OFFSET = 0x05;
	static int AMOUNT_OFFSET = 0x06;
	
	/*
	 *  Constructor for loading
	 */
	
	Event event;

	byte[] saveBytes;
	
	AllPolyPressureEvent(byte[] ba) {
		PolyPressureEvent ppe = new PolyPressureEvent();
		ppe.setTick(AllEvent.readTick(ba));
		ppe.setTrack(ba[AllEvent.TRACK_OFFSET]);
		ppe.setNote(ba[NOTE_OFFSET]);
		ppe.setAmount(ba[AMOUNT_OFFSET]);
		event = ppe;
	}

	
	/*
	 *  Constructor for saving
	 */
	
	AllPolyPressureEvent(Event e) {
		PolyPressureEvent ppe = (PolyPressureEvent) e;
		saveBytes = new byte[8];
		saveBytes[AllEvent.EVENT_ID_OFFSET] = AllEvent.POLY_PRESSURE_ID;
		AllEvent.writeTick(saveBytes, (int) e.getTick());
		saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
		saveBytes[NOTE_OFFSET] = (byte) ppe.getNote();
		saveBytes[AMOUNT_OFFSET] = (byte) ppe.getAmount();
	}
	
}
