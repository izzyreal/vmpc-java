package com.mpc.file.sndreader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.mpc.disk.MpcFile;
import com.mpc.sampler.Sampler;

public class SndReader {
	private byte[] sndFileArray;
	private SndHeaderReader sndHeader;
	private MpcFile sndFile;

	public SndReader(MpcFile soundFile) {
		sndFile = soundFile;
		sndFileArray = sndFile.getBytes();
		sndHeader = new SndHeaderReader(this);
	}

	public String getName() {
		return sndHeader.getName();
	}

	public boolean isMono() {
		return sndHeader.isMono();
	}

	public int getNumberOfFrames() {
		return sndHeader.getNumberOfFrames();
	}

	public int getSampleRate() {
		return sndHeader.getSampleRate();
	}

	public int getLevel() {
		return sndHeader.getLevel();
	}

	public int getStart() {
		return sndHeader.getStart();
	}

	public int getEnd() {
		return sndHeader.getEnd();
	}

	public int getLoopLength() {
		return sndHeader.getLoopLength();
	}

	public boolean isLoopEnabled() {
		return sndHeader.isLoopEnabled();
	}

	public int getTune() {
		return sndHeader.getTune();
	}

	public int getNumberOfBeats() {
		return sndHeader.getNumberOfBeats();
	}

	public float[] getSampleData() {
		int length = sndHeader.getNumberOfFrames();
		int monoLength = length;
		float[] fa = new float[length];
		float[] fa1 = new float[length];
		if (!sndHeader.isMono()) length *= 2;

		int j = 0;
		for (int i = 0; i < length; i++) {
			byte[] valueArray = Arrays.copyOfRange(sndFileArray, 0x2A + j,
					0x2C + j);
			ByteBuffer buf = ByteBuffer.wrap(valueArray);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			short value = buf.getShort();
			float f = (float) (value / 32768.0);
			if (sndHeader.isMono()) {
				fa[i] = f;
			} else {
				if (i < monoLength) {
					fa[i] = f;
				} else {
					fa1[i-monoLength] = f;
				}
			}
			j += 2;
		}
		
		if (!sndHeader.isMono()) {
			float[] temp = Sampler.mergeToStereo(fa, fa1);
			fa = temp;
		}
		
		return fa;
	}

	protected byte[] getSndFileArray() {
		return sndFile.getBytes();
	}

}