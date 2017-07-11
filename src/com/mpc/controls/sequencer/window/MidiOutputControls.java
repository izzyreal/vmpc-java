package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class MidiOutputControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("firstletter")) {
			nameGui.setName(mpcSequence.getDeviceName(swGui.getDeviceNumber() + notch));
			nameGui.setParameterName("devicename");
			nameGui.setNameLimit(8);
			mainFrame.openScreen("name", "dialogpanel");
		}

		if (param.equals("softthru")) swGui.setSoftThru(swGui.getSoftThru() + notch);
		if (param.equals("devicenumber")) swGui.setDeviceNumber(swGui.getDeviceNumber() + notch);

	}

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mainFrame.openScreen("midioutputmonitor", "windowpanel");
			break;
		}

	}

}
