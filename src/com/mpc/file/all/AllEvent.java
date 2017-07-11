package com.mpc.file.all;

import com.mpc.Util;
import com.mpc.file.BitUtils;
import com.mpc.file.Definitions;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PitchBendEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.SystemExclusiveEvent;

class AllEvent {

	final private static int TICK_BYTE1_OFFSET = 0x00;
	final private static int TICK_BYTE2_OFFSET = 0x01;

	final private static int TICK_BYTE3_OFFSET = 0x02;
	final static int[] TICK_BYTE3_BIT_RANGE = new int[] { 0, 3 };
	
	final static int TRACK_OFFSET =0x03;
	final static int EVENT_ID_OFFSET = 0x04;
	final static byte POLY_PRESSURE_ID = (byte) (0xA0 & 0xFF);
	final static byte CONTROL_CHANGE_ID = (byte) (0xB0 & 0xFF);
	final static byte PGM_CHANGE_ID = (byte) (0xC0 & 0xFF);
	final static byte CH_PRESSURE_ID = (byte) (0xD0 & 0xFF);
	final static byte PITCH_BEND_ID = (byte) (0xE0 & 0xFF);
	final static byte SYS_EX_ID = (byte) (0xF0 & 0xFF);

	Event event;
	byte[] bytes;

	AllEvent(byte[] ba) {
		byte eventID = ba[EVENT_ID_OFFSET];

		if (eventID < 0) {
			switch (ba[EVENT_ID_OFFSET]) {
			case POLY_PRESSURE_ID:
				event = new AllPolyPressureEvent(ba).event;
				break;
			case CONTROL_CHANGE_ID:
				event = new AllControlChangeEvent(ba).event;
				break;
			case PGM_CHANGE_ID:
				event = new AllProgramChangeEvent(ba).event;
				break;
			case CH_PRESSURE_ID:
				event = new AllChannelPressureEvent(ba).event;
				break;
			case PITCH_BEND_ID:
				event = new AllPitchBendEvent(ba).event;
				break;
			case SYS_EX_ID:
				event = new AllSysExEvent(ba).event;
				break;
			}
		} else {
			AllNoteEvent ane = new AllNoteEvent(ba);
			// if (ane.getDuration() > 9999 || ane.getNote() == -1 ||
			// e.getVelocity() == -1) continue;
			NoteEvent ne = new NoteEvent(ane.getNote());
			ne.setDuration(ane.getDuration());
			ne.setTick(ane.getTick());
			ne.setVelocity(ane.getVelocity());
			ne.setVariationValue(ane.getVariationValue());
			ne.setVariationTypeNumber(ane.getVariationType());
			ne.setTrack(ba[TRACK_OFFSET]);
			event = ne;
		}
	}

	AllEvent(Event event) {
		if (event instanceof NoteEvent) {
			bytes = new AllNoteEvent(event).getBytes();
		} else if (event instanceof PolyPressureEvent) {
			bytes = new AllPolyPressureEvent(event).saveBytes;
		} else if (event instanceof ControlChangeEvent) {
			bytes = new AllControlChangeEvent(event).saveBytes;
		} else if (event instanceof ProgramChangeEvent) {
			bytes = new AllProgramChangeEvent(event).saveBytes;
		} else if (event instanceof ChannelPressureEvent) {
			bytes = new AllChannelPressureEvent(event).saveBytes;
		} else if (event instanceof PitchBendEvent) {
			bytes = new AllPitchBendEvent(event).saveBytes;
		} else if (event instanceof SystemExclusiveEvent || event instanceof MixerEvent) {
			bytes = new AllSysExEvent(event).saveBytes;
		}
	}

	Event getEvent() {
		return event;
	}

	byte[] getBytes() {
		return bytes;
	}
	
	static int readTick(byte[] b) {
		short s3 = (short) (BitUtils.removeUnusedBits(b[TICK_BYTE3_OFFSET], TICK_BYTE3_BIT_RANGE) & 0xFF);
		int result = Util.bytePairToUnsignedInt(new byte[] { b[TICK_BYTE1_OFFSET], b[TICK_BYTE2_OFFSET] })
				+ (s3 * Definitions.MAX_UINT16_VALUE);
		return result;
	}
	
	static byte[] writeTick(byte[] event, int tick){
		int remainder = tick % Definitions.MAX_UINT16_VALUE;
		byte[] ba = Util.unsignedIntToBytePair(remainder);
		event[TICK_BYTE1_OFFSET] = ba[0];
		event[TICK_BYTE2_OFFSET] = ba[1];
		short s3 = (short) Math.floor(tick / Definitions.MAX_UINT16_VALUE);
		try {
			event[TICK_BYTE3_OFFSET] = BitUtils.stitchBytes(event[TICK_BYTE3_OFFSET], AllNoteEvent.DURATION_BYTE1_BIT_RANGE, (byte) s3,
					TICK_BYTE3_BIT_RANGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return event;
	}


}
