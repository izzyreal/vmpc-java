package com.mpc.controls.sampler.dialog;

import org.apache.commons.lang3.StringUtils;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class ConvertSoundControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		if (param.equals("convert")) soundGui.setConvert(i < 0 ? 0 : 1);

	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("sound", "windowpanel");
			break;
		case 4:
			if (soundGui.getConvert() == 0) {
				String name = sampler.getSoundName(soundGui.getSoundIndex());
				name = name.replace(" ", "");
				name = StringUtils.rightPad(name, 16, '_');
				name = name.substring(0, 14);

				if (sampler.getSound(soundGui.getSoundIndex()).isMono()) {
					soundGui.setNewStName(name + "-S");
					soundGui.setRSource(soundGui.getSoundIndex());
					mainFrame.openScreen("monotostereo", "dialogpanel");
				} else {
					soundGui.setNewLName(name + "-L");
					soundGui.setNewRName(name + "-R");
					mainFrame.openScreen("stereotomono", "dialogpanel");
				}
			} else {
				mainFrame.openScreen("resample", "dialogpanel");
				soundGui.setNewFs(sampler.getSound(soundGui.getSoundIndex()).getSampleRate());
				String newSampleName = sampler.getSoundName(soundGui.getSoundIndex());
				newSampleName = newSampleName.replaceAll("\\s+$", "");
				newSampleName = sampler.addOrIncreaseNumber(newSampleName);
				soundGui.setNewName(newSampleName);
			}
		}
	}
}
