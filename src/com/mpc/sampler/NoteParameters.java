package com.mpc.sampler;

import java.util.Observable;

import com.mpc.gui.Bootstrap;
import com.mpc.tootextensions.MpcNoteParameters;

public class NoteParameters extends Observable implements Cloneable, MpcNoteParameters {

	private int soundNumber;
	private int soundGenerationMode;
	private int velocityRangeLower;
	private int optionalNoteA;
	private int velocityRangeUpper;
	private int optionalNoteB;
	private int voiceOverlap;
	private int muteAssignA;
	private int muteAssignB;
	private int tune;
	private int attack;
	private int decay = 5;
	private int decayMode;
	private int filterFrequency = 100;
	private int filterResonance;
	private int filterAttack;
	private int filterDecay;
	private int filterEnvelopeAmount;
	private int velocityToLevel;
	private int velocityToAttack;
	private int velocityToStart;
	private int velocityToFilterFrequency;
	private int sliderParameterNumber;
	private int velocityToPitch;
	private int number;

	public NoteParameters(int number) {
		this.number = number;
		velocityRangeLower = 44;
		velocityRangeUpper = 88;
		velocityToLevel = 100;
		muteAssignA = 34;
		muteAssignB = 34;
	}

	@Override
	public int getSndNumber() {
		return soundNumber;
	}

	public void setSoundNumberNoLimit(int i) {
		soundNumber = i;
	}

	public void setSoundNumber(int i) {

		if (i < -1 || i != -1 && i > Bootstrap.getGui().getMpc().getSampler().getSoundCount() - 1) return;

		soundNumber = i;
		setChanged();
		notifyObservers("samplenumber");
	}

	public void setSoundGenMode(int i) {
		if (soundGenerationMode == i) return;
		if (i < 0 || i > 3) return;
		soundGenerationMode = i;
		setChanged();
		notifyObservers("soundgenerationmode");
	}

	@Override
	public int getSoundGenerationMode() {
		return soundGenerationMode;
	}

	public void setVeloRangeLower(int i) {

		if (velocityRangeLower == i) return;
		if (i < 0 || i > 126) return;
		if (i > velocityRangeUpper - 2) setVeloRangeUpper(i - 1);
		velocityRangeLower = i;
		setChanged();
		notifyObservers("ifover1");
	}

	@Override
	public int getVelocityRangeLower() {
		return velocityRangeLower;
	}

	public void setOptNoteA(int i) {
		if (optionalNoteA == i) return;
		if (i < 0 || i > 63) return;
		optionalNoteA = i;
		setChanged();
		notifyObservers("use1");
	}

	@Override
	public int getOptionalNoteA() {
		return optionalNoteA;
	}

	public void setVeloRangeUpper(int i) {
		if (velocityRangeUpper == i) return;
		if (i < velocityRangeLower + 1 || i > 127) return;
		velocityRangeUpper = i;
		setChanged();
		notifyObservers("ifover2");
	}

	@Override
	public int getVelocityRangeUpper() {
		return velocityRangeUpper;
	}

	public void setOptionalNoteB(int i) {
		if (optionalNoteB == i) return;
		if (i < 0 || i > 63) return;
		optionalNoteB = i;
		setChanged();
		notifyObservers("use2");
	}

	@Override
	public int getOptionalNoteB() {
		return optionalNoteB;
	}

	@Override
	public int getVoiceOverlap() {
		return voiceOverlap;
	}

	public void setVoiceOverlap(int i) {
		if (i < 0 || i > 2) return;
		voiceOverlap = i;
		setChanged();
		notifyObservers("voiceoverlap");
	}

	public void setMuteAssignA(int i) {
		if (i < 34 || i > 98) return;
		muteAssignA = i;
		setChanged();
		notifyObservers("muteassigna");
	}

	@Override
	public int getMuteAssignA() {
		return muteAssignA;
	}

	public void setMuteAssignB(int i) {
		if (i < 34 || i > 98) return;
		muteAssignB = i;
		setChanged();
		notifyObservers("muteassignb");
	}

	@Override
	public int getMuteAssignB() {
		return muteAssignB;
	}

	public void setTune(int i) {
		if (tune == i) return;
		if (i < -240 || i > 240) return;
		tune = i;
		setChanged();
		notifyObservers("tune");
	}

	@Override
	public int getTune() {
		return tune;
	}

	public void setAttack(int i) {
		if (attack == i) return;
		if (i < 0 || i > 100) return;
		attack = i;
		setChanged();
		notifyObservers("attack");
	}

	@Override
	public int getAttack() {
		return attack;
	}

	public void setDecay(int i) {
		if (decay == i) return;
		if (i < 0 || i > 100) return;
		decay = i;
		setChanged();
		notifyObservers("decay");
	}

	@Override
	public int getDecay() {
		return decay;
	}

	public void setDecayMode(int i) {
		if (decayMode == i) return;
		if (i < 0 || i > 1) return;
		decayMode = i;
		setChanged();
		notifyObservers("dcymd");
	}

	@Override
	public int getDecayMode() {
		return decayMode;
	}

	public void setFilterFrequency(int i) {
		if (filterFrequency == i) return;
		if (i < 0 || i > 100) return;
		filterFrequency = i;
		setChanged();
		notifyObservers("freq");
	}

	@Override
	public int getFilterFrequency() {
		return filterFrequency;
	}

	public void setFilterResonance(int i) {
		if (filterResonance == i) return;
		if (i < 0 || i > 15) return;
		filterResonance = i;
		setChanged();
		notifyObservers("reson");
	}

	@Override
	public int getFilterResonance() {
		return filterResonance;
	}

	public void setFilterAttack(int i) {
		if (filterAttack == i) return;
		if (i < 0 || i > 100) return;
		filterAttack = i;
		setChanged();
		notifyObservers("filterattack");
	}

	@Override
	public int getFilterAttack() {
		return filterAttack;
	}

	public void setFilterDecay(int i) {
		if (filterDecay == i) return;
		if (i < 0 || i > 100) return;
		filterDecay = i;
		setChanged();
		notifyObservers("filterdecay");
	}

	@Override
	public int getFilterDecay() {
		return filterDecay;
	}

	public void setFilterEnvelopeAmount(int i) {
		if (filterEnvelopeAmount == i) return;
		if (i < 0 || i > 100) return;
		filterEnvelopeAmount = i;
		setChanged();
		notifyObservers("filterenvelopeamount");
	}

	@Override
	public int getFilterEnvelopeAmount() {
		return filterEnvelopeAmount;
	}

	public void setVeloToLevel(int i) {
		if (velocityToLevel == i) return;
		if (i < 0 || i > 100) return;
		velocityToLevel = i;
		setChanged();
		notifyObservers("velocitytolevel");
	}

	@Override
	public int getVeloToLevel() {
		return velocityToLevel;
	}

	public void setVelocityToAttack(int i) {
		if (velocityToAttack == i) return;
		if (i < 0 || i > 100) return;
		velocityToAttack = i;
		setChanged();
		notifyObservers("velocitytoattack");
	}

	@Override
	public int getVelocityToAttack() {
		return velocityToAttack;
	}

	public void setVelocityToStart(int i) {
		if (velocityToStart == i) return;
		if (i < 0 || i > 100) return;
		velocityToStart = i;
		setChanged();
		notifyObservers("velocitytostart");
	}

	@Override
	public int getVelocityToStart() {
		return velocityToStart;
	}

	public void setVelocityToFilterFrequency(int i) {
		if (velocityToFilterFrequency == i) return;
		if (i < 0 || i > 100) return;
		velocityToFilterFrequency = i;
		setChanged();
		notifyObservers("velocitytofilterfrequency");
	}

	@Override
	public int getVelocityToFilterFrequency() {
		return velocityToFilterFrequency;
	}

	public void setSliderParameterNumber(int i) {
		if (sliderParameterNumber == i) return;
		if (i < 0 || i > 3) return;
		sliderParameterNumber = i;
		setChanged();
		notifyObservers("sliderparameternumber");
	}

	@Override
	public int getSliderParameterNumber() {
		return sliderParameterNumber;
	}

	public void setVelocityToPitch(int i) {
		if (velocityToPitch == i) return;
		if (i < -120 || i > 120) return;
		velocityToPitch = i;
		setChanged();
		notifyObservers("velocitytopitch");
	}

	@Override
	public int getVelocityToPitch() {
		return velocityToPitch;
	}

	@Override
	public NoteParameters clone() throws CloneNotSupportedException {
		return (NoteParameters) super.clone();
	}

	public int getNumber() {
		return number + 35;
	}
}