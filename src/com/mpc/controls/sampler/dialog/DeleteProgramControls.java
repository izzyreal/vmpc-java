package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Program;

public class DeleteProgramControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			mainFrame.openScreen("deleteallprograms", "dialogpanel");
			break;
		case 3:
			mainFrame.openScreen("program", "windowpanel");
			break;
		case 4:
			if (sampler.getProgramCount() > 1) {
				sampler.getPrograms().remove(swGui.getDeletePgm());
				checkProgramReferences();
			} else {
				sampler.getPrograms().set(0, new Program());
				sampler.getProgram(0).setName("NewPgm-A");
			}

			mainFrame.openScreen("program", "windowpanel");
			break;
		}

	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("pgm")) swGui.setDeletePgm(swGui.getDeletePgm() + notch);

	}
}
