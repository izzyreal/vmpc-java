package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class NumberOfZonesControls extends AbstractSamplerControls {

	@Override
	public void function(int f) {
		super.function(f);
		switch (f) {
		case 4:
			soundGui.initZones(sound.getLastFrameIndex() + 1);
			mainFrame.openScreen("zone", "mainpanel");
			break;
		}
	}

	@Override
	public void turnWheel(int increment) {
		init();
		if (param == null) return;
		if (param.equals("numberofzones")) soundGui.setNumberOfZones(soundGui.getNumberOfZones() + increment);
	}

}