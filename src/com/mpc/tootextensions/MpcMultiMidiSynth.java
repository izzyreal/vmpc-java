package com.mpc.tootextensions;

import static uk.org.toot.midi.message.ChannelMsg.CONTROL_CHANGE;
import static uk.org.toot.midi.message.ChannelMsg.PITCH_BEND;
import static uk.org.toot.midi.message.ChannelMsg.getCommand;
import static uk.org.toot.midi.message.ChannelMsg.isChannel;
import static uk.org.toot.midi.message.ShortMsg.getData1;
import static uk.org.toot.midi.message.ShortMsg.getData1and2;
import static uk.org.toot.midi.message.ShortMsg.getData2;
import static uk.org.toot.midi.misc.Controller.ALL_CONTROLLERS_OFF;
import static uk.org.toot.midi.misc.Controller.ALL_NOTES_OFF;
import static uk.org.toot.midi.misc.Controller.ALL_SOUND_OFF;

import javax.sound.midi.MidiMessage;

import uk.org.toot.midi.message.ChannelMsg;
import uk.org.toot.midi.message.NoteMsg;
import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.synths.multi.MultiMidiSynth;
import uk.org.toot.synth.synths.multi.MultiSynthControls;

public class MpcMultiMidiSynth extends MultiMidiSynth {

	public MpcMultiMidiSynth(MultiSynthControls controls) {
		super(controls);
	}
	
    protected void mpcTransportChannel(int track, MidiMessage msg, int chan, int varType, int varValue, int l) {
        mpcTransportChannel(track, msg, mapChannel(chan), varType, varValue, l);        
    }

    public void mpcTransport(int track, MidiMessage msg, long timestamp, int varType, int varValue, int l) {
		if ( isChannel(msg) ) {
		    mpcTransportChannel(track, msg, ChannelMsg.getChannel(msg), varType, varValue, l);
		}
	}

	protected void mpcTransportChannel(int track, MidiMessage msg, SynthChannel synthChannel, int varType, int varValue, int l) {
        if ( synthChannel == null ) return;
        if ( NoteMsg.isNote(msg) ) {
            int pitch = NoteMsg.getPitch(msg);
            int velocity = NoteMsg.getVelocity(msg);
            boolean on = NoteMsg.isOn(msg);
            if ( on && velocity != 0) {
                ((MpcSoundPlayerChannel) synthChannel).mpcNoteOn(track, pitch, velocity, varType, varValue, l);
            } else {
                ((MpcSoundPlayerChannel) synthChannel).mpcNoteOff(pitch, l);
            }
        } else {
            int cmd = getCommand(msg);
            switch ( cmd ) {
            case PITCH_BEND:
                synthChannel.setPitchBend(getData1and2(msg));
                break;
            case CONTROL_CHANGE:
                int controller = getData1(msg);
                if ( controller == ALL_CONTROLLERS_OFF ) {
                    synthChannel.resetAllControllers();
                } else if ( controller == ALL_NOTES_OFF ) {
                    synthChannel.allNotesOff();
                } else if ( controller == ALL_SOUND_OFF ) {
                    synthChannel.allSoundOff();
                } else {
                    synthChannel.controlChange(controller, getData2(msg));
                }
                break;
            case ChannelMsg.CHANNEL_PRESSURE:
                synthChannel.setChannelPressure(getData1(msg));
                break;
            }
        }
	    
	}


	@Override
	protected SynthChannel mapChannel(int chan) {
		return super.getChannel(chan);		
	}
	
}
