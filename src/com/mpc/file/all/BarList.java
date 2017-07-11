package com.mpc.file.all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mpc.sequencer.MpcSequence;

class BarList {

	// Loading

	List<Bar> bars = new ArrayList<Bar>();

	// Saving

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	BarList(byte[] loadBytes) {

		Bar previousBar = null;
		for (int i = 0; i < 999; i++) {
			Bar bar = new Bar(Arrays.copyOfRange(loadBytes, i * 4, (i * 4) + 4), previousBar);
			if (bar.lastTick == 0) break;
			bars.add(bar);
			previousBar = bar;
		}
	}

	List<Bar> getBars() {
		return bars;
	}

	/*
	 * Constructor and methods for saving
	 */

	public BarList(MpcSequence seq) {
		saveBytes = new byte[3996];
		long[] barLengths = seq.getBarLengths();
		int ticksPerBeat = 0;
		int lastTick = 0;
		for (int i = 0; i < seq.getLastBar() + 1; i++) {
			lastTick += barLengths[i];
			ticksPerBeat = (int) (barLengths[i] / seq.getNumerator(i));
			Bar bar = new Bar(ticksPerBeat, lastTick);

			for (int j = 0; j < 4; j++)
				saveBytes[(i * 4) + j] = bar.getBytes()[j];

			// if (i==seq.getLastBar()-1) {
			//
			// bar = new Bar(ticksPerBeat, 0);
			// for (int j = 0; j < 4; j++)
			// saveBytes[(i * 4) + j + 4] = bar.getBytes()[j];
			//
			// }
		}
		Bar bar = new Bar(ticksPerBeat, 0);
		for (int i = 0; i < 4; i++)
			saveBytes[(seq.getLastBar() + 1) * 4 + i] = bar.getBytes()[i];

	}

	byte[] getBytes() {
		return saveBytes;
	}

}
