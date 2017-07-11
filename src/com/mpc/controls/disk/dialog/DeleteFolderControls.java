package com.mpc.controls.disk.dialog;

import javax.swing.SwingUtilities;

import com.mpc.controls.disk.AbstractDiskControls;

public class DeleteFolderControls extends AbstractDiskControls {

	@Override
	public void function(int i) {
		super.function(i);
		switch (i) {
//		case 3:
//			mainFrame.panel("directory", "windowpanel");
//			break;
		case 4:
			mainFrame.popupPanel("Delete:" + directoryGui.getSelectedFile().getName(), 85);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (disk.deleteDir(directoryGui.getSelectedFile())) {
							disk.flush();
							disk.initFiles();
						}
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mainFrame.removePopup();
					mainFrame.openScreen("directory", "windowpanel");
				}
			});
			break;
		}
	}

}