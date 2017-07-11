package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class DeleteAllTracksControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("deletetrack", "dialogpanel");
			break;
		case 4:
			mpcSequence.purgeAllTracks();
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}
}
