package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class CopyTrackControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 4:
			sequencer.copyTrack(swGui.getTr0(), swGui.getTr1(), sequencer.getActiveSequenceIndex(),
					sequencer.getActiveSequenceIndex());
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.contains("0")) swGui.setTr0(swGui.getTr0() + notch);
		if (param.contains("1")) swGui.setTr1(swGui.getTr1() + notch);
	}

}
