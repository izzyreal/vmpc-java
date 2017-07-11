package com.mpc.controls;

import java.awt.event.KeyEvent;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;

public class StepEditorKbRelease {

	private Gui gui = Bootstrap.getGui();
	private MainFrame mainFrame = gui.getMainFrame();

	public void stepEditorKbRelease(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_F1)
			mainFrame.openScreen("sequencer_step", "mainpanel");

		if (Util.getFocus() != null && Util.getFocus().length() == 2
				&& e.getKeyCode() == KeyEvent.VK_SHIFT) {

			int eventNumber = Integer.parseInt(Util.getFocus().substring(1, 2));

			gui.getStepEditorGui().setSelectionEndIndex(
					eventNumber + gui.getStepEditorGui().getyOffset());
		}
	}
}