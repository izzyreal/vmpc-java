package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.Event;

public class PasteEventControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 4:

			for (Event event : seGui.getPlaceHolder()) {
				Event eventClone = (Event) event.clone();
				eventClone.setTick(sequencer.getTickPosition());
				track.addEvent(eventClone);
			}

			mainFrame.openScreen("sequencer_step", "mainpanel");
			break;
		}
	}
}
