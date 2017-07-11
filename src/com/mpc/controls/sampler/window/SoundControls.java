package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class SoundControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		if (param.equals("soundname")) {

			nameGui.setName(mainFrame.lookupTextField("soundname").getText());

			nameGui.setParameterName("soundname");
			mainFrame.openScreen("name", "dialogpanel");

		}
	}

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mainFrame.openScreen("deletesound", "dialogpanel");
			break;
		case 2:
			mainFrame.openScreen("convertsound", "dialogpanel");
			break;
		case 4:
			String newSampleName = sampler.getSoundName(soundGui.getSoundIndex());
			newSampleName = newSampleName.replaceAll("\\s+$", "");
			newSampleName = sampler.addOrIncreaseNumber(newSampleName);
			mainFrame.openScreen("copysound", "dialogpanel");
			soundGui.setNewName(newSampleName);
			break;
		}

	}
}
