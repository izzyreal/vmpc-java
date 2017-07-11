package com.mpc.disk;

import com.mpc.gui.Bootstrap;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Pad;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sound;

public class ProgramImportAdapter {

	private final Program outputProgram;
	private final int[] soundsDestIndex;

	public ProgramImportAdapter(Program inputProgram, int[] soundsDestIndex) {
		this.soundsDestIndex = soundsDestIndex;
		outputProgram = inputProgram;

		for (int i = 35; i <= 98; i++) {
			processNoteParameters(outputProgram.getNoteParameters(i));
			initMixer(i);
		}
		
	}

	private void processNoteParameters(NoteParameters np) {
		final int pgmSoundNumber = np.getSndNumber();
		if (pgmSoundNumber == -1) return;
		np.setSoundNumber(soundsDestIndex[pgmSoundNumber]);
	}

	private void initMixer(int note) {
		Sound sound = Bootstrap.getGui().getMpc().getSampler().getSound(outputProgram.getNoteParameters(note).getSndNumber());
		if (sound == null) return;
		Pad pad = outputProgram.getPad(outputProgram.getPadNumberFromNote(note));
		if (sound.isMono()) {
			pad.getMixerChannel().setStereo(false);
		} else {
			pad.getMixerChannel().setStereo(true);
		}
	}

	public Program get() {
		return outputProgram;
	}
}