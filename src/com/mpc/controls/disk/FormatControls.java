package com.mpc.controls.disk;

public class FormatControls extends AbstractDiskControls {

	public void function(int i) {
		init();
		switch(i) {
		case 0:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 1:
			mainFrame.openScreen("save", "mainpanel");
			break;
		}
	}

}
