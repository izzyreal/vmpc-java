package com.mpc.sequencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mpc.Util;
import com.mpc.controls.AbstractControls;
import com.mpc.controls.KbMouseController;
import com.mpc.gui.Bootstrap;

public class NoteRepeatProcessor {

	final FrameSequencer fs;
	private long lastProcessed = -1l;

	NoteRepeatProcessor(FrameSequencer fs) {
		this.fs = fs;
	}

	void process(Set<Long> ticks) {
		if (!KbMouseController.tapIsPressed) return;
		final int tcValue = Sequencer.tickValues[fs.sequencer.getTcIndex()];
		final int swingPercentage = Bootstrap.getGui().getSequencerWindowGui().getSwing();
		List<Long> result = new ArrayList<Long>();
		Iterator<Long> i = ticks.iterator();
		while (i.hasNext()) {
			long l = i.next();
			result.add(l);
		}
		Collections.sort(result);

		for (Long l : result) {

			if (lastProcessed == l.longValue()) {
				continue;
			} else {
				lastProcessed = l.longValue();
			}

//			 System.out.println("nr " + l);

			if (tcValue == 24 || tcValue == 48) {
				int swingOffset = (int) ((swingPercentage - 50) * (4.0 / 100.0) * (tcValue / 2.0));
				if (l.longValue() % (tcValue * 2) == swingOffset + tcValue) {
					repeatPad(l.longValue());
				} else if (l.longValue() % (tcValue * 2) == 0) {
					repeatPad(l.longValue());
				}
			} else {
				if (l.longValue() % tcValue == 0) {
					repeatPad(l.longValue());
				}
			}
		}
	}

	void repeatPad(long tick) {
		AbstractControls controls = Bootstrap.getGui().getControls(Util.getCsn());
//		int offset = fs.getEventFrameOffset(tick);
		System.out.println("\nRepeating pad with tick " + tick);
		if (controls != null) {
			for (Integer i : KbMouseController.pressedPads)
				controls.pad(i, KbMouseController.pressedPadVelos[i], true, tick);
		}
	}

}
