package com.mpc.file.aps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mpc.tootextensions.MpcNoteParameters;

class ApsNoteParameters {

	int soundNumber;
	int soundGenerationMode;
	int velocityRangeLower;
	int alsoPlay1;
	int velocityRangeUpper;
	int alsoPlay2;
	int voiceOverlap;
	int mute1;
	int mute2;
	int tune;
	int attack;
	int decay;
	int decayMode;
	int cutoffFrequency;
	int resonance;
	int filterAttack;
	int filterDecay;
	int filterEnvelopeAmount;
	int velocityToLevel;
	int velocityToAttack;
	int velocityToStart;
	int velocityToFilterFrequency;
	int sliderParameter;
	int velocityToPitch;

	final byte[] saveBytes = new byte[26];
	
	ApsNoteParameters(byte[] loadBytes) {

		soundNumber = (loadBytes[0] & 0xFF) == 255 ? -1 : (loadBytes[0] & 0xFF); // bytes[1] is also soundnumber... no idea why
		soundGenerationMode = loadBytes[2] & 0xFF;
		velocityRangeLower = loadBytes[3] & 0xFF;
		alsoPlay1 = loadBytes[4] & 0xFF;
		velocityRangeUpper = loadBytes[5] & 0xFF;
		alsoPlay2 = loadBytes[6] & 0xFF;
		voiceOverlap = loadBytes[7] & 0xFF;
		mute1 = loadBytes[8] & 0xFF;
		mute2 = loadBytes[9] & 0xFF;
		ByteBuffer buf = ByteBuffer.wrap(new byte[] { loadBytes[10], loadBytes[11] });
		buf.order(ByteOrder.LITTLE_ENDIAN);
		tune = (short) buf.getShort();
		attack = loadBytes[12] & 0xFF;
		decay = loadBytes[13] & 0xFF;
		decayMode = loadBytes[14] & 0xFF;
		cutoffFrequency = loadBytes[15] & 0xFF;
		resonance = loadBytes[16] & 0xFF;
		filterAttack = loadBytes[17] & 0xFF;
		filterDecay = loadBytes[18] & 0xFF;
		filterEnvelopeAmount = loadBytes[19] & 0xFF;
		velocityToLevel = loadBytes[20] & 0xFF;
		velocityToAttack = loadBytes[21] & 0xFF;
		velocityToStart = loadBytes[22] & 0xFF;
		velocityToFilterFrequency = loadBytes[23] & 0xFF;
		sliderParameter = loadBytes[24] & 0xFF;
		velocityToPitch = loadBytes[25];

	}

	ApsNoteParameters(MpcNoteParameters np) {
		saveBytes[0] = (byte) (np.getSndNumber() == -1 ? 255 : np.getSndNumber());
		saveBytes[1] = saveBytes[0];
		saveBytes[2] = (byte) np.getSoundGenerationMode();
		saveBytes[3] = (byte) np.getVelocityRangeLower();
		saveBytes[4] = (byte) np.getOptionalNoteA();
		saveBytes[5] = (byte) np.getVelocityRangeUpper();
		saveBytes[6] = (byte) np.getOptionalNoteB();
		saveBytes[7] = (byte) np.getVoiceOverlap();
		saveBytes[8] = (byte) np.getMuteAssignA();
		saveBytes[9] = (byte) np.getMuteAssignA();
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putShort((short)np.getTune());
		buf.rewind();
		saveBytes[10] = buf.get();
		saveBytes[11] = buf.get();
		saveBytes[12] = (byte) np.getAttack();
		saveBytes[13] = (byte) np.getDecay();
		saveBytes[14] = (byte) np.getDecayMode();
		saveBytes[15] = (byte) np.getFilterFrequency();
		saveBytes[16] = (byte) np.getFilterResonance();
		saveBytes[17] = (byte) np.getFilterAttack();
		saveBytes[18] = (byte) np.getFilterDecay();
		saveBytes[19] = (byte) np.getFilterEnvelopeAmount();
		saveBytes[20] = (byte) np.getVeloToLevel();
		saveBytes[21] = (byte) np.getVelocityToAttack();
		saveBytes[22] = (byte) np.getVelocityToStart();
		saveBytes[23] = (byte) np.getVelocityToFilterFrequency();
		saveBytes[24] = (byte) np.getSliderParameterNumber();
		saveBytes[25] = (byte) (np.getVelocityToPitch() & 0xFF);
	}
	
	int getSoundNumber() {
		return soundNumber;
	}

	int getVoiceOverlap() {
		return voiceOverlap;
	}

	int getTune() {
		return tune;
	}

	int getDecayMode() {
		return decayMode;
	}

	int getSoundGenerationMode() {
		return soundGenerationMode;
	}

	int getVelocityRangeLower() {
		return velocityRangeLower;
	}

	int getAlsoPlay1() {
		return alsoPlay1;
	}

	int getVelocityRangeUpper() {
		return velocityRangeUpper;
	}

	int getAlsoPlay2() {
		return alsoPlay2;
	}

	int getMute1() {
		return mute1;
	}

	int getMute2() {
		return mute2;
	}

	int getAttack() {
		return attack;
	}

	int getDecay() {
		return decay;
	}

	int getCutoffFrequency() {
		return cutoffFrequency;
	}

	int getResonance() {
		return resonance;
	}

	int getVelocityToFilterAttack() {
		return filterAttack;
	}

	int getVelocityToFilterDecay() {
		return filterDecay;
	}

	int getVelocityToFilterAmount() {
		return filterEnvelopeAmount;
	}

	int getVelocityToLevel() {
		return velocityToLevel;
	}

	int getVelocityToAttack() {
		return velocityToAttack;
	}

	int getVelocityToStart() {
		return velocityToStart;
	}

	int getVelocityToFilterFrequency() {
		return velocityToFilterFrequency;
	}

	int getSliderParameter() {
		return sliderParameter;
	}

	int getVelocityToPitch() {
		return velocityToPitch;
	}

	byte[] getBytes() {
		return saveBytes;
	}

}