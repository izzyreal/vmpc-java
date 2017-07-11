package com.mpc.file.pgmreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mpc.disk.MpcFile;

public class ProgramFileReader {

	private PgmHeader pgmHeader;
	private SoundNames sampleNames;
	private ProgramName programName;
	private PgmAllNoteParameters midiNotes;
	private Mixer mixer;
	private Slider slider;
	private Pads pads;

	private Path programFile;

	public ProgramFileReader(MpcFile f) {
		
		this.programFile = f.getFile().toPath();

		pgmHeader = new PgmHeader(this);
		programName = new ProgramName(this);
		sampleNames = new SoundNames(this);
		programName = new ProgramName(this);
		midiNotes = new PgmAllNoteParameters(this);
		mixer = new Mixer(this);
		slider = new Slider(this);
		pads = new Pads(this);
	}

	protected byte[] readProgramFileArray() {
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(programFile);
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public PgmHeader getHeader() {
		return pgmHeader;
	}

	public SoundNames getSampleNames() {
		return sampleNames;
	}

	public PgmAllNoteParameters getAllNoteParameters() {
		return midiNotes;
	}

	public Mixer getMixer() {
		return mixer;
	}

	public Pads getPads() {
		return pads;
	}

	public ProgramName getProgramName() {
		return programName;
	}

	public Slider getSlider() {
		return slider;
	}
}