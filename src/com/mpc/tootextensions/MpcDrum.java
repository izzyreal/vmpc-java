package com.mpc.tootextensions;

public interface MpcDrum {

	void setProgram(int p);

	int getProgram();

	boolean receivesPgmChange();

	void setReceivePgmChange(boolean b);

	boolean receivesMidiVolume();

	void setReceiveMidiVolume(boolean b);

	void kill(MpcVoice mpcVoice);

}
