package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class MidiInputControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mainFrame.openScreen("midiinputmonitor", "windowpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("receivech")) swGui.setReceiveCh(swGui.getReceiveCh() + notch);
		if (param.equals("seq")) swGui.setProgChangeSeq(notch > 0);
		if (param.equals("duration")) swGui.setSustainPedalToDuration(notch > 0);
		if (param.equals("midifilter")) swGui.setMidiFilterEnabled(notch > 0);
		if (param.equals("type")) swGui.setFilterType(swGui.getMidiFilterType() + notch);
		if (param.equals("pass")) swGui.setPass(notch > 0);
	}

}
