package com.mpc.controls.disk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.mpc.disk.AbstractDisk;
import com.mpc.disk.Disk;
import com.mpc.file.mid.MidiReader;
import com.mpc.file.wav.WavFileException;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;

public class LoadControls extends AbstractDiskControls {

	@Override
	public void function(int i) {
		init();
		switch (i) {
		case 1:
			mainFrame.openScreen("save", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("format", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("setup", "mainpanel");
			break;
		case 5:
			if (disk == null) return;
			if (disk.getFileNames().size() == 0) return;
			selectedFile = diskGui.getSelectedFile();
			String extension = AbstractDisk.splitName(selectedFile.getName())[1];
			if (extension.equalsIgnoreCase("snd") || extension.equalsIgnoreCase("wav")) {
				try {
					mpc.loadSound(false);
				} catch (InterruptedException | IOException | WavFileException e) {
					e.printStackTrace();
				}
				return;
			}

			if (extension.equalsIgnoreCase("pgm")) {
				mainFrame.openScreen("loadaprogram", "windowpanel");
				return;
			}

			if (extension.equalsIgnoreCase("mid")) {
				MpcSequence newSeq = new MpcSequence(mpc, Sequencer.defaultTrackNames);
				newSeq.init(2);
				MidiReader mr = new MidiReader(mpc, selectedFile, newSeq);

				try {
					diskWindowGui.setSequence(mr.getSequence());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return;
				}

				mainFrame.openScreen("loadasequence", "windowpanel");
				List<Integer> usedSeqs = sequencer.getUsedSequenceIndexes();
				int index;
				for (index = 0; index < 99; index++) {
					if (!usedSeqs.contains(index)) break;
				}
				diskWindowGui.setLoadInto(index);
			}

			if (extension.equalsIgnoreCase("all")) {
				mainFrame.openScreen("mpc2000xlallfile", "windowpanel");
				return;

			}

			if (extension.equalsIgnoreCase("aps")) {
				mainFrame.openScreen("loadapsfile", "windowpanel");
				return;
			}

			if (diskGui.isSelectedFileDirectory()) {

				if (((Disk) disk).moveForward(diskGui.getSelectedFile().getName())) {
					diskGui.setView(diskGui.getView());
				}
				return;
			}
			break;
		}
	}

	@Override
	public void openWindow() {
		init();
		if (param.equals("directory") || param.equals("file")) {
			if (mpc.getDisk() == null) return;
			directoryGui.setPreviousScreenName("load");
			directoryGui.findYOffset0();
			directoryGui.setYOffset1(diskGui.getFileLoad());
			mainFrame.openScreen("directory", "windowpanel");
			return;
		}
	}

	@Override
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("view")) diskGui.setView(diskGui.getView() + notch);
		if (param.equals("file")) diskGui.setSelectedFileNumberLimited(diskGui.getFileLoad() + notch);
	}
}
