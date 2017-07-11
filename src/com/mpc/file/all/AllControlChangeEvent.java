package com.mpc.file.all;

import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.Event;

class AllControlChangeEvent {

	static int CONTROLLER_OFFSET = 0x05;
	static int AMOUNT_OFFSET = 0x06;
	
	/*
	 *  Constructor for loading
	 */
	
	Event event;

	byte[] saveBytes;
	
	AllControlChangeEvent(byte[] ba) {
		ControlChangeEvent cce = new ControlChangeEvent();
		cce.setTick(AllEvent.readTick(ba));
		cce.setTrack(ba[AllEvent.TRACK_OFFSET]);
		cce.setController(ba[CONTROLLER_OFFSET]);
		cce.setAmount(ba[AMOUNT_OFFSET]);
		event = cce;
	}

	
	/*
	 *  Constructor for saving
	 */
	
	AllControlChangeEvent(Event e) {
		ControlChangeEvent cce = (ControlChangeEvent) e;
		saveBytes = new byte[8];
		saveBytes[AllEvent.EVENT_ID_OFFSET] = AllEvent.CONTROL_CHANGE_ID;
		AllEvent.writeTick(saveBytes, (int) e.getTick());
		saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
		saveBytes[CONTROLLER_OFFSET] = (byte) cce.getController();
		saveBytes[AMOUNT_OFFSET] = (byte) cce.getAmount();
	}
	
}
