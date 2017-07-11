package com.mpc.command;

import java.util.ArrayList;
import java.util.List;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.sequencer.StepEditorGui;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PitchBendEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.SystemExclusiveEvent;

public class CopySelectedNotes {

	StepEditorGui seqGui = Bootstrap.getGui().getStepEditorGui();
		
	public void execute() {
		seqGui.setSelectedEvents();
		List<Event> tempList = new ArrayList<Event>();
		for (Event event : seqGui.getSelectedEvents()) {
			if (event instanceof NoteEvent) {
				tempList.add((NoteEvent) event.clone());
			}
			if (event instanceof PitchBendEvent) {
				tempList.add((PitchBendEvent) event.clone());
			}
			if (event instanceof ChannelPressureEvent) {
				tempList.add((ChannelPressureEvent) event.clone());
			}
			if (event instanceof MixerEvent) {
				tempList.add((MixerEvent) event.clone());
			}
			if (event instanceof PolyPressureEvent) {
				tempList.add((PolyPressureEvent) event.clone());
			}
			if (event instanceof SystemExclusiveEvent) {
				tempList.add((SystemExclusiveEvent) event.clone());
			}
			if (event instanceof ControlChangeEvent) {
				tempList.add((ControlChangeEvent) event.clone());
			}
			if (event instanceof ProgramChangeEvent) {
				tempList.add((ProgramChangeEvent) event.clone());
			}
		}
		seqGui.setPlaceHolder(tempList);
		seqGui.clearSelection();
	}
	
	public void undo() {
	}
}