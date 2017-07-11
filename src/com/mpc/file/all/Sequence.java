package com.mpc.file.all;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Util;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.SystemExclusiveEvent;

public class Sequence {

	final static int MAX_SYSEX_SIZE = 256;

	final static byte EVENT_ID_OFFSET = 0x04;
	final static byte POLY_PRESSURE_ID = (byte) (0xA0 & 0xFF);
	final static byte CONTROL_CHANGE_ID = (byte) (0xB0 & 0xFF);
	final static byte PGM_CHANGE_ID = (byte) (0xC0 & 0xFF);
	final static byte CH_PRESSURE_ID = (byte) (0xD0 & 0xFF);
	final static byte PITCH_BEND_ID = (byte) (0xE0 & 0xFF);
	final static byte SYS_EX_ID = (byte) 0xF0;
	final static byte SYS_EX_TERMINATOR_ID = (byte) 0xF8;

	/*
	 * TERMINATOR Marks the end of a sequence chunk. If total event count is
	 * even numbered, insert 1 terminator extra.
	 */

	final static byte[] TERMINATOR = { (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF),
			(byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF),
			(byte) (0xFF & 0xFF) };

	final static int MAX_EVENT_SEG_COUNT = 50000; // as stated in the manual
	final static int EVENT_SEG_LENGTH = 0x08;

	/*
	 * Offsets relative to chunk start.
	 */

	final static int NAME_OFFSET = 0x0000;

	final static int LAST_EVENT_INDEX_OFFSET = 0x0010;
	final static int SEQUENCE_INDEX_OFFSET = 0x0012;
	final static int PADDING1_OFFSET = 0x0013;
	final static byte[] PADDING1 = new byte[] { 0x01, 0x01, 0x00 };
	final static int TEMPO_BYTE1_OFFSET = 0x0016;
	final static int TEMPO_BYTE2_OFFSET = 0x0017;

	final static int PADDING2_OFFSET = 0x0018;
	final static byte[] PADDING2 = new byte[] { 0x04, 0x04 };

	final static int BAR_COUNT_BYTE1_OFFSET = 0x001A;
	final static int BAR_COUNT_BYTE2_OFFSET = 0x001B;

	final static int LAST_TICK_BYTE1_OFFSET = 0x001C;
	final static int LAST_TICK_BYTE2_OFFSET = 0x001D;

	final static int UNKNOWN32_BIT_INT_OFFSET = 0x0020;

	final static int LOOP_FIRST_OFFSET = 0x0030; // 2 bytes
	final static int LOOP_LAST_OFFSET = 0x0032; // 2 bytes, FF FF == END
	final static int LOOP_ENABLED_OFFSET = 0x0034;

	final static int PADDING4_OFFSET = 0x003B;
	final static byte[] PADDING4 = new byte[] { 0x28, 0x00, (byte) (0x80 & 0xFF), 0x00, 0x00 };

	final static int LAST_TICK_BYTE3_OFFSET = 0x0040;
	final static int LAST_TICK_BYTE4_OFFSET = 0x0041;

	final static int DEVICE_NAMES_OFFSET = 0x0078;

	final static int TRACKS_OFFSET = 0x0180;
	final static int TRACKS_LENGTH = 0x06E4;

	final static int BAR_LIST_OFFSET = 0x1503;
	final static int BAR_LIST_LENGTH = 0x0F9C; // 999 bars, 4 bytes each

	final static int EVENTS_OFFSET = 0x2800;

	/*
	 * Attributes for loading.
	 */

	String name;
	int barCount;
	int loopFirst;
	int loopLast;
	boolean loopLastEnd;
	boolean loop;
	BigDecimal tempo;
	String[] devNames = new String[33];
	Tracks tracks;
	BarList barList;
	List<Event> allEvents;

	/*
	 * Attributes for saving.
	 */

	private byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	Sequence(byte[] b) {

		barList = new BarList(Arrays.copyOfRange(b, BAR_LIST_OFFSET, BAR_LIST_OFFSET + BAR_LIST_LENGTH));

		try {
			name = new String(Arrays.copyOfRange(b, NAME_OFFSET, NAME_OFFSET + AllParser.NAME_LENGTH), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		tempo = new BigDecimal("" + getTempoDouble(new byte[] { b[TEMPO_BYTE1_OFFSET], b[TEMPO_BYTE2_OFFSET] }));

		barCount = Util.bytePairToUnsignedInt(new byte[] { b[BAR_COUNT_BYTE1_OFFSET], b[BAR_COUNT_BYTE2_OFFSET] });
		System.out.println(b[BAR_COUNT_BYTE1_OFFSET] + " " +  b[BAR_COUNT_BYTE2_OFFSET] );
		loopFirst = Util.bytePairToUnsignedInt(new byte[] { b[LOOP_FIRST_OFFSET], b[LOOP_FIRST_OFFSET + 1] });
		loopLast = Util.bytePairToUnsignedInt(new byte[] { b[LOOP_LAST_OFFSET], b[LOOP_LAST_OFFSET + 1] });
		// System.out.println("loop last " + loopLast);
		if (loopLast > 998) {
			loopLast = barCount;
			loopLastEnd = true;
		}
		loop = (b[LOOP_ENABLED_OFFSET] > 0);

		for (int i = 0; i < 33; i++) {
			int offset = DEVICE_NAMES_OFFSET + (i * AllParser.DEV_NAME_LENGTH);
			devNames[i] = new String(Arrays.copyOfRange(b, offset, offset + AllParser.DEV_NAME_LENGTH));
		}

		tracks = new Tracks(Arrays.copyOfRange(b, TRACKS_OFFSET, TRACKS_LENGTH));

		allEvents = readEvents(b);
	}

	private static List<Event> readEvents(byte[] seqBytes) {
		List<Event> aeList = new ArrayList<Event>();
		for (byte[] ba : readEventSegments(seqBytes)) {
			AllEvent ae = new AllEvent(ba);
			System.out.println("Adding event at tick " + ae.getEvent().getTick());
			aeList.add(ae.getEvent());
		}

		return aeList;
	}

	private static List<byte[]> readEventSegments(byte[] seqBytes) {
		List<byte[]> eventArrays = new ArrayList<byte[]>();
		int candidateOffset = EVENTS_OFFSET;

		for (int i = 0; i < MAX_EVENT_SEG_COUNT; i++) {
			int sysexSegs = 0;
			byte[] ea = Arrays.copyOfRange(seqBytes, candidateOffset, candidateOffset + EVENT_SEG_LENGTH);
			if (Arrays.equals(ea, TERMINATOR)) break;

			if (ea[EVENT_ID_OFFSET] == SYS_EX_ID) {
				for (sysexSegs = 0; sysexSegs < MAX_SYSEX_SIZE; sysexSegs++) {
					byte[] potentialTerminator = Arrays.copyOfRange(seqBytes,
							candidateOffset + (sysexSegs * EVENT_SEG_LENGTH),
							candidateOffset + (sysexSegs * EVENT_SEG_LENGTH) + EVENT_SEG_LENGTH);
					if (potentialTerminator[EVENT_ID_OFFSET] == SYS_EX_TERMINATOR_ID) break;
				}
				sysexSegs++;
				ea = Arrays.copyOfRange(seqBytes, candidateOffset, candidateOffset + (sysexSegs * EVENT_SEG_LENGTH));
			}
			eventArrays.add(ea);
			candidateOffset += ea.length;
		}

		return eventArrays;
	}

	private double getTempoDouble(byte[] bytePair) {
		double k = 0;
		int s = Util.bytePairToUnsignedInt(bytePair);
		k = (double) s;
		return k / 10.0;
	}

	static int getNumberOfEventSegmentsForThisSeq(byte[] seqBytes) {
		int accum = 0;
		for (byte[] ba : readEventSegments(seqBytes))
			accum += ba.length / 8;
		return accum;
	}

	int getEventAmount() {
		return allEvents.size();
	}

	/*
	 * Constructor and methods for saving
	 */

	Sequence(MpcSequence seq, int number) {
		int segmentCountLastEventIndex = SequenceNames.getSegmentCount(seq);
		int segmentCount = getSegmentCount(seq);
		System.out.println("SequenceNames.Segmentcount: " + segmentCountLastEventIndex);
		System.out.println("Segmentcount: " + segmentCount);

		int terminatorCount = (segmentCount & 1) == 0 ? 2 : 1;

		saveBytes = new byte[10240 + (segmentCount * Sequence.EVENT_SEG_LENGTH)
				+ (terminatorCount * Sequence.EVENT_SEG_LENGTH)];

		for (int i = 0; i < AllParser.NAME_LENGTH; i++)
			saveBytes[i] = StringUtils.rightPad(seq.getName(), AllParser.NAME_LENGTH).getBytes()[i];

		if ((segmentCountLastEventIndex & 1) != 0) segmentCountLastEventIndex--;
		segmentCountLastEventIndex /= 2;

		byte[] lastEventIndexBytes = Util
				.unsignedIntToBytePair(1 + (segmentCountLastEventIndex < 0 ? 0 : segmentCountLastEventIndex));

		saveBytes[LAST_EVENT_INDEX_OFFSET] = lastEventIndexBytes[0];
		saveBytes[LAST_EVENT_INDEX_OFFSET + 1] = lastEventIndexBytes[1];

		for (int i = PADDING1_OFFSET; i < PADDING1_OFFSET + PADDING1.length; i++)
			saveBytes[i] = PADDING1[i - PADDING1_OFFSET];

		setTempoDouble(seq.getTempoChangeEvents().get(0).getInitialTempo().doubleValue());

		for (int i = PADDING2_OFFSET; i < PADDING2_OFFSET + PADDING2.length; i++)
			saveBytes[i] = PADDING2[i - PADDING2_OFFSET];

		setBarCount(seq.getLastBar() + 1);

		setLastTick(seq);
		saveBytes[SEQUENCE_INDEX_OFFSET] = (byte) number;
		setUnknown32BitInt(seq);

		byte[] loopStartBytes = Util.unsignedIntToBytePair(seq.getFirstLoopBar());
		byte[] loopEndBytes = Util.unsignedIntToBytePair(seq.getLastLoopBar());
		if (seq.isLastLoopBarEnd()) loopEndBytes = new byte[] { (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF) };
		saveBytes[LOOP_FIRST_OFFSET] = loopStartBytes[0];
		saveBytes[LOOP_FIRST_OFFSET + 1] = loopStartBytes[1];
		saveBytes[LOOP_LAST_OFFSET] = loopEndBytes[0];
		saveBytes[LOOP_LAST_OFFSET + 1] = loopEndBytes[1];
		saveBytes[LOOP_ENABLED_OFFSET] = (byte) (seq.isLoopEnabled() ? 1 : 0);

		for (int i = 0; i < PADDING4.length; i++)
			saveBytes[PADDING4_OFFSET + i] = PADDING4[i];

		for (int i = 0; i < 33; i++) {
			int offset = DEVICE_NAMES_OFFSET + (i * AllParser.DEV_NAME_LENGTH);
			for (int j = 0; j < AllParser.DEV_NAME_LENGTH; j++)
				saveBytes[offset + j] = StringUtils.rightPad(seq.getDeviceName(i), AllParser.DEV_NAME_LENGTH)
						.getBytes()[j];
		}

		Tracks tracks = new Tracks(seq);
		for (int i = 0; i < TRACKS_LENGTH; i++)
			saveBytes[i + TRACKS_OFFSET] = tracks.getBytes()[i];

		// TODO Rev eng loop start end

		BarList barList = new BarList(seq);
		for (int i = BAR_LIST_OFFSET; i < BAR_LIST_OFFSET + BAR_LIST_LENGTH; i++)
			saveBytes[i] = barList.getBytes()[i - BAR_LIST_OFFSET];

		byte[] eventArraysChunk = createEventSegmentsChunk(seq);
		System.out.println("save bytes size " + saveBytes.length);
		for (int i = EVENTS_OFFSET; i < EVENTS_OFFSET + eventArraysChunk.length; i++)
			saveBytes[i] = eventArraysChunk[i - EVENTS_OFFSET];

		// always terminate a Sequence
		for (int i = saveBytes.length - 8; i < saveBytes.length; i++)
			saveBytes[i] = (byte) 0xFF;

	}

	private static int getSegmentCount(MpcSequence seq) {
		int segmentCount = 0;
		for (MpcTrack t : seq.getMpcTracks()) {
			if (t.getTrackIndex() > 63) break;
			for (Event e : t.getEvents())
				if (e instanceof SystemExclusiveEvent) {
					int dataSegments = (int) (Math.ceil(((SystemExclusiveEvent) e).getBytes().length / 8.0));
					segmentCount += dataSegments + 2;
				} else if (e instanceof MixerEvent) {
					segmentCount += 4;
				} else {
					segmentCount++;
				}
		}
		return segmentCount;
	}

	private void setUnknown32BitInt(MpcSequence seq) {

		byte[] unknownNumberBytes1 = Util.get32BitIntBytes(10000000);
		byte[] unknownNumberBytes2 = Util.get32BitIntBytes((int) (seq.getLastTick() * 5208.333333333333));
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				saveBytes[UNKNOWN32_BIT_INT_OFFSET + j + (i * 4)] = unknownNumberBytes1[j];
			}
		}
		for (int i = 2; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				saveBytes[UNKNOWN32_BIT_INT_OFFSET + j + (i * 4)] = unknownNumberBytes2[j];
			}
		}

	}

	private void setBarCount(int i) {
		byte[] ba = Util.unsignedIntToBytePair(i);
		saveBytes[BAR_COUNT_BYTE1_OFFSET] = ba[0];
		saveBytes[BAR_COUNT_BYTE2_OFFSET] = ba[1];
	}

	private byte[] createEventSegmentsChunk(MpcSequence seq) {

		List<byte[]> ea = new ArrayList<byte[]>();

		for (int i = 0; i < seq.getLastTick(); i++) {
			for (MpcTrack t : seq.getMpcTracks()) {
				if (t.getTrackIndex() > 63) break;
				for (Event e : t.getEvents()) {
					if (e.getTick() == i) {
						e.setTrack(t.getTrackIndex());
						ea.add(new AllEvent(e).getBytes());
					}
				}
			}
		}

		ea.add(TERMINATOR);
		// int segmentCount = getSegmentCount(seq);
		// if ((segmentCount & 1) != 0) {
		// System.out.println("adding 2nd terminator");
		// ea.add(TERMINATOR);
		// }

		return Util.stitchByteArrays(ea);
	}

	private void setTempoDouble(double tempo) {
		byte[] ba = Util.unsignedIntToBytePair((short) (tempo * 10.0));
		saveBytes[TEMPO_BYTE1_OFFSET] = ba[0];
		saveBytes[TEMPO_BYTE2_OFFSET] = ba[1];
	}

	private void setLastTick(MpcSequence seq) {
		int lastTick = (int) seq.getLastTick();
		int remainder = lastTick % 65536;
		byte[] b = Util.unsignedIntToBytePair(remainder);
		int large = (int) Math.floor(lastTick / 65536.0);

		// int lastTickThatIsSomeHowDifferent = 768; TODO

		saveBytes[LAST_TICK_BYTE1_OFFSET] = b[0];
		saveBytes[LAST_TICK_BYTE2_OFFSET] = b[1];
		saveBytes[LAST_TICK_BYTE2_OFFSET + 1] = (byte) large;

		saveBytes[LAST_TICK_BYTE3_OFFSET] = b[0];
		saveBytes[LAST_TICK_BYTE4_OFFSET] = b[1];
		saveBytes[LAST_TICK_BYTE4_OFFSET + 1] = (byte) large;
	}

	byte[] getBytes() {
		return saveBytes;
	}

}
