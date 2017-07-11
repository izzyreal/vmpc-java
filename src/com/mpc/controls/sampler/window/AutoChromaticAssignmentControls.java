package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Sampler;

public class AutoChromaticAssignmentControls extends AbstractSamplerControls {
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("source")) {
			samplerGui.setPadAndNote(samplerGui.getPad(), samplerGui.getNote() + notch);
			swGui.setAutoChromAssSnd(Sampler.getLastNp(program).getSndNumber());
		}

		if (param.equals("programname")) {

			nameGui.setName(swGui.getNewName());
			nameGui.setParameterName("autochrom");
			mainFrame.openScreen("name", "dialogpanel");
		}

		if (param.equals("snd")) swGui.setAutoChromAssSnd(swGui.getAutoChromAssSnd() + notch);
		if (param.equals("originalkey")) swGui.setOriginalKey(swGui.getOriginalKey() + notch);
		if (param.equals("tune")) swGui.setTune(swGui.getTune() + notch);
	}
}
