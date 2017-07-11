package com.mpc.controls.vmpc;

import com.mpc.Util;

public class BufferSizeControls extends AbstractVmpcControls {

	private int reset = -1;

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			if (reset != -1)
				ams.setActiveServer(reset);
			openMain("audio");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("frames")) {

			if (ams.isAsio()) {
				int gran = ams.getAsioAudioServer().getBufferGranularity();
				if (gran == -1) {
					if (notch > 0) {
						ams.increaseBufferSize();
					} else {
						ams.decreaseBufferSize();
					}
					mainFrame.openScreen("buffersize", "windowpanel");
				} else {
					int server = ams.getActiveServerIndex();
					reset = server;
					ams.getAsioAudioServer().openControlPanel();
					mainFrame.openScreen("buffersize", "windowpanel");
				}
			} else if (ams.isCoreAudio()) {
				ams.setBufferSize(ams.getCoreAudioServer()
						.getOutputLatencyFrames() + (notch * 2));
//				Util.sleep(500);
				mainFrame.openScreen("buffersize", "windowpanel");
			} else if (ams.isJava()) {
				float candidate = ams.getTimedAudioServer()
						.getLatencyMilliseconds() + (notch * 4);
				if (candidate < 1600 || candidate > 9999)
					return;
				ams.getTimedAudioServer().setLatencyMilliseconds(candidate);
				mainFrame.openScreen("buffersize", "windowpanel");
			}
		}
	}

}
