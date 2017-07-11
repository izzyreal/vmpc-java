package com.mpc.sequencer;

public class PitchBendEvent extends Event {
    private int pitchBendAmount;

	public void setAmount(int i) {
		if (i<-8192||i>8191) return;
        pitchBendAmount = i;
        setChanged();
        notifyObservers("stepeditor");
    }

    public int getAmount() {
        return pitchBendAmount;
    }
}
