package com.mpc.tootextensions;


public interface MpcNoteParameters  {
	
	int getSndNumber();

	int getTune();

	int getVoiceOverlap();
	
	int getAttack();
	
	int getDecay();
	
	int getDecayMode();

	int getVeloToLevel();

	int getFilterFrequency();

	int getFilterResonance();

	int getVelocityToAttack();

	int getVelocityToStart();

	int getVelocityToFilterFrequency();
	
	int getFilterAttack();

	int getFilterDecay();
	
	int getFilterEnvelopeAmount();

	int getSoundGenerationMode();

	int getVelocityRangeLower();

	int getVelocityRangeUpper();

	int getOptionalNoteA();

	int getOptionalNoteB();

	int getMuteAssignA();

	int getMuteAssignB();

	int getVelocityToPitch();

	int getSliderParameterNumber();
}
