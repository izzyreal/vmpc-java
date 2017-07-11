package com.mpc.gui.disk.window;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.disk.Disk;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;

public class DeleteAllFilesObserver implements Observer {
	private String[] views = { "All Files", ".SND", ".PGM", ".APS", ".MID", ".ALL", ".WAV", ".SEQ", ".SET" };

	private MainFrame mainFrame;

	private DiskWindowGui diskWindowGui;
	private Disk disk;

	private LayeredScreen ls;

	private String csn;

	private JTextField deleteField;

	public DeleteAllFilesObserver(Mpc mpc, Gui gui) throws UnsupportedEncodingException {

		mainFrame = gui.getMainFrame();
		disk = mpc.getDisk();
		diskWindowGui = gui.getDirectoryWindowGui();

		ls = mainFrame.getLayeredScreen();

		ls = mainFrame.getLayeredScreen();
		csn = ls.getCurrentScreenName();

		diskWindowGui.addObserver(this);

		((Observable) disk).addObserver(this);

		deleteField = mainFrame.lookupTextField("delete");
		displayDelete();

	}

	private void displayDelete() {
		deleteField.setText(views[diskWindowGui.getDelete()]);
	}

	@Override
	public void update(Observable o, Object arg) {

		String parameter = (String) arg;

		switch (parameter) {

		case "delete":
			displayDelete();
			break;

		}
	}
}