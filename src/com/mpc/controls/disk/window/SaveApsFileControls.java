package com.mpc.controls.disk.window;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.file.aps.ApsSaver;
import com.mpc.gui.Bootstrap;

public class SaveApsFileControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("file")) {
//			gui.getNameGui().setName(diskGui.getSaveApsName());
			gui.getNameGui().setParameterName("saveapsname");
			mainFrame.openScreen("name", "dialogpanel");
		}
		if (param.equals("save")) diskGui.setSave(diskGui.getPgmSave() + notch);
		if (param.equals("replacesamesounds")) diskGui.setSaveReplaceSameSounds(notch > 0);

	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("save", "mainpanel");
			break;
		case 4:
			String apsFileName = Util.getFileName(Bootstrap.getGui().getNameGui().getName()) + ".APS";
			new ApsSaver(mpc, apsFileName);
			break;
		}
	}
}
