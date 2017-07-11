package com.mpc.gui.misc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;

public class SecondSeqObserver implements Observer {

	private JTextField sqField;
	private JLabel sequenceNameLabel;
	
	private SecondSeqGui secondSeqGui;

	public SecondSeqObserver(MainFrame mainFrame) {

		secondSeqGui = Bootstrap.getGui().getSecondSeqGui();
		secondSeqGui.deleteObservers();
		secondSeqGui.addObserver(this);

		sqField = mainFrame.lookupTextField("sq");
		sequenceNameLabel = mainFrame.lookupLabel("sequencename");
		
		displaySq();
	}

	private void displaySq() {
		String sqName = Bootstrap.getGui().getMpc().getSequencer().getSequence(secondSeqGui.getSq()).getName();
		sqField.setText(Util.padLeft2Zeroes(secondSeqGui.getSq() + 1));
		sequenceNameLabel.setText("-" + sqName);
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {
		case "sq":
			displaySq();
			break;
		}
	}

}
