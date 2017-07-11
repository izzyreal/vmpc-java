package com.mpc.controls.disk.window;

import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.file.all.AllLoader;

public class MPC2000XLAllFileControls extends AbstractDiskControls {

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			diskGui.setSequencesFromAllFile((new AllLoader(diskGui.getSelectedFile()).getSequences()));
			mainFrame.openScreen("loadasequencefromall", "windowpanel");
			break;
		case 3:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 4:
			new AllLoader(mpc, diskGui.getSelectedFile());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

}
