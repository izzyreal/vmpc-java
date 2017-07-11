package com.mpc.controls.other;

public class InitControls extends AbstractOtherControls {

	public void function(int i) {
		init();
		switch(i) {
		case 0:
			openMain("others");
			break;
		case 2:
			openMain("ver");
			break;
			
		case 5:
			break;
		}
	}
	
}
