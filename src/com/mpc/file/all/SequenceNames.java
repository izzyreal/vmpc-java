package com.mpc.file.all;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.SystemExclusiveEvent;
import com.mpc.tootextensions.TrackContainer;

class SequenceNames {

	final static int LENGTH = 0x06F6;
	final static int ENTRY_LENGTH = 0x12;
	final static int LAST_EVENT_INDEX_OFFSET = 0x10;
	/*
	 * Attributes for loading
	 */

	String[] names = new String[100];
	/*
	 * Attributes for saving
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	SequenceNames(byte[] b) {

		try {
			for (int i = 0; i < 100; i++) {
				int offset = i * ENTRY_LENGTH;
				names[i] = new String(Arrays.copyOfRange(b, offset, offset + AllParser.NAME_LENGTH), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String[] getNames() {
		return names;
	}

	/*
	 * Constructor and methods for saving
	 */

	SequenceNames(Mpc mpc) {

		saveBytes = new byte[LENGTH];
		List<TrackContainer> seqs = mpc.getSequencer().getSequences();

		for (int i = 0; i < 99; i++) {

			String name = ((MpcSequence) seqs.get(i)).getName();

			int offset = i * ENTRY_LENGTH;

			for (int j = 0; j < AllParser.NAME_LENGTH; j++)
				saveBytes[offset + j] = StringUtils.rightPad(name, 16).getBytes()[j];

			if (!name.contains("(Unused)")) {
				int eventSegmentCount = getSegmentCount((MpcSequence) seqs.get(i));
				if ((eventSegmentCount & 1) != 0) eventSegmentCount--;

				int lastEventIndex = 641 + (eventSegmentCount / 2);

				if (lastEventIndex < 641) lastEventIndex = 641;
				byte[] eventCountBytes = Util.unsignedIntToBytePair(lastEventIndex);
				saveBytes[offset + LAST_EVENT_INDEX_OFFSET] = eventCountBytes[0];
				saveBytes[offset + LAST_EVENT_INDEX_OFFSET + 1] = eventCountBytes[1];
			}

		}
	}

	byte[] getBytes() {
		return saveBytes;
	}

	static int getSegmentCount(MpcSequence seq) {
		int segmentCount = 0;
		for (MpcTrack t : seq.getMpcTracks()) {
			if (t.getTrackIndex() > 63) break;
			for (Event e : t.getEvents())
				if (e instanceof SystemExclusiveEvent) {
					int dataSegments = (int) (Math.ceil(((SystemExclusiveEvent) e).getBytes().length / 8.0));
					segmentCount += dataSegments + 1;
				} else if (e instanceof MixerEvent) {
					segmentCount += 2;
				} else {
					segmentCount++;
				}
		}
		return segmentCount;
	}
}
