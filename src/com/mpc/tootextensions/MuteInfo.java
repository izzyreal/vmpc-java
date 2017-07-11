package com.mpc.tootextensions;
public class MuteInfo {

	private int note;
	private int drum;

	public void setNote(int note) {
		this.note = note;
	}
	
	public void setDrum(int drum) {
		this.drum = drum;
	}
	
	public int getNote() {
		return note;
	}
	
	public int getDrum() {
		return drum;
	}
	
	public boolean muteMe(int note, int drum) {
		if (this.note == note && this.drum == drum) return true;
		return false;
	}
}
