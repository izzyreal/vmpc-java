package com.mpc.file.sndwriter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SndHeaderWriter {

	private byte[] headerArray;

	public SndHeaderWriter(SndWriter sndWriter) {
		headerArray = new byte[42];
		setFirstTwoBytes();
		
	}

	protected byte[] getHeaderArray() {
		return headerArray;
	}

	protected void setFirstTwoBytes() {
		headerArray[0] = 0x01;
		headerArray[1] = 0x04;
	}

	protected void setName(String s) {
		byte[] nameArray = new byte[0x10];
		for (int i=0;i<s.length();i++) 
			nameArray[i] = (byte) s.charAt(i);
		for (int i=s.length();i<nameArray.length;i++)
			nameArray[i] = 0x20;
		
		for (int i=0;i<nameArray.length;i++)
			headerArray[i+0x02] = nameArray[i];
	}
	
	protected void setLevel(int i) {
		headerArray[0x13] = (byte) i;
	}
	
	protected void setTune(int i) {
		headerArray[0x14] = (byte) i;
	}
	
	protected void setMono(boolean b) {
		headerArray[0x15] = (byte) (b ? 0 : 1);
	}
	
	protected void setStart(int i) {
		putLE(0x16, i);
	}

	protected void setEnd(int i) {
		putLE(0x1A, i);
	}
	
	protected void setFrameCount(int i) {
		putLE(0x1E, i);
	}
	
	protected void setLoopLength(int i) {
		putLE(0x22, i);
	}
	
	protected void setLoopEnabled(boolean b) {
		headerArray[0x26] = (byte) (b ? 1 : 0);
	}
	
	protected void setBeatCount(int i) {
		headerArray[0x27] = (byte) i;
	}
	
	protected void setSampleRate(int i) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putShort((short) (i-65536));
	}
	
	private void putLE(int offset, int value) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(value);
		byte[] ba = buf.array();
		for (int j=0;j<ba.length;j++)
			headerArray[j+offset] = ba[j];
	}
}