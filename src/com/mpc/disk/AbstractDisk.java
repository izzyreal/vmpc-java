package com.mpc.disk;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.mpc.Util;
import com.mpc.file.mid.MidiWriter;
import com.mpc.file.pgmwriter.PgmWriter;
import com.mpc.file.sndwriter.SndWriter;
import com.mpc.file.wav.MpcWavFile;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.disk.DiskGui;
import com.mpc.gui.disk.window.DirectoryGui;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sound;
import com.mpc.sequencer.MpcSequence;

public abstract class AbstractDisk extends Observable implements Disk {

	protected final String[] extensions = { "", "SND", "PGM", "APS", "MID", "ALL", "WAV", "SEQ", "SET" };

	protected List<MpcFile> files;
	protected List<MpcFile> parentFiles;

	public static String formatFileSize(long size) {
		String hrSize = null;

		double b = size;
		double k = size / 1024.0;
		double m = ((size / 1024.0) / 1024.0);
		double g = (((size / 1024.0) / 1024.0) / 1024.0);
		double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

		DecimalFormat dec = new DecimalFormat("0.00");

		if (t > 1) {
			hrSize = dec.format(t).concat(" TB");
		} else if (g > 1) {
			hrSize = dec.format(g).concat(" GB");
		} else if (m > 1) {
			hrSize = dec.format(m).concat(" MB");
		} else if (k > 1) {
			hrSize = dec.format(k).concat(" KB");
		} else {
			hrSize = dec.format(b).concat(" Bytes");
		}

		return hrSize;
	}

	public static String[] splitName(String s) {

		if (!s.contains(".")) return (new String[] { s, "" });

		int i = s.lastIndexOf(".");

		return (new String[] { s.substring(0, i), s.substring(i + 1) });
	}

	public static String padRightSpace(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padFileName16(String s) {

		if (!s.contains(".")) return s;

		int periodIndex = s.lastIndexOf(".");
		String name = s.substring(0, periodIndex);
		String ext = s.substring(periodIndex);
		name = padRightSpace(name, 16);
		return name + ext;
	}

	@Override
	public MpcFile getFile(int i) {
		return files.get(i);
	}

	@Override
	public List<String> getFileNames() {
		List<String> fileNames = new ArrayList<String>();
		if (files != null) for (MpcFile f : files)
			fileNames.add(f.getName());
		return fileNames;
	}

	@Override
	public String getFileName(int i) {
		return files.get(i).getName();
	}

	@Override
	public List<String> getParentFileNames() {
		List<String> fileNames = new ArrayList<String>();
		for (MpcFile f : parentFiles)
			fileNames.add(f.getName().length() < 8 ? f.getName() : f.getName().substring(0, 8));
		return fileNames;
	}

	public boolean renameSelectedFile(String s) { // File or dir, so have
		// to figure out which
		// column

		DiskGui diskGui = Bootstrap.getGui().getDiskGui();
		DirectoryGui dirGui = Bootstrap.getGui().getDirectoryGui();

		boolean left = dirGui.getXPos() == 0;

		int fileNumber = left ? dirGui.getYpos0() + dirGui.getYOffsetFirst() : diskGui.getFileLoad();

		MpcFile file = left ? getParentFile(fileNumber) : getFile(fileNumber);

		return file.setName(s);
	}

	public boolean deleteSelectedFile() { // Only !dir, so always right column
		DiskGui diskGui = Bootstrap.getGui().getDiskGui();
		// System.out.println("trying to delete " +
		// files.get(diskGui.getFileLoad()).getName());
		return files.get(diskGui.getFileLoad()).delete();
	}

	@Override
	public List<MpcFile> getFiles() {
		return files;
	}

	@Override
	public MpcFile getParentFile(int i) {
		return parentFiles.get(i);
	}

	@Override
	public List<MpcFile> getParentFiles() {
		return parentFiles;
	}

	@Override
	public void writeSound(Sound s) {
		MpcFile newFile = newFile(Util.getFileName(s.getName()) + ".SND");
		writeSound(s, newFile);
	}

	@Override
	public void writeWav(Sound s) {
		MpcFile newFile = newFile(Util.getFileName(s.getName()) + ".WAV");
		writeWav(s, newFile);
	}

	@Override
	public void writeSound(Sound s, MpcFile f) {
		SndWriter sw = new SndWriter(s);
		byte[] sndArray = sw.getSndFileArray();
		f.setFileData(sndArray);
		flush();
		initFiles();
	}

	@Override
	public void writeWav(Sound s, MpcFile f) {
		float[] fa = s.getSampleData();
		double[] faDouble = new double[fa.length];
		for (int i = 0; i < faDouble.length; i++)
			faDouble[i] = fa[i];

		try {
			MpcWavFile wavFile = MpcWavFile.newWavFile(s.isMono() ? 1 : 2, faDouble.length / (s.isMono() ? 1 : 2), 16,
					s.getSampleRate());
			
			wavFile.writeFrames(faDouble, faDouble.length / (s.isMono() ? 1 : 2));
			wavFile.close();
			byte[] wavBytes = wavFile.getResult();
			f.setFileData(wavBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		flush();
		initFiles();
	}

	@Override
	public void writeWavToTemp(Sound s) {
		File f = new File(System.getProperty("user.home") + "/Mpc/temp/" + s.getName() + ".WAV");
		float[] fa = s.getSampleData();
		double[] faDouble = new double[fa.length];
		for (int i = 0; i < faDouble.length; i++)
			faDouble[i] = fa[i];

		try {
			FileOutputStream fos = new FileOutputStream(f.getPath());
			MpcWavFile wavFile = MpcWavFile.newWavFile(s.isMono() ? 1 : 2, faDouble.length / (s.isMono() ? 1 : 2), 16,
					s.getSampleRate());
			wavFile.writeFrames(faDouble, faDouble.length / (s.isMono() ? 1 : 2));
			wavFile.close();
			byte[] wavBytes = wavFile.getResult();
			fos.write(wavBytes);
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		flush();
		initFiles();
	}

	@Override
	public void writeSequence(MpcSequence s, String fileName) {
		if (checkExists(fileName)) return;
		MidiWriter mw = new MidiWriter(s);
		byte[] mfArray = mw.getBytes();
		MpcFile newMidFile = newFile(fileName);
		newMidFile.setFileData(mfArray);
		flush();
		initFiles();
	}

	@Override
	public boolean checkExists(String fileName) {
		// fileName = fileName.replaceAll(" ", "");
		for (String str : getFileNames()) {
			System.out.println("checking against " + str);
			if (str.equalsIgnoreCase(fileName)) {
				// System.out.println(fileName + " exists");
				return true;
			}
		}
		return false;
	}

	@Override
	public MpcFile getFile(String fileName) {
		String tempfileName = fileName.replaceAll(" ", "");
		for (MpcFile f : files)
			if (f.getName().replaceAll(" ", "").equalsIgnoreCase(tempfileName)) return f;
		return null;
	}

	@Override
	public void writeProgram(Program program, String fileName) {
		if (checkExists(fileName)) return;
		PgmWriter writer = new PgmWriter(program, Bootstrap.getGui().getMpc().getSampler());
		MpcFile pgmFile = newFile(fileName);
		pgmFile.setFileData(writer.get());
		List<Sound> sounds = new ArrayList<Sound>();
		for (NoteParameters n : program.getNotesParameters())
			if (n.getSndNumber() != -1) sounds.add(Bootstrap.getGui().getMpc().getSampler().getSound(n.getSndNumber()));
		int save = Bootstrap.getGui().getDiskGui().getPgmSave();
		if (save != 0) new SoundSaver(sounds, save == 1 ? false : true);
		flush();
		initFiles();
		setBusy(false);
	}
}
