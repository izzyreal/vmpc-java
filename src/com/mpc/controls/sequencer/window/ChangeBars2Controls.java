package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class ChangeBars2Controls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 2:
			mainFrame.openScreen("sequencer", "mainpanel");
			mainFrame.openScreen("changebars", "windowpanel");
			break;
		case 4:
			
			if (swGui.getNewBars() < mpcSequence.getLastBar()) {
				mpcSequence.deleteBars(swGui.getNewBars() + 1, mpcSequence.getLastBar());
			}

			if (swGui.getNewBars() > mpcSequence.getLastBar()) {
				mpcSequence.insertBars(swGui.getNewBars() - mpcSequence.getLastBar(), mpcSequence.getLastBar());
			}
			mainFrame.openScreen("sequencer", "mainpanel");
			sequencer.setBar(0);		
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("newbars")) swGui.setNewBars(swGui.getNewBars() + notch);
	}

}
