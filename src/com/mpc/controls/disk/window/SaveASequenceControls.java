package com.mpc.controls.disk.window;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.sequencer.MpcSequence;

public class SaveASequenceControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("saveas")) diskGui.setSaveSequenceAs(diskGui.getSaveSequenceAs() + notch);
		if (param.equals("file")) {
//			gui.getNameGui().setName(diskGui.getSaveSequenceName());
			gui.getNameGui().setParameterName("savesequencename");
			mainFrame.openScreen("name", "dialogpanel");
		}

	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("save", "mainpanel");
			break;
		case 4:
			String fileName = Util.getFileName(nameGui.getName()) + ".MID";

			if (disk.checkExists(fileName)) {
				mainFrame.openScreen("filealreadyexists", "windowpanel");
				return;
			}
			MpcSequence seq = sequencer.getActiveSequence();
			disk.writeSequence(seq, fileName);
			mainFrame.openScreen("save", "mainpanel");
			break;
		}
	}
}
