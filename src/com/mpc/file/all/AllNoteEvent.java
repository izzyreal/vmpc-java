package com.mpc.file.all;

import com.mpc.file.BitUtils;
import com.mpc.sequencer.NoteEvent;

class AllNoteEvent {

	/*
	 * Offsets relative to event start.
	 */

	/*
	 * A tick value, 20 bits in total, is composed of respectively a uint16le
	 * and a 4 bit int. The latter is multiplied by 65536 for ticks >= 65536.
	 * The outcome + the value of the uint16 is the tick value.
	 * 
	 * Notice that the unused bits of byte #3 are used by the next attribute
	 * (track number).
	 * 
	 * BIT_RANGEs are provided to inform the parser of the indexes of the bits
	 * that are used by an attribute. { 0, 3 } means bits 1, 2, 3 and 4 are in
	 * use.
	 * 
	 */

	final private static int DURATION_BYTE1_OFFSET = 0x02;
	final static int[] DURATION_BYTE1_BIT_RANGE = new int[] { 4, 7 };

	final private static int DURATION_BYTE2_OFFSET = 0x03;
	final private static int[] DURATION_BYTE2_BIT_RANGE = new int[] { 6, 7 }; // was
																				// 6,7

	final private static int TRACK_NUMBER_OFFSET = 0x03;
	final private static int[] TRACK_NUMBER_BIT_RANGE = new int[] { 0, 5 }; // was
																			// 0,5

	final private static int NOTE_NUMBER_OFFSET = 0x04;

	final private static int DURATION_BYTE3_OFFSET = 0x05;

	final private static int VELOCITY_OFFSET = 0x06;
	final private static int[] VELOCITY_BIT_RANGE = new int[] { 0, 6 };

	final private static int VAR_TYPE_BYTE1_OFFSET = 0x06;
	final private static int VAR_TYPE_BYTE1_BIT = 7;

	final private static int VAR_TYPE_BYTE2_OFFSET = 0x07;
	final private static int VAR_TYPE_BYTE2_BIT = 7;

	final private static int VAR_VALUE_OFFSET = 0x07;
	final private static int[] VAR_VALUE_BIT_RANGE = new int[] { 0, 6 };

	/*
	 * Attributes for loading.
	 */

	int note;
	int tick;
	int duration;
	int track;
	int velocity;
	int variationType;
	int variationValue;

	/*
	 * Attributes for saving.
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	AllNoteEvent(byte[] b) {
		note = b[NOTE_NUMBER_OFFSET];
		tick = AllEvent.readTick(b);
		track = readTrackNumber(b);
		duration = readDuration(b) - (track * 4); // track*4 is a hack
//		if (note == 43) System.out.println("found note 43 at tick " + tick + " track " + track + " duration " + duration );
		velocity = readVelocity(b);
//		System.out.println("velocity: " + velocity);
		variationValue = readVariationValue(b);
		variationType = readVariationType(b);
	}

	int getNote() {
		return note;
	}

	int getTick() {
		return tick;
	}

	int getDuration() {
		return duration;
	}

	int getTrack() {
		return track;
	}

	int getVelocity() {
		return velocity;
	}

	int getVariationType() {
		return variationType;
	}

	int getVariationValue() {
		return variationValue;
	}

	private int readDuration(byte[] b) {

		byte b1 = b[DURATION_BYTE1_OFFSET];
		byte b2 = b[DURATION_BYTE2_OFFSET];
		byte b3 = b[DURATION_BYTE3_OFFSET];

		if (b1 == 0xFF && b2 == 0xFF && b3 == 0xFF) return -1;

		b1 = BitUtils.removeUnusedBits(b1, DURATION_BYTE1_BIT_RANGE);
		b2 = BitUtils.removeUnusedBits(b2, DURATION_BYTE2_BIT_RANGE);

		int i1 = (int) (b1 & 0xFF);
		int i2 = (int) (b2 & 0xFF);
		int i3 = (int) (b3 & 0xFF);

		return (i1 << 6) + (i2 << 2) + i3;
	}

	private int readTrackNumber(byte[] ba) {
		byte b = BitUtils.removeUnusedBits(ba[TRACK_NUMBER_OFFSET], TRACK_NUMBER_BIT_RANGE);
		return b;

	}

	private int readVelocity(byte[] ba) {
		byte b = BitUtils.removeUnusedBits(ba[VELOCITY_OFFSET], VELOCITY_BIT_RANGE);
		return b;
	}

	private int readVariationValue(byte[] ba) {
		byte b = BitUtils.removeUnusedBits(ba[VAR_VALUE_OFFSET], VAR_VALUE_BIT_RANGE);
		return b;
	}

	private int readVariationType(byte[] ba) {
		byte byte1 = ba[VAR_TYPE_BYTE1_OFFSET];
		byte byte2 = ba[VAR_TYPE_BYTE2_OFFSET];
		boolean b1 = BitUtils.isBitOn(byte1, VAR_TYPE_BYTE1_BIT);
		boolean b2 = BitUtils.isBitOn(byte2, VAR_TYPE_BYTE2_BIT);
		if (b1 && b2) return 3;
		if (b1 && !b2) return 2;
		if (!b1 && b2) return 1;
		if (!b1 && !b2) return 0;
		return -1;
	}

	/*
	 * Constructor and methods for saving
	 */

	AllNoteEvent(com.mpc.sequencer.Event event) {
		saveBytes = new byte[Sequence.EVENT_SEG_LENGTH];
		NoteEvent ne = (NoteEvent) event;
		saveBytes[NOTE_NUMBER_OFFSET] = (byte) ne.getNote();
		try {
			saveBytes = setVelocity(saveBytes, ne.getVelocity());
			saveBytes = setTrackNumber(saveBytes, ne.getTrack());
			saveBytes = setVariationType(saveBytes, ne.getVariationTypeNumber());
			saveBytes = setVariationValue(saveBytes, ne.getVariationValue());
			saveBytes = AllEvent.writeTick(saveBytes, (int) ne.getTick());
			saveBytes = setDuration(saveBytes, (int) ne.getDuration());
		} catch (Exception e) {
			System.out.println("Error stitching bytes! Check for overlap/non-contiguousness in ranges.");
		}
	}

	byte[] setVelocity(byte[] event, int v) throws Exception {
		byte value = (byte) v;
		event[VELOCITY_OFFSET] = BitUtils.stitchBytes(event[VELOCITY_OFFSET],
				new int[] { VAR_TYPE_BYTE1_BIT, VAR_TYPE_BYTE1_BIT }, value, VELOCITY_BIT_RANGE);
		return event;
	}

	byte[] setTrackNumber(byte[] event, int t) throws Exception {
		byte value = (byte) t;
		event[TRACK_NUMBER_OFFSET] = BitUtils.stitchBytes(event[TRACK_NUMBER_OFFSET], DURATION_BYTE2_BIT_RANGE, value,
				TRACK_NUMBER_BIT_RANGE);
		return event;
	}

	byte[] setVariationValue(byte[] event, int v) throws Exception {
		byte value = (byte) v;
		event[VAR_VALUE_OFFSET] = BitUtils.stitchBytes(event[VAR_VALUE_OFFSET],
				new int[] { VAR_TYPE_BYTE2_BIT, VAR_TYPE_BYTE2_BIT }, value, VAR_VALUE_BIT_RANGE);
		return event;
	}

	byte[] setDuration(byte[] event, int duration) throws Exception {

		short s1 = (short) (duration >> 6);
		short s2 = (short) (duration >> 2);
		short s3 = (short) (duration & 0xFF);

		event[DURATION_BYTE1_OFFSET] = BitUtils.stitchBytes(event[DURATION_BYTE1_OFFSET], AllEvent.TICK_BYTE3_BIT_RANGE,
				(byte) s1, DURATION_BYTE1_BIT_RANGE);
		event[DURATION_BYTE2_OFFSET] = BitUtils.stitchBytes(event[DURATION_BYTE2_OFFSET], TRACK_NUMBER_BIT_RANGE,
				(byte) s2, DURATION_BYTE2_BIT_RANGE);
		event[DURATION_BYTE3_OFFSET] = (byte) s3;
		return event;
	}

	byte[] setVariationType(byte[] event, int type) {
		byte byte1 = event[VAR_TYPE_BYTE1_OFFSET];
		byte byte2 = event[VAR_TYPE_BYTE2_OFFSET];

		switch (type) {
		case 0:
			BitUtils.setBit(byte1, VAR_TYPE_BYTE1_BIT, false);
			BitUtils.setBit(byte2, VAR_TYPE_BYTE2_BIT, false);
			break;
		case 1:
			BitUtils.setBit(byte1, VAR_TYPE_BYTE1_BIT, false);
			BitUtils.setBit(byte2, VAR_TYPE_BYTE2_BIT, true);
			break;
		case 2:
			BitUtils.setBit(byte1, VAR_TYPE_BYTE1_BIT, true);
			BitUtils.setBit(byte2, VAR_TYPE_BYTE2_BIT, false);
			break;
		case 3:
			BitUtils.setBit(byte1, VAR_TYPE_BYTE1_BIT, true);
			BitUtils.setBit(byte2, VAR_TYPE_BYTE2_BIT, true);
			break;
		}
		event[VAR_TYPE_BYTE1_OFFSET] = byte1;
		event[VAR_TYPE_BYTE2_OFFSET] = byte2;
		return event;
	}

	byte[] getBytes() {
		return saveBytes;
	}

}
