package com.mpc.controls.mixer;

public class SelectDrumMixerControls extends AbstractMixerControls {

	public void function(int i) {
		init();
		if (i < 4) {
			gui.getSamplerGui().setSelectedDrum(i);
			gui.getSamplerGui().setPrevScreenName(csn);
			mainFrame.openScreen("mixer", "mainpanel");
		}
	}
}