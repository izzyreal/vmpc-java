package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class VelocityModulationControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("veloattack")) lastNp.setVelocityToAttack(lastNp.getVelocityToAttack() + notch);
		if (param.equals("velostart")) lastNp.setVelocityToStart(lastNp.getVelocityToStart() + notch);
		if (param.equals("velolevel")) lastNp.setVeloToLevel(lastNp.getVeloToLevel() + notch);
		if (param.equals("note")) samplerGui.setPadAndNote(samplerGui.getPad(), samplerGui.getNote() + notch);

	}

}
