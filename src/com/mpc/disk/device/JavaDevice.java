package com.mpc.disk.device;

import java.io.File;

public class JavaDevice implements Device {

	private final boolean valid;
	private File root;
	
	public JavaDevice(File root) {
		
		if (!root.exists()) {
			valid = false;
			return;
		}
		
		valid = true;
		this.root = root; 
	}
	
	@Override
	public File getRoot() {
		return root;
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public void flush() {
		// nothing to do
	}

	@Override
	public Object getFileSystem() {
		return null;
	}

	@Override
	public String getAbsolutePath() {
		return null;
	}

	@Override
	public long getSize() {
		return 909303;
	}

	@Override
	public String getVolumeName() {
		// TODO Auto-generated method stub
		return null;
	}
}
