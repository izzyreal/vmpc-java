package com.mpc.file.pgmreader;

import java.util.Arrays;

public class PgmHeader  {

	
	private byte[] headerArray;
	
	public PgmHeader(ProgramFileReader programFile) {
		headerArray = Arrays.copyOfRange(programFile.readProgramFileArray(), 0, 4);
	}

	protected byte[] getHeaderArray () {
		return headerArray;
	}

	protected boolean verifyFirstTwoBytes (){
		boolean verifyFirstTwoBytes = false;
		int i = headerArray[0];
		int j = headerArray[1];
		if (i == 7 && j == 4) {
			verifyFirstTwoBytes = true;
		}
			return verifyFirstTwoBytes;
	}
	
	public int getNumberOfSamples() {
		int numberOfSamples = headerArray[2] & 0xFF; 
		return numberOfSamples;
	}
	
//	protected boolean verifyNumberOfSamples(){
//		boolean verifyNumberOfSamples = false;
//		long correctFileSize = 0;
//		correctFileSize = (headerArray[2] * 17) + 2290;
//		if (programFile.getFileSize() == correctFileSize) verifyNumberOfSamples = true;
//		return verifyNumberOfSamples;
//	}
}