package com.mpc.sequencer;

public class MixerEvent extends Event {
    private int mixerParameter;
    private int padNumber;
    private int mixerParameterValue;

	public void setParameter(int i) {
		if (i<0||i>3) return;
        mixerParameter = i;
        setChanged();
        notifyObservers("stepeditor");
    }

    public int getParameter() {
        return mixerParameter;
    }

	public void setPadNumber(int i) {
		if (i<0||i>63) return;
        padNumber = i;
        setChanged();
        notifyObservers("stepeditor");
    }

    public int getPad() {
        return padNumber;
    }

	public void setValue(int i) {
		if (i<0||i>100) return;
        mixerParameterValue = i;
        setChanged();
        notifyObservers("stepeditor");
    }

    public int getValue() {
        return mixerParameterValue;
    }
}
