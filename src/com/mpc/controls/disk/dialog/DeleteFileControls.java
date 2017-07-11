package com.mpc.controls.disk.dialog;

import javax.swing.SwingUtilities;

import com.mpc.controls.disk.AbstractDiskControls;

public class DeleteFileControls extends AbstractDiskControls {

	@Override
	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			mainFrame.openScreen("deleteallfiles", "dialog2panel");
			break;
//		case 3:
//			mainFrame.panel("directory", "windowpanel");
//			break;
		case 4:
			mainFrame.popupPanel("Delete:" + directoryGui.getSelectedFile().getName(), 85);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					mainFrame.removePopup();

					if (disk.deleteSelectedFile()) {

						disk.flush();
						disk.initFiles();

						diskGui.setFileLoad(diskGui.getFileLoad() - 1);

						directoryGui.setYOffset1(directoryGui.getYOffsetSecond() - 1);

					}

					if (diskGui.getFileLoad() < 0) diskGui.setFileLoad(0);

					mainFrame.openScreen("directory", "windowpanel");
				}
			});
		break;
		}
	}

}