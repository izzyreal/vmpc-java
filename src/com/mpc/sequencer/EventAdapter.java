package com.mpc.sequencer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.sequencer.window.MultiRecordingSetupLine;
import com.mpc.gui.sequencer.window.SequencerWindowGui;

/*
 *  Class to convert MidiMessages to Events
 */

public class EventAdapter {

	private Event event;
	private SequencerWindowGui gui;
	private MultiRecordingSetupLine[] mrs;
	private MidiClockEvent midiClockEvent;
	private NoteEvent noteEvent;
	
	public void process(MidiMessage msg, SequencerWindowGui gui) {
		this.gui = gui;
		mrs = gui.getMrsLines();		
		if (msg instanceof ShortMessage) event = convert((ShortMessage)msg);
		
	}

	public EventAdapter() {
		midiClockEvent = new MidiClockEvent(0);
		noteEvent = new NoteEvent(35);
	}

	private Event convert(ShortMessage msg) {
		if (msg.getStatus() == ShortMessage.TIMING_CLOCK || msg.getStatus() == ShortMessage.START || msg.getStatus() == ShortMessage.STOP) {
			midiClockEvent.setStatus(msg.getStatus());
			return midiClockEvent;
		}
		
		if (gui.getReceiveCh() != -1 && !(msg.getChannel() == gui.getReceiveCh())) return null;
		
		if (msg.getStatus() == ShortMessage.NOTE_ON || msg.getStatus() == ShortMessage.NOTE_OFF) {
			noteEvent.setNote(msg.getData1());
			if (msg.getStatus() == ShortMessage.NOTE_ON) noteEvent.setVelocity(msg.getData2());
			if (msg.getStatus() == ShortMessage.NOTE_OFF) noteEvent.setVelocityZero();			
			int track = Bootstrap.getGui().getMpc().getSequencer().getActiveTrackIndex();
			if (Bootstrap.getGui().getMpc().getSequencer().isRecordingModeMulti()) track = mrs[msg.getChannel()].getTrack(); 
			noteEvent.setTrack(track);
			noteEvent.setDuration(0);
			noteEvent.setVariationValue(64);
			return noteEvent;
		}		
		return null;
	}
	
	public Event get() {
		return event;
	}
	
	
	
}
