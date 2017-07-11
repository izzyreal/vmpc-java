package com.mpc.gui.vmpc;

import java.util.Observable;

import javax.swing.SwingUtilities;

public class MidiGui extends Observable {

	private int in;
	private int in1TransmitterNumber = 0;
	private int in2TransmitterNumber = -1;
	private int out;
	private int outAReceiverNumber = -1;
	private int outBReceiverNumber = -1;
		
	public void setIn(int i) {
		System.out.println("setting in to " + i);
		if (i<-1||i>1) return;
		in = i;
		setChanged();
		notifyObservers("in");
	}
	
	public int getIn() {
		return in;
	}
	
	public void setOut(int i) {
		if (i<-1||i>1) return;
		out = i;
		setChanged();
		notifyObservers("out");
	}
	
	public int getOut() {
		return out;
	}

	public void setIn1TransmitterNumber(int i, int max) {
		if (i<-1||i>max) return;
		int difference = i - in1TransmitterNumber;
		if (i == in2TransmitterNumber && in2TransmitterNumber != -1) {
			if (difference < 0 && i + difference < -1) return;
			if (difference > 0 && i + difference > max) return;
			i += difference;
		}
		
		in1TransmitterNumber = i;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setChanged();
				notifyObservers("dev0");
			}
		});
	}
	
	public int getIn1TransmitterNumber() {
		return in1TransmitterNumber;
	}
	
	public void setIn2TransmitterNumber(int i, int max) {
		if (i<-1||i>max) return;
		int difference = i - in2TransmitterNumber;
		if (i == in1TransmitterNumber && in1TransmitterNumber != -1) {
			if (difference < 0 && i + difference < -1) return;
			if (difference > 0 && i + difference > max) return;
			i += (difference);
		}
		in2TransmitterNumber = i;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setChanged();
				notifyObservers("dev0");
			}
		});

	}
	
	public int getIn2TransmitterNumber() {
		return in2TransmitterNumber;
	}
	
	public void setOutAReceiverNumber(int i, int max) {
		if (i<-1||i>max) return;
		int difference = i - outAReceiverNumber;
		if (i == outBReceiverNumber && outBReceiverNumber != -1) {
			if (difference < 0 && i + difference < -1) return;
			if (difference > 0 && i + difference > max) return;
			i += (difference);
		}
		outAReceiverNumber = i;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setChanged();
				notifyObservers("dev1");
			}
		});
	}
	
	public int getOutAReceiverIndex() {
		return outAReceiverNumber;
	}
	
	public void setOutBReceiverNumber(int i, int max) {
		if (i<-1||i>max) return;
		int difference = i - outBReceiverNumber;
		if (i == outAReceiverNumber && outAReceiverNumber != -1) {
			if (difference < 0 && i + difference < -1) return;
			if (difference > 0 && i + difference > max) return;
			i += (difference);
		}
		outBReceiverNumber = i;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setChanged();
				notifyObservers("dev1");
			}
		});
	}
	
	public int getOutBReceiverIndex() {
		return outBReceiverNumber;
	}
}