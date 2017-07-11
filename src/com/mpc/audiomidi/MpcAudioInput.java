package com.mpc.audiomidi;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.system.AudioInput;

public class MpcAudioInput implements AudioInput {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void open() throws Exception {		
	}

	@Override
	public int processAudio(AudioBuffer buffer) {
		return 0;
	}

	@Override
	public void close() throws Exception {
	}

}
