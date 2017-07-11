package com.mpc.tootextensions;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioBuffer.MetaInfo;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioProcessAdapter;

public class MpcInputAdapter extends AudioProcessAdapter {

	private AudioProcess audioProcess;

	public MpcInputAdapter(AudioProcess process) {
		super(process);
		this.audioProcess = process;
	}

	@Override
	public int processAudio(AudioBuffer buf) {
		buf.setMetaInfo(new MetaInfo("export", "adapter"));
		return super.processAudio(buf);
	}

	public AudioProcess getProcess() {
		return audioProcess;
	}
	
}
