package com.mpc.file.pgmwriter;

import com.mpc.sampler.Pad;
import com.mpc.sampler.Program;

public class Mixer {

	protected char[] mixerArray;

	public Mixer(Program program) {

		mixerArray = new char[384 + 3];

		for (int i = 0; i < 64; i++) {

			Pad pad = program.getPad(program.getPadNumberFromNote(i+35));			
						
			setVolume(i, pad.getMixerChannel().getLevel());

			setPan(i, pad.getMixerChannel().getPanning());

			setVolumeIndividual(i, pad.getMixerChannel()
					.getVolumeIndividualOut());

			setOutput(i, pad.getMixerChannel().getOutput());

			setEffectsSendLevel(i, pad.getMixerChannel()
					.getFxSendLevel());

			setEffectsOutput(i, pad.getMixerChannel().getFxPath());

		}

		mixerArray[384] = 0x00;
		mixerArray[385] = 0x40;
		mixerArray[386] = 0x00;

	}

	char[] getMixerArray() {
		return mixerArray;
	}

	private void setEffectsOutput(int pad, int effectsOutputNumber) {
		mixerArray[(pad * 6) + 0] = (char) effectsOutputNumber;
	}

	private void setVolume(int pad, int volume) {
		mixerArray[(pad * 6) + 1] = (char) volume;
	}

	private void setPan(int pad, int pan) {
		mixerArray[(pad * 6) + 2] = (char) pan;
	}

	private void setVolumeIndividual(int pad, int volumeIndividual) {
		mixerArray[(pad * 6) + 3] = (char) volumeIndividual;
	}

	private void setOutput(int pad, int output) {
		mixerArray[(pad * 6) + 4] = (char) output;
	}

	private void setEffectsSendLevel(int pad, int effectsSendLevel) {
		mixerArray[(pad * 6) + 5] = (char) effectsSendLevel;
	}

}