package com.mpc.gui.other;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;

public class OthersObserver implements Observer {

	private JTextField tapAveragingField;
	
	private OthersGui othersGui;
	
	public OthersObserver(MainFrame mainFrame) {
		
		othersGui = Bootstrap.getGui().getOthersGui();
		othersGui.deleteObservers();
		othersGui.addObserver(this);
		
		tapAveragingField = mainFrame.lookupTextField("tapaveraging");
		
		displayTapAveraging();
	}
	
	private void displayTapAveraging() {
		tapAveragingField.setText(""+othersGui.getTapAveraging());
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		switch((String)arg1) {
		case "tapaveraging":
			displayTapAveraging();
			break;
		}
	}

}
