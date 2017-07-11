package com.mpc.controls.disk.window;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.disk.MpcFile;
import com.mpc.sampler.Sound;

public class SaveASoundControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("file") && notch > 0) sampler.setSoundGuiPrevSound();
		if (param.equals("file") && notch < 0) sampler.setSoundGuiNextSound();
		if (param.equals("filetype")) diskGui.setFileTypeSaveSound(diskGui.getFileTypeSaveSound() + notch);

	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("save", "mainpanel");
			break;
		case 4:
			Sound s = sampler.getSound(soundGui.getSoundIndex());
			int type = diskGui.getFileTypeSaveSound();
			String ext = type == 0 ? ".SND" : ".WAV";
			String fileName = Util.getFileName(nameGui.getName()) + ext;
			if (disk.checkExists(fileName)) {
				mainFrame.openScreen("filealreadyexists", "windowpanel");
				return;
			}
			disk.flush();
			disk.initFiles();
			MpcFile f = disk.newFile(fileName);
			if (type == 0) {
				disk.writeSound(s, f);
			} else {
				disk.writeWav(s, f);
			}
			disk.flush();
			disk.initFiles();
			mainFrame.openScreen("save", "mainpanel");
			break;
		}
	}
}
