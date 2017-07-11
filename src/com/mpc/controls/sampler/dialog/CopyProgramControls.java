package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class CopyProgramControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("program", "windowpanel");
			break;
		case 4:
			if (swGui.getPgm0() == swGui.getPgm1()) return;
			mainFrame.openScreen("program", "windowpanel");
			// TODO Copy program
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("pgm0")) swGui.setPgm0(swGui.getPgm0() + notch);
		if (param.equals("pgm1")) swGui.setPgm1(swGui.getPgm1() + notch);
	}

}
