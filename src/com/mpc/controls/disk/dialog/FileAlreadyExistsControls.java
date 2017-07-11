package com.mpc.controls.disk.dialog;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.disk.MpcFile;
import com.mpc.file.all.AllParser;
import com.mpc.file.aps.ApsSaver;
import com.mpc.sampler.Sound;
import com.mpc.sequencer.MpcSequence;

public class FileAlreadyExistsControls extends AbstractDiskControls {

	public void function(int i) {
		super.function(i);

		switch (i) {
		case 2:
			if (ls.getPreviousScreenName().equals("saveaprogram")) {
				String pfileName = Util.getFileName(nameGui.getName()) + ".PGM";
				boolean success = disk.getFile(pfileName).delete();
				if (success) {
					disk.flush();
					disk.initFiles();
					disk.writeProgram(program, pfileName);
				}
				mainFrame.openScreen("save", "mainpanel");
			} else if (ls.getPreviousScreenName().equals("saveasequence")) {
				String sfileName = Util.getFileName(nameGui.getName()) + ".MID";
				boolean success = disk.getFile(sfileName).delete();
				if (success) {
					disk.flush();
					disk.initFiles();
					disk.writeSequence(sequencer.getActiveSequence(), sfileName);
					mainFrame.openScreen("save", "mainpanel");
				}
				mainFrame.openScreen("save", "mainpanel");
			} else if (ls.getPreviousScreenName().equals("saveapsfile")) {
				String apsName = Util.getFileName(nameGui.getName()) + ".APS";
				boolean success = disk.getFile(apsName).delete();
				if (success) {
					disk.flush();
					disk.initFiles();
					new ApsSaver(mpc, apsName);
				}
			} else if (ls.getPreviousScreenName().equals("saveallfile")) {
				String allName = Util.getFileName(nameGui.getName()) + ".ALL";
				disk.initFiles();
				boolean success = disk.getFile(allName).delete();
				if (success) {
					disk.flush();
					disk.initFiles();
					AllParser allParser = new AllParser(mpc, Util.getFileName(nameGui.getName()));
					MpcFile f = disk.newFile(allName);
					f.setFileData(allParser.getBytes());
					disk.flush();
					disk.initFiles();
					mainFrame.openScreen("save", "mainpanel");								
				}
				
			} else if (ls.getPreviousScreenName().equals("saveasound")) {
				Sound s = sampler.getSound(soundGui.getSoundIndex());
				int type = diskGui.getFileTypeSaveSound();
				String ext = type == 0 ? ".SND" : ".WAV";
				String fileName = Util.getFileName(nameGui.getName()) + ext;
				disk.getFile(fileName).delete();
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
			}
			break;
		case 4:
			if (ls.getPreviousScreenName().equals("saveaprogram")) {
				nameGui.setParameterName("savingpgm");
				mainFrame.openScreen("name", "dialogpanel");
			} else if (ls.getPreviousScreenName().equals("saveasequence")) {
				nameGui.setParameterName("savingmid");
				mainFrame.openScreen("name", "dialogpanel");
			} else if (ls.getPreviousScreenName().equals("saveapsfile")) {
				nameGui.setParameterName("savingaps");
				mainFrame.openScreen("name", "dialogpanel");
			} else if (ls.getPreviousScreenName().equals("saveallfile")) {
				nameGui.setParameterName("saveallfile");
				mainFrame.openScreen("name", "dialogpanel");
			} else if (ls.getPreviousScreenName().equals("saveasound")) {
				nameGui.setParameterName("saveasound");
				mainFrame.openScreen("name", "dialogpanel");
			}
			break;
		}
	}
}
