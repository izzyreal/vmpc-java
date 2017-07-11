package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class ChangeTsigControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 4:
			mpcSequence.setTimeSignature(swGui.getBar0(), swGui.getBar1(), swGui.getNewTimeSignature().getNumerator(),
					swGui.getNewTimeSignature().getDenominator());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("bar0")) swGui.setBar0(swGui.getBar0() + notch, mpcSequence.getLastBar());
		if (param.equals("bar1")) swGui.setBar1(swGui.getBar1() + notch, mpcSequence.getLastBar());
		if (param.equals("newtsig") && notch > 0) swGui.getNewTimeSignature().increase();
		if (param.equals("newtsig") && notch < 0) swGui.getNewTimeSignature().decrease();
	}

}
