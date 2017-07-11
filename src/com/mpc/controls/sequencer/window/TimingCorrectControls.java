package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.Sequencer;

public class TimingCorrectControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 4:

			if (swGui.getNoteValue() == 0) return;

			track.correctTimeRange(swGui.getTime0(), swGui.getTime1(),
					Sequencer.tickValues[swGui.getNoteValue()]);

			int[] noteRange = new int[2];
			if (track.getBusNumber() != 0) {
				if (swGui.getDrumNote() != 34) {
					noteRange[0] = swGui.getDrumNote();
					noteRange[1] = swGui.getDrumNote();
				} else {
					noteRange[0] = 0;
					noteRange[1] = 127;
				}
			} else {
				noteRange[0] = swGui.getMidiNote0();
				noteRange[1] = swGui.getMidiNote1();
			}
			track.swing(swGui.getNoteValue(), swGui.getSwing(), noteRange,
					track.getEventRange(swGui.getTime0(), swGui.getTime1()));
			track.shiftTiming(swGui.isShiftTimingLater(), swGui.getAmount(), mpcSequence.getLastTick(),
					track.getEventRange(swGui.getTime0(), swGui.getTime1()));

			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("notevalue")) swGui.setNoteValue(swGui.getNoteValue() + notch);
		if (param.equals("swing")) swGui.setSwing(swGui.getSwing() + notch);
		if (param.equals("shifttiming")) swGui.setShiftTimingLater(notch > 0);
		if (param.equals("amount")) swGui.setAmount(swGui.getAmount() + notch);
		checkAllTimesAndNotes(notch);
	}

}
