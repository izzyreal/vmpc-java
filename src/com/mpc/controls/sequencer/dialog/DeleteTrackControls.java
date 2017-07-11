package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.MpcSequence;

public class DeleteTrackControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("tr")) swGui.setTrackNumber(swGui.getTrackNumber() + notch);

	}

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			mainFrame.openScreen("deletealltracks", "dialogpanel");
			break;
		case 3:
			mainFrame.openScreen("sequence", "windowpanel");
			break;
		case 4:
			MpcSequence s = sequencer.getActiveSequence();
			s.purgeTrack(swGui.getTrackNumber());
			mainFrame.openScreen("sequencer", "mainpanel");

		}
	}

}
