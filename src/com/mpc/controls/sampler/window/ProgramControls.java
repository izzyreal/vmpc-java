package com.mpc.controls.sampler.window;

import com.mpc.Mpc;
import com.mpc.controls.sampler.AbstractSamplerControls;

public class ProgramControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("programname")) {
			nameGui.setName(program.getName());
			nameGui.setParameterName(param);
			mainFrame.openScreen("name", "dialogpanel");
		}

		if (param.equals("midiprogramchange")) program.setMidiProgramChange(program.getMidiProgramChange() + notch);

	}

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mainFrame.openScreen("deleteprogram", "dialogpanel");
			break;
		case 2:
			int letterNumber = sampler.getProgramCount() + 21;
			String newName = "NewPgm-" + Mpc.akaiAscii[letterNumber];
			swGui.setNewName(newName);
			mainFrame.openScreen("createnewprogram", "dialogpanel");
			break;
		case 4:
			mainFrame.openScreen("copyprogram", "dialogpanel");
			break;

		}
	}

}
