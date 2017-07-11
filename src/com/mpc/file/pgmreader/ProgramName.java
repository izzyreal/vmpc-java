package com.mpc.file.pgmreader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ProgramName {

	int sampleNamesSize = 0;

	protected byte[] programNameArray;

	private ProgramFileReader programFile;
	
	public ProgramName(ProgramFileReader programFile) {
		 this.programFile = programFile;
	}

	protected int getSampleNamesSize () {
		sampleNamesSize = programFile.getSampleNames().getSampleNamesSize();
		return sampleNamesSize;
	}

	protected int getProgramNameStart () {
		int programNameStart = 4 + getSampleNamesSize() + 2;
		return programNameStart;
	}

	protected int getProgramNameEnd () {
		int programNameEnd = 4 + getSampleNamesSize() + 2 + 17;
		return programNameEnd;
	}


	protected byte[] getProgramNameArray() {
		programNameArray = Arrays.copyOfRange(programFile.readProgramFileArray(), getProgramNameStart(), getProgramNameEnd()); // 4 bytes header, 2 bytes padding
		return programNameArray;
	}

	public void setProgramNameArray() {

	}
	
	public String getProgramNameASCII() throws UnsupportedEncodingException {
		String programNameASCII = new String(getProgramNameArray(), "UTF-8");
		String little = programNameASCII.substring(0, 16);
		return little;
	}
	
	public void setProgramNameASCII(String programName) throws UnsupportedEncodingException {
		programNameArray = programName.getBytes();
			int j = 0;
			for (int i = getProgramNameStart(); i < getProgramNameEnd(); i++){
				programFile.readProgramFileArray()[i] = programNameArray[j];
				j++;
		}
	}
}