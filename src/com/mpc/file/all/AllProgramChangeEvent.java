package com.mpc.file.all;

import com.mpc.sequencer.Event;
import com.mpc.sequencer.ProgramChangeEvent;

class AllProgramChangeEvent {

	static int PROGRAM_OFFSET = 0x05;
	
	/*
	 *  Constructor for loading
	 */
	
	Event event;

	byte[] saveBytes;
	
	AllProgramChangeEvent(byte[] ba) {
		ProgramChangeEvent pce = new ProgramChangeEvent();
		pce.setTick(AllEvent.readTick(ba));
		pce.setTrack(ba[AllEvent.TRACK_OFFSET]);
		pce.setProgram(ba[PROGRAM_OFFSET]+1);
		event = pce;
	}

	
	/*
	 *  Constructor for saving
	 */
	
	AllProgramChangeEvent(Event e) {
		ProgramChangeEvent pce = (ProgramChangeEvent) e;
		saveBytes = new byte[8];
		saveBytes[AllEvent.EVENT_ID_OFFSET] = AllEvent.PGM_CHANGE_ID;
		AllEvent.writeTick(saveBytes, (int) e.getTick());
		saveBytes[AllEvent.TRACK_OFFSET] = (byte) e.getTrack();
		saveBytes[PROGRAM_OFFSET] = (byte) (pce.getProgram() - 1);
	}
	
}
