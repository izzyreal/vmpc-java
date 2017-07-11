package com.mpc.controls.sequencer;

public class NextSeqControls extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("sq")) {
			if (sequencer.isPlaying()) {
				sequencer.setNextSq(sequencer.getCurrentlyPlayingSequenceIndex() + notch);
			} else {
				sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex() + notch);
			}
		}
		if (param.equals("nextsq")) sequencer.setNextSq(sequencer.getNextSq() + notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 5:
			mainFrame.openScreen("nextseqpad", "mainpanel");
			break;
		}
	}
}