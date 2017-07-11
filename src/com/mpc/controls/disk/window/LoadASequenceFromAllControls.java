package com.mpc.controls.disk.window;

import com.mpc.controls.disk.AbstractDiskControls;

public class LoadASequenceFromAllControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("file")) diskGui.setFileLoad(diskGui.getFileLoad() + notch);
		if (param.equals("loadinto")) diskWindowGui.setLoadInto(diskWindowGui.getLoadInto() + notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			// play
			break;
		case 3:
			mainFrame.openScreen("mpc2000xlallfile", "windowpanel");
			break;
		case 4:
			sequencer.setSequence(sequencer.getActiveSequenceIndex(),
					diskGui.getSequencesFromAllFile().get(diskGui.getFileLoad()));
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

}
