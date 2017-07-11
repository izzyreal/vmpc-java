package com.mpc.file.pgmreader;

import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PgmAllNoteParameters {

	int sampleNamesSize = 0;

	protected static byte[] midiNotesArray;

	int padNumber = 0;

	private ProgramFileReader programFile;

	public PgmAllNoteParameters(ProgramFileReader programFile) {
		this.programFile = programFile;
	}

	protected int getPadNumber(int midiNote) {
		Pads pds = programFile.getPads();
		for (int pad = 0; pad < 64; pad++) {
			if (midiNote == (pds.getNote(pad))) {
				padNumber = pad;
			}
		}
		return padNumber;
	}

	protected int getSampleNamesSize() {
		sampleNamesSize = programFile.getSampleNames().getSampleNamesSize();
		return sampleNamesSize;
	}

	protected int getMidiNotesStart() {
		int midiNotesStart = 4 + getSampleNamesSize() + 2 + 17 + 9 + 6;
		return midiNotesStart;
	}

	protected int getMidiNotesEnd() {
		int midiNotesEnd = 4 + getSampleNamesSize() + 2 + 17 + 9 + 6 + 1601;
		return midiNotesEnd;
	}

	protected byte[] getMidiNotesArray() {
		midiNotesArray = Arrays.copyOfRange(programFile.readProgramFileArray(),
				getMidiNotesStart(), getMidiNotesEnd());
		return midiNotesArray;
	}

	public int getSampleSelect(int midiNote) {
		byte sampleSelect = getMidiNotesArray()[(midiNote * 25) + 0];
		int unsignedInt = getUnsignedInt(sampleSelect);
		return unsignedInt;
	}

	public byte getSoundGenerationMode(int midiNote) {
		byte soundGenerationMode = getMidiNotesArray()[(midiNote * 25) + 1];
		return soundGenerationMode;
	}

	public byte getVelocityRangeLower(int midiNote) {
		byte velocityRangeLower = getMidiNotesArray()[(midiNote * 25) + 2];
		return velocityRangeLower;
	}

	public byte getAlsoPlayUse1(int midiNote) {
		byte alsoPlayUse1 = getMidiNotesArray()[(midiNote * 25) + 3];
		return alsoPlayUse1;
	}

	public byte getVelocityRangeUpper(int midiNote) {
		byte velocityRangeUpper = getMidiNotesArray()[(midiNote * 25) + 4];
		return velocityRangeUpper;
	}

	public byte getAlsoPlayUse2(int midiNote) {
		byte alsoPlayUse2 = getMidiNotesArray()[(midiNote * 25) + 5];
		return alsoPlayUse2;
	}

	public byte getVoiceOverlap(int midiNote) {
		byte voiceOverlap = getMidiNotesArray()[(midiNote * 25) + 6];
		return voiceOverlap;
	}

	public byte getMuteAssign1(int midiNote) {
		byte muteAssign1 = getMidiNotesArray()[(midiNote * 25) + 7];
		return muteAssign1;
	}

	public byte getMuteAssign2(int midiNote) {
		byte muteAssign2 = getMidiNotesArray()[(midiNote * 25) + 8];
		return muteAssign2;
	}

	public short getTune(int midiNote) {
		byte[] midiNotesArray = getMidiNotesArray();
		int startPos = (midiNote * 25) + 9;
		int endPos = (midiNote * 25) + 11;
		byte[] tuneBytes = Arrays.copyOfRange(midiNotesArray, startPos, endPos);
		ByteBuffer buf = ByteBuffer.wrap(tuneBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		short tune = buf.getShort();
		return tune;
	}

	public byte getAttack(int midiNote) {
		byte attack = getMidiNotesArray()[(midiNote * 25) + 11];
		return attack;
	}

	public byte getDecay(int midiNote) {
		byte decay = getMidiNotesArray()[(midiNote * 25) + 12];
		return decay;
	}

	public byte getDecayMode(int midiNote) {
		byte decayMode = getMidiNotesArray()[(midiNote * 25) + 13];
		return decayMode;
	}

	public byte getCutoff(int midiNote) {
		byte cutoff = getMidiNotesArray()[(midiNote * 25) + 14];
		return cutoff;
	}

	public byte getResonance(int midiNote) {
		byte resonance = getMidiNotesArray()[(midiNote * 25) + 15];
		return resonance;
	}

	public byte getVelEnvToFiltAtt(int midiNote) {
		byte velEnvToFiltAtt = getMidiNotesArray()[(midiNote * 25) + 16];
		return velEnvToFiltAtt;
	}

	public byte getVelEnvToFiltDec(int midiNote) {
		byte velEnvToFiltDec = getMidiNotesArray()[(midiNote  * 25) + 17];
		return velEnvToFiltDec;
	}

	public byte getVelEnvToFiltAmt(int midiNote) {
		byte velEnvToFiltAmt = getMidiNotesArray()[(midiNote * 25) + 18];
		return velEnvToFiltAmt;
	}

	public byte getVelocityToLevel(int midiNote) {
		byte velocityToLevel = getMidiNotesArray()[(midiNote * 25) + 19];
		return velocityToLevel;
	}

	public byte getVelocityToAttack(int midiNote) {
		byte velocityToAttack = getMidiNotesArray()[(midiNote * 25) + 20];
		return velocityToAttack;
	}

	public byte getVelocityToStart(int midiNote) {
		byte velocityToStart = getMidiNotesArray()[(midiNote * 25) + 21];
		return velocityToStart;
	}

	public byte getVelocityToCutoff(int midiNote) {
		byte velocityToCutoff = getMidiNotesArray()[(midiNote * 25) + 22];
		return velocityToCutoff;
	}

	public byte getSliderParameter(int midiNote) {
		byte sliderParameter = getMidiNotesArray()[(midiNote * 25) + 23];
		return sliderParameter;
	}

	public byte getVelocityToPitch(int midiNote) {
		byte velocityToPitch = getMidiNotesArray()[(midiNote * 25) + 24];
		return velocityToPitch;
	}

	public int getUnsignedInt(byte b) {
		int unsignedInt = (0x000000FF) & b;
		return unsignedInt;
	}

}
