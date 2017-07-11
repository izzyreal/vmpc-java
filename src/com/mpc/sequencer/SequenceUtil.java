package com.mpc.sequencer;

import java.util.ArrayList;
import java.util.List;

public class SequenceUtil {

	public static void setTimeSignature(MpcSequence mpcSequence, int firstBar, int tsLastBar, int num, int den) {
		int newDenTicks = (int) (96 * (4.0 / den));
		long[] newBarLengths = new long[999];
		int lastBar = mpcSequence.getLastBar();
		for (int i = 0; i < 999; i++) {
			newBarLengths[i] = mpcSequence.getBarLengths()[i];
		}

		for (int i = firstBar; i < (tsLastBar + 1); i++) {
			newBarLengths[i] = newDenTicks * num;
			mpcSequence.getNumerators()[i] = num;
			mpcSequence.getDenominators()[i] = den;
		}

		if (tsLastBar == mpcSequence.getLastBar()) {
			mpcSequence.getNumerators()[mpcSequence.getLastBar() + 1] = num;
			mpcSequence.getDenominators()[mpcSequence.getLastBar() + 1] = den;
		} else {
			mpcSequence.getNumerators()[lastBar + 1] = mpcSequence.getNumerators()[lastBar];
			mpcSequence.getDenominators()[lastBar + 1] = mpcSequence.getDenominators()[lastBar];
		}

		long[] oldBarStartPos = new long[999];
		oldBarStartPos[0] = 0;

		for (int i = firstBar; i < 999; i++) {
			if (i == 0) {
				oldBarStartPos[i] = 0;
				continue;
			}
			oldBarStartPos[i] = oldBarStartPos[i - 1] + mpcSequence.getBarLengths()[i - 1];
		}

		long[] newBarStartPos = new long[999];

		for (int i = firstBar; i < 999; i++) {
			if (i == 0) {
				newBarStartPos[i] = 0;
				continue;
			}
			newBarStartPos[i] = newBarStartPos[i - 1] + newBarLengths[i - 1];
		}

		List<Event> newEvents;

		for (MpcTrack t : mpcSequence.getMpcTracks()) {
	
			if (t.getTrackIndex() > 63) continue;

			newEvents = new ArrayList<Event>();
			for (Event e : ((com.mpc.sequencer.MpcTrack) t).getEvents()) {

				for (int i = 0; i < firstBar; i++) {
					if (e.getTick() >= oldBarStartPos[i] && e.getTick() < (oldBarStartPos[i] + mpcSequence.getBarLengths()[i])) {
						newEvents.add(e);
					}
				}

				for (int i = firstBar; i < 999; i++) {
					if (e.getTick() >= oldBarStartPos[i] && e.getTick() < (oldBarStartPos[i] + newBarLengths[i])) {
						e.setTick(e.getTick() - (oldBarStartPos[i] - newBarStartPos[i]));
						newEvents.add(e);
					}
				}
			}
			((com.mpc.sequencer.MpcTrack) t).setEvents(newEvents);
		}

		for (int i = 0; i < 999; i++) {
			mpcSequence.getBarLengths()[i] = newBarLengths[i];
		}

		mpcSequence.initMetaTracks();
	}

}
