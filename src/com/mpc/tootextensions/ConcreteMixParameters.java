package com.mpc.tootextensions;

public class ConcreteMixParameters implements MpcMixParameters {

	private int panning = 50;
	private int level = 100;
	private int fxPath = 0;
	private int volumeIndividualOut = 100;
	private int output = 0;
	private int fxSendLevel = 0;

	@Override
	public int getPanning() {
		return panning;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getFxPath() {
		return fxPath;
	}

	@Override
	public int getVolumeIndividualOut() {
		return volumeIndividualOut;
	}

	@Override
	public int getOutput() {
		return output;
	}

	@Override
	public int getFxSendLevel() {
		return fxSendLevel;
	}

	@Override
	public void setFxPath(int i) {
		fxPath = i;
	}

	@Override
	public void setLevel(int i) {
		level = i;
	}

	@Override
	public void setPanning(int i) {
		panning = i;
	}

	@Override
	public void setVolumeIndividualOut(int i) {
		volumeIndividualOut = i;
	}

	@Override
	public void setOutput(int i) {
		output = i;
	}

	@Override
	public void setFxSendLevel(int i) {
		fxSendLevel = i;
	}
}