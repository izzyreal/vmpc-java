package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;

public class DeleteSequenceControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("sq")) sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex() + notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			mainFrame.openScreen("deleteallsequences", "dialogpanel");
			break;
		case 3:
			mainFrame.openScreen("sequence", "windowpanel");
			break;
		case 4:
			MpcSequence s = new MpcSequence(gui.getMpc(), Sequencer.defaultTrackNames);
			sequencer.setSequence(sequencer.getActiveSequenceIndex(), s);
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

}
