package com.mpc.sampler;

import java.util.Observable;

import com.mpc.gui.Bootstrap;
import com.mpc.tootextensions.MpcSoundOscillatorControls;
import com.mpc.tootextensions.MpcSoundOscillatorVariables;

public class Sound extends Observable implements MpcSoundOscillatorVariables {

	private String name;

	private int memoryIndex = -1;
	private int numberOfBeats;

	private MpcSoundOscillatorControls msoc;

	public Sound(int rate) {
		memoryIndex = Bootstrap.getGui().getMpc().getSampler().getSounds()
				.size();
		numberOfBeats = 4;

		msoc = new MpcSoundOscillatorControls(memoryIndex, name,
				0);
	}
	
	public Sound() {
		msoc = new MpcSoundOscillatorControls(-1, "click", 0);
	}

	public void setName(String s) {
		name = s;
		setChanged();
		notifyObservers("samplename");
	}

	protected int getMemoryIndex() {
		return memoryIndex;
	}

	public void setMemoryIndex(int i) {
		memoryIndex = i;
	}

	public void setNumberOfBeats(int i) {
		if (i < 1 || i > 32)
			return;
		numberOfBeats = i;
		setChanged();
		notifyObservers("beat");
	}

	public int getBeatCount() {
		return numberOfBeats;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float[] getSampleData() {
		return msoc.getSampleData();
	}

	@Override
	public int getSndLevel() {
		return msoc.getSndLevel();
	}

	@Override
	public int getTune() {
		return msoc.getTune();
	}

	@Override
	public int getStart() {
		return msoc.getStart();
	}

	@Override
	public int getEnd() {
		return msoc.getEnd();
	}

	@Override
	public boolean isLoopEnabled() {
		return msoc.isLoopEnabled();
	}

	@Override
	public int getLoopTo() {
		return msoc.getLoopTo();
	}

	@Override
	public boolean isMono() {
		return msoc.isMono();
	}

	@Override
	public float getRateConversion() {
		return msoc.getRateConversion();
	}

	@Override
	public float[] getSampleDataLeft() {
		return msoc.getSampleDataLeft();
	}

	@Override
	public float[] getSampleDataRight() {
		return msoc.getSampleDataRight();
	}

	@Override
	public int getSampleRate() {
		return msoc.getSampleRate();
	}

	@Override
	public int getLastFrameIndex() {
		return msoc.getLastFrameIndex();
	}

	public void setSampleData(float[] newSampleData) {
		msoc.setSampleData(newSampleData);
	}

	public void setMono(boolean mono) {
		msoc.setMono(mono);
	}

	public void setEnd(int newLength) {
		msoc.setEnd(newLength);
	}

	public void setLevel(int level) {
		msoc.setSndLevel(level);
	}

	public void setStart(int start) {
		msoc.setStart(start);
	}

	public void setLoopEnabled(boolean loopEnabled) {
		msoc.setLoopEnabled(loopEnabled);
	}

	public void setLoopTo(int loopTo) {
		msoc.setLoopTo(loopTo);
	}

	public void setTune(int tune) {
		msoc.setTune(tune);
	}
	
	public MpcSoundOscillatorControls getMsoc() {
		return msoc;
	}
}