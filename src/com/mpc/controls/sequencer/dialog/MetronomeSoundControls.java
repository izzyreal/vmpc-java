package com.mpc.controls.sequencer.dialog;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class MetronomeSoundControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch(i) {
		case 3:
			mainFrame.openScreen("countmetronome", "windowpanel");
			break;
		}
	}
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("sound")) swGui.setMetronomeSound(swGui.getMetronomeSound() + notch);
		if (param.equals("volume")) swGui.setClickVolume(swGui.getClickVolume() + notch);
		if (param.equals("output")) swGui.setClickOutput(swGui.getClickOutput() + notch);
		if (param.equals("accent")) swGui.setAccentNote(swGui.getAccentNote() + notch);
		if (param.equals("normal")) swGui.setNormalNote(swGui.getNormalNote() + notch);
		if (param.equals("velocityaccent")) swGui.setAccentVelo(swGui.getAccentVelo() + notch);
		if (param.equals("velocitynormal")) swGui.setNormalVelo(swGui.getNormalVelo() + notch);
	}

}
