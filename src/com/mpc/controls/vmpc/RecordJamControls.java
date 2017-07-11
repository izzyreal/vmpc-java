package com.mpc.controls.vmpc;

import com.mpc.Util;
import com.mpc.audiomidi.DirectToDiskSettings;

public class RecordJamControls extends AbstractVmpcControls {

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("directtodiskrecorder", "windowpanel");
			break;
		case 4:
			final String outputFolder = d2dRecorderGui.getOutputfolder();
			final int minutes = 10;
			final long lengthInFrames = 44100 * 60 * minutes;
			final boolean split = d2dRecorderGui.isSplitLR();
			new Thread(new Runnable() {
				public void run() {
					mainFrame.popupPanel("PREPARING FILES...", 130);
					mpc.getAudioMidiServices().prepareBouncing(new DirectToDiskSettings(lengthInFrames, outputFolder, split));
					mpc.getAudioMidiServices().startBouncing();
					mainFrame.popupPanel("RECORDING JAM (" + minutes + " MIN.)", 120);
					Util.sleep(1000);
					mainFrame.removePopup();
				}
			}).start();
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

}
