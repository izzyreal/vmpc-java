package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Program;

public class DeleteAllProgramsControls extends AbstractSamplerControls {

	public void function(int j) {
		init();
		switch (j) {
		case 3:
			mainFrame.openScreen("deleteprogram", "dialogpanel");
			break;
		case 4:
			if (sampler.getProgramCount() > 1) {

				for (int i = 1; i < sampler.getProgramCount(); i++)
					sampler.getPrograms().remove(i);

			}

			sampler.getPrograms().set(0, new Program());
			sampler.getProgram(0).setName("NewPgm-A");

			checkProgramReferences();

			mainFrame.openScreen("deleteprogram", "dialogpanel");
			break;
		}

	}

}
