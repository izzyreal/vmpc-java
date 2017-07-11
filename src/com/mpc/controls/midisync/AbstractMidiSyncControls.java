package com.mpc.controls.midisync;

import com.mpc.audiomidi.MpcMidiPorts;
import com.mpc.controls.AbstractControls;
import com.mpc.gui.midisync.MidiSyncGui;

abstract class AbstractMidiSyncControls extends AbstractControls {

	protected MidiSyncGui midiSyncGui;
	protected MpcMidiPorts mpcMidiPorts;

	protected void init() {
		super.init();
		midiSyncGui = gui.getMidiSyncGui();
		mpcMidiPorts = mpc.getMidiPorts();
	}
}
