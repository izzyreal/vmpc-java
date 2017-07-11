package com.mpc.gui.misc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;

public class TransObserver implements Observer {

	private TransGui transGui;

	private JTextField trField;
	private JLabel trackNameLabel;

	private JTextField transposeAmountField;

	private JTextField bar0Field;
	private JTextField bar1Field;

	public TransObserver(MainFrame mainFrame) {

		transGui = Bootstrap.getGui().getTransGui();
		transGui.deleteObservers();
		transGui.addObserver(this);

		trField = mainFrame.lookupTextField("tr");
		trackNameLabel = mainFrame.lookupLabel("trackname");

		transposeAmountField = mainFrame.lookupTextField("transposeamount");

		bar0Field = mainFrame.lookupTextField("bar0");
		bar1Field = mainFrame.lookupTextField("bar1");

		displayTr();
		displayTransposeAmount();
		displayBars();
	}

	private void displayTransposeAmount() {
		transposeAmountField.setText(Util.padLeftSpace("" + transGui.getAmount(), 3));
	}

	private void displayTr() {
		String trName = transGui.getTr() == -1 ? "ALL"
				: Bootstrap.getGui().getMpc().getSequencer().getActiveSequence().getTrack(transGui.getTr()).getName();
		trField.setText(Util.padLeft2Zeroes(transGui.getTr() + 1));
		trackNameLabel.setText(trName);
	}

	private void displayBars() {
		bar0Field.setText("" + (transGui.getBar0() + 1));
		bar1Field.setText("" + (transGui.getBar1() + 1));
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {

		case "amount":
			displayTransposeAmount();
			break;

		case "tr":
			displayTr();
			break;

		case "bars":
			displayBars();
			break;
		}

	}

}
