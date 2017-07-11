package com.mpc.gui.sampler.window;

import java.util.Observable;

import com.mpc.gui.Bootstrap;

public class SamplerWindowGui extends Observable {

	public static String[] padFocusNames = { "a3", "b3", "c3", "d3", "a2",
			"b2", "c2", "d2", "a1", "b1", "c1", "d1", "a0", "b0", "c0", "d0" };

	// Delete Program
	private int deletePgm = 0;

	// Copy Program
	private int pgm0 = 0;
	private int pgm1 = 0;

	// Create New Program + Auto Chromatic Assignment
	private String newName = "";

	// Create New Program
	private int newProgramChange = 1;

	// Initialize Pad Assign
	private boolean initPadAssignMaster = false;

	// Copy Note Parameters
	private int prog0 = 0;
	private int prog1 = 0;
	private int note0 = 0;
	private int note1 = 0;

	// Auto Chromatic Assignment
	private int autoChromAssSnd = 0;
	private int tune = 0;
	private int originalKey = 60;

	public int getDeletePgm() {
		return deletePgm;
	}

	public void setDeletePgm(int i) {

		if (i < 0
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getProgramCount() - 1)
			return;

		deletePgm = i;

		setChanged();
		notifyObservers("deletepgm");
	}

	public int getPgm0() {
		return pgm0;
	}

	public void setPgm0(int i) {

		if (i < 0
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getProgramCount() - 1)
			return;

		this.pgm0 = i;

		setChanged();
		notifyObservers("pgm0");

	}

	public int getPgm1() {
		return pgm1;
	}

	public void setPgm1(int i) {

		if (i < 0
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getProgramCount() - 1)
			return;

		pgm1 = i;

		setChanged();
		notifyObservers("pgm1");

	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String s) {
		newName = s;
	}

	public int getNewProgramChange() {
		return newProgramChange;
	}

	public void setNewProgramChange(int i) {

		if (i < 1 || i > 128)
			return;

		newProgramChange = i;

		setChanged();
		notifyObservers("newprogramchange");

	}

	public static int getPadNumberFromFocus() {

		int pn = -1;

		for (int i = 0; i < padFocusNames.length; i++) {
			if (padFocusNames[i].equals(Bootstrap
					.getGui()
					.getMainFrame()
					.getFocus(
							Bootstrap.getGui().getMainFrame()
									.getLayeredScreen().getWindowPanel()))) {
				pn = i;
				break;
			}
		}

		pn += (Bootstrap.getGui().getSamplerGui().getBank() * 16);

		return pn;
	}

	public static String getFocusFromPadNumber() {
		int padNr = Bootstrap.getGui().getSamplerGui().getPad();
		while (padNr > 15)
			padNr -= 16;
		return padFocusNames[padNr];
	}

	public void setInitPadAssignMaster(boolean b) {

		initPadAssignMaster = b;

		setChanged();
		notifyObservers("initpadassign");
	}

	public boolean isInitPadAssignMaster() {
		return initPadAssignMaster;
	}

	public int getProg0() {
		return prog0;
	}

	public void setProg0(int i) {
		if (i < 0
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getProgramCount() - 1)
			return;
		prog0 = i;
		setChanged();
		notifyObservers("prog0");
	}

	public int getProg1() {
		return prog1;
	}

	public void setProg1(int i) {
		if (i < 0
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getProgramCount() - 1)
			return;
		prog1 = i;
		setChanged();
		notifyObservers("prog1");
	}

	public int getNote0() {
		return note0;
	}

	public void setNote0(int i) {
		if (i < 0 || i > 63)
			return;
		note0 = i;
		setChanged();
		notifyObservers("note0");
	}

	public int getNote1() {
		return note1;
	}

	public void setNote1(int i) {
		if (i < 0 || i > 63)
			return;
		note1 = i;
		setChanged();
		notifyObservers("note1");
	}

	public int getAutoChromAssSnd() {
		return autoChromAssSnd;
	}

	public void setAutoChromAssSnd(int i) {

		if (i < -1
				|| i > Bootstrap.getGui().getMpc().getSampler()
						.getSoundCount())
			return;

		this.autoChromAssSnd = i;
		setChanged();
		notifyObservers("autochromasssnd");
	}

	public int getTune() {
		return tune;
	}

	public void setTune(int i) {
		if (i < -240 || i > 240)
			return;
		tune = i;
		setChanged();
		notifyObservers("tune");
	}

	public int getOriginalKey() {
		return originalKey;
	}

	public void setOriginalKey(int i) {
		if (i < 35 || i > 98)
			return;
		originalKey = i;
		setChanged();
		notifyObservers("originalkey");
	}
}