package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Program;

public class CreateNewProgramControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("program", "windowpanel");
			break;
		case 4:
			sampler.getPrograms().add(new Program());
			sampler.getProgram(sampler.getProgramCount() - 1).setName(swGui.getNewName());
			sampler.getProgram(sampler.getProgramCount() - 1).setMidiProgramChange(swGui.getNewProgramChange());
			mpcSoundPlayerChannel.setProgram(sampler.getProgramCount() - 1);

			mainFrame.openScreen("program", "windowpanel");
			break;
		}
	}

}
