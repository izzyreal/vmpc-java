package com.mpc.controls.sequencer;

import com.mpc.sampler.Slider;

public class AssignControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		Slider slider = program.getSlider();
		int parameter = slider.getParameter();
		switch (param) {
		case "assignnote":
			slider.setAssignNote(slider.getNote() + notch);
			break;
		case "parameter":
			slider.setParameter(slider.getParameter() + notch);
			break;
		case "highrange":
			switch (parameter) {
			case 0:
				slider.setTuneHighRange(slider.getTuneHighRange() + notch);
				break;
			case 1:
				slider.setDecayHighRange(slider.getDecayHighRange() + notch);
				break;
			case 2:
				slider.setAttackHighRange(slider.getAttackHighRange() + notch);
				break;
			case 3:
				slider.setFilterHighRange(slider.getFilterHighRange() + notch);
				break;
			}
			break;

		case "lowrange":
			switch (parameter) {
			case 0:
				slider.setTuneLowRange(slider.getTuneLowRange() + notch);
				break;
			case 1:
				slider.setDecayLowRange(slider.getDecayLowRange() + notch);
				break;
			case 2:
				slider.setAttackLowRange(slider.getAttackLowRange() + notch);
				break;
			case 3:
				slider.setFilterLowRange(slider.getFilterLowRange() + notch);
				break;
			}
			break;

		case "assignnv":
			slider.setControlChange(slider.getControlChange() + notch);
			break;
		}
	}

	public void pad(int i, int velo, boolean repeat) {
		super.pad(i, velo, repeat, 0);
		int nn = program.getNoteFromPad(i + (samplerGui.getBank() * 16));
		program.getSlider().setAssignNote(nn);
	}
}