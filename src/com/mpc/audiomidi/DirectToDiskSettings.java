package com.mpc.audiomidi;

public class DirectToDiskSettings {

	public long lengthInFrames;
	public String outputFolder;
	public boolean split;

	public DirectToDiskSettings(long lengthInFrames, String outputFolder, boolean split) {
		this.lengthInFrames = lengthInFrames;
		this.outputFolder = outputFolder;
		this.split = split;
	}
}
