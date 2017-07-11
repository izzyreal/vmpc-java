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

public class CopySelectedNote {

	private String focus;
	
	private StepEditorGui seqGui = Bootstrap.getGui().getStepEditorGui();

	public CopySelectedNote(String focus) {
		this.focus = focus;
	}
	
	public void execute() {
		int eventNumber = Integer.parseInt(focus.substring(1, 2));
		List<Event> tempList = new ArrayList<Event>();
		if (seqGui.getVisibleEvents()[eventNumber] instanceof NoteEvent) {
			tempList.add((NoteEvent) seqGui.getVisibleEvents()[eventNumber]
					.clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof PitchBendEvent) {
			tempList.add((PitchBendEvent) seqGui.getVisibleEvents()[eventNumber]
					.clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof ChannelPressureEvent) {
			tempList.add((ChannelPressureEvent) seqGui
					.getVisibleEvents()[eventNumber].clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof MixerEvent) {
			tempList.add((MixerEvent) seqGui.getVisibleEvents()[eventNumber]
					.clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof PolyPressureEvent) {
			tempList.add((PolyPressureEvent) seqGui.getVisibleEvents()[eventNumber]
					.clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof SystemExclusiveEvent) {
			tempList.add((SystemExclusiveEvent) seqGui
					.getVisibleEvents()[eventNumber].clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof ControlChangeEvent) {
			tempList.add((ControlChangeEvent) seqGui
					.getVisibleEvents()[eventNumber].clone());
		}
		if (seqGui.getVisibleEvents()[eventNumber] instanceof ProgramChangeEvent) {
			tempList.add((ProgramChangeEvent) seqGui
					.getVisibleEvents()[eventNumber].clone());
		}
		seqGui.setPlaceHolder(tempList);

	}
}
