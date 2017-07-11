package com.mpc.gui.sampler;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Sampler;

public class PurgeObserver implements Observer {

	private MainFrame mainFrame;
	private Mpc mpc;
	private Sampler sampler;

	private JLabel valueLabel;

	public PurgeObserver(Gui gui) {

		mpc = gui.getMpc();
		mainFrame = gui.getMainFrame();

		sampler = mpc.getSampler();

		valueLabel = mainFrame.lookupLabel("value");
		displayValue();
	}

	private void displayValue() {
		valueLabel.setText(Util.padLeftSpace("" + sampler.getUnusedSampleAmount(), 3));
	}

	@Override
	public void update(Observable arg0, Object arg1) {

	}

}