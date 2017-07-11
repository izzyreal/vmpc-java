package com.mpc.controls.other;

public class VerControls extends AbstractOtherControls {

	
	public void function(int i) {
		init();
		switch(i) {
		case 0:
			openMain("others");
			break;
		case 1:
			openMain("init");
			break;
		}
	}

}
