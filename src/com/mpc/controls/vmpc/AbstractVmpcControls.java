package com.mpc.controls.vmpc;

import com.mpc.audiomidi.AudioMidiServices;
import com.mpc.audiomidi.MpcMidiPorts;
import com.mpc.controls.AbstractControls;
import com.mpc.gui.midisync.MidiSyncGui;
import com.mpc.gui.vmpc.AudioGui;
import com.mpc.gui.vmpc.DirectToDiskRecorderGui;
import com.mpc.gui.vmpc.MidiGui;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.SeqUtil;

abstract class AbstractVmpcControls extends AbstractControls {

	protected MidiSyncGui midiSyncGui;
	protected MpcMidiPorts mpcMidiPorts;
	protected MidiGui midiGui;
	protected AudioGui audioGui;
	protected DirectToDiskRecorderGui d2dRecorderGui;
	protected AudioMidiServices ams;
	
	protected void init() {
		super.init();
		midiSyncGui = gui.getMidiSyncGui();
		mpcMidiPorts = mpc.getMidiPorts();
		midiGui = gui.getMidiGui();
		audioGui = gui.getAudioGui();
		d2dRecorderGui = gui.getD2DRecorderGui();
		ams = gui.getMpc().getAudioMidiServices();
	}
	
	protected void checkAllTimes(int notch) {
		init();
		MpcSequence mpcSequence = sequencer.getSequence(d2dRecorderGui.getSq());
		if (param.equals("time0")) {
			d2dRecorderGui.setTime0(SeqUtil.getTickFromBar((SeqUtil.getBarFromTick(mpcSequence, d2dRecorderGui.getTime0())) + notch, mpcSequence,
					d2dRecorderGui.getTime0()));
		}

		if (param.equals("time1")) {
			d2dRecorderGui.setTime0(SeqUtil.setBeat((SeqUtil.getBeat(mpcSequence, d2dRecorderGui.getTime0())) + notch, mpcSequence,
					d2dRecorderGui.getTime0()));
		}

		if (param.equals("time2")) {
			d2dRecorderGui.setTime0(SeqUtil.setClockNumber((SeqUtil.getClockNumber(mpcSequence, d2dRecorderGui.getTime0())) + notch,
					mpcSequence, d2dRecorderGui.getTime0()));
		}

		if (param.equals("time3")) {
			d2dRecorderGui.setTime1(SeqUtil.getTickFromBar((SeqUtil.getBarFromTick(mpcSequence, d2dRecorderGui.getTime1())) + notch, mpcSequence,
					d2dRecorderGui.getTime1()));
		}

		if (param.equals("time4")) {
			d2dRecorderGui.setTime1(SeqUtil.setBeat((SeqUtil.getBeat(mpcSequence, d2dRecorderGui.getTime1())) + notch, mpcSequence,
					d2dRecorderGui.getTime1()));
		}

		if (param.equals("time5")) {
			d2dRecorderGui.setTime1(SeqUtil.setClockNumber((SeqUtil.getClockNumber(mpcSequence, d2dRecorderGui.getTime1())) + notch,
					mpcSequence, d2dRecorderGui.getTime1()));
		}
	}
}
