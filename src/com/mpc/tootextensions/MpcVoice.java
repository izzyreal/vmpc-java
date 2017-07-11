package com.mpc.tootextensions;

import uk.org.toot.audio.core.AudioProcess;

public interface MpcVoice extends AudioProcess {

	int getStripNumber();

	void init(int track, int velo, int padNumber, MpcSoundOscillatorVariables vars, MpcNoteParameters np, int varType,
			int varValue, int muteNote, int muteDrum, int framePos, boolean enableEnvs);

	MuteInfo getMuteInfo();

	void startDecay();

	boolean isDecaying();

	int getPadNumber();

	int getVoiceOverlap();

	void setParent(MpcSoundPlayerChannel mpcSoundPlayerChannel);

	void startDecay(int offset);

}
