package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class DeleteSoundControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			mainFrame.openScreen("deleteallsound", "dialogpanel");
			break;
		case 3:
			mainFrame.openScreen("sound", "windowpanel");
			break;
		case 4:
			sampler.deleteSample(soundGui.getSoundIndex());
			if (sampler.getSoundCount() > 0) {
				mainFrame.openScreen("sound", "windowpanel");
			} else {
				mainFrame.openScreen(soundGui.getPreviousScreenName(), "mainpanel");
			}
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("snd")) soundGui.setSoundIndex(soundGui.getSoundIndex() + notch);
	}

}
