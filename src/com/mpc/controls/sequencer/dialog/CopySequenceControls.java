package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class CopySequenceControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			sequencer.copySequence(swGui.getSq0(), swGui.getSq1());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		case 4:
			sequencer.copySequence(swGui.getSq0(), swGui.getSq1());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}

	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.contains("0")) swGui.setSq0(swGui.getSq0() + notch);
		if (param.contains("1")) swGui.setSq1(swGui.getSq1() + notch);
	}
}
