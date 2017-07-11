package com.mpc.tootextensions;

public interface MpcProgram {

	String getName();
	
	int getPadNumberFromNote(int note);

	MpcNoteParameters getNoteParameters(int note);

	MpcMixParameters getPadMixer(int padNumber);

	MpcSlider getSlider();

	int getNoteFromPad(int i);
	
}
