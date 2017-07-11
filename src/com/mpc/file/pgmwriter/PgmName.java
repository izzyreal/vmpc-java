package com.mpc.file.pgmwriter;

import com.mpc.sampler.Program;

public class PgmName {

	int sampleNamesSize = 0;

	protected final char[] programNameArray;

	public PgmName(Program program) {

		char[] ca = program.getName().toCharArray();
		char[] temp = new char[17];

		for (int i = 0; i < ca.length; i++)
			temp[i] = ca[i];

		for (int i = ca.length; i < 16; i++)
			temp[i] = 0x20; // padding

		temp[16] = 0x00;

		programNameArray = temp;
		sampleNamesSize = program.getNumberOfSamples() * 17;
	}

	protected char[] getPgmNameArray() {
		return programNameArray;
	}
}