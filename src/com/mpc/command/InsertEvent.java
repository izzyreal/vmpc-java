package com.mpc.command;

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
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.SystemExclusiveEvent;
import com.mpc.sequencer.MpcTrack;

public class InsertEvent {

	private StepEditorGui seqGui = Bootstrap.getGui().getStepEditorGui();
	private MpcTrack track;
	private Sequencer sequencer;

	public InsertEvent(MpcTrack track, Sequencer sequencer) {
		this.track = track;
		this.sequencer = sequencer;
	}
	
	public void execute() {
		Event event = null;
		if (seqGui.getInsertEventType() == 0) {
			event = new NoteEvent();
			((NoteEvent) event).setDuration(24);
			((NoteEvent) event).setNote(60);
			((NoteEvent) event).setVelocity(127);
			((NoteEvent) event).setVariationTypeNumber(0);
			((NoteEvent) event).setVariationValue(64);
		}
		if (seqGui.getInsertEventType() == 1) {
			event = new PitchBendEvent();
			((PitchBendEvent) event).setAmount(0);
		}
		if (seqGui.getInsertEventType() == 2) {
			event = new ControlChangeEvent();
			((ControlChangeEvent) event).setController(0);
			((ControlChangeEvent) event).setAmount(0);
		}
		if (seqGui.getInsertEventType() == 3) {
			event = new ProgramChangeEvent();
			((ProgramChangeEvent) event).setProgram(1);
		}
		if (seqGui.getInsertEventType() == 4) {
			event = new ChannelPressureEvent();
			((ChannelPressureEvent) event).setAmount(0);
		}
		if (seqGui.getInsertEventType() == 5) {
			event = new PolyPressureEvent();
			((PolyPressureEvent) event).setNote(60);
			((PolyPressureEvent) event).setAmount(0);
		}
		if (seqGui.getInsertEventType() == 6) {
			event = new SystemExclusiveEvent();
			((SystemExclusiveEvent) event).setByteA(0xF0);
			((SystemExclusiveEvent) event).setByteB(0xF7);
		}
		if (seqGui.getInsertEventType() == 7) {
			event = new MixerEvent();
			((MixerEvent) event).setPadNumber(0);
			((MixerEvent) event).setParameter(0);
			((MixerEvent) event).setValue(0);
		}
		event.setTick(sequencer.getTickPosition());
		track.addEvent(event);
	}
}
