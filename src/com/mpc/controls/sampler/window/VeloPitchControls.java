package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class VeloPitchControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("tune")) lastNp.setTune(lastNp.getTune() + notch);
		if (param.equals("velopitch")) lastNp.setVelocityToPitch(lastNp.getVelocityToPitch() + notch);
		if (param.equals("note")) samplerGui.setPadAndNote(samplerGui.getPad(), samplerGui.getNote() + notch);
	}

}
