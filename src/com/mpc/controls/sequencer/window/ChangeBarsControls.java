package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class ChangeBarsControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mpcSequence.insertBars(swGui.getChangeBarsNumberOfBars(), swGui.getChangeBarsAfterBar());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		case 4:
			mpcSequence.deleteBars(swGui.getChangeBarsFirstBar(), swGui.getChangeBarsLastBar());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;

		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("afterbar")) {
			swGui.setChangeBarsAfterBar(swGui.getChangeBarsAfterBar() + notch, mpcSequence.getLastBar());
		}
		if (param.equals("numberofbars")) {
			swGui.setChangeBarsNumberOfBars(swGui.getChangeBarsNumberOfBars() + notch, mpcSequence.getLastBar());
		}

		if (param.equals("firstbar")) {
			swGui.setChangeBarsFirstBar(swGui.getChangeBarsFirstBar() + notch, mpcSequence.getLastBar());
		}
		if (param.equals("lastbar")) {
			swGui.setChangeBarsLastBar(swGui.getChangeBarsLastBar() + notch, mpcSequence.getLastBar());
		}

	}

}
