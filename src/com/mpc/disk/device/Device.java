package com.mpc.disk.device;

public interface Device {
	
	public Object getRoot();
	
	public void close();

	public void flush();

	public Object getFileSystem();

	public String getAbsolutePath();

	public long getSize();

	public String getVolumeName();
	
}
