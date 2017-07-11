package com.mpc.controls.disk.dialog;

import com.mpc.controls.disk.AbstractDiskControls;

public class DeleteAllFilesControls extends AbstractDiskControls {

	@Override
	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("deletefile", "dialogpanel");
			ls.setPreviousScreenName("directory");
			ls.setPreviousPanel(ls.getWindowPanel());
			break;
		case 4:
			boolean success = disk.deleteAllFiles(diskWindowGui.getDelete());

			if (success) {

				diskGui.setFileLoad(0);
				directoryGui.setYOffset1(0);
				disk.initFiles();

			}

			mainFrame.openScreen("directory", "windowpanel");
			break;
		}
	}

}