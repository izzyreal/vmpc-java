package com.mpc.controls.mixer;

import com.mpc.controls.AbstractControls;
import com.mpc.gui.sampler.MixerGui;
import com.mpc.gui.sampler.MixerSetupGui;
import com.mpc.sampler.MixerChannel;

public abstract class AbstractMixerControls extends AbstractControls {
	
	protected MixerGui mixerGui;
	protected MixerSetupGui mixerSetupGui;
	protected MixerChannel mixerChannel;
	
	protected void init() {
		super.init();
		mixerGui = gui.getMixerGui();
		mixerSetupGui = gui.getMixerSetupGui();
		mixerChannel = program.getPad(program.getPadNumberFromNote(mixerGui.getChannelSettingsNote())).getMixerChannel();
	}	
}
