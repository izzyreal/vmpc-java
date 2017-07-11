package com.mpc.tootextensions;

import uk.org.toot.control.CompoundControl;

public class MpcSoundOscillatorControls extends CompoundControl implements MpcSoundOscillatorVariables {

	private boolean mono, loopEnabled;

	private int sampleRate;
	private int sndLevel, tune;
	private int start, end, loopTo;

	private float[] sampleData;
	private float[] sampleDataLeft;
	private float[] sampleDataRight;

	public MpcSoundOscillatorControls(int id, String name, int instanceIndex) {
		super(id, instanceIndex, name);
		sampleRate = 44100;
		mono = false;
		sndLevel = 100;
		tune = 0;
	}

	public void setSndLevel(int i) {
		if (i < 0 || i > 200) return;
		sndLevel = i;
		setChanged();
		notifyObservers("level");
	}

	public void setTune(int i) {
		if (i < -120 || i > 120) return;
		tune = i;
		setChanged();
		notifyObservers("tune");
	}

	public void setLoopEnabled(boolean b) {
		loopEnabled = b;
		setChanged();
		notifyObservers("loopenabled");
	}

	public void setStart(int i) {
		int value = i;
		if (value < 0) {
			if (start == 0) return;
			value = 0;
		}
		if (value >= getLastFrameIndex()) {
			if (start == getLastFrameIndex()) return;
			value = getLastFrameIndex();
		}
		start = value;
		if (start > end) setEnd(start);
		setChanged();
		notifyObservers("start");
	}

	public void setEnd(int i) {
		int value = i;
		if (value < 0) {
			if (end == 0) return;
			value = 0;
		}
		if (value > getLastFrameIndex()) {
			if (end == getLastFrameIndex()) return;
			value = getLastFrameIndex();
		}
		end = value;
		if (end < loopTo) setLoopTo(end);
		if (end < start) setStart(end);
		setChanged();
		notifyObservers("end");
	}

	public void setMono(boolean b) {
		mono = b;
		end = getLastFrameIndex() + 1;
		loopTo = end;
	}

	public void setSampleData(float[] fa) {
		sampleData = fa;
		sampleDataLeft = new float[sampleData.length / 2];
		sampleDataRight = new float[sampleData.length / 2];
		int k = 0;
		for (int i = 0; i < sampleDataLeft.length; i++) {
			sampleDataLeft[i] = sampleData[k++];
			sampleDataRight[i] = sampleData[k++];
		}
	}

	public void setLoopTo(int i) {
		if (i < 0 || i > getLastFrameIndex()) return;
		loopTo = i;
		if (loopTo > end) setEnd(loopTo);
		setChanged();
		notifyObservers("loopto");
	}

	@Override
	public int getLastFrameIndex() {
		return (isMono() ? sampleData.length : (sampleData.length / 2)) - 1;
	}

	@Override
	public int getTune() {
		return tune;
	}

	@Override
	public boolean isLoopEnabled() {
		return loopEnabled;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public float[] getSampleData() {
		return sampleData;
	}

	@Override
	public boolean isMono() {
		return mono;
	}

	@Override
	public float getRateConversion() {
		return (float) (sampleRate / 44100.0);
	}

	@Override
	public int getLoopTo() {
		return loopTo;
	}

	@Override
	public float[] getSampleDataLeft() {
		if (mono) return null;
		return sampleDataLeft;
	}

	@Override
	public float[] getSampleDataRight() {
		if (mono) return null;
		return sampleDataRight;
	}

	@Override
	public int getSampleRate() {
		return sampleRate;
	}

	@Override
	public int getSndLevel() {
		return sndLevel;
	}
}
