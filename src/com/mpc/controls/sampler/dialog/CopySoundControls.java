package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Sound;

public class CopySoundControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("sound", "windowpanel");
			break;
		case 4:
			Sound sound = sampler.getSound(soundGui.getSoundIndex());
			Sound newSound = sampler.copySound(sound);
			newSound.setName(soundGui.getNewName());
			sampler.getSounds().add(newSound);
			soundGui.setSoundIndex(sampler.getSoundCount() - 1);
			mainFrame.openScreen("sound", "windowpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("snd")) {
			soundGui.setSoundIndex(sampler.getNextSoundIndex(soundGui.getSoundIndex(), notch > 0));
			String newSampleName = sampler.getSoundName(soundGui.getSoundIndex());
			newSampleName = newSampleName.replaceAll("\\s+$", "");
			newSampleName = sampler.addOrIncreaseNumber(newSampleName);
			soundGui.setNewName(newSampleName);
		}

		if (param.equals("newname")) {
			nameGui.setName(mainFrame.lookupTextField("newname").getText());
			nameGui.setParameterName("newname");
			mainFrame.openScreen("name", "dialogpanel");
		}

	}

}
