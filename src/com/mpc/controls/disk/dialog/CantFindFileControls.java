package com.mpc.controls.disk.dialog;

import com.mpc.controls.disk.AbstractDiskControls;

public class CantFindFileControls extends AbstractDiskControls {

	public void function(int i) {
		init();
		switch (i) {
		case 1:
			diskGui.setSkipAll(true);
			ls.getWindowPanel().removeAll();
			diskGui.setWaitingForUser(false);
			break;
		case 2:
			diskGui.setWaitingForUser(false);
			break;
		}

	}
}
