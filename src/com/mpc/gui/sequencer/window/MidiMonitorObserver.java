package com.mpc.gui.sequencer.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.Timer;

import com.mpc.Mpc;
import com.mpc.gui.MainFrame;

public class MidiMonitorObserver implements Observer {

	private Timer blinkTimer;

	private JLabel a0;
	private JLabel a1;
	private JLabel a2;
	private JLabel a3;
	private JLabel a4;
	private JLabel a5;
	private JLabel a6;
	private JLabel a7;
	private JLabel a8;
	private JLabel a9;
	private JLabel a10;
	private JLabel a11;
	private JLabel a12;
	private JLabel a13;
	private JLabel a14;
	private JLabel a15;
	private JLabel b0;
	private JLabel b1;
	private JLabel b2;
	private JLabel b3;
	private JLabel b4;
	private JLabel b5;
	private JLabel b6;
	private JLabel b7;
	private JLabel b8;
	private JLabel b9;
	private JLabel b10;
	private JLabel b11;
	private JLabel b12;
	private JLabel b13;
	private JLabel b14;
	private JLabel b15;

	public MidiMonitorObserver(Mpc mpc, MainFrame mainFrame) {
		mpc.getEventHandler().deleteObservers();
		mpc.getEventHandler().addObserver(this);
		mpc.deleteObservers();
		mpc.addObserver(this);
		a0 = mainFrame.lookupLabel("0");
		a1 = mainFrame.lookupLabel("1");
		a2 = mainFrame.lookupLabel("2");
		a3 = mainFrame.lookupLabel("3");
		a4 = mainFrame.lookupLabel("4");
		a5 = mainFrame.lookupLabel("5");
		a6 = mainFrame.lookupLabel("6");
		a7 = mainFrame.lookupLabel("7");
		a8 = mainFrame.lookupLabel("8");
		a9 = mainFrame.lookupLabel("9");
		a10 = mainFrame.lookupLabel("10");
		a11 = mainFrame.lookupLabel("11");
		a12 = mainFrame.lookupLabel("12");
		a13 = mainFrame.lookupLabel("13");
		a14 = mainFrame.lookupLabel("14");
		a15 = mainFrame.lookupLabel("15");
		b0 = mainFrame.lookupLabel("16");
		b1 = mainFrame.lookupLabel("17");
		b2 = mainFrame.lookupLabel("18");
		b3 = mainFrame.lookupLabel("19");
		b4 = mainFrame.lookupLabel("20");
		b5 = mainFrame.lookupLabel("21");
		b6 = mainFrame.lookupLabel("22");
		b7 = mainFrame.lookupLabel("23");
		b8 = mainFrame.lookupLabel("24");
		b9 = mainFrame.lookupLabel("25");
		b10 = mainFrame.lookupLabel("26");
		b11 = mainFrame.lookupLabel("27");
		b12 = mainFrame.lookupLabel("28");
		b13 = mainFrame.lookupLabel("29");
		b14 = mainFrame.lookupLabel("30");
		b15 = mainFrame.lookupLabel("31");

	}

	public void initTimer(final JLabel label) {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				label.setText("");
				blinkTimer.stop();
			}
		};

		blinkTimer = new Timer(50, action);
		blinkTimer.setRepeats(false);
		blinkTimer.start();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("received update " + ((String)arg1));
		switch ((String) arg1) {
		case "a0":
			a0.setText("\u00CC");
			initTimer(a0);
			break;

		case "a1":
			a1.setText("\u00CC");
			initTimer(a1);
			break;

		case "a2":
			a2.setText("\u00CC");
			initTimer(a2);
			break;

		case "a3":
			a3.setText("\u00CC");
			initTimer(a3);
			break;

		case "a4":
			a4.setText("\u00CC");
			initTimer(a4);
			break;

		case "a5":
			a5.setText("\u00CC");
			initTimer(a5);
			break;

		case "a6":
			a6.setText("\u00CC");
			initTimer(a6);
			break;

		case "a7":
			a7.setText("\u00CC");
			initTimer(a7);
			break;

		case "a8":
			a8.setText("\u00CC");
			initTimer(a8);
			break;

		case "a9":
			a9.setText("\u00CC");
			initTimer(a9);
			break;

		case "a10":
			a10.setText("\u00CC");
			initTimer(a10);
			break;

		case "a11":
			a11.setText("\u00CC");
			initTimer(a11);
			break;

		case "a12":
			a12.setText("\u00CC");
			initTimer(a12);
			break;

		case "a13":
			a13.setText("\u00CC");
			initTimer(a13);
			break;

		case "a14":
			a14.setText("\u00CC");
			initTimer(a14);
			break;

		case "a15":
			a15.setText("\u00CC");
			initTimer(a15);
			break;

		case "b0":
			b0.setText("\u00CC");
			initTimer(b0);
			break;

		case "b1":
			b1.setText("\u00CC");
			initTimer(b1);
			break;

		case "b2":
			b2.setText("\u00CC");
			initTimer(b2);
			break;

		case "b3":
			b3.setText("\u00CC");
			initTimer(b3);
			break;

		case "b4":
			b4.setText("\u00CC");
			initTimer(b4);
			break;

		case "b5":
			b5.setText("\u00CC");
			initTimer(b5);
			break;

		case "b6":
			b6.setText("\u00CC");
			initTimer(b6);
			break;

		case "b7":
			b7.setText("\u00CC");
			initTimer(b7);
			break;

		case "b8":
			b8.setText("\u00CC");
			initTimer(b8);
			break;

		case "b9":
			b9.setText("\u00CC");
			initTimer(b9);
			break;

		case "b10":
			b10.setText("\u00CC");
			initTimer(b10);
			break;

		case "b11":
			b11.setText("\u00CC");
			initTimer(b11);
			break;

		case "b12":
			b12.setText("\u00CC");
			initTimer(b12);
			break;

		case "b13":
			b13.setText("\u00CC");
			initTimer(b13);
			break;

		case "b14":
			b14.setText("\u00CC");
			initTimer(b14);
			break;

		case "b15":
			b15.setText("\u00CC");
			initTimer(b15);
			break;
		}
	}
}
