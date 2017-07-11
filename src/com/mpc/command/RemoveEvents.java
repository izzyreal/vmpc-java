package com.mpc.command;

import java.util.ArrayList;
import java.util.List;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.sequencer.StepEditorGui;
import com.mpc.sequencer.EmptyEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MpcTrack;

public class RemoveEvents {

	private StepEditorGui seqGui = Bootstrap.getGui().getStepEditorGui();
	private MpcTrack track;

	public RemoveEvents(MpcTrack track) {
		this.track = track;
	}

	public void execute() {
		int firstEventIndex = seqGui.getSelectionStartIndex();
		int lastEventIndex = seqGui.getSelectionEndIndex();
		if (firstEventIndex > lastEventIndex) {
			firstEventIndex = seqGui.getSelectionEndIndex();
			lastEventIndex = seqGui.getSelectionStartIndex();
		}
		int eventCounter = 0;
		List<Event> tempList = new ArrayList<Event>();

		for (Event event : seqGui.getEventsAtCurrentTick()) {
			tempList.add(event);
		}

		for (Event event : tempList) {
			if (eventCounter >= firstEventIndex
					&& eventCounter <= lastEventIndex) {
				if (!(event instanceof EmptyEvent)) {
					track.getEvents().remove(event);
				}
			}
			eventCounter++;
		}
		seqGui.clearSelection();
		seqGui.setyOffset(0);
	}
}
