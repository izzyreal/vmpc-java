package com.mpc.controls.disk.window;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.disk.MpcFile;
import com.mpc.file.all.AllParser;

public class SaveAllFileControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		if (param.equals("file")) {
//			gui.getNameGui().setName(diskGui.getSaveAllName());
			gui.getNameGui().setParameterName("saveallfile");
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
			String allName = Util.getFileName(nameGui.getName());
			mpc.getDisk().initFiles();
			if (mpc.getDisk().checkExists(allName + ".ALL")) {
				mainFrame.openScreen("filealreadyexists", "windowpanel");
				return;
			}
			AllParser allParser = new AllParser(mpc, Util.getFileName(nameGui.getName()));
			MpcFile f = disk.newFile(allName + ".ALL");
			f.setFileData(allParser.getBytes());
			disk.flush();
			disk.initFiles();
			mainFrame.openScreen("save", "mainpanel");
			break;
		}
	}
}
