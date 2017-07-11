package com.mpc.controls.disk.window;

import com.mpc.command.KeepSound;
import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.sampler.Sound;

public class LoadASoundControls extends AbstractDiskControls {

	public void pad(int i) {
		// pads disabled
	}
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("assigntonote")) {

			int nextNn = samplerGui.getNote() + notch;
			int nextPn = program.getPadNumberFromNote(nextNn);

			samplerGui.setPadAndNote(nextPn, nextNn);
		}
	}

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			sampler.stopAllVoices();
			Sound s = sampler.getPreviewSound();
			int start = s.getStart();
			int end = s.getSampleData().length;
			int loopTo = -1;
			int overlapMode = 1;
			if (s.isLoopEnabled()) {
				loopTo = s.getLoopTo();
				overlapMode = 2;
			}
			if (!s.isMono()) end /= 2;
			sampler.playPreviewSample(start, end, loopTo, overlapMode);
			break;
		case 3:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 4:
			KeepSound command = new KeepSound(mpc, sampler.getPreviewSound());
			command.execute();
			mainFrame.openScreen("load", "mainpanel");
			break;
		}
	}
}