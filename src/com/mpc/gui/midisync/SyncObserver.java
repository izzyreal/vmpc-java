package com.mpc.gui.midisync;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;

public class SyncObserver implements Observer {

	private MidiSyncGui msGui;
	
	private JTextField inField;
	private JTextField outField;
	private JTextField modeInField;
	private JTextField modeOutField;
	private JTextField receiveMMCField;
	private JTextField sendMMCField;

	private String[] modeNames = {"OFF", "MIDI CLOCK", "TIME CODE"};
	
	public SyncObserver(MainFrame mainFrame) {
	
		msGui = Bootstrap.getGui().getMidiSyncGui();
		
		msGui.deleteObservers();
		msGui.addObserver(this);
		
		inField = mainFrame.lookupTextField("in");
		outField = mainFrame.lookupTextField("out");
		modeInField = mainFrame.lookupTextField("modein");
		modeOutField = mainFrame.lookupTextField("modeout");
		receiveMMCField = mainFrame.lookupTextField("receivemmc");
		sendMMCField = mainFrame.lookupTextField("sendmmc");
		
		displayIn();
		displayOut();
		displayModeIn();
		displayModeOut();
		displayReceiveMMC();
		displaySendMMC();
		
	}
		
	private void displayIn() {
		inField.setText(""+(msGui.getIn()+1));
	}

	private void displayOut() {
		String out = " A";
		if (msGui.getOut() == 1) out = " B";
		if (msGui.getOut() == 2) out = "A/B";
		outField.setText(out);
	}

	private void displayModeIn() {
		modeInField.setText(modeNames[msGui.getModeIn()]);
	}

	private void displayModeOut() {
		modeOutField.setText(modeNames[msGui.getModeOut()]);
	}


	private void displayReceiveMMC() {
		String mmc = msGui.isReceiveMMCEnabled() ? "ON" : "OFF";
		receiveMMCField.setText(mmc);
	}

	private void displaySendMMC() {
		String mmc = msGui.isSendMMCEnabled() ? "ON" : "OFF";
		sendMMCField.setText(mmc);
	}

	public void update(Observable o, Object arg) {

		switch((String)arg) {
		case "in":
			displayIn();
			break;
			
		case "out":
			displayOut();
			break;
			
		case "modein":
			displayModeIn();
			break;
			
		case "modeout":
			displayModeOut();
			break;
			
		case "receivemmc":
			displayReceiveMMC();
			break;
			
		case "sendmcc":
			displaySendMMC();
			break;
		}
		
	}
	
}
