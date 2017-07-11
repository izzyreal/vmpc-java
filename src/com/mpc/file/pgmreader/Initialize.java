package com.mpc.file.pgmreader;

public class Initialize {
/*
	//final ProgramFile p = new ProgramFile();

	final long fileSize = ProgramFile.progFile.getFileSize();

	boolean isBigEnough = ProgramFile.progFile.isBigEnough();

	public void initAll() throws UnsupportedEncodingException {
		checkFileSize();
		checkHeader();
		printInfo();
	}

	public void checkFileSize() {
		System.out.print("File is " + fileSize);
		if (isBigEnough) {
			System.out
					.println(" bytes so that's big enough (2290 bytes minimum)");
		} else {
			System.out.println(" bytes so that's too SMALL!");
		}
	}

	public void checkHeader() {
		final Header h = new Header();

		if (h.verifyFirstTwoBytes()) {
			System.out.println("First 2 bytes look ok.");
		} else {
			System.out.println("First 2 bytes DON'T look ok!");
		}

		System.out.print("Number of samples: " + (h.getNumberOfSamples()));

		if (h.verifyNumberOfSamples()) {
			System.out
					.println(" which matches the filesize (n samples * 17 + 2290)");
		} else {
			System.out.println(" which MISMATCHES filesize!");
		}
	}

	public void checkSampleSegmentSize() {
		final SampleNames s = new SampleNames();

		System.out.println("Sample names segment size is "
				+ (s.getSampleNamesSize()));
	}

	public void printInfo() throws UnsupportedEncodingException {
		final SampleNames s = new SampleNames();

		System.out.println("Sample names segment size is "
				+ (s.getSampleNamesSize()));

//		System.out.println("Sample names: " + s.getSampleNamesASCII());

		final ProgramName pn = new ProgramName();

		System.out.println("Program name: " + pn.getProgramNameASCII());

		final Slider sl = new Slider();

		// System.out.println("Slider params: " +
		// Arrays.toString(sl.getSliderArray()));
		System.out.println("Slider note assign: " + (sl.getMidiNoteAssign()));
		System.out.println("Slider tune low: " + (sl.getTuneLow()));
		System.out.println("Slider tune high: " + (sl.getTuneHigh()));
		System.out.println("Slider decay low: " + (sl.getDecayLow()));
		System.out.println("Slider decay high: " + (sl.getDecayHigh()));
		System.out.println("Slider attack low: " + (sl.getAttackLow()));
		System.out.println("Slider attack high: " + (sl.getAttackHigh()));
		System.out.println("Slider filter low: " + (sl.getFilterLow()));
		System.out.println("Slider filter high: " + (sl.getFilterHigh()));

		final ProgramChange pc = new ProgramChange();

		System.out.println("Program change: " + pc.getProgramChange());

		System.out.println("ProgChChunk: "
				+ Arrays.toString(pc.getProgramChangeArray()));

		final MidiNotes m = new MidiNotes();

		//System.out.println("MIDI note data: "	+ Arrays.toString(m.getMidiNotesArray()));
		int midiNote = 37;
		String tune = String.valueOf(ProgramFile.midiNotes.getTune(midiNote));
		System.out.println("Tune of MIDI note " + midiNote + " is " + tune);
		int sampleSelector = m.getSampleSelect(midiNote);
		if (sampleSelector != 255) {
		System.out.println("Sample select MIDI note " + midiNote + " is sample " + sampleSelector + " (" + s.getSampleName(sampleSelector) + ")");
		} else { System.out.println("MIDI note " + midiNote + " has no sample selected " );}
		System.out.println("MIDI note " + midiNote + " belongs to pad " + m.getPadNumber(midiNote));
		
		final Mixer mx = new Mixer();

		//System.out
		//		.println("Mixer data: " + Arrays.toString(mx.getMixerArray()));

		int pad = m.getPadNumber(midiNote);
		System.out.println("Pad " + pad + " volume: " + mx.getVolume(pad));
		System.out.println("Pad " + pad + " pan: " + mx.getPan(pad));
		System.out.println("Pad " + pad + " volume individual: " + mx.getVolumeIndividual(pad));
		System.out.println("Pad " + pad + " output: " + mx.getOutput(pad));
		System.out.println("Pad " + pad + " effects send level: " + mx.getEffectsSendLevel(pad));
		System.out.println("Pad " + pad + " effects output: " + mx.getEffectsOutput(pad));
		
		final Pads pd = new Pads();

		//System.out.println("Pad data: " + Arrays.toString(pd.getPadsArray()));
		//System.out.println("Pad " + pad + " has MIDI note " + pd.getPadMidiNote(pad) + " assigned to it, which triggers " + pd.getPadSampleName(pad));
		if (pd.getPadSampleSelect(pad) == 255 ) { System.out.println("Pad " + pad + " triggers no sample"); } else {
		System.out.println("Pad " + pad + " triggers " + pd.getPadSampleName(pad));
		}
		//int sampleSelect = 3;
		//System.out.println("Sample name " + sampleSelect + ": "
		//		+ s.getSampleName(sampleSelect));

	}
	*/
}
