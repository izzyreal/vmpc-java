package com.mpc.controls.sampler;

public class PurgeControls extends AbstractSamplerControls {

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
			mainFrame.openScreen("drum", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("selectdrum", "mainpanel");
			break;
		case 4:
			sampler.purge();
			mainFrame.openScreen("purge", "mainpanel");
			break;
		}
	}

}