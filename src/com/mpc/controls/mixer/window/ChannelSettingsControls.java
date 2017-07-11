package com.mpc.controls.mixer.window;

import com.mpc.controls.mixer.AbstractMixerControls;

public class ChannelSettingsControls extends AbstractMixerControls {

	@Override
	public void turnWheel(int increment) {
		init();
		int notch = getNotch(increment);
		if (param.equals("note"))
			mixerGui.setChannelSettingsNote(mixerGui.getChannelSettingsNote() + notch);
		if (param.equals("stereovolume")) mixerChannel.setLevel(mixerChannel.getLevel() + notch);
		if (param.equals("individualvolume"))
			mixerChannel.setVolumeIndividualOut(mixerChannel.getVolumeIndividualOut() + notch);
		if (param.equals("fxsendlevel")) mixerChannel.setFxSendLevel(mixerChannel.getFxSendLevel() + notch);
		if (param.equals("panning")) mixerChannel.setPanning(mixerChannel.getPanning() + notch);
		if (param.equals("output")) mixerChannel.setOutput(mixerChannel.getOutput() + notch);
		if (param.equals("fxpath")) mixerChannel.setFxPath(mixerChannel.getFxPath() + notch);
		if (param.equals("followstereo")) mixerChannel.setFollowStereo(true);
	}

}