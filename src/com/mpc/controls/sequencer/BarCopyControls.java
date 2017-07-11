package com.mpc.controls.sequencer;

import com.mpc.sequencer.Event;
import com.mpc.sequencer.MpcTrack;

public class BarCopyControls extends AbstractSequencerControls {

	@Override
	public void function(int j) {
		init();
		switch (j) {
		case 0:
			gui.getEditSequenceGui().setFromSq(barCopyGui.getFromSq());
			gui.getEditSequenceGui().setToSq(barCopyGui.getToSq());
			mainFrame.openScreen("edit", "mainpanel");
			break;
		case 2:
			gui.getTrMoveGui().setSq(barCopyGui.getFromSq());
			mainFrame.openScreen("trmove", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("user", "mainpanel");
			break;
		case 4:
			break;
		case 5:
			if (!toSeq.isUsed()) toSeq.init(fromSeq.getLastBar());

			int firstBar = barCopyGui.getFirstBar();
			int lastBar = barCopyGui.getLastBar();
			int copies = barCopyGui.getCopies();
			int numberOfBars = (lastBar - firstBar + 1) * copies;
			int afterBar = barCopyGui.getAfterBar();

			toSeq.insertBars(numberOfBars, afterBar);

			for (int i = 0; i < numberOfBars; i++) {
				toSeq.setTimeSignature(i + afterBar + 1, i + afterBar + 1, fromSeq.getNumerator(i + firstBar),
						fromSeq.getNumerator(i + firstBar));
			}

			long firstTick = 0;
			long lastTick = 0;
			long firstTickOfToSeq = 0;
			long offset = 0;
			long segmentLengthTicks = 0;

			for (int i = 0; i < 999; i++) {
				if (i == firstBar) break;
				firstTick += fromSeq.getBarLengths()[i];
			}

			for (int i = 0; i < 999; i++) {
				lastTick += fromSeq.getBarLengths()[i];
				if (i == lastBar) break;
			}

			for (int i = 0; i < 999; i++) {
				firstTickOfToSeq += toSeq.getBarLengths()[i];
			}

			segmentLengthTicks = lastTick - firstTick;
			offset = firstTickOfToSeq - firstTick;

			for (int i = 0; i < 64; i++) {
				for (Event event : ((com.mpc.sequencer.MpcTrack) fromSeq.getTrack(i)).getEvents()) {
					if (event.getTick() >= firstTick && event.getTick() < lastTick) {
						if (!((MpcTrack) toSeq.getTrack(i)).isUsed()) {
							((MpcTrack) toSeq.getTrack(i)).setUsed(true);
						}
						for (int k = 0; k < copies; k++) {
							Event clone = (Event) event.clone();
							clone.setTick(clone.getTick() + offset + (k * segmentLengthTicks));
							((MpcTrack) toSeq.getTrack(i)).addEvent(clone);
						}
					}
				}
			}
			break;
		}
	}

	@Override
	public void turnWheel(int increment) {
		init();
		int notch = getNotch(increment);

		if (param.equals("fromsq")) {
			barCopyGui.setFromSq(barCopyGui.getFromSq() + notch);
			fromSeq = sequencer.getSequence(barCopyGui.getFromSq());
			if (barCopyGui.getLastBar() > fromSeq.getLastBar())
				barCopyGui.setLastBar(fromSeq.getLastBar(), fromSeq.getLastBar());
		}

		if (param.equals("tosq")) {
			barCopyGui.setToSq(barCopyGui.getToSq() + notch);
			toSeq = sequencer.getSequence(barCopyGui.getToSq());
			if (barCopyGui.getAfterBar() > toSeq.getLastBar())
				barCopyGui.setAfterBar(toSeq.getLastBar(), toSeq.getLastBar());
		}

		if (param.equals("afterbar")) {
			barCopyGui.setAfterBar(barCopyGui.getAfterBar() + notch, toSeq.getLastBar());
		}

		if (param.equals("firstbar")) {
			barCopyGui.setFirstBar(barCopyGui.getFirstBar() + notch, fromSeq.getLastBar());
		}

		if (param.equals("lastbar")) {
			barCopyGui.setLastBar(barCopyGui.getLastBar() + notch, fromSeq.getLastBar());
		}

		if (param.equals("copies")) {
			barCopyGui.setCopies(barCopyGui.getCopies() + notch);
		}
	}
}