package com.mpc.file.pgmreader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SoundNames {

	protected byte[] sampleNamesArray;

	private ProgramFileReader programFile;

	public SoundNames(ProgramFileReader programFile) {
		this.programFile = programFile;
	}

	protected int getSampleNamesSize() {
		int sampleNamesSize = ((programFile.getHeader().getNumberOfSamples()) * 17);
		return sampleNamesSize;
	}

	protected byte[] getSampleNamesArray() {
		int sampleNamesSize = getSampleNamesSize();
		sampleNamesArray = Arrays
				.copyOfRange(programFile.readProgramFileArray(), 4,
						(4 + sampleNamesSize + 2)); // 4 bytes header, 2 bytes
													// padding
		return sampleNamesArray;
	}

	public String getSampleName(int sampleNumber)
			throws UnsupportedEncodingException {
		String sampleNameString = new String();
		PgmHeader h = programFile.getHeader();
		if (sampleNumber < h.getNumberOfSamples()) {
			sampleNamesArray = getSampleNamesArray();
			byte[] sampleName = Arrays.copyOfRange(sampleNamesArray,
					(sampleNumber * 17), ((sampleNumber * 17) + 16));
			sampleNameString = new String(sampleName, "UTF-8");
		} else {
			sampleNameString = "OFF";
		}
		return sampleNameString;
	}
}
