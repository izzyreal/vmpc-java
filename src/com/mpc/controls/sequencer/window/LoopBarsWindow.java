package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class LoopBarsWindow extends AbstractSequencerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("firstbar")) mpcSequence.setFirstLoopBar(mpcSequence.getFirstLoopBar() + notch);
		if (param.equals("lastbar")) mpcSequence.setLastLoopBar(mpcSequence.getLastLoopBar() + notch);

		if (param.equals("numberofbars") && notch < 0) {
			if (mpcSequence.isLastLoopBarEnd()) {
				mpcSequence.setLastLoopBar(mpcSequence.getLastLoopBar() - 1);
				mpcSequence.setLastLoopBar(mpcSequence.getLastLoopBar() - 1);
				return;
			}
			if (mpcSequence.getLastLoopBar() > mpcSequence.getFirstLoopBar())
				mpcSequence.setLastLoopBar(mpcSequence.getLastLoopBar() - 1);

		}

		if (param.equals("numberofbars")) mpcSequence.setLastLoopBar(mpcSequence.getLastLoopBar() + notch);

	}

}
