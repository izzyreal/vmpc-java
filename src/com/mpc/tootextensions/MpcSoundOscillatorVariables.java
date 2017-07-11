package com.mpc.tootextensions;


public interface MpcSoundOscillatorVariables {

	float[] getSampleData();

	int getSndLevel();
	
	int getTune();

	int getStart();

	int getEnd();

	boolean isLoopEnabled();

	int getLoopTo();

	boolean isMono();

	float getRateConversion();

	float[] getSampleDataRight();

	float[] getSampleDataLeft();

	int getSampleRate();

	int getLastFrameIndex();

	String getName();
}
