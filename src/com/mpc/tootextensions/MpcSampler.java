package com.mpc.tootextensions;

public interface MpcSampler {

	MpcProgram getProgram(int programNumber);

	int getProgramCount();

	MpcSoundOscillatorVariables getSound(int index);

	MpcSoundOscillatorVariables getClickSound();

	MpcSoundOscillatorVariables getPreviewSound();

	int getSoundCount();

}
