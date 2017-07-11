package com.mpc.controls.sequencer;

import com.mpc.controls.KbMouseController;

public class TrMuteControls extends AbstractSequencerControls {

	public void right() {
		// disabled
	}
	
	public void pad(int i, int velo) {
		init();
		if (KbMouseController.f6IsPressed || sequencer.isSoloEnabled()) {
			if (!sequencer.isSoloEnabled()) sequencer.setSoloEnabled(true);
			sequencer.setSelectedTrackIndex(i + (bank * 16));
			mainFrame.getLayeredScreen().removeCurrentBackground();
			mainFrame.getLayeredScreen().setCurrentBackground("trackmutesolo2");
		} else {
			sequencer.getActiveSequence().getTrack(i + (bank * 16))
					.setOn(!sequencer.getActiveSequence().getTrack(i + (bank * 16)).isOn());
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("sq")) sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex() + notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 5:
			KbMouseController.f6IsPressed = true;
			if (sequencer.isSoloEnabled()) {
				mainFrame.getLayeredScreen().removeCurrentBackground();
				mainFrame.getLayeredScreen().setCurrentBackground("trackmute");
				sequencer.setSoloEnabled(false);
			} else {
				mainFrame.getLayeredScreen().removeCurrentBackground();
				mainFrame.getLayeredScreen().setCurrentBackground("trackmutesolo1");
			}
			break;
		}
		
	}
}