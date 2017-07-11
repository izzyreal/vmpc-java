package com.mpc.controls.other;

import com.mpc.controls.AbstractControls;
import com.mpc.controls.KbMouseController;

public class OthersControls extends AbstractControls {

	public void function(int i) {
		init();
		switch(i) {
		case 1:
			openMain("init");
			break;
		case 2:
			openMain("ver");
			break;
		}
	}
	
	public void turnWheel(int increment) {
		init();
		int notch = getNotch(increment);

		if (param.equals("tapaveraging") && !KbMouseController.altIsPressed)
			gui.getOthersGui().setTapAveraging(gui.getOthersGui().getTapAveraging() + notch);
		
		if (KbMouseController.altIsPressed) gui.getOthersGui().setContrast(gui.getOthersGui().getContrast() + notch);

	}

}
