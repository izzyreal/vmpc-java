// Copyright (C) 2007 Steve Taylor.
// Distributed under under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the License,
// or (at your option) any later version.

package com.mpc.audiomidi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;

import org.tritonus.share.TCircularBuffer;

import com.mpc.Util;
import com.mpc.disk.MpcFile;
import com.mpc.file.wav.MpcWavFile;
import com.mpc.gui.Bootstrap;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.AudioProcessAdapter;

/**
 * State transitions in an export cycle: start(); reading = true; writing =
 * true; stop(); reading = false; writing = false;
 *
 * export is active when reading || writing Writing lags reading because it
 * represents the output end of the circular buffer writing to file.
 */
public class ExportAudioProcessAdapter extends AudioProcessAdapter implements Runnable {
	protected String name;
	protected TCircularBuffer circularBuffer;
	protected AudioFormat format;
	protected boolean reading = false; // reading the buffer
	protected boolean writing = false; // writing the file
	protected File file;
	private Thread writeThread;

	private RandomAccessFile raf;
	private int lengthInBytes;
	// private boolean firstBuffer = false;

	public ExportAudioProcessAdapter(AudioProcess process, AudioFormat format, String name) {
		super(process);
		this.format = format;
		this.name = name;
		System.out.println(format.toString());
	}

	public void prepare(File file, long lengthInFrames) {
		circularBuffer = new TCircularBuffer(100000, true, true, null);
		lengthInBytes = (int) lengthInFrames * 2 * 2;
		if (lengthInBytes % 2 != 0) lengthInBytes++;

		try {
			if (file.exists()) file.delete();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			int written = 0;
			byte[] buffer = null;
			while (lengthInBytes - written > 512) {
				buffer = new byte[512];
				fos.write(buffer);
				written += 512;
			}
			int remaining = lengthInBytes - written;
			buffer = new byte[remaining];
			fos.write(buffer);
			fos.close();
			raf = new RandomAccessFile(file, "rw");

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (reading || writing) {
			throw new IllegalStateException("Can't setFile() when already exporting");
		}
		this.file = file;
	}

	public void start() {
		if (file == null) {
			throw new IllegalStateException("null export file");
		}
		if (reading || writing) return; // already started
		// firstBuffer = true;
		reading = true;
		// start file write thread which opens file, writes, then closes file
		writeThread = new Thread(this, name + " Export");
		writeThread.start();
	}

	@Override
	public int processAudio(AudioBuffer buf) {
		int ret = super.processAudio(buf);
		if (reading) {
			byte[] ba = new byte[buf.getByteArrayBufferSize(format)];
			buf.convertToByteArray(ba, 0, format);
			circularBuffer.write(ba, 0, ba.length);
		}
		return ret;
	}

	public void stop() {
		if (!reading) return; // already stopped
		reading = false;
	}

	public void run() {
		int written = 0;
		writing = true;
		// open file with format
		//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		byte[] ba = null;
		while ((reading || circularBuffer.availableRead() > 0) && writing) {
			boolean close = false;
			try {
				ba = new byte[circularBuffer.availableRead()];
				if (ba.length + written > lengthInBytes) {
					byte[] newBa = new byte[lengthInBytes - written];
					for (int i = 0; i < newBa.length; i++)
						newBa[i] = ba[i];
					ba = newBa;
					close = true;
				}
				if (ba.length == 0) {
					Thread.sleep(2);
					continue;
				}

				circularBuffer.read(ba);

				raf.seek(written);
				raf.write(ba);
				if (close) break;
				written += ba.length;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		// close file
		writing = false;
		reading = false;
	}

	public void writeWav() {
		try {
			int[] faDouble = new int[lengthInBytes / 2];
			System.out.println("faDouble.length " + faDouble.length);
			System.out.println("lengthInBytes" + lengthInBytes);
			int read = 0;
			int converted = 0;
			boolean nonZeroDetected = false;
			while (lengthInBytes - read > 512) {
				raf.seek(read);
				byte[] ba = new byte[512];
				raf.read(ba);
				byte[] ba2 = new byte[2];
				for (int i = 0; i < 512; i += 2) {
					ba2 = new byte[] { ba[i], ba[i + 1] };
					int value = Util.bytePairToUnsignedInt(ba2);
					faDouble[converted++] = value;
					if (value != 0) {
						if (!nonZeroDetected) nonZeroDetected = true;
					}
				}
				read += 512;
			}
			int remain = lengthInBytes - read;
			byte[] remainder = new byte[remain];
			raf.seek(read);
			raf.read(remainder);
			raf.close();
			file.delete();
			for (int i = 0; i < remain; i += 2) {
				byte[] ba = new byte[] { remainder[i], remainder[i + 1] };
				int value = Util.bytePairToUnsignedInt(ba);
				faDouble[converted++] = value;
				if (value != 0) {
					if (!nonZeroDetected) nonZeroDetected = true;
				}
			}
			
			if (nonZeroDetected) {

				MpcFile mpcFile = new MpcFile(new File(Bootstrap.home + "/Mpc/recordings/" + file.getName() + ".WAV"));

				MpcWavFile mpcWavFile = MpcWavFile.newWavFile(2, faDouble.length / 2, 16, 44100);

				mpcWavFile.writeFrames(faDouble, faDouble.length / 2);
				mpcWavFile.close();
				byte[] wavBytes = mpcWavFile.getResult();
				mpcFile.setFileData(wavBytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
