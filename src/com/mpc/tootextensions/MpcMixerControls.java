package com.mpc.tootextensions;

import uk.org.toot.audio.mixer.MixerControls;

public class MpcMixerControls extends MixerControls {
	
	public MpcMixerControls(String name, float smoothingFactor) {
		super(name, smoothingFactor);
	}

	@Override
    public MpcFaderControl createFaderControl(boolean muted) {
		return new MpcFaderControl(muted);		
	}	
}
