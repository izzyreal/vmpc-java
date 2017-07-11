package com.mpc.file.pgmreader;

import java.util.Arrays;

public class Slider {
	int sampleNamesSize = 0;

	protected byte[] sliderArray;

	private ProgramFileReader programFile;
	
	public Slider(ProgramFileReader programFile) {
		 this.programFile = programFile;
	}

	protected int getSampleNamesSize () {
		sampleNamesSize = programFile.getSampleNames().getSampleNamesSize();
		return sampleNamesSize;
	}

	protected int getSliderStart () {
		int sliderStart = 4 + getSampleNamesSize() + 2 + 17;
		return sliderStart;
	}

	protected int getSliderEnd () {
		int sliderEnd = 4 + getSampleNamesSize() + 2 + 17 + 10 + 5;
		return sliderEnd;
	}

	protected byte[] getSliderArray() {
		sliderArray = Arrays.copyOfRange(programFile.readProgramFileArray(), getSliderStart(), getSliderEnd());
		return sliderArray;
	}

	public int getMidiNoteAssign() {
		byte midiNoteAssign = getSliderArray()[0];
		return midiNoteAssign;
	}
	
	public int getTuneLow(){
		byte tuneLow = getSliderArray()[1];
		return tuneLow;
	}

	public int getTuneHigh(){
		byte tuneHigh = getSliderArray()[2];
		return tuneHigh;
	}

	public int getDecayLow(){
		byte decayLow = getSliderArray()[3];
		return decayLow;
	}

	public int getDecayHigh(){
		byte decayHigh = getSliderArray()[4];
		return decayHigh;
	}

	public int getAttackLow(){
		byte attackLow = getSliderArray()[5];
		return attackLow;
	}

	public int getAttackHigh(){
		byte attackHigh = getSliderArray()[6];
		return attackHigh;
	}
	public int getFilterLow(){
		byte filterLow = getSliderArray()[7];
		return filterLow;
	}

	public int getFilterHigh(){
		byte filterHigh = getSliderArray()[8];
		return filterHigh;
	}

	public int getControlChange(){
		byte controlChange = getSliderArray()[9];
		return controlChange;
	}
}