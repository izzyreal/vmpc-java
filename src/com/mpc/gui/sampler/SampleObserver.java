package com.mpc.gui.sampler;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Sampler;

public class SampleObserver implements Observer, Runnable {

	private String[] inputNames = { "ANALOG", "DIGITAL" };
	private String[] modeNames = { "MONO L", "MONO R", "STEREO" };
	private String[] monitorNames = { "OFF", "L/R", "1/2", "3/4", "5/6", "7/8" };

	private JTextField inputField;
	private JTextField thresholdField;
	private JTextField modeField;
	private JTextField timeField;
	private JTextField monitorField;
	private JTextField preRecField;
	private Sampler sampler;
	private SamplerGui samplerGui;

	private JLabel vuLeftLabel;
	private JLabel vuRightLabel;

	private boolean vu_ready = false;

	private int levelL = 0;
	private int levelR = 0;

	private final static String vu_normal = "\u00F5";
	private final static String vu_threshold = "\u00F6";
	private final static String vu_peak = "\u00F8";
	private final static String vu_peak_threshold = "\u00F9";
	private final static String vu_normal_threshold = "\u00FA";
	private final static String vu_peak_threshold_normal = "\u00FB";

	public SampleObserver(MainFrame mainFrame, Sampler sampler) {
		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);
		this.sampler = sampler;
		
		inputField = mainFrame.lookupTextField("input");
		thresholdField = mainFrame.lookupTextField("threshold");
		modeField = mainFrame.lookupTextField("mode");
		timeField = mainFrame.lookupTextField("time");
		monitorField = mainFrame.lookupTextField("monitor");
		preRecField = mainFrame.lookupTextField("prerec");

		displayInput();
		displayThreshold();
		displayMode();
		displayTime();
		displayMonitor();
		displayPreRec();

		vuLeftLabel = new JLabel();
		vuLeftLabel.setLocation(80, 57);
		vuLeftLabel.setSize(550, 18);
		vuLeftLabel.setForeground(Bootstrap.lcdOn);
		mainFrame.getLayeredScreen().getWindowPanel().add(vuLeftLabel);

		vuRightLabel = new JLabel();
		vuRightLabel.setLocation(80, 75);
		vuRightLabel.setSize(550, 18);
		vuRightLabel.setForeground(Bootstrap.lcdOn);
		mainFrame.getLayeredScreen().getWindowPanel().add(vuRightLabel);

		mainFrame.getLayeredScreen().repaint();
		vu_ready = true;
	}

	private void displayInput() {
		inputField.setText(inputNames[samplerGui.getInput()]);
	}

	private void displayThreshold() {
		String threshold = samplerGui.getThreshold() == -64 ? "-\u00D9\u00DA"
				: "" + samplerGui.getThreshold();
		thresholdField.setText(threshold);
	}

	private void displayMode() {
		modeField.setText(modeNames[samplerGui.getMode()]);

	}

	private void displayTime() {
		String time = "" + samplerGui.getTime();
		time = time.substring(0, time.length() - 1) + "."
				+ time.substring(time.length() - 1);
		timeField.setText(time);
	}

	private void displayMonitor() {
		monitorField.setText(monitorNames[samplerGui.getMonitor()]);
	}

	private void displayPreRec() {
		preRecField.setText(samplerGui.getPreRec() + "ms");
	}

	@Override
	public void update(Observable o, Object arg) {
		String s = (String) arg;

		switch (s) {

		case "vumeter":
			if (!vu_ready) return;
			updateVU();
			break;
		case "input":
			displayInput();
			break;
		case "threshold":
			displayThreshold();
			break;
		case "mode":
			displayMode();
			break;
		case "time":
			displayTime();
			break;
		case "monitor":
			displayMonitor();
			break;
		case "prerec":
			displayPreRec();
			break;
		}
	}

	private void updateVU() {
		new Thread(this).run();
	}

	@Override
	public void run() {
		String lString = "";
		String rString = "";

		int peaklValue = sampler.getPeakL();
		int peakrValue = sampler.getPeakR();

		int thresholdValue = (int) ((samplerGui.getThreshold() + 63) * 0.53125);
		int levell = sampler.getLevelL();
		int levelr = sampler.getLevelR();
	
		for (int i = 0; i < 34; i++) {
			String l = null, r = null;

			boolean normall = i <= levell;
			boolean normalr = i <= levelr;
			boolean threshold = i == thresholdValue;
			boolean peakl = i == peaklValue;
			boolean peakr = i == peakrValue;

			if (threshold && peakl) l = vu_peak_threshold;
			if (threshold && peakr) r = vu_peak_threshold;
			if (threshold && normall && !peakl) l = vu_normal_threshold;
			if (threshold && normalr && !peakr) r = vu_normal_threshold;
			if (threshold && !peakl && !normall) l = vu_threshold;
			if (threshold && !peakr && !normalr) r = vu_threshold;
			if (normall && !peakl && !threshold) l = vu_normal;
			if (normalr && !peakr && !threshold) r = vu_normal;
			if (peakl && !threshold) l = vu_peak;
			if (peakr && !threshold) r = vu_peak;
			if (peakl && threshold && levell == 33) l = vu_peak_threshold_normal;
			if (peakr && threshold && levelr == 33) r = vu_peak_threshold_normal;
				
			lString += (l == null ? " " : l);
			rString += (r == null ? " " : r);
		}
		final String flString = lString;
		final String frString = rString;
		new Thread() {
			public void run() {
			vuLeftLabel.setText(flString);
			}
		}.start();;
		new Thread() {
			public void run() {
				vuRightLabel.setText(frString);
			}
		}.start();;
		
	}
}
