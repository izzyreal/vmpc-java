package com.mpc.gui.sampler.window;

import java.util.Observable;

import com.mpc.gui.Bootstrap;

public class EditSoundGui extends Observable {

	private int edit;
	private int insertSoundNumber;
	private int timeStretchRatio = 10000;
	private int timeStretchPresetNumber;
	private int timeStretchAdjust;
	private String newName;
	private String previousScreenName;
	private String[] newNames = new String[16];
	private boolean createNewProgram;
	private int endMargin = 30;

	public void setNewName(String s) {
		newName = s;
	}

	public String getNewName() {
		return newName;
	}

	public void setEdit(int i) {
		if (i < 0 || i > 8)
			return;
		if (!previousScreenName.equals("zone") && i > 7)
			return;
		edit = i;
		setChanged();
		notifyObservers("edit");
	}

	public int getEdit() {
		return edit;
	}

	public int getInsertSndNr() {
		return insertSoundNumber;
	}

	public void setInsertSndNr(int i) {
		if (i < 0
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getSoundCount() - 1)
			return;
		insertSoundNumber = i;
		setChanged();
		notifyObservers("insertsoundnumber");
	}

	public void setTimeStretchRatio(int i) {
		if (i < 5000 || i > 20000)
			return;
		timeStretchRatio = i;
		setChanged();
		notifyObservers("timestretchratio");
	}

	public int getTimeStretchRatio() {
		return timeStretchRatio;
	}

	public void setTimeStretchPresetNumber(int i) {
		if (i < 0 || i > 53)
			return;
		timeStretchPresetNumber = i;
		setChanged();
		notifyObservers("timestretchpresetnumber");
	}

	public int getTimeStretchPresetNumber() {
		return timeStretchPresetNumber;
	}

	public void setTimeStretchAdjust(int i) {
		if (i < -100 || i > 100)
			return;
		timeStretchAdjust = i;
		setChanged();
		notifyObservers("timestretchadjust");
	}

	public int getTimeStretchAdjust() {
		return timeStretchAdjust;
	}

	public void setPreviousScreenName(String s) {
		previousScreenName = s;
	}

	public String getPreviousScreenName() {
		return previousScreenName;
	}

	public void setNewName(int i, String s) {
		newNames[i] = s;
	}

	public String getNewName(int i) {
		return newNames[i];
	}

	public boolean getCreateNewProgram() {
		return createNewProgram;
	}

	public void setCreateNewProgram(boolean b) {
		createNewProgram = b;
		setChanged();
		notifyObservers("createnewprogram");
	}

	public int getEndMargin() {
		return endMargin;
	}

	public void setEndMargin(int i) {
		if (i < 0 || i > 99)
			return;
		endMargin = i;
		setChanged();
		notifyObservers("endmargin");
	}
}