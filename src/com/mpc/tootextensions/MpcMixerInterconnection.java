package com.mpc.tootextensions;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.SimpleAudioProcess;
import uk.org.toot.audio.mixer.MixerInterconnection;
import uk.org.toot.audio.server.AudioServer;

public class MpcMixerInterconnection implements MixerInterconnection {
	AudioProcess inputProcess;
	AudioProcess outputProcess;
	private boolean leftEnabled;
	private boolean rightEnabled;

	MpcMixerInterconnection(String name, AudioServer server) {
		final AudioBuffer sharedBuffer = server.createAudioBuffer(name);
		inputProcess = new SimpleAudioProcess() {
			public int processAudio(AudioBuffer buffer) {
				sharedBuffer.copyFrom(buffer);
				if (!leftEnabled) sharedBuffer.makeSilence(0);
				if (!rightEnabled) sharedBuffer.makeSilence(1);
				return AUDIO_OK;
			}
		};
		outputProcess = new SimpleAudioProcess() {
			public int processAudio(AudioBuffer buffer) {
				buffer.copyFrom(sharedBuffer);
				return AUDIO_OK;
			}
		};
	}

	@Override
	public AudioProcess getInputProcess() {
		return inputProcess;
	}

	@Override
	public AudioProcess getOutputProcess() {
		return outputProcess;
	}

	public void setLeftEnabled(boolean b) {
		leftEnabled = b;
	}

	public void setRightEnabled(boolean b) {
		rightEnabled = b;
	}

}
