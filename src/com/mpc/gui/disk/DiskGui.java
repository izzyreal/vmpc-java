package com.mpc.gui.disk;

import java.util.List;
import java.util.Observable;

import com.mpc.Mpc;
import com.mpc.disk.MpcFile;
import com.mpc.gui.Bootstrap;
import com.mpc.sequencer.MpcSequence;

public class DiskGui extends Observable {
	
	private List<MpcSequence> sequencesFromAllFile;

	private int fileLoad;
	private int delete;
	private int view;
	private int type;

	private boolean loadReplaceSound;
	private boolean clearProgramWhenLoading;

	private boolean waitingForUser = false;
	private boolean skipAll;
	private String cannotFindFileName;

	private Mpc mpc;
	private boolean saveReplaceSameSounds;
	private int save;
	private int fileTypeSaveSound;
	private int saveSequenceAs = 1;

	private boolean dontAssignSoundWhenLoading;

//	private String saveApsName;
//	private String saveSequenceName;
//	private String saveAllName;

	public DiskGui(Mpc mpc) {
		this.mpc = mpc;
		loadReplaceSound = false;
	}
	
	public void setType(int i) {
		if (i < 0 || i > 5) return;
		type = i;
		setChanged();
		notifyObservers("type");
	}

	public int getType() {
		return type;
	}

	public MpcFile getSelectedFile() {
		return mpc.getDisk().getFile(fileLoad);
	}

	public String getSelectedFileName() {
		return mpc.getDisk().getFileNames().get(fileLoad);
	}

	public void setDelete(int i) {
		if (i < 0 || i > 8) return;
		delete = i;
		setChanged();
		notifyObservers("delete");
	}

	public int getDelete() {
		return delete;
	}

	public boolean isSelectedFileDirectory() {
		return mpc.getDisk().getFile(fileLoad).isDirectory();
	}

	public long getFileSize(int i) {
		if (mpc.getDisk().getFile(i) == null || mpc.getDisk().getFile(i).isDirectory()) return 0;
		return mpc.getDisk().getFile(i).length() / 1024;
	}

	public int getView() {
		return view;
	}

	public void setView(int i) {
		if (i < 0 || i > 8) return;
		view = i;
		mpc.getDisk().initFiles();
		fileLoad = 0;
		setChanged();
		notifyObservers("view");
	}

	public boolean getLoadReplaceSound() {
		return loadReplaceSound;
	}

	public void setLoadReplaceSound(boolean b) {
		loadReplaceSound = b;
		setChanged();
		notifyObservers("loadreplacesound");
	}

	public int getFileLoad() {
		return fileLoad;
	}
	
	public void setSelectedFileNumberLimited(int i) {
		if (i < 0 || i > mpc.getDisk().getFileNames().size() - 1) return;
		fileLoad = i;
		setChanged();
		notifyObservers("fileselect");
	}

	public void setFileLoad(int i) {
		if (i<0) return;
		fileLoad = i;
		setChanged();
		notifyObservers("fileload");
	}

	public void openPopup(String soundFileName, String extension) {
		Bootstrap.getGui().getMainFrame().popupPanel("LOADING " + soundFileName + "."
				+ extension.toUpperCase(), 85);
	}

	public void removePopup() {
		Bootstrap.getGui().getMainFrame().removePopup();
	}

	public boolean getClearProgramWhenLoading() {
		return clearProgramWhenLoading;
	}

	public void setClearProgramWhenLoading(boolean b) {
		clearProgramWhenLoading = b;
	}

	public void setWaitingForUser(boolean b) {
		waitingForUser = b;
	}

	public boolean isWaitingForUser() {
		return waitingForUser;
	}

	public void setSkipAll(boolean b) {
		skipAll = b;
	}

	public boolean getSkipAll() {
		return skipAll;
	}

	public void setCannotFindFileName(String s) {
		cannotFindFileName = s;
	}

	public String getCannotFindFileName() {
		return cannotFindFileName;
	}

	public int getPgmSave() {
		return save;
	}
	
	public boolean getSaveReplaceSameSounds() {
		return saveReplaceSameSounds;
	}

	public void setSaveReplaceSameSounds(boolean b) {
		saveReplaceSameSounds = b;
		setChanged();
		notifyObservers("savereplacesamesounds");
	}

	public void setSave(int i) {
		if (i<0||i>2) return;
		save = i;
		setChanged();
		notifyObservers("save");
	}

	public int getFileTypeSaveSound() {
		return fileTypeSaveSound;
	}
	
	public void setFileTypeSaveSound(int i) {
		if (i<0||i>1) return;
		fileTypeSaveSound = i;
		setChanged();
		notifyObservers("filetype");
	}

	public int getSaveSequenceAs() {
		return saveSequenceAs;
	}
	
	public void setSaveSequenceAs(int i) {
		if (i<0||i>1) return;
		saveSequenceAs = i;
		setChanged();
		notifyObservers("savesequenceas");
	}

//	public String getSaveSequenceName() {
//		return saveSequenceName;
//	}
//	
//	public void setSaveSequenceName(String s) {
//		saveSequenceName = s;
//	}
	
	public boolean dontAssignSoundWhenLoading() {
		return dontAssignSoundWhenLoading;
	}
	
	public void setDontAssignSoundWhenLoading(boolean b) {
		dontAssignSoundWhenLoading = b;
		setChanged();
		notifyObservers("padandnote");
	}

//	public String getSaveApsName() {
//		return saveApsName;
//	}
//	
//	public void setSaveApsName(String s) {
//		saveApsName = s;
//	}
	
	public void setSequencesFromAllFile(List<MpcSequence> sequences) {
		sequencesFromAllFile = sequences;
	}
	
	public List<MpcSequence> getSequencesFromAllFile() {
		return sequencesFromAllFile;
	}

//	public String getSaveAllName() {
//		return saveAllName;
//	}
//	
//	public void setSaveAllName(String s) {
//		saveAllName = s;
//	}
}