package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class DeleteAllSoundControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("deletesound", "dialogpanel");
			break;
		case 4:
			sampler.deleteAllSamples();
			mainFrame.openScreen(soundGui.getPreviousScreenName(), "mainpanel");
			break;
		}

	}

}
