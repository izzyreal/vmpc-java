package com.mpc.controls.misc;

public class SecondSeqControls extends AbstractMiscControls {

	public void function(int i) {
		init();
		switch(i) {
		case 0:
			openMain("punch");
			break;
		case 1:
			openMain("trans");
			break;
			
		case 5:
			break;
		}
	}
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("sq")) secondSeqGui.setSq(secondSeqGui.getSq() + notch);
	}

}
