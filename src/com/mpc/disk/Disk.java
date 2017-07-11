package com.mpc.disk;

import java.util.List;

import com.mpc.sampler.Program;
import com.mpc.sampler.Sound;
import com.mpc.sequencer.MpcSequence;

public interface Disk {

	public void setBusy(boolean b);

	public void openLoadASound();

	public boolean isBusy();

	public boolean isDirectory(MpcFile f);

	public String getDirectoryName();

	public void initFiles();

	public List<String> getFileNames();

	public String getFileName(int i);

	public List<String> getParentFileNames();

	public void close();

	public void flush();

	public boolean moveBack();

	public MpcFile getFile(int i);

	public MpcFile getParentFile(int i);

	public boolean moveForward(String directoryName);

	public boolean isRoot();
	
	public boolean deleteDir(MpcFile f);
	
	public boolean deleteAllFiles(int dwGuiDelete);

	public boolean deleteSelectedFile();

	public boolean renameSelectedFile(String string);

	public boolean newFolder(String newDirName);
	
	public MpcFile newFile(String newFileName);

	public List<MpcFile> getFiles();
	
	String getAbsolutePath();

	public com.mpc.disk.Stores.Store getStore();

	List<MpcFile> getParentFiles();

	public void writeSound(Sound s);

	public void writeWav(Sound s);

	public void writeSequence(MpcSequence seq, String fileName);
	
	public boolean checkExists(String fileName);
	
	public MpcFile getFile(String fileName);

	public void writeProgram(Program program, String fileName);

	public void writeWavToTemp(Sound sound);

	public void writeSound(Sound s, MpcFile f);

	public void writeWav(Sound s, MpcFile f);
	
		
}