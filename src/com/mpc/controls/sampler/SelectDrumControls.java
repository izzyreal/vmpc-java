package com.mpc.controls.sampler;

public class SelectDrumControls extends AbstractSamplerControls {

	public void function(int i) {
		init();

		if (i < 4) {
			samplerGui.setSelectedDrum(i);
			String prevCsn = ls.getPreviousScreenName();
			String name = "programassign";
			if (prevCsn.equals("programparams") || prevCsn.equals("drum") || prevCsn.equals("purge")) name = prevCsn;
			if (samplerGui.getNote() < 35) samplerGui.setPadAndNote(program.getPadNumberFromNote(35), 35);
			mainFrame.openScreen(name, "mainpanel");
			return;
		}
	}

}
