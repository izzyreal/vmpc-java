package com.mpc.controls.sampler;

import com.mpc.sampler.Sampler;

public class InitPadAssignControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		swGui.setInitPadAssignMaster(notch > 0);

	}

	public void function(int i) {
		init();
		switch (i) {
		case 4:

			if (swGui.isInitPadAssignMaster()) {
				Sampler.masterPadAssign = Sampler.initMasterPadAssign;
			} else {
				program.initPadAssign();
			}
			samplerGui.setPadAssignMaster(swGui.isInitPadAssignMaster());
			mainFrame.openScreen("programassign", "mainpanel");
			break;
		}
	}
}
