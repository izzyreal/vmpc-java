package com.mpc.controls.sequencer;

import com.mpc.controls.AbstractControls;
import com.mpc.controls.Controls;
import com.mpc.gui.sequencer.BarCopyGui;
import com.mpc.gui.sequencer.EditSequenceGui;
import com.mpc.gui.sequencer.SongGui;
import com.mpc.gui.sequencer.StepEditorGui;
import com.mpc.gui.sequencer.TrMoveGui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.SeqUtil;

public abstract class AbstractSequencerControls extends AbstractControls implements Controls {

	protected StepEditorGui seGui;
	protected BarCopyGui barCopyGui;
	protected SongGui songGui;
	protected SequencerWindowGui swGui;
	protected EditSequenceGui editSequenceGui;
	protected MpcSequence fromSeq;
	protected MpcSequence toSeq;
	protected MpcSequence mpcSequence;
	protected TrMoveGui trMoveGui;

	protected void init() {
		super.init();
		seGui = gui.getStepEditorGui();
		songGui = gui.getSongGui();
		swGui = gui.getSequencerWindowGui();
		barCopyGui = gui.getBarCopyGui();
		editSequenceGui = gui.getEditSequenceGui();
		trMoveGui = gui.getTrMoveGui();
		fromSeq = sequencer.getSequence(barCopyGui.getFromSq());
		toSeq = sequencer.getSequence(barCopyGui.getToSq());
		mpcSequence = sequencer.getActiveSequence();
	}

	protected void checkAllTimesAndNotes(int notch) {
		init();
		if (param.equals("time0")) {
			swGui.setTime0(SeqUtil.getTickFromBar((SeqUtil.getBarFromTick(mpcSequence, swGui.getTime0())) + notch, mpcSequence,
					swGui.getTime0()));
		}

		if (param.equals("time1")) {
			swGui.setTime0(SeqUtil.setBeat((SeqUtil.getBeat(mpcSequence, swGui.getTime0())) + notch, mpcSequence,
					swGui.getTime0()));
		}

		if (param.equals("time2")) {
			swGui.setTime0(SeqUtil.setClockNumber((SeqUtil.getClockNumber(mpcSequence, swGui.getTime0())) + notch,
					mpcSequence, swGui.getTime0()));
		}

		if (param.equals("time3")) {
			swGui.setTime1(SeqUtil.getTickFromBar((SeqUtil.getBarFromTick(mpcSequence, swGui.getTime1())) + notch, mpcSequence,
					swGui.getTime1()));
		}

		if (param.equals("time4")) {
			swGui.setTime1(SeqUtil.setBeat((SeqUtil.getBeat(mpcSequence, swGui.getTime1())) + notch, mpcSequence,
					swGui.getTime1()));
		}

		if (param.equals("time5")) {
			swGui.setTime1(SeqUtil.setClockNumber((SeqUtil.getClockNumber(mpcSequence, swGui.getTime1())) + notch,
					mpcSequence, swGui.getTime1()));
		}

		if (param.equals("notes0")) {
			if (track.getBusNumber() != 0) {
				swGui.setDrumNote(swGui.getDrumNote() + notch);
			} else {
				swGui.setMidiNote0(swGui.getMidiNote0() + notch);
			}
		}

		if (param.equals("notes1")) swGui.setMidiNote1(swGui.getMidiNote1() + notch);

	}
}
