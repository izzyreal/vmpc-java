package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.Sampler;

public class KeepOrRetryControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 1:
			mainFrame.openScreen("sample", "mainpanel");
			break;
		case 3:
			sampler.playPreviewSample(0, sampler.getPreviewSound().getLastFrameIndex(), 0, 2);
			break;
		case 4:
			sampler.getSounds().add(sampler.getPreviewSound());
			Sampler.getLastNp(program).setSoundNumber(sampler.getSoundCount() - 1);
			gui.getSoundGui().initZones(sampler.getPreviewSound().getLastFrameIndex());
			gui.getSoundGui().setSoundIndex(sampler.getSoundCount() - 1);
			mainFrame.openScreen("sample", "mainpanel");
			break;
		}

	}

	public void turnWheel(int i) {
		init();
		nameGui.setName(sampler.getPreviewSound().getName());
		nameGui.setParameterName(param);
		mainFrame.openScreen("name", "dialogpanel");
	}
}
