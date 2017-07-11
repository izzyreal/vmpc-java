package com.mpc.controls.disk.window;

import com.mpc.controls.disk.AbstractDiskControls;

public class LoadASequenceControls extends AbstractDiskControls {

	@Override
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("loadinto")) diskWindowGui.setLoadInto(diskWindowGui.getLoadInto() + notch);
	}

	@Override
	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 4:
			sequencer.setSequence(diskWindowGui.getLoadInto(), diskWindowGui.getSequence());
			sequencer.setSelectedSequenceIndex(diskWindowGui.getLoadInto());
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}

}
