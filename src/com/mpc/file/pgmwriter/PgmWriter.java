package com.mpc.file.pgmwriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.mpc.disk.AbstractDisk;
import com.mpc.disk.Disk;
import com.mpc.disk.JavaDisk;
import com.mpc.gui.Bootstrap;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;

import de.waldheinz.fs.fat.AkaiFatLfnDirectoryEntry;

public class PgmWriter {

	private final Program program;
	private final char[] header;
	private char[] pgmName;
	private char[] sampleNames;
	private char[] slider;
	private char[] midiNotes;
	private char[] mixer;
	private char[] pads;

	public PgmWriter(Program program, Sampler sampler) {
		this.program = program;
		SampleNames sn = new SampleNames(program, sampler);
		sampleNames = sn.getSampleNamesArray();

		header = new Header(sn.getNumberOfSamples()).getHeaderArray();
		pgmName = new PgmName(program).getPgmNameArray();
		slider = new Slider(program).getSliderArray();
		midiNotes = new MidiNotes(program, sn.getSnConvTable())
				.getMidiNotesArray();
		mixer = new Mixer(program).getMixerArray();
		pads = new Pads(program).getPadsArray();
	}

	public byte[] get() {

		char[][] caa = new char[7][];
		caa[0] = header;
		caa[1] = sampleNames;
		caa[2] = pgmName;
		caa[3] = slider;
		caa[4] = midiNotes;
		caa[5] = mixer;
		caa[6] = pads;
		int programFileSize = 0;

		for (char[] ca : caa)
			programFileSize += ca.length;

		ByteBuffer bb = ByteBuffer.allocate(programFileSize);
		for (char[] ca : caa)
			for (char c : ca)
				bb.put((byte) c);
		
		return bb.array();
	}
}