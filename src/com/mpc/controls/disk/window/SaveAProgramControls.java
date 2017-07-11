package com.mpc.controls.disk.window;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;

public class SaveAProgramControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
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
			String fileName = Util.getFileName(nameGui.getName()) + ".PGM";
			System.out.println("wanting to save as " + fileName);
			if (disk.checkExists(fileName)) {
				nameGui.setName(program.getName());
				mainFrame.openScreen("filealreadyexists", "windowpanel");
				return;
			}
			disk.writeProgram(program, fileName);
			mainFrame.openScreen("save", "mainpanel");
			break;
		}
	}
}
