package com.mpc.tootextensions;

public interface MpcMixParameters {

	int getFxPath();

	int getLevel();	
	
	int getPanning();
	
	int getVolumeIndividualOut();
	
	int getOutput();
	
	int getFxSendLevel();
	
	void setFxPath(int i);
	
	void setLevel(int i);
	
	void setPanning(int i);
	
	void setVolumeIndividualOut(int i);
	
	void setOutput(int i);

	void setFxSendLevel(int i);
	
}
