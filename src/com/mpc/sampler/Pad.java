package com.mpc.sampler;

import java.util.Observable;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.tootextensions.MpcPad;

public class Pad extends Observable implements MpcPad {

	private int note;

	private MixerChannel mixerChannel;
	private final int number;
		
	public Pad(int number) {

		this.number = number;
		note = Bootstrap.getUserDefaults().getPadNotes()[number];
		mixerChannel = new MixerChannel();		
	}

	public void setNote(int i) {

		if (i < 34 || i > 98)
			return;

		if (SamplerGui.isPadAssignMaster()) {
			Sampler.masterPadAssign[number] = i;
		} else {
			note = i;
		}
		setChanged();
		notifyObservers("padnotenumber");
		setChanged();
		notifyObservers("note");
		setChanged();
		notifyObservers("samplenumber");
	}

	public int getNote() {

		if (SamplerGui.isPadAssignMaster())
			return Sampler.masterPadAssign[number];

		return note;
	}

	public MixerChannel getMixerChannel() {
		return mixerChannel;
	}
	
	public int getNumber() {
		return number;
	}
}