package com.mpc.file.pgmwriter;

import com.mpc.sampler.Program;

public class Slider {

	protected final char[] sliderArray;

	public Slider(Program program) {

		sliderArray = new char[15];

		if (program.getSlider().getNote() == -1) {
			setMidiNoteAssign(0);
		} else {
			setMidiNoteAssign(program.getSlider().getNote() + 35);
		}
		setTuneLow(program.getSlider().getTuneLowRange());
		setTuneHigh(program.getSlider().getTuneHighRange());
		setDecayLow(program.getSlider().getDecayLowRange());
		setDecayHigh(program.getSlider().getDecayHighRange());
		setAttackLow(program.getSlider().getAttackLowRange());
		setAttackHigh(program.getSlider().getAttackHighRange());
		setFilterLow(program.getSlider().getFilterLowRange());
		setFilterHigh(program.getSlider().getFilterHighRange());
		setControlChange(program.getSlider().getControlChange());

		sliderArray[10] = 0x23;
		sliderArray[11] = 0x40;
		sliderArray[12] = 0x00;
		sliderArray[13] = 0x19;
		sliderArray[14] = 0x00;
	}

	char[] getSliderArray() {
		return sliderArray;
	}

	private void setMidiNoteAssign(int midiNoteAssign) {
		sliderArray[0] = (char) midiNoteAssign;
	}

	private void setTuneLow(int tuneLow) {
		sliderArray[1] = (char) tuneLow;
	}

	private void setTuneHigh(int tuneHigh) {
		sliderArray[2] = (char) tuneHigh;
	}

	private void setDecayLow(int decayLow) {
		sliderArray[3] = (char) decayLow;
	}

	private void setDecayHigh(int decayHigh) {
		sliderArray[4] = (char) decayHigh;
	}

	private void setAttackLow(int attackLow) {
		sliderArray[5] = (char) attackLow;
	}

	private void setAttackHigh(int attackHigh) {
		sliderArray[6] = (char) attackHigh;
	}

	private void setFilterLow(int filterLow) {
		sliderArray[7] = (char) filterLow;
	}

	private void setFilterHigh(int filterHigh) {
		sliderArray[8] = (char) filterHigh;
	}

	private void setControlChange(int controlChange) {
		sliderArray[9] = (char) controlChange;

	}

}