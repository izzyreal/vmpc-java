package com.mpc.file.pgmreader;

import java.util.Arrays;

public class Mixer {
	
	protected byte[] mixerArray;

	private ProgramFileReader programFile;
	
	public Mixer(ProgramFileReader programFile) {
		 this.programFile = programFile;
	}

	protected int getSampleNamesSize () {
		int sampleNamesSize = programFile.getSampleNames().getSampleNamesSize();
		return sampleNamesSize;
	}

	protected int getMixerStart () {
		int mixerStart = 4 + getSampleNamesSize() + 2 + 17 + 9 + 6 + 1601;
		return mixerStart;
	}

	protected int getMixerEnd () {
		int mixerEnd = 4 + getSampleNamesSize() + 2 + 17 + 9 + 6 + 1601 + 387;
		return mixerEnd;
	}

	protected byte[] getMixerArray() {
		mixerArray = Arrays.copyOfRange(programFile.readProgramFileArray(), getMixerStart(), getMixerEnd());
		return mixerArray;
	}

	public int getEffectsOutput(int note) {
		byte effectsOutput = getMixerArray()[(note*6) + 0];
		return effectsOutput;
	}
	
	public int getVolume(int note) {
		byte volume = getMixerArray()[(note*6) + 1];
		return volume;
	}

	public int getPan(int note) {
		byte pan = getMixerArray()[(note*6) + 2];
		return pan;
	}

	public int getVolumeIndividual(int note) {
		byte volumeIndividual = getMixerArray()[(note*6) + 3];
		return volumeIndividual;
	}

	public int getOutput(int note) {
		byte output = getMixerArray()[(note*6) + 4];
		return output;
	}

	public int getEffectsSendLevel(int note) {
		byte effectsSendLevel = getMixerArray()[(note*6) + 5];
		return effectsSendLevel;
	}
}