package com.mpc.controls.sampler;

public class DrumControls extends AbstractSamplerControls {

	@Override
	public void function(int f) {
		init();
		switch (f) {
		case 0:
			mainFrame.openScreen("programassign", "mainpanel");
			break;
		case 1:
			mainFrame.openScreen("programparams", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("selectdrum", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("purge", "mainpanel");
			break;
		}
	}

	@Override
	public void turnWheel(int increment) {
		init();
//		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		int notch = getNotch(increment);
		boolean yes = notch > 0;
		if (param.equals("drum")) samplerGui.setSelectedDrum(samplerGui.getSelectedDrum() + notch);

		if (param.equals("pgm")) mpcSoundPlayerChannel.setProgram(mpcSoundPlayerChannel.getProgram() + notch);

		if (param.equals("programchange")) mpcSoundPlayerChannel.setReceivePgmChange(yes);

		if (param.equals("midivolume")) mpcSoundPlayerChannel.setReceiveMidiVolume(yes);

		if (param.equals("padtointernalsound")) samplerGui.setPadToIntSound(yes);
	}
}