package com.mpc.file.sndreader;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SndHeaderReader {

	private byte[] headerArray;

	public SndHeaderReader(SndReader sndReader) {
		headerArray = Arrays.copyOfRange(sndReader.getSndFileArray(), 0, 42);
	}

	protected byte[] getHeaderArray() {
		return headerArray;
	}

	protected boolean verifyFirstTwoBytes() {
		boolean verifyFirstTwoBytes = false;
		int i = headerArray[0];
		int j = headerArray[1];
		if (i == 1 && j < 5) {
			verifyFirstTwoBytes = true;
		}
		return verifyFirstTwoBytes;
	}

	protected String getName() {
		byte[] nameArray = Arrays.copyOfRange(headerArray,
				0x02, 0x12);
		String name = "";
		try {
			name = new String(nameArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	protected int getLevel() {
		return headerArray[0x13];
	}
	
	protected int getTune() {
		return headerArray[0x14];
	}
	
	protected boolean isMono() {
		int channels = headerArray[0x15] + 1;
		boolean mono = false;
		if (channels == 1) mono = true;
		return mono;
	}
	
	protected int getStart() {
		byte[] startArray = Arrays.copyOfRange(headerArray, 0x16, 0x1A);
		ByteBuffer buf = ByteBuffer.wrap(startArray);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int start = buf.getInt();
		return start;
	}

	protected int getEnd() {
		byte[] endArray = Arrays.copyOfRange(headerArray, 0x1A, 0x1E);
		ByteBuffer buf = ByteBuffer.wrap(endArray);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int end = buf.getInt();
		return end;
	}
	
	protected int getNumberOfFrames() {
		byte[] numberOfFramesArray = Arrays.copyOfRange(headerArray, 0x1E, 0x22);
		ByteBuffer buf = ByteBuffer.wrap(numberOfFramesArray);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int numberOfFrames = buf.getInt();
		return numberOfFrames;
	}
	
	protected int getLoopLength() {
		byte[] loopLengthArray = Arrays.copyOfRange(headerArray, 0x22, 0x26);
		ByteBuffer buf = ByteBuffer.wrap(loopLengthArray);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int loopLength = buf.getInt();
		return loopLength;
	}
	
	protected boolean isLoopEnabled() {
		int loop = headerArray[0x26];
		boolean loopEnabled = false;
		if (loop == 1) loopEnabled = true;
		return loopEnabled;
	}
	
	protected int getNumberOfBeats() {
		return headerArray[0x27];
	}
	
	protected int getSampleRate() {
		byte[] rateArray = Arrays.copyOfRange(headerArray, 0x28, 0x2A);
		ByteBuffer buf = ByteBuffer.wrap(rateArray);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		short rate = buf.getShort();
		return (int) rate + 65536;
	}
}