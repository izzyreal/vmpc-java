package com.mpc.file.pgmwriter;

import java.util.List;

import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;

public class MidiNotes {

	protected final char[] midiNotesArray;

	public MidiNotes(Program program, List<Integer> snConvTable) {

		midiNotesArray = new char[1601];

		for (int i = 0; i < 64; i++) {
			
			NoteParameters nn = program.getNoteParameters(i+35);

			if (nn.getSndNumber() == -1) {
				setSampleSelect(i, 255);
			} else {
				setSampleSelect(i, snConvTable.get(nn.getSndNumber()).intValue());
			}
	
			setSoundGenerationMode(i, nn.getSoundGenerationMode());

			setVelocityRangeLower(i, nn.getVelocityRangeLower());

			setAlsoPlayUse1(i, nn.getOptionalNoteA());

			setVelocityRangeUpper(i, nn.getVelocityRangeUpper());

			setAlsoPlayUse2(i, nn.getOptionalNoteB());

			setVoiceOverlap(i, nn.getVoiceOverlap());

			setMuteAssign1(i, nn.getMuteAssignA());

			setMuteAssign2(i, nn.getMuteAssignB());

			setTune(i, (short) (nn.getTune()));

			setAttack(i, nn.getAttack());

			setDecay(i, nn.getDecay());

			setDecayMode(i, nn.getDecayMode());

			setCutoff(i, nn.getFilterFrequency());

			setResonance(i, nn.getFilterResonance());

			setVelEnvToFiltAtt(i, nn.getFilterAttack());

			setVelEnvToFiltDec(i, nn.getFilterDecay());

			setVelEnvToFiltAmt(i, nn.getFilterEnvelopeAmount());

			setVelocityToLevel(i, nn.getVeloToLevel());

			setVelocityToAttack(i, nn.getVelocityToAttack());

			setVelocityToStart(i, nn.getVelocityToStart());

			setVelocityToCutoff(i, nn.getVelocityToFilterFrequency());

			setSliderParameter(i, nn.getSliderParameterNumber());

			setVelocityToPitch(i, nn.getVelocityToPitch());

		}

		midiNotesArray[1600] = 0x06;
	}

	char[] getMidiNotesArray() {
		return midiNotesArray;
	}

	private void setSampleSelect(int midiNote, int sampleNumber) {
		char sampleSelect = (char) sampleNumber;
		midiNotesArray[((midiNote) * 25) + 0] = sampleSelect;
	}

	private void setSoundGenerationMode(int midiNote, int soundGenerationMode) {
		midiNotesArray[((midiNote) * 25) + 1] = (char) soundGenerationMode;
	}

	private void setVelocityRangeLower(int midiNote, int velocityRangeLower) {
		midiNotesArray[((midiNote) * 25) + 2] = (char) velocityRangeLower;
	}

	private void setAlsoPlayUse1(int midiNote, int alsoPlayUse1) {
		midiNotesArray[((midiNote) * 25) + 3] = (char) alsoPlayUse1;
	}

	private void setVelocityRangeUpper(int midiNote, int velocityRangeUpper) {
		midiNotesArray[((midiNote) * 25) + 4] = (char) velocityRangeUpper;
	}

	private void setAlsoPlayUse2(int midiNote, int alsoPlayUse2) {
		midiNotesArray[((midiNote) * 25) + 5] = (char) alsoPlayUse2;
	}

	private void setVoiceOverlap(int midiNote, int voiceOverlap) {
		midiNotesArray[((midiNote) * 25) + 6] = (char) voiceOverlap;
	}

	private void setMuteAssign1(int midiNote, int muteAssign1) {
		midiNotesArray[((midiNote) * 25) + 7] = (char) muteAssign1;
	}

	private void setMuteAssign2(int midiNote, int muteAssign2) {
		midiNotesArray[((midiNote) * 25) + 8] = (char) muteAssign2;
	}

	private void setTune(int midiNote, short tune) {
		int startPos = ((midiNote) * 25) + 9;
		setShort(midiNotesArray, startPos, tune);
	}

	private void setAttack(int midiNote, int attack) {
		midiNotesArray[((midiNote) * 25) + 11] = (char) attack;
	}

	private void setDecay(int midiNote, int decay) {
		midiNotesArray[((midiNote) * 25) + 12] = (char) decay;
	}

	private void setDecayMode(int midiNote, int decayMode) {
		midiNotesArray[((midiNote) * 25) + 13] = (char) decayMode;
	}

	private void setCutoff(int midiNote, int cutoff) {
		midiNotesArray[((midiNote) * 25) + 14] = (char) cutoff;
	}

	private void setResonance(int midiNote, int resonance) {
		midiNotesArray[((midiNote) * 25) + 15] = (char) resonance;
	}

	private void setVelEnvToFiltAtt(int midiNote, int velEnvToFiltAtt) {
		midiNotesArray[((midiNote) * 25) + 16] = (char) velEnvToFiltAtt;
	}

	private void setVelEnvToFiltDec(int midiNote, int velEnvToFiltDec) {
		midiNotesArray[((midiNote) * 25) + 17] = (char) velEnvToFiltDec;
	}

	private void setVelEnvToFiltAmt(int midiNote, int velEnvToFiltAmt) {
		midiNotesArray[((midiNote) * 25) + 18] = (char) velEnvToFiltAmt;
	}

	private void setVelocityToLevel(int midiNote, int velocityToLevel) {
		midiNotesArray[((midiNote) * 25) + 19] = (char) velocityToLevel;
	}

	private void setVelocityToAttack(int midiNote, int velocityToAttack) {
		midiNotesArray[((midiNote) * 25) + 20] = (char) velocityToAttack;
	}

	private void setVelocityToStart(int midiNote, int velocityToStart) {
		midiNotesArray[((midiNote) * 25) + 21] = (char) velocityToStart;
	}

	private void setVelocityToCutoff(int midiNote, int velocityToCutoff) {
		midiNotesArray[((midiNote) * 25) + 22] = (char) velocityToCutoff;
	}

	private void setSliderParameter(int midiNote, int sliderParameter) {
		midiNotesArray[((midiNote) * 25) + 23] = (char) sliderParameter;
	}

	private void setVelocityToPitch(int midiNote, int velocityToPitch) {
		midiNotesArray[((midiNote) * 25) + 24] = (char) velocityToPitch;
	}

	private char[] setShort(char[] ca, int offset, short s) {
		ca[offset] = (char) (s & 0xff);
		ca[offset + 1] = (char) ((s >> 8) & 0xff);
		return ca;
	}
}