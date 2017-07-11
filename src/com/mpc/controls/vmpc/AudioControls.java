package com.mpc.controls.vmpc;

import java.util.List;

import com.mpc.Util;

public class AudioControls extends AbstractVmpcControls {

	public void openWindow() {
		init();
		// if (param.equals("server")) {
		// if (ams.getSelectedServer() == ams.getActiveServerIndex()) {
		if (ams.isAsio() || ams.isJava() || ams.isCoreAudio()) mainFrame.openScreen("buffersize", "windowpanel");
		// }
		// }
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("server")) ams.setSelectedServer(ams.getSelectedServer() + notch);
		if (param.equals("in")) audioGui.setIn(audioGui.getIn() + notch);
		if (param.equals("out")) audioGui.setOut(audioGui.getOut() + notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 1:
			openMain("midi");
			break;
		case 2:
			openMain("disk");
			break;
		case 5:
			// apply new audio settings
			if (!ams.setActiveServer(ams.getSelectedServer())) {
				new Thread() {
					public void run() {
						mainFrame.popupPanel("DEVICE ERROR!", 170);
						Util.sleep(2000);
						mainFrame.removePopup();
					}
				}.start();
			}
			break;
		}
	}

}
