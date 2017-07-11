package com.mpc.controls.sampler.dialog;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Sound;

public class StereoToMonoControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("stereosource"))
			soundGui.setSoundIndex(sampler.getNextSoundIndex(soundGui.getSoundIndex(), notch > 0));

		if (param.equals("newlname")) {
			nameGui.setName(mainFrame.lookupTextField("newlname").getText());
			nameGui.setParameterName("newlname");
			mainFrame.openScreen("name", "dialogpanel");
		}

		if (param.equals("newrname")) {
			nameGui.setName(mainFrame.lookupTextField("newrname").getText());
			nameGui.setParameterName("newrname");
			mainFrame.openScreen("name", "dialogpanel");
		}
	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("sound", "windowpanel");
			break;

		case 4:
			Sound sound = sampler.getSound(soundGui.getSoundIndex());

			if (sound.isMono()) return;

			Sound left = new Sound(sound.getSampleRate());
			Sound right = new Sound(sound.getSampleRate());

			left.setName(soundGui.getNewLName());
			right.setName(soundGui.getNewRName());

			left.setSampleData(sound.getSampleDataLeft());
			right.setSampleData(sound.getSampleDataRight());

			left.setMono(true);
			right.setMono(true);

			sampler.getSounds().add(left);
			sampler.getSounds().add(right);

			mainFrame.openScreen("sound", "windowpanel");
			break;
		}
	}
}
