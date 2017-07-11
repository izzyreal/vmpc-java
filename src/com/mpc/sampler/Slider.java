package com.mpc.sampler;

import java.util.Observable;

import com.mpc.tootextensions.MpcSlider;

public class Slider extends Observable implements MpcSlider {

	private int assignNote = 34;
	private int tuneLowRange;
	private int tuneHighRange;
	private int decayLowRange;
	private int decayHighRange;
	private int attackLowRange;
	private int attackHighRange;
	private int filterLowRange;
	private int filterHighRange;
	private int controlChange;
	private int parameter;

	public void setAssignNote(int i) {

		if (i < 34 || i > 98) return;

		assignNote = i;

		setChanged();
		notifyObservers("assignnote");
	}

	@Override
	public int getNote() {
		return assignNote;
	}

	public void setTuneLowRange(int i) {

		if (i < -120 || i > 120) return;

		tuneLowRange = i;

		setChanged();
		notifyObservers("lowrange");

		if (tuneLowRange > tuneHighRange) setTuneHighRange(tuneLowRange);

	}

	@Override
	public int getTuneLowRange() {
		return tuneLowRange;
	}

	public void setTuneHighRange(int i) {

		if (i < -120 || i > 120) return;

		tuneHighRange = i;

		setChanged();
		notifyObservers("highrange");

		if (tuneHighRange < tuneLowRange) setTuneLowRange(tuneHighRange);

	}

	@Override
	public int getTuneHighRange() {
		return tuneHighRange;
	}

	public void setDecayLowRange(int i) {
		if (i < 0 || i > 100) return;
		decayLowRange = i;
		setChanged();
		notifyObservers("lowrange");
		if (decayLowRange > decayHighRange) setDecayHighRange(decayLowRange);

	}

	@Override
	public int getDecayLowRange() {
		return decayLowRange;
	}

	public void setDecayHighRange(int i) {
		if (i < 0 || i > 100) return;
		decayHighRange = i;
		setChanged();
		notifyObservers("highrange");
		if (decayHighRange < decayLowRange) setDecayLowRange(decayHighRange);
	}

	@Override
	public int getDecayHighRange() {
		return decayHighRange;
	}

	public void setAttackLowRange(int i) {
		if (i < 0 || i > 100) return;
		attackLowRange = i;
		setChanged();
		notifyObservers("lowrange");
		if (attackLowRange > attackHighRange) setAttackHighRange(attackLowRange);
	}

	@Override
	public int getAttackLowRange() {
		return attackLowRange;
	}

	public void setAttackHighRange(int i) {
		if (i < 0 || i > 100) return;
		attackHighRange = i;
		setChanged();
		notifyObservers("highrange");
		if (attackHighRange < attackLowRange) setAttackLowRange(attackHighRange);
	}

	@Override
	public int getAttackHighRange() {
		return attackHighRange;
	}

	public void setFilterLowRange(int i) {
		if (i < -50 || i > 50) return;
		filterLowRange = i;
		setChanged();
		notifyObservers("lowrange");
		if (filterLowRange > filterHighRange) setFilterHighRange(filterLowRange);
	}

	@Override
	public int getFilterLowRange() {
		return filterLowRange;
	}

	public void setFilterHighRange(int i) {
		if (i < -50 || i > 50) return;
		filterHighRange = i;
		setChanged();
		notifyObservers("highrange");
		if (filterHighRange < filterLowRange) setFilterLowRange(filterHighRange);
	}

	@Override
	public int getFilterHighRange() {
		return filterHighRange;
	}

	public void setControlChange(int i) {
		if (i < 0 || i > 128) return;
		controlChange = i;
		setChanged();
		notifyObservers("controlchange");
	}

	@Override
	public int getControlChange() {
		return controlChange;
	}

	@Override
	public int getParameter() {
		return parameter;
	}

	public void setParameter(int i) {
		if (i < 0 || i > 3) return;
		parameter = i;
		setChanged();
		notifyObservers("parameter");
	}
}
