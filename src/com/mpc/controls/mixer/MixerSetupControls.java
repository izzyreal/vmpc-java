package com.mpc.controls.mixer;

public class MixerSetupControls extends AbstractMixerControls {

	public void turnWheel(int increment) {
		init();
		int notch = getNotch(increment);
		boolean yes = notch > 0;
		if (param.equals("stereomixsource")) mixerSetupGui.setStereoMixSourceDrum(yes);
		if (param.equals("indivfxsource")) mixerSetupGui.setIndivFxSourceDrum(yes);
		if (param.equals("copypgmmixtodrum")) mixerSetupGui.setCopyPgmMixToDrumEnabled(yes);
		if (param.equals("recordmixchanges")) mixerSetupGui.setRecordMixChangesEnabled(yes);
		if (param.equals("masterlevel")) mixerSetupGui.setMasterLevel(mixerSetupGui.getMasterLevel() + notch);
		if (param.equals("fxdrum")) mixerSetupGui.setFxDrum(mixerSetupGui.getFxDrum() + notch);
	}

	public void function(int i) {
		init();
		if (i < 4) {
			gui.getSamplerGui().setSelectedDrum(i);
			gui.getSamplerGui().setPrevScreenName(csn);
			mainFrame.openScreen("mixer", "mainpanel");
		}
	}
}