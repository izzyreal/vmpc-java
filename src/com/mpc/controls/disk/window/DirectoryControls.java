package com.mpc.controls.disk.window;

import com.mpc.Util;
import com.mpc.controls.disk.AbstractDiskControls;

public class DirectoryControls extends AbstractDiskControls {

	@Override
	public void function(int f) {
		super.function(f);
		switch (f) {
		case 1:
			if (directoryGui.getSelectedFile() == null) return;
			if (directoryGui.getSelectedFile().isDirectory()) {
				mainFrame.openScreen("deletefolder", "dialogpanel");
			} else {
				mainFrame.openScreen("deletefile", "dialogpanel");
			}
			break;
		case 2:
			if (directoryGui.getSelectedFile() == null) return;
			String fileNameNoExt = Util.splitName(directoryGui.getSelectedFile().getName())[0];
			nameGui.setName(fileNameNoExt);

			if (directoryGui.getSelectedFile().isDirectory()) {
				nameGui.setNameLimit(8);
			}

			nameGui.setParameterName("rename");
			mainFrame.openScreen("name", "dialogpanel");
			break;
		case 4:
			if (directoryGui.getXPos() == 0) return;
			nameGui.setName("NEWFOLDR");
			nameGui.setNameLimit(8);
			nameGui.setParameterName("newfolder");
			mainFrame.openScreen("name", "dialogpanel");
			break;
		}
	}

	@Override
	public void left() {
		init();
		directoryGui.left();
	}

	@Override
	public void right() {
		init();
		directoryGui.right();
	}

	@Override
	public void up() {
		init();
		directoryGui.up();
	}

	@Override
	public void down() {
		init();
		directoryGui.down();
	}

	@Override
	public void turnWheel(int i) {
		init();
		if (i > 0) {
			directoryGui.up();
		} else {
			directoryGui.down();
		}
	}
}