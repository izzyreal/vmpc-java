package com.mpc.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JLabel;

public class StartUp {

	private Gui gui = Bootstrap.getGui();
	private MainFrame mainFrame;

	public void startUp() throws InterruptedException, FileNotFoundException,
			IOException {
		mainFrame = gui.getMainFrame();
		mainFrame.getLedPanel().setPadBankA(true);
		mainFrame.getLedPanel().setPadBankB(true);
		mainFrame.getLedPanel().setPadBankC(true);
		mainFrame.getLedPanel().setPadBankD(true);
		mainFrame.getLedPanel().setAfter(true);
		mainFrame.getLedPanel().setUndoSeq(true);
		mainFrame.getLedPanel().setRec(true);
		mainFrame.getLedPanel().setOverDub(true);
		mainFrame.getLedPanel().setPlay(true);
		mainFrame.getLedPanel().setFullLevel(true);
		mainFrame.getLedPanel().setSixteenLevels(true);
		mainFrame.getLedPanel().setNextSeq(true);
		mainFrame.getLedPanel().setTrackMute(true);

		Thread.sleep(1);

		mainFrame.getScreen().setBackground(Bootstrap.lcdOn);
		Thread.sleep(18);
		mainFrame.getScreen().setBackground(Bootstrap.lcdOff);

		JLabel welcome = new JLabel("MPC2000XL");
		welcome.setSize(9 * 6 * 2, 16);
		welcome.setLocation(194, 54);
		welcome.setForeground(Bootstrap.lcdOn);

		mainFrame.getLayeredScreen().add(welcome);
		Thread.sleep(100);

		mainFrame.getLayeredScreen().remove(welcome);
		mainFrame.getScreen().repaint();

		mainFrame.openScreen("sequencer", "mainpanel");
		mainFrame.popupPanel("Wait.......", 190);
		mainFrame.getLedPanel().setPadBankB(false);
		mainFrame.getLedPanel().setPadBankC(false);
		mainFrame.getLedPanel().setPadBankD(false);
		mainFrame.getLedPanel().setAfter(false);
		mainFrame.getLedPanel().setUndoSeq(false);
		mainFrame.getLedPanel().setRec(false);
		mainFrame.getLedPanel().setOverDub(false);
		mainFrame.getLedPanel().setPlay(false);
		mainFrame.getLedPanel().setFullLevel(false);
		mainFrame.getLedPanel().setSixteenLevels(false);
		mainFrame.getLedPanel().setNextSeq(false);
		mainFrame.getLedPanel().setTrackMute(false);
		Thread.sleep(200);

		mainFrame.popupPanel("detecting memory...", 44 * 2);
		Thread.sleep(200);

		mainFrame.popupPanel("Wait.......", 190);
		Thread.sleep(200);

		mainFrame.getLayeredScreen().removePopup();
		gui.getMpc().initDisks();
		mainFrame.openScreen("load", "mainpanel");
		Thread.sleep(200);
		mainFrame.getLayeredScreen().mainPanel.removeAll();

		mainFrame.openScreen("sequencer", "mainpanel");
	}
}
