package com.mpc.file.wav;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CachedOutputStream extends OutputStream {
	
	private List<Byte> bytes = new ArrayList<Byte>();
	
	@Override
	public void write(int b) throws IOException {
		bytes.add((byte) b);
	}
	
	protected byte[] get() {
		byte[] result = new byte[bytes.size()];
		for (int i=0;i<bytes.size();i++)
			result[i] = bytes.get(i);
		return result;
	}
	
}
