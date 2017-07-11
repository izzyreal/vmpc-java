package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.NoteEvent;

public class EditVelocityControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 4:
			for (Event event : track.getEvents()) {
				if (event instanceof NoteEvent) {
					if (event.getTick() >= swGui.getTime0() && event.getTick() <= swGui.getTime1()) {
						if (swGui.getEditType() == 0) {
							((NoteEvent) event).setVelocity(((NoteEvent) event).getVelocity() + swGui.getValue());
						}

						if (swGui.getEditType() == 1) {
							((NoteEvent) event).setVelocity(((NoteEvent) event).getVelocity() - swGui.getValue());
						}

						if (swGui.getEditType() == 2) {
							((NoteEvent) event).setVelocity(
									(int) (((NoteEvent) event).getVelocity() * (swGui.getValue() / 100.0)));
						}

						if (swGui.getEditType() == 3) {
							((NoteEvent) event).setVelocity(swGui.getValue());
						}
					}
				}
			}
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		
		if (param.equals("edittype")) swGui.setEditType(swGui.getEditType() + notch);
		if (param.equals("value")) swGui.setValue(swGui.getValue() + notch);

		checkAllTimesAndNotes(notch);
	}

}
