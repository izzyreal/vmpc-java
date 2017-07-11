package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Sampler;
import com.mpc.sampler.Sound;

public class MonoToStereoControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("lsource") && notch < 0) sampler.setSoundGuiPrevSound();
		if (param.equals("lsource") && notch > 0) sampler.setSoundGuiNextSound();

		if (param.equals("rsource")) soundGui.setRSource(sampler.getNextSoundIndex(soundGui.getRSource(), notch > 0));
	}

	public void function(int j) {
		init();
		switch (j) {
		case 3:
			mainFrame.openScreen("sound", "windowpanel");
			break;
		case 4:
			if (sampler.getSound(soundGui.getSoundIndex()).isMono()
					&& sampler.getSound(soundGui.getRSource()).isMono()) {
				Sound left = sampler.getSound(soundGui.getSoundIndex());
				Sound right = sampler.getSound(soundGui.getRSource());
				float[] newSampleDataRight = null;
				if (right.getSampleRate() > left.getSampleRate()) {
					newSampleDataRight = new float[left.getSampleData().length];
					for (int i = 0; i < newSampleDataRight.length; i++)
						newSampleDataRight[i] = right.getSampleData()[i];
				} else {
					newSampleDataRight = right.getSampleData();
				}

				Sound newSample = new Sound(left.getSampleRate());
				newSample.setName(soundGui.getNewStName());
				float[] newSampleData = Sampler.mergeToStereo(left.getSampleData(), newSampleDataRight);
				newSample.setSampleData(newSampleData);
				newSample.setMono(false);
				sampler.getSounds().add(newSample);
				mainFrame.openScreen("sound", "windowpanel");
			} else {
				return;
			}
		}
	}

}
