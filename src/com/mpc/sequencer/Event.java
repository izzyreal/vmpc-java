package com.mpc.sequencer;

import java.util.Observable;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

abstract public class Event extends Observable implements Cloneable {
	protected long tick;

	protected int track;

	private MidiMessage shortMessage;
	
	public Event() {
		shortMessage = new ShortMessage();
	}
	
	public void setTick(long relativeTick) {
		tick = relativeTick;
	}
	
	public long getTick() {
		return tick;
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}

	public int getTrack() {
		return track;
	}
	
	public void setTrack(int i) {
		track = i;
	}

	public MidiMessage getShortMessage() {
		return shortMessage;
	}
}
