package com.mpc.file.sndwriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mpc.sampler.Sound;

public class SndWriter {
	private final static int HEADER_SIZE = 42;
	private SndHeaderWriter sndHeaderWriter;
	private Sound sound;

	private byte[] sndFileArray;
	
	public SndWriter(Sound sound) {
		this.sound = sound;
		sndHeaderWriter = new SndHeaderWriter(this);
		setValues();
	}

	private void setValues() {
		setName(sound.getName());
		setMono(sound.isMono());
		setFramesCount(sound.getLastFrameIndex()+1);
		setSampleRate(sound.getSampleRate());
		setLevel(sound.getSndLevel());
		setStart(sound.getStart());
		setEnd(sound.getEnd());
		setLoopLength(sound.getEnd() - sound.getLoopTo());
		setLoopEnabled(sound.isLoopEnabled());
		setTune(sound.getTune());
		setBeatCount(sound.getBeatCount());
		setSampleData(sound.getSampleData(), sound.isMono());
	}
	
	private void setName(String s) {
		sndHeaderWriter.setName(s);
	}

	private void setMono(boolean b) {
		sndHeaderWriter.setMono(b);
	}

	private void setFramesCount(int i) {
		sndHeaderWriter.setFrameCount(i);
	}

	private void setSampleRate(int i) {
		sndHeaderWriter.setSampleRate(i);
	}

	private void setLevel(int i) {
		sndHeaderWriter.setLevel(i);
	}

	private void setStart(int i) {
		sndHeaderWriter.setStart(i);
	}

	private void setEnd(int i) {
		sndHeaderWriter.setEnd(i);
	}

	private void setLoopLength(int i) {
		sndHeaderWriter.setLoopLength(i);
	}

	private void setLoopEnabled(boolean b) {
		sndHeaderWriter.setLoopEnabled(b);
	}

	private void setTune(int i) {
		sndHeaderWriter.setTune(i);
	}

	private void setBeatCount(int i) {
		sndHeaderWriter.setBeatCount(i);
	}

	private void setSampleData(float[] fa, boolean b) {
		System.out.println("Mono: " + b);
		sndFileArray = new byte[HEADER_SIZE + (fa.length*2)];
		int frames = b ? fa.length : (int) (fa.length / 2);
		sndHeaderWriter.setFrameCount(frames);
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int sPos = 0;
		int bytePos = HEADER_SIZE;
		for (int i=0;i<frames;i++) {
			bb.clear();
			bb.putShort((short) (fa[sPos++]*32768));
			byte[] ba = bb.array();
			sndFileArray[bytePos++] = ba[0];
			sndFileArray[bytePos++] = ba[1];
			if (!b) {
				bytePos -= 2;
				bb.clear();
				bb.putShort((short) (fa[sPos++]*32768));
				ba = bb.array();
				sndFileArray[bytePos++ + (frames*2)] = ba[0];
				sndFileArray[bytePos++ + (frames*2)] = ba[1];
			}
		}
	}

	public byte[] getSndFileArray() {
		byte[] header = sndHeaderWriter.getHeaderArray(); 
		for (int i=0;i<header.length;i++) 
			sndFileArray[i] = header[i];
		return sndFileArray;
	}
	
//	public void writeSndFileArray(File file) {
//		byte[] header = sndHeaderWriter.getHeaderArray(); 
//		for (int i=0;i<header.length;i++) 
//			sndFileArray[i] = header[i];
		
//		try {
//			file.createNewFile();
//			FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
//			fos.write(sndFileArray);
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}