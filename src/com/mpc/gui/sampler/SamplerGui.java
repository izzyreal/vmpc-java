package com.mpc.gui.sampler;

import java.util.Observable;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;

public class SamplerGui extends Observable {

	private int bank;
	private int pad;
	private int note;
	private static boolean padAssignMaster;
	private String prevScreenName;

	private int prevNote;
	private int prevPad;

	private boolean padToIntSound = true;

	private int input;
	private int threshold = -20;
	private int mode = 1;
	private int time = 100; // seconds
	private int monitor = 1;
	private int preRec = 100; // ms

	private String newName;

	private int selectedDrum;
	
	public SamplerGui() {

		bank = 0;
		pad = 0;
		note = 60;
		prevNote = 60;
		padAssignMaster = false;
		selectedDrum = 0;
	}

	public void setPadAndNote(int pad, int note) {

		if (pad < -1 || pad > 63 || note < 34 | note > 98) return;

		if (prevPad != pad && pad != -1) // Make sure a valid last pressed pad
											// number is remembered.
			prevPad = pad;

		this.pad = pad;
		if (note != 34) // Make sure a valid last selected
											// note number is stored.
			prevNote = note;
		
		this.note = note;

		setChanged();
		notifyObservers("padandnote");
	}

	public int getNote() {
		return note;
	}

	public int getPad() {
		return pad;
	}

	public void setPadAssignMaster(boolean b) {
		if (padAssignMaster == b) return;
		padAssignMaster = b;
		setChanged();
		notifyObservers("padassignmode");
	}

	public static boolean isPadAssignMaster() {
		return padAssignMaster;
	}

	public void setBank(int i) {
		if (i == bank) return;
		if (i < 0 || i > 3) return;
		bank = i;

		Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankA(false);
		Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankB(false);
		Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankC(false);
		Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankD(false);

		if (i == 0) Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankA(true);
		if (i == 1) Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankB(true);
		if (i == 2) Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankC(true);
		if (i == 3) Bootstrap.getGui().getMainFrame().getLedPanel().setPadBankD(true);

		setChanged();
		notifyObservers("bank");

	}

	public int getBank() {
		return bank;
	}

	public void setSelectedDrum(int i) {
		if (i<0||i>3) return;
		selectedDrum = i;
		setChanged();
		notifyObservers("drum");
	}
	
	public int getSelectedDrum() {
		return selectedDrum;
	}
	
	public void setTrackDrum(int i) {
		if (i < 0 || i > 3) return;
		Mpc mpc = Bootstrap.getGui().getMpc();
		MpcSequence seq = mpc.getSequencer().getActiveSequence();
		MpcTrack t = seq.getTrack(mpc.getSequencer().getActiveTrackIndex());
		t.setBusNumber(i + 1);
		setChanged();
		notifyObservers("drum");
	}

	public int getTrackDrum() {
		Mpc mpc = Bootstrap.getGui().getMpc();
		MpcSequence seq = mpc.getSequencer().getActiveSequence();
		MpcTrack t = seq.getTrack(mpc.getSequencer().getActiveTrackIndex());
		return t.getBusNumber() - 1;
	}

	public String getPrevScreenName() {
		return prevScreenName;
	}

	public void setPrevScreenName(String s) {
		prevScreenName = s;
	}

	public int getPrevNote() {
		return prevNote;
	}

	public int getPrevPad() {
		return prevPad;
	}

	public boolean isPadToIntSound() {
		return padToIntSound;
	}

	public void setPadToIntSound(boolean b) {
		padToIntSound = b;
		setChanged();
		notifyObservers("padtointsound");
	}

	public int getInput() {
		return input;
	}

	public void setInput(int i) {
		if (i < 0 || i > 1) return;
		input = i;
		setChanged();
		notifyObservers("input");
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int i) {
		if (i < -64 || i > 0) return;
		threshold = i;
		setChanged();
		notifyObservers("threshold");
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int i) {
		if (i < 0 || i > 2) return;
		mode = i;
		setChanged();
		notifyObservers("mode");
	}

	public int getTime() {
		return time;
	}

	public void setTime(int i) {
		if (i < 0 || i > 3786) return;
		time = i;
		setChanged();
		notifyObservers("time");
	}

	public int getMonitor() {
		return monitor;
	}

	public void setMonitor(int i) {
		if (i < 0 || i > 5) return;
		monitor = i;
		setChanged();
		notifyObservers("monitor");
	}

	public int getPreRec() {
		return preRec;
	}

	public void setPreRec(int i) {
		if (i < 0 || i > 100) return;
		preRec = i;
		setChanged();
		notifyObservers("prerec");
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewName() {
		return newName;
	}

	public void notify(String string) {
		setChanged();
		notifyObservers(string);
	}
}