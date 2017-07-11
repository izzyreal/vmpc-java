package com.mpc.controls.misc;

import com.mpc.controls.AbstractControls;
import com.mpc.gui.misc.PunchGui;
import com.mpc.gui.misc.SecondSeqGui;
import com.mpc.gui.misc.TransGui;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.SeqUtil;

abstract class AbstractMiscControls extends AbstractControls {

	protected PunchGui punchGui;
	protected TransGui transGui;
	protected SecondSeqGui secondSeqGui;
	
	protected void init() {
		super.init();
		punchGui = gui.getPunchGui();
		transGui = gui.getTransGui();
		secondSeqGui = gui.getSecondSeqGui();
				
	}
	
	protected void checkAllTimes(int notch) {
		init();
		MpcSequence mpcSequence = sequencer.getActiveSequence();
		if (param.equals("time0")) {
			punchGui.setTime0(SeqUtil.getTickFromBar((SeqUtil.getBarFromTick(mpcSequence, punchGui.getTime0())) + notch, mpcSequence,
					punchGui.getTime0()));
		}

		if (param.equals("time1")) {
			punchGui.setTime0(SeqUtil.setBeat((SeqUtil.getBeat(mpcSequence, punchGui.getTime0())) + notch, mpcSequence,
					punchGui.getTime0()));
		}

		if (param.equals("time2")) {
			punchGui.setTime0(SeqUtil.setClockNumber((SeqUtil.getClockNumber(mpcSequence, punchGui.getTime0())) + notch,
					mpcSequence, punchGui.getTime0()));
		}

		if (param.equals("time3")) {
			punchGui.setTime1(SeqUtil.getTickFromBar((SeqUtil.getBarFromTick(mpcSequence, punchGui.getTime1())) + notch, mpcSequence,
					punchGui.getTime1()));
		}

		if (param.equals("time4")) {
			punchGui.setTime1(SeqUtil.setBeat((SeqUtil.getBeat(mpcSequence, punchGui.getTime1())) + notch, mpcSequence,
					punchGui.getTime1()));
		}

		if (param.equals("time5")) {
			punchGui.setTime1(SeqUtil.setClockNumber((SeqUtil.getClockNumber(mpcSequence, punchGui.getTime1())) + notch,
					mpcSequence, punchGui.getTime1()));
		}
	}

}
