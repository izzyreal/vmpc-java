package com.mpc.sequencer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiAdapter {

	/*
	 * Class to help the sampler convert its proprietary Events to MidiMessages.
	 */

	private ShortMessage message;

	private ShortMessage shortMessage;
	
	public MidiAdapter() {
		message = new ShortMessage();
		shortMessage = new ShortMessage();
	}
	
	public void process(Event event, int channel, int newVelo) {
		if (event instanceof NoteEvent) message = convert((NoteEvent) event, channel, newVelo);		
	}

	private ShortMessage convert(NoteEvent event, int channel, int newVelo) {
		int messageType = ShortMessage.NOTE_ON;
		if (event.getVelocity() == 0) messageType = ShortMessage.NOTE_OFF;
		try {
			shortMessage.setMessage(messageType, channel, event.getNote(),
					newVelo == -1 ? event.getVelocity() : newVelo);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return shortMessage;
	}

	public MidiMessage get() {
		return message;
	}
}
