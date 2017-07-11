package com.mpc.tootextensions;

import uk.org.toot.audio.fader.FaderControl;
import uk.org.toot.audio.mixer.MixControlIds;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.LinearLaw;

public class MpcFaderControl extends FaderControl {

	private static ControlLaw mpcFaderLaw = new LinearLaw(0, 100, "");

	public MpcFaderControl(boolean muted) {
		super(MixControlIds.GAIN, mpcFaderLaw, muted ? 0 : 100);
		gain = muted ? 0f : 1f;
	}

	@Override
	public void setValue(float value) {
		gain = value * 0.01f;
		super.setValue(value);
	}
}
