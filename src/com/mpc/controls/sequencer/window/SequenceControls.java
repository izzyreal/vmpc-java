package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class SequenceControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch(i) {
		case 1:
			mainFrame.openScreen("deletesequence", "dialogpanel");
			break;		
		case 4:
			mainFrame.openScreen("copysequence", "dialogpanel");
			break;	
		}
	}

	public void turnWheel(int i) {
		init();
		if (param.contains("default")) {
			nameGui.setName(sequencer.getDefaultSequenceName());
		} else {
			nameGui.setName(mpcSequence.getName());
		}
		nameGui.setParameterName(param);
		mainFrame.openScreen("name", "dialogpanel");

	}
		
}
