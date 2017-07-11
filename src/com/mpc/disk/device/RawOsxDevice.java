package com.mpc.disk.device;

import java.io.IOException;

import de.waldheinz.fs.FileSystem;
import de.waldheinz.fs.FileSystemFactory;
import de.waldheinz.fs.fat.AkaiFatLfnDirectory;
import de.waldheinz.fs.util.RawOsxVolume;

public class RawOsxDevice implements Device {

	private String absolutePath;
	private static RawOsxVolume rawVolume;
	private static FileSystem fs;

	public RawOsxDevice(String fileName) throws Exception {
		this.absolutePath = fileName;
		boolean readOnly = true;
		rawVolume = new RawOsxVolume(fileName, readOnly);
		fs = FileSystemFactory.createAkai(rawVolume, readOnly);

	}

	@Override
	public AkaiFatLfnDirectory getRoot() {
		try {
			return (AkaiFatLfnDirectory) fs.getRoot();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FileSystem getFileSystem() {
		return fs;
	}

	@Override
	public void close() {
		try {
			fs.close();
			rawVolume.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void flush() {
		try {
			fs.flush();
			fs.close();
			fs = FileSystemFactory.createAkai(rawVolume, false);
			rawVolume.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAbsolutePath() {
		return absolutePath;
	}

	@Override
	public long getSize() {
		try {
			return rawVolume.getSize();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String getVolumeName() {
		try {
			return fs.getRoot().toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "<no vol>";
		}
	}

}