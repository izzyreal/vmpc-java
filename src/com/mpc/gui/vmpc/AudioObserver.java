package com.mpc.gui.vmpc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.audiomidi.AudioMidiServices;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.MpcTextField;

public class AudioObserver implements Observer {

	private AudioGui audioGui;
	public static String[] inNames = { "RECORD IN", "DIGITAL IN" };
	public static String[] outNames = { "STEREO OUT", "ASSIGNABLE MIX OUT 1/2", "ASSIGNABLE MIX OUT 3/4",
			"ASSIGNABLE MIX OUT 5/6", "ASSIGNABLE MIX OUT 7/8" };

	private JTextField serverField;
	private JTextField dev0Field;
	private JTextField dev1Field;
	private JTextField outField;
	private JTextField inField;

	private AudioMidiServices ams;

	public AudioObserver(Mpc mpc, MainFrame mainFrame) {

		mpc.deleteObservers();
		mpc.addObserver(this);

		ams = mpc.getAudioMidiServices();

		ams.deleteObservers();
		ams.addObserver(this);

		audioGui = Bootstrap.getGui().getAudioGui();
		audioGui.deleteObservers();
		audioGui.addObserver(this);

		serverField = mainFrame.lookupTextField("server");
		dev0Field = mainFrame.lookupTextField("dev0");
		dev1Field = mainFrame.lookupTextField("dev1");
		inField = mainFrame.lookupTextField("in");
		outField = mainFrame.lookupTextField("out");

		displayServer();
		displayIn();
		displayDev0();
		displayOut();
		displayDev1();
		displayFunctionBox();
	}

	private void displayServer() {
		serverField.setText(ams.getServerNameAndModel(ams.getSelectedServer()));
	}

	private void displayDev1() {
		if (ams.getSelectedServer() != ams.getActiveServerIndex() || ams.isDisabled()) {
			dev1Field.setText(" <press APPLY to activate server>");
		} else {
			if (audioGui.getOut() < ams.getOutputNames().size()) {
				dev1Field.setText(ams.getOutputNames().get(audioGui.getOut()));
			} else {
				dev1Field.setText("< not enough outputs on device >");
			}
		}
		((MpcTextField) dev1Field).enableScrolling(new JTextField[] { dev1Field, outField });
	}

	private void displayOut() {
		outField.setText(outNames[audioGui.getOut()]);
		displayDev1();
	}

	private void displayDev0() {
		if (ams.getSelectedServer() != ams.getActiveServerIndex() || ams.isDisabled()) {
			dev0Field.setText(" <press APPLY to activate server>");
		} else {
			if (audioGui.getIn() < ams.getInputNames().size()) {
				dev0Field.setText(ams.getInputNames().get(audioGui.getIn()));
			} else {
				dev0Field.setText("< not enough inputs on device >");
			}
		}
		((MpcTextField) dev0Field).enableScrolling(new JTextField[] { dev0Field, inField });
	}

	private void displayIn() {
		inField.setText(inNames[audioGui.getIn()]);
		displayDev0();
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {
		case "selectedserver":
			displayServer();
			displayIn();
			displayDev0();
			displayOut();
			displayDev1();
			displayFunctionBox();
			break;
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

	void displayFunctionBox() {
		if (ams.getSelectedServer() == ams.getActiveServerIndex()) {
			Bootstrap.getGui().getMainFrame().getLayeredScreen().drawFunctionBoxes("audio_no_start");
		} else {
			Bootstrap.getGui().getMainFrame().getLayeredScreen().drawFunctionBoxes("audio");			
		}

	}

}
