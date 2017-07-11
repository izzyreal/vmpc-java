package com.mpc.controls.sequencer;

public class NextSeqPadControls extends AbstractSequencerControls {

	public void right() {
		// disabled
	}
	
		public void pad(int i, int velo) {
		init();
		sequencer.setNextSqPad(i + (bank * 16));
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
			mainFrame.openScreen("nextseq", "mainpanel");
			break;
		}
	}

}