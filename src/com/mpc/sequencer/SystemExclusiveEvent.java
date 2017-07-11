package com.mpc.sequencer;

public class SystemExclusiveEvent extends Event {
	private byte[] bytes = new byte[2];

   	public void setByteA(int i) {
   		if (i<0||i>255) return;
        bytes[0] = (byte) i;
        setChanged();
        notifyObservers("stepeditor");
    }

    public int getByteA() {
        return bytes[0];
    }

	public void setByteB(int i) {
   		if (i<0||i>255) return;
        bytes[1] = (byte) i;
        setChanged();
        notifyObservers("stepeditor");
    }

    public int getByteB() {
        return bytes[1];
    }
    
    public void setBytes(byte[] ba) {
    	bytes = ba;
    }
    
    public byte[] getBytes() {
    	return bytes;
    }
}