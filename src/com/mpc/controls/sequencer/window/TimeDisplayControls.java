package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class TimeDisplayControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("displaystyle")) swGui.setDisplayStyle(swGui.getDisplayStyle() + notch);
		if (param.equals("starttime")) swGui.setStartTime(swGui.getStartTime() + notch);
		if (param.equals("h")) swGui.setH(swGui.getH() + notch);
		if (param.equals("m")) swGui.setM(swGui.getM() + notch);
		if (param.equals("s")) swGui.setS(swGui.getS() + notch);
		if (param.equals("f")) swGui.setF(swGui.getF() + notch);
		if (param.equals("framerate")) swGui.setFrameRate(swGui.getFrameRate() + notch);
	}

}
