package com.mpc.controls.disk.window;

import java.io.IOException;

import com.mpc.controls.disk.AbstractDiskControls;
import com.mpc.file.wav.WavFileException;

public class LoadAProgramControls extends AbstractDiskControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("loadreplacesound")) diskGui.setLoadReplaceSound(notch > 0);

	}

	public void function(int i) {
		init();
		switch (i) {
		case 2:
			diskGui.setClearProgramWhenLoading(true);
			try {
				mpc.loadProgram();
			} catch (InterruptedException | IOException | WavFileException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 4:
			diskGui.setClearProgramWhenLoading(false);
			try {
				mpc.loadProgram();
			} catch (InterruptedException | IOException | WavFileException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
