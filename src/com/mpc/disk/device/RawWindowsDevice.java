package com.mpc.disk.device;

import java.io.IOException;

import com.mpc.disk.RawDisk;

import de.waldheinz.fs.FileSystem;
import de.waldheinz.fs.FileSystemFactory;
import de.waldheinz.fs.fat.AkaiFatLfnDirectory;
import de.waldheinz.fs.util.RawWindowsVolume;

public class RawWindowsDevice implements Device {

	private static RawWindowsVolume rawVolume;
	private static FileSystem fs;
	private String driveLetter;
	private long totalSpace;
	public RawWindowsDevice(String driveLetter, RawDisk disk, long totalSpace) throws Exception {
		this.driveLetter = driveLetter;
		this.totalSpace = totalSpace;
		boolean readOnly = true;
		rawVolume = new RawWindowsVolume(driveLetter, readOnly, totalSpace);
		try {
		fs = FileSystemFactory.createAkai(rawVolume, readOnly);
		} catch (Exception e) {
			throw e;
		}
	}

	public FileSystem getFileSystem() {
		return fs;
	}

	@Override
	public AkaiFatLfnDirectory getRoot() {
		try {
			return (AkaiFatLfnDirectory) fs.getRoot();
		} catch (IOException e) {
			return null;
		}
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
//			rawVolume.close();
//			try {
//				rawVolume = new RawWindowsVolume(driveLetter, false, totalSpace);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			rawVolume.flush();
			fs = FileSystemFactory.createAkai(rawVolume, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAbsolutePath() {
		return driveLetter;
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
		return "<no vol>";
	}
}