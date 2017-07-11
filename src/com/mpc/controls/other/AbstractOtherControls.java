package com.mpc.controls.other;

import com.mpc.Mpc;
import com.mpc.controls.AbstractControls;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.NameGui;
import com.mpc.gui.UserDefaults;
import com.mpc.gui.disk.window.DirectoryGui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.MpcTrack;

public abstract class AbstractOtherControls extends AbstractControls {

	protected int[] tickValues = Sequencer.tickValues;

	protected char[] akaiAsciiChar = Mpc.akaiAsciiChar;

	protected Gui gui;
	protected SequencerWindowGui swGui;
	protected UserDefaults ud;

	protected NameGui nameGui;
	protected DirectoryGui directoryGui;
	protected Sampler sampler;
	protected Sequencer sequencer;
	protected MpcSequence mpcSequence;
	protected MpcTrack track;
	protected int trackNum;
	protected int seqNum;
	protected Program program;

	protected void init() {
		super.init();
		ud = Bootstrap.getUserDefaults();
		tickValues = Sequencer.tickValues;
		akaiAsciiChar = Mpc.akaiAsciiChar;
		gui = Bootstrap.getGui();
		swGui = gui.getSequencerWindowGui();
		nameGui = gui.getNameGui();
		directoryGui = gui.getDirectoryGui();
		sampler = mpc.getSampler();
		sequencer = mpc.getSequencer();
		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		program = track
				.getBusNumber() == 0 ? null : sampler.getProgram(sampler.getDrumBusProgramNumber(track
				.getBusNumber()));

	}
}
