package com.mpc.command;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sampler.MixerChannel;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Pad;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sound;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.MpcTrack;

public class KeepSound {

	private Mpc mpc;
	private Sequencer sequencer;
	private MpcSequence mpcSequence;
	private MpcTrack track;
	private Sampler sampler;
	private Sound sound;

	private SamplerGui samplerGui;

	public KeepSound(Mpc mpc, Sound s) {
		this.mpc = mpc;
		sound = s;
		samplerGui = Bootstrap.getGui().getSamplerGui();
	}

	public void execute() {
		sequencer = mpc.getSequencer();
		mpcSequence = sequencer.getActiveSequence();

		sampler = mpc.getSampler();
		track = (com.mpc.sequencer.MpcTrack) mpcSequence.getTrack(sequencer.getActiveTrackIndex());
		sampler.getSounds().add(sound);

		int bus = track.getBusNumber();

		int programNumber = sampler.getDrumBusProgramNumber(bus);
		Program program = sampler.getProgram(programNumber);

		if (samplerGui.getNote() != 34) {
			NoteParameters noteParameters = program.getNoteParameters(samplerGui.getNote());
			noteParameters.setSoundNumber(sampler.getSoundCount() - 1);
			if (sound.isLoopEnabled()) noteParameters.setVoiceOverlap(2);

			int pn = program.getPadNumberFromNote(samplerGui.getNote());

			if (pn != -1) {

				Pad pad = program.getPad(pn);

				MixerChannel mixerChannel = pad.getMixerChannel();

				if (sampler.getSound(sampler.getSoundCount() - 1).isMono()) {
					mixerChannel.setStereo(false);
				} else {
					mixerChannel.setStereo(true);
				}
			}
		}

		Bootstrap.getGui().getSoundGui()
				.initZones(sampler.getSound(Bootstrap.getGui().getSoundGui().getSoundIndex()).getLastFrameIndex() + 1);
	}
}