package com.mpc.file.all;

import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.Event;

class AllChannelPressureEvent {

	static int AMOUNT_OFFSET = 0x05;
	
	/*
	 *  Constructor for loading
	 */
	
	Event event;

	byte[] saveBytes;
	
	AllChannelPressureEvent(byte[] ba) {
		ChannelPressureEvent cpe = new ChannelPressureEvent();
		cpe.setTick(AllEvent.readTick(ba));
		cpe.setTrack(ba[AllEvent.TRACK_OFFSET]);
		cpe.setAmount(ba[AMOUNT_OFFSET]);
		event = cpe;
	}

	
	/*
	 *  Constructor for saving
	 */
	
	AllChannelPressureEvent(Event e) {
		ChannelPressureEvent cpe = (ChannelPressureEvent) e;
		saveBytes = new byte[8];
		saveBytes[AllEvent.EVENT_ID_OFFSET] = AllEvent.CH_PRESSURE_ID;
		AllEvent.writeTick(saveBytes, (int) e.getTick());
		saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
		saveBytes[AMOUNT_OFFSET] = (byte) cpe.getAmount();
	}
	
}
