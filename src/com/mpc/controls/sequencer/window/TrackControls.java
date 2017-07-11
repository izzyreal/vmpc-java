package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class TrackControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mainFrame.openScreen("deletetrack", "dialogpanel");
			break;
		case 4:
			mainFrame.openScreen("copytrack", "dialogpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		if (param.contains("default")) {
			nameGui.setName(sequencer.getDefaultTrackName(sequencer.getActiveTrackIndex()));
		} else {
			nameGui.setName(track.getName());
		}
		nameGui.setParameterName(param);
		mainFrame.openScreen("name", "dialogpanel");
	}

}
