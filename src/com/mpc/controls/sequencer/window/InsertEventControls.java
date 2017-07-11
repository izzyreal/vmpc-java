package com.mpc.controls.sequencer.window;

import com.mpc.command.InsertEvent;
import com.mpc.controls.sequencer.AbstractSequencerControls;

public class InsertEventControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 4:
			InsertEvent command = new InsertEvent(track, sequencer);
			command.execute();
			mainFrame.openScreen("sequencer_step", "mainpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("eventtype")) seGui.setInsertEventType(seGui.getInsertEventType() + notch);
	}
}
