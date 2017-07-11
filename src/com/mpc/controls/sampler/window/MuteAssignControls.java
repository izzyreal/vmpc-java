package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class MuteAssignControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("note")) samplerGui.setPadAndNote(samplerGui.getPad(), samplerGui.getNote() + notch);
		if (param.equals("note0")) lastNp.setMuteAssignA(lastNp.getMuteAssignA() + notch);
		if (param.equals("note1")) lastNp.setMuteAssignB(lastNp.getMuteAssignB() + notch);

	}
}
