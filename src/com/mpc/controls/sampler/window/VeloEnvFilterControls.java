package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class VeloEnvFilterControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("attack")) lastNp.setFilterAttack(lastNp.getFilterAttack() + notch);
		if (param.equals("decay")) lastNp.setFilterDecay(lastNp.getFilterDecay() + notch);
		if (param.equals("amount")) lastNp.setFilterEnvelopeAmount(lastNp.getFilterEnvelopeAmount() + notch);
		if (param.equals("velofreq")) lastNp.setVelocityToFilterFrequency(lastNp.getVelocityToFilterFrequency() + notch);
		if (param.equals("note")) samplerGui.setPadAndNote(samplerGui.getPad(), samplerGui.getNote() + notch);

	}

}
