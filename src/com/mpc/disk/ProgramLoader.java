package com.mpc.disk;

import java.util.List;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.sampler.Program;

public class ProgramLoader implements Runnable {

	private Program result;
	private final Mpc mpc;
	private final MpcFile file;
	private final boolean replace;

	private Thread thread;

	public ProgramLoader(Mpc mpc, MpcFile file, boolean replace) {
		this.mpc = mpc;
		this.file = file;
		this.replace = replace;

	}

	public void loadProgram() {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		PgmToProgramConverter converter = new PgmToProgramConverter(file);
		Program p = converter.get();

		List<String> pgmSoundNames = converter.getSoundNames();
		int[] soundsDestIndex = new int[pgmSoundNames.size()];

		for (int i = 0; i < pgmSoundNames.size(); i++) {

			String ext = "snd";

			MpcFile soundFile = null;
			String soundFileName = pgmSoundNames.get(i).replaceAll(" ", "");

			for (MpcFile f : mpc.getDisk().getFiles())
				if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".SND")) soundFile = f;

			if (soundFile == null) {
				for (MpcFile f : mpc.getDisk().getFiles())
					if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".WAV")) soundFile = f;

				ext = "wav";
			}

			if (soundFile != null) {
				loadSound(soundFileName, ext, soundFile, soundsDestIndex, mpc, replace, i);
			} else {
				if (mpc.getDisk() instanceof JavaDisk) { // making sure akai
															// 16.3 files can be
															// found when using
															// non raw disk
															// access
					ext = "snd";
					soundFileName = soundFileName.substring(0, 8);
					for (MpcFile f : mpc.getDisk().getFiles())
						if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".SND")) soundFile = f;

					if (soundFile == null) {
						for (MpcFile f : mpc.getDisk().getFiles())
							if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".WAV")) soundFile = f;

						ext = "wav";
					}
				}
				if (soundFile == null) notfound(soundFileName, ext);
			}
		}

		ProgramImportAdapter adapter = new ProgramImportAdapter(p, soundsDestIndex);
		result = adapter.get();
		mpc.importLoadedProgram();
		Bootstrap.getGui().getDiskGui().removePopup();
		mpc.getDisk().setBusy(false);
		Bootstrap.getGui().getMainFrame().openScreen("load", "mainpanel");
	}

	private void loadSound(String soundFileName, String ext, MpcFile soundFile, int[] soundsDestIndex, Mpc mpc,
			boolean replace, int loadSoundIndex) {

		int addedSoundIndex = -1;

		SoundLoader sl = new SoundLoader(mpc.getSampler().getSounds(), replace);
		sl.setPartOfProgram(true);
		try {
			addedSoundIndex = sl.loadSound(soundFile);
			if (addedSoundIndex != -1) {
				showPopup(soundFileName, ext, soundFile.length());
				soundsDestIndex[loadSoundIndex] = addedSoundIndex;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showPopup(String name, String ext, int sampleSize) {
		Bootstrap.getGui().getDiskGui().openPopup(AbstractDisk.padRightSpace(name, 16), ext);
		if (mpc.getDisk() instanceof JavaDisk) {
			try {
				int sleepTime = sampleSize / 400;
				if (sleepTime < 300) sleepTime = 300;
				Thread.sleep(sleepTime / 5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void notfound(String soundFileName, String ext) {
		boolean skipAll = Bootstrap.getGui().getDiskGui().getSkipAll();
		System.out.println("not found: " + soundFileName + "." + ext);
		System.out.println("skip all: " + skipAll);
		if (!skipAll) {

			Bootstrap.getGui().getDiskGui().openPopup(soundFileName, ext);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Bootstrap.getGui().getMainFrame().removePopup();
			Bootstrap.getGui().getDiskGui().setWaitingForUser(true);

			Bootstrap.getGui().getDiskGui().setCannotFindFileName(soundFileName);

			Bootstrap.getGui().getMainFrame().openScreen("cantfindfile", "windowpanel");

			while (Bootstrap.getGui().getDiskGui().isWaitingForUser()) {

				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public Program get() {
		return result;
	}

}