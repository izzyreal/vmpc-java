package com.mpc.file.pgmwriter;


public class Header {

	private char[] headerArray;

	public Header(int numberOfSamples) {
		headerArray = new char[4];
		writeFirstTwoBytes();
		setNumberOfSamples(numberOfSamples);
		headerArray[3] = 0;
	}

	private void writeFirstTwoBytes() {
		headerArray[0] = 7;
		headerArray[1] = 4;
	}

	private void setNumberOfSamples(int numberOfSamples) {
		headerArray[2] = (char) numberOfSamples;
	}
	
	char[] getHeaderArray() {
		return headerArray;
	}
}