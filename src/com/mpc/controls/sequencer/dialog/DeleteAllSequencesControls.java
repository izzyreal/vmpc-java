package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class DeleteAllSequencesControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("deletesequence", "dialogpanel");
			break;
		case 4:
			sequencer.purgeAllSequences();
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}

	}
}
