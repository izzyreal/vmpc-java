/* Generated by Together */

package com.mpc.sampler;

import java.util.Observable;

import com.mpc.tootextensions.MpcMixParameters;

public class MixerChannel extends Observable implements MpcMixParameters {
    private int output;
    private int volumeIndividualOut;
    private int panning;
    private int level;
    private int fxPath;
	private int fxSendLevel;
	private boolean followStereo;
	
	private boolean stereo;
	
	public MixerChannel() {
		output = 0;
		volumeIndividualOut = 100;
		panning = 50;
		level = 100;
		fxPath = 0;
		fxSendLevel = 0;
		followStereo = false;
		
		stereo = true;
	}
	
	public void setFollowStereo(boolean b) {
		if (followStereo == b) return;
		followStereo = b;
		setChanged();
		notifyObservers("followstereo");
	}
	
	public boolean isFollowingStereo() {
		return followStereo;
	}
	
	public void setStereo(boolean b) {
		stereo = b;
	}
	
	public boolean isStereo() {
		return stereo;
	}
	
    public int getOutput() {
		return output;
    }

    public void setOutput(int i) {
    	if (i<0||i>8) return;
		output = i;
		setChanged();
		notifyObservers("output");
	}

    public void setVolumeIndividualOut(int i) {
    	if (i<0||i>100) return;
    	volumeIndividualOut = i;
    	setChanged();
    	notifyObservers("volumeindividual");
    }

    public int getVolumeIndividualOut() {
		return volumeIndividualOut;
    }

    public void setPanning(int i) {
    	if (i<0||i>100) return;
    	panning = i;
    	setChanged();
    	notifyObservers("panning");
    }

    @Override
    public int getPanning() {
		return panning;
    }

    public void setLevel(int i) {
    	if (i<0||i>100) return;
    	level = i;
    	setChanged();
    	notifyObservers("volume");
    }

    @Override
    public int getLevel() {
		return level;
    }

    public void setFxPath(int i) {
    	if (i<0||i>4) return;
    	fxPath = i;
    	setChanged();
    	notifyObservers("fxpath");
    }

    public int getFxPath() {
		return fxPath;
    }

    public void setFxSendLevel(int i) {
    	if (i<0||i>100) return;
    	fxSendLevel = i;
    	setChanged();
    	notifyObservers("fxsendlevel");
    }

    public int getFxSendLevel() {
		return fxSendLevel;
    }
}
