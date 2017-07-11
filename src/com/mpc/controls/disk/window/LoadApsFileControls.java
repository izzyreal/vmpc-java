package com.mpc.controls.disk.window;

import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.file.aps.ApsLoader;

public class LoadApsFileControls extends AbstractDiskControls {

	public void function(int i) {
		init();
		switch(i) {
		case 3:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 4:
			final ApsLoader al = new ApsLoader(diskGui.getSelectedFile());
			al.load();
			//sampler.getPrograms().clear();
//			sampler.getPrograms().addAll(al.getPrograms());
//			mainFrame.panel("load", "mainpanel");
			break;
		}
	}
	
}
