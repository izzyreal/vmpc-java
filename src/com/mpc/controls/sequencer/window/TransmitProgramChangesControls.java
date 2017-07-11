package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class TransmitProgramChangesControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("inthistrack")) swGui.setTransmitProgramChangesInThisTrack(notch > 0);

	}
}
