package com.mpc.gui.disk;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.disk.window.DiskWindowGui;
import com.mpc.sequencer.MpcSequence;

public class LoadASequenceFromAllObserver implements Observer {

	private JTextField fileField;
	private JLabel fileLabel;
	private JTextField loadIntoField;
	private JLabel loadIntoLabel;

	private DiskGui diskGui;
	private DiskWindowGui diskWindowGui;
	
	public LoadASequenceFromAllObserver(MainFrame mainFrame) {

		diskGui = Bootstrap.getGui().getDiskGui();
		diskWindowGui = Bootstrap.getGui().getDirectoryWindowGui();
		
		diskGui.deleteObservers();
		diskGui.addObserver(this);
		
		diskWindowGui.deleteObservers();
		diskWindowGui.addObserver(this);
		
		fileField = mainFrame.lookupTextField("file");
		fileLabel = mainFrame.lookupLabel("file");
		loadIntoField = mainFrame.lookupTextField("loadinto");
		loadIntoLabel = mainFrame.lookupLabel("loadinto");

		displayFile();
		displayLoadInto();
		
	}
	
	private void displayFile() {
		fileField.setText(Util.padLeft2Zeroes(diskGui.getFileLoad()+1));
		MpcSequence candidate = diskGui.getSequencesFromAllFile().get(diskGui.getFileLoad());
		String name = candidate == null ? "(Unused)":candidate.getName();
		fileLabel.setText("-" + name);
	}

	private void displayLoadInto() {
		loadIntoField.setText(Util.padLeft2Zeroes(Bootstrap.getGui().getDirectoryWindowGui().getLoadInto()+1));
		loadIntoLabel.setText("-" + Bootstrap.getGui().getMpc().getSequencer().getSequence(Bootstrap.getGui().getDirectoryWindowGui().getLoadInto()).getName());		
	}




	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {

		case "fileload":
			displayFile();
			break;
		case "loadinto":
			displayLoadInto();
			break;

		}

	}

}
