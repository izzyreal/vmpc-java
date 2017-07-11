package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class CountMetronomeControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		case 4:
			mainFrame.openScreen("metronomesound", "dialogpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("countin")) swGui.setCountIn(swGui.getCountInMode() + notch);
		if (param.equals("inplay")) swGui.setInPlay(notch > 0);
		if (param.equals("rate")) {
			swGui.setRate(swGui.getRate() + notch);
			sequencer.getActiveSequence().initMetaTracks();
		}
		if (param.equals("inrec")) swGui.setInRec(notch > 0);
		if (param.equals("waitforkey")) swGui.setWaitForKey(notch > 0);
	}

}
