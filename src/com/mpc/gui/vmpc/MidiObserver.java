package com.mpc.gui.vmpc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.audiomidi.MpcMidiPorts;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;

public class MidiObserver implements Observer {

	private MidiGui midiGui;
	private String[] inNames = { "MIDI In 1", "MIDI In 2" };
	private String[] outNames = { "MIDI OUT A", "MIDI OUT B" };
	private MpcMidiPorts mpcMidiPorts;
	private JTextField dev0Field;
	private JTextField dev1Field;
	private JTextField outField;
	private JTextField inField;

	public MidiObserver(Mpc mpc, MainFrame mainFrame) {
		mpc.deleteObservers();
		mpc.addObserver(this);
		this.mpcMidiPorts = mpc.getMidiPorts();
		midiGui = Bootstrap.getGui().getMidiGui();
		midiGui.deleteObservers();
		midiGui.addObserver(this);

		dev0Field = mainFrame.lookupTextField("dev0");
		dev1Field = mainFrame.lookupTextField("dev1");
		inField = mainFrame.lookupTextField("in");
		outField = mainFrame.lookupTextField("out");

		displayIn();
		displayDev0();
		displayOut();
		displayDev1();

	}

	private void displayDev1() {
		if (midiGui.getOut() == 0) {
			dev1Field.setText(mpcMidiPorts.getCurrentMidiOutADeviceName());
		}
		if (midiGui.getOut() == 1) {
			dev1Field.setText(mpcMidiPorts.getCurrentMidiOutBDeviceName());
		}
	}

	private void displayOut() {
		outField.setText(outNames[midiGui.getOut()]);
		displayDev1();
	}

	private void displayDev0() {
		if (midiGui.getIn() == 0) {
			dev0Field.setText(mpcMidiPorts.getCurrentMidiIn1DeviceName());
		}
		if (midiGui.getIn() == 1) {
			dev0Field.setText(mpcMidiPorts.getCurrentMidiIn2DeviceName());
		}
	}

	private void displayIn() {
		inField.setText(inNames[midiGui.getIn()]);
		displayDev0();
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {
		case "in":
			displayIn();
			break;

		case "out":
			displayOut();
			break;

		case "dev0":
			displayDev0();
			break;

		case "dev1":
			displayDev1();
			break;
		}
	}
}
