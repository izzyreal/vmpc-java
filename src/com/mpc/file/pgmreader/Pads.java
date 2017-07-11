package com.mpc.file.pgmreader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Pads {

	int sampleNamesSize = 0;

	protected byte[] padsArray;

	private ProgramFileReader programFile;
	
	public Pads(ProgramFileReader programFile) {
		 this.programFile = programFile;
	}

	protected int getSampleNamesSize () {
		sampleNamesSize = programFile.getSampleNames().getSampleNamesSize();
		return sampleNamesSize;
	}

	protected int getPadsStart () {
		int padsStart = 4 + getSampleNamesSize() + 2 + 17 + 9 + 6 + 1601 + 387;
		return padsStart;
	}

	protected int getPadsEnd () {
		int padsEnd = 4 + getSampleNamesSize() + 2 + 17 + 9 + 6 + 1601 + 387 + 264;
		return padsEnd;
	}

	protected byte[] getPadsArray() {
		padsArray = Arrays.copyOfRange(programFile.readProgramFileArray(), getPadsStart(), getPadsEnd());
		return padsArray;
	}

	public int getNote(int pad) {
		int padMidiNote = getPadsArray()[pad];
		return padMidiNote;
	}

	protected int getPadSampleSelect (int pad) {
		int midiNote = getNote(pad);
		PgmAllNoteParameters m = programFile.getAllNoteParameters();
		int padSampleSelect = m.getSampleSelect(midiNote);
		return padSampleSelect;
	}
	
	public String getPadSampleName(int pad) throws UnsupportedEncodingException {
		String sampleName;
		int midiNote = getNote(pad);
		PgmAllNoteParameters m = programFile.getAllNoteParameters();
		int sampleNumber = m.getSampleSelect(midiNote);
		SoundNames s = programFile.getSampleNames();
		sampleName = s.getSampleName(sampleNumber);
		return sampleName;
	}	
}