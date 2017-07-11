package com.mpc.disk;

import java.util.List;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.disk.DiskGui;
import com.mpc.sampler.Sound;

public class SoundSaver implements Runnable {

	Mpc mpc = Bootstrap.getGui().getMpc();
	Disk disk = mpc.getDisk();
	DiskGui diskGui = Bootstrap.getGui().getDiskGui();

	MainFrame mainFrame = Bootstrap.getGui().getMainFrame();

	final private List<Sound> sounds;
	final private boolean wav;

	public SoundSaver(List<Sound> sounds, boolean wav) {
		disk.setBusy(true);
		this.sounds = sounds;
		this.wav = wav;
		new Thread(this).start();
	}

	@Override
	public void run() {
		final String ext = wav ? ".WAV" : ".SND";
		for (Sound s : sounds) {
			boolean skip = false;

			String fileName = s.getName().replaceAll(" ", "") + ext;
			mainFrame.popupPanel("Writing " + fileName.toUpperCase(), 85);
			if (disk.checkExists(fileName)) {
				if (diskGui.getSaveReplaceSameSounds()) {
					System.out.println("trying to remove existing sound...");
					boolean success = disk.getFile(fileName).delete();
					System.out.println("Success: " + success);
				} else {
					skip = true;
				}
			}

			if (skip) continue;
			if (!wav) {
				disk.writeSound(s);
			} else {
				disk.writeWav(s);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mainFrame.removePopup();
//		disk.flush();
//		disk.initFiles();
//		mainFrame.panel("save", "mainpanel");
//		disk.setBusy(false);
	}
}
