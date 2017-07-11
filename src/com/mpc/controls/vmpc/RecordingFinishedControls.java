package com.mpc.controls.vmpc;

public class RecordingFinishedControls extends AbstractVmpcControls {

	public void function(int i) {
		init();
		switch(i) {
		case 3:
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}
	
}
