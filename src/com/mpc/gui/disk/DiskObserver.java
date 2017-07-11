package com.mpc.gui.disk;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.disk.AbstractDisk;
import com.mpc.disk.Disk;
import com.mpc.disk.MpcFile;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.disk.window.DiskWindowGui;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.gui.sampler.SoundGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class DiskObserver implements Observer {
	private String[] views = { "All Files", ".SND", ".PGM", ".APS", ".MID", ".ALL", ".WAV", ".SEQ", ".SET" };
	private String[] types = { "Save All Sequences & Songs", "Save a Sequence", "Save All Program and Sounds",
			"Save a Program & Sounds", "Save a Sound", "Copy Operating System" };

	private String[] pgmSaveNames = { "PROGRAM ONLY", "WITH SOUNDS", "WITH .WAV" };
	private String[] apsSaveNames = { "APS ONLY", "WITH SOUNDS", "WITH .WAV" };

	private MainFrame mainFrame;

	private DiskGui diskGui;
	private SoundGui soundGui;
	private Disk disk;
	private Sequencer sequencer;
	private Sampler sampler;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;
	private Program program;
	private SamplerGui samplerGui;

	// load/save
	private JTextField viewField;
	private JTextField directoryField;
	private JTextField fileField;
	private JTextField typeField;
	private JTextField deviceField;
	private JLabel loadFileNameLabel;
	private JLabel sizeLabel;
	private JLabel freesndLabel;
	private JLabel freeseqLabel;
	private JLabel freeLabel;

	// keep sound
	private JTextField loadReplaceSoundField;
	private JTextField assignToNoteField;

	private LayeredScreen slp;
	private String csn;
	private JTextField loadIntoField;
	private DiskWindowGui diskWindowGui;
	private JLabel nameLabel;
	private JLabel fileLabel;
	private JTextField saveField;
	private JTextField replaceSameSoundsField;
	private JTextField fileTypeField;
	private JTextField saveAsField;

	public DiskObserver(Mpc mpc, Gui gui) {
		samplerGui = gui.getSamplerGui();
		soundGui = gui.getSoundGui();
		diskWindowGui = gui.getDirectoryWindowGui();

		diskWindowGui.deleteObservers();
		diskWindowGui.addObserver(this);

		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		soundGui.deleteObservers();
		soundGui.addObserver(this);

		mainFrame = gui.getMainFrame();
		disk = mpc.getDisk();
		sampler = mpc.getSampler();
		sequencer = mpc.getSequencer();
		sequencer.deleteObservers();
		sequencer.addObserver(this);
		diskGui = gui.getDiskGui();
		slp = mainFrame.getLayeredScreen();

		if (!mpc.getAudioMidiServices().isDisabled()) {
			int candidate = samplerGui.getTrackDrum();
			if (candidate < 0) candidate = 0;
			mpcSoundPlayerChannel = sampler.getDrum(candidate);
			mpcSoundPlayerChannel.deleteObservers();
			mpcSoundPlayerChannel.addObserver(this);
			program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());
		}

		slp = mainFrame.getLayeredScreen();
		csn = slp.getCurrentScreenName();

		diskGui.addObserver(this);
		if (disk != null) ((Observable) disk).addObserver(this);

		if (csn.equals("load")) {
			viewField = mainFrame.lookupTextField("view");
			directoryField = mainFrame.lookupTextField("directory");
			fileField = mainFrame.lookupTextField("file");
			sizeLabel = mainFrame.lookupLabel("size");
			freesndLabel = mainFrame.lookupLabel("freesnd");
			freeseqLabel = mainFrame.lookupLabel("freeseq");
			displayView();
			if (disk != null) {
				displayDirectory();
				displayFile();
				displaySize();
			}
			displayFreeSnd();
			freeseqLabel.setText("  2640K");
		}

		if (csn.equals("save")) {
			typeField = mainFrame.lookupTextField("type");
			fileField = mainFrame.lookupTextField("file");
			deviceField = mainFrame.lookupTextField("device");
			sizeLabel = mainFrame.lookupLabel("size");
			freeLabel = mainFrame.lookupLabel("free");
			displaySize();
			displayType();
			displayFile();
			displayDevice();
			displayFree();
		}

		if (csn.equals("loadaprogram")) {
			loadReplaceSoundField = mainFrame.lookupTextField("loadreplacesound");
			displayLoadReplaceSound();
		}

		if (csn.equals("saveaprogram")) {
			fileLabel = mainFrame.lookupLabel("file");
			saveField = mainFrame.lookupTextField("save");
			replaceSameSoundsField = mainFrame.lookupTextField("replacesamesounds");
			displayFile();
			displaySave();
			displayReplaceSameSounds();
		}

		if (csn.equals("saveapsfile")) {
			fileField = mainFrame.lookupTextField("file");
			saveField = mainFrame.lookupTextField("save");
			replaceSameSoundsField = mainFrame.lookupTextField("replacesamesounds");
			displayFile();
			displaySave();
			displayReplaceSameSounds();
		}

		if (csn.equals("loadasound")) {
			loadFileNameLabel = mainFrame.lookupLabel("loadsoundfilename");
			loadFileNameLabel.setText("File:" + diskGui.getSelectedFileName());
			assignToNoteField = mainFrame.lookupTextField("assigntonote");
			displayAssignToNote();
		}

		if (csn.equals("saveasound")) {
			fileField = mainFrame.lookupTextField("file");
			fileTypeField = mainFrame.lookupTextField("filetype");
			displayFile();
			displayFileType();
		}

		if (csn.equals("loadasequence")) {
			fileLabel = mainFrame.lookupLabel("file");
			loadIntoField = mainFrame.lookupTextField("loadinto");
			nameLabel = mainFrame.lookupLabel("name");

			displayFile();
			displayLoadInto();

		}

		if (csn.equals("saveasequence")) {
			saveAsField = mainFrame.lookupTextField("saveas");
			fileField = mainFrame.lookupTextField("file");
			fileLabel = mainFrame.lookupLabel("file");

			displaySaveAs();
			displayFile();
		}

		if (csn.equals("cantfindfile")) {
			fileField = mainFrame.lookupTextField("file");
			fileField.setText(diskGui.getCannotFindFileName());
		}

	}

	private void displaySaveAs() {
		saveAsField.setText("MIDI FILE TYPE " + diskGui.getSaveSequenceAs());
	}

	private void displayFileType() {
		fileTypeField.setText(diskGui.getFileTypeSaveSound() == 0 ? "MPC2000" : "WAV");
	}

	private void displaySave() {
		if (csn.equals("saveaprogram")) {
			saveField.setText(pgmSaveNames[diskGui.getPgmSave()]);
		}
		if (csn.equals("saveapsfile")) {
			saveField.setText(apsSaveNames[diskGui.getPgmSave()]);
		}
	}

	private void displayReplaceSameSounds() {
		replaceSameSoundsField.setText(diskGui.getSaveReplaceSameSounds() ? "YES" : "NO");
	}

	private void displayFree() {
		String space = "" + File.listRoots()[0].getFreeSpace();
		if (space.length() > 9) space = space.substring(0, 3) + "GB";
		freeLabel.setText(space);
	}

	private void displayDevice() {
		deviceField.setText("SCSI-1");
	}

	private void displayFreeSnd() {
		freesndLabel.setText(" " + Util.padLeftSpace("" + (sampler.getFreeSampleSpace() / 1000), 5) + "K");
	}

	private void displayAssignToNote() {

		int nn = samplerGui.getNote();
		int pn = program.getPadNumberFromNote(nn);

		String padName = pn == -1 ? "OFF" : sampler.getPadName(pn);
		String noteName = nn == 34 ? "--" : "" + (nn);
		assignToNoteField.setText(noteName + "/" + padName);
	}

	private void displaySize() {

		if (csn.equals("load")) {
			if (((Disk) disk).getFileNames().size() == 0) {
				sizeLabel.setText("      K");
				return;
			}
			// System.out.println(f.getName() + " is causing trouble");
			sizeLabel.setText(Util.padLeftSpace("" + diskGui.getFileSize(diskGui.getFileLoad()) + "K", 7));
		}

		if (csn.equals("save")) {
			int size = 0;
			switch (diskGui.getType()) {
			case 0:
				size = sequencer.getUsedSequenceCount() * 25;
				break;
			case 1:
				size = sequencer.getActiveSequence().isUsed()
						? 10 + (int) (sequencer.getActiveSequence().getEventCount() / 1000) : -1;
				break;
			case 2:
				size = sampler.getProgramCount() * 4;
				break;
			case 3:
				size = 4;
				break;
			case 4:
				size = sampler.getSoundCount() == 0 ? -1
						: sampler.getSound(soundGui.getSoundIndex()).getSampleData().length * 2 / 1000;
				break;
			case 5:
				size = 512;
				break;
			}
			sizeLabel.setText(Util.padLeftSpace("" + (size == -1 ? 0 : size), 6) + "K");
		}
	}

	private void displayLoadInto() {
		int seqn = diskWindowGui.getLoadInto();
		loadIntoField.setText(Util.padLeft2Zeroes(seqn + 1));
		nameLabel.setText("-" + sequencer.getSequence(seqn).getName());
	}

	private void displayFile() {

		if (csn.equals("saveapsfile")) {
			fileField.setText(Bootstrap.getGui().getNameGui().getName());
		}

		if (csn.equals("saveasound")) {
			fileField.setText(Bootstrap.getGui().getNameGui().getName());
		}

		if (csn.equals("saveaprogram")) {
			fileLabel.setText(Bootstrap.getGui().getNameGui().getName() + ".PGM");
			return;
		}

		if (csn.equals("loadasequence")) {
			MpcSequence s = diskWindowGui.getSequence();
			fileLabel.setText("File:" + Util.padFileName16(s.getName()).toUpperCase() + ".MID");
			return;
		}

		if (csn.equals("saveasequence")) {
			String name = Bootstrap.getGui().getNameGui().getName();
			fileField.setText(name.substring(0, 1));
			fileLabel.setText(name.substring(1));
		}

		if (csn.equals("load")) {
			if (((Disk) disk).getFileNames().size() == 0) {
				fileField.setText("");
				return;
			}
			String selectedFileName = diskGui.getSelectedFileName();
			MpcFile selectedFile = diskGui.getSelectedFile();
			if (selectedFileName.length() != 0 && selectedFile != null && selectedFile.isDirectory()) {
				fileField.setText("\u00C3" + padExtensionRight((AbstractDisk.splitName(selectedFileName)[0]), 16));
			} else {
				fileField.setText(padExtensionRight((selectedFileName), 16));
			}
		}

		if (csn.equals("save")) {
			String file = "";
			switch (diskGui.getType()) {
			case 0:
				file = "ALL_SEQ_SONG1";
				break;
			case 1:
				String num = Util.padLeft2Zeroes(sequencer.getActiveSequenceIndex() + 1);
				String name = sequencer.getActiveSequence().getName();
				file = num + "-" + name;
				break;
			case 2:
				file = "ALL_PROGRAM";
				break;
			case 3:
				file = sampler.getProgram(mpcSoundPlayerChannel.getProgram()).getName();
				break;
			case 4:
				file = sampler.getSoundCount() == 0 ? "" : sampler.getSound(soundGui.getSoundIndex()).getName();
				break;
			}
			fileField.setText(file);
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		String parameter = (String) arg;

		switch (parameter) {

		case "savesequenceas":
			displaySaveAs();
			break;

		case "filetype":
			displayFileType();
			break;

		case "pgm":
			displayFile();
			break;

		case "save":
			displaySave();
			break;

		case "savereplacesamesounds":
			displayReplaceSameSounds();
			break;

		case "loadinto":
			displayLoadInto();
			break;

		case "directory":
			displayDirectory();
			break;

		case "view":
			displayView();
			displayDirectory();
			displayFile();
			displaySize();
			break;

		case "type":
			displayType();
		case "fileselect":
		case "filesave":
			displayFile();
			displaySize();
			break;

		case "loadreplacesound":
			displayLoadReplaceSound();
			break;

		case "removepopup":
			mainFrame.removePopup();
			break;

		case "padandnote":
		case "note":
			if (csn.equals("loadasound")) displayAssignToNote();
			break;

		case "loadasound":
			mainFrame.openScreen("loadasound", "windowpanel");
			break;

		case "seqnumbername":
		case "soundnumber":
			displayFile();
			displaySize();
			break;
		}

	}

	private void displayType() {
		typeField.setText(types[diskGui.getType()]);
	}

	private void displayLoadReplaceSound() {
		loadReplaceSoundField.setText(diskGui.getLoadReplaceSound() ? "YES" : "NO(FASTER)");
	}

	private void displayView() {
		viewField.setText(views[diskGui.getView()]);
	}

	private void displayDirectory() {
		directoryField.setText(((Disk) disk).getDirectoryName());
	}

	private String padExtensionRight(String s, int n) {
		int periodIndex = s.lastIndexOf('.');
		if (periodIndex == -1) return s;
		String fileName = s.substring(0, periodIndex);
		String extension = s.substring(periodIndex, s.length());
		fileName = String.format("%1$-" + n + "s", fileName);
		return fileName + extension;
	}
}