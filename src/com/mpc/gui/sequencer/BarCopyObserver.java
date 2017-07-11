package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.TimeSignature;

public class BarCopyObserver implements Observer {

	private Gui gui;
	private BarCopyGui bcGui;

	private Sequencer sequencer;
	private int trackNum;
	private int seqNum;

	private MpcSequence mpcSequence;
	private MpcTrack track;
	private TimeSignature timeSig;

	private JTextField fromSqField;
	private JTextField toSqField;
	private JTextField firstBarField;
	private JTextField lastBarField;
	private JTextField afterBarField;
	private JTextField copiesField;


	public BarCopyObserver(Sequencer sequencer,
			MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sequencer = sequencer;

		this.gui = Bootstrap.getGui();
		bcGui = gui.getBarCopyGui();
		bcGui.deleteObservers();
		bcGui.addObserver(this);

		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();

		fromSqField = mainFrame.lookupTextField("fromsq");
		toSqField = mainFrame.lookupTextField("tosq");
		firstBarField = mainFrame.lookupTextField("firstbar");
		lastBarField = mainFrame.lookupTextField("lastbar");
		afterBarField = mainFrame.lookupTextField("afterbar");
		copiesField = mainFrame.lookupTextField("copies");		
		
		sequencer.deleteObservers();
		sequencer.addObserver(this);
		mpcSequence.deleteObservers();
		mpcSequence.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);
		timeSig.deleteObservers();
		timeSig.addObserver(this);

		displayFromSq();
		displayToSq();
		displayFirstBar();
		displayLastBar();
		displayAfterBar();
		displayCopies();
	}

	private void displayCopies() {
			copiesField.setText(Util.padLeftSpace("" + bcGui.getCopies(), 3));
	}


	@Override
	public void update(Observable o, Object arg) {

		track.deleteObservers();
		mpcSequence.deleteObservers();
		timeSig.deleteObservers();
		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		timeSig = mpcSequence.getTimeSignature();
		track.addObserver(this);
		mpcSequence.addObserver(this);
		timeSig.addObserver(this);

		switch ((String) arg) {

		case "fromsq":
			displayFromSq();
			break;
		
		case "tosq":
			displayToSq();
			break;

		case "firstbar":
			displayFirstBar();
			break;

		case "lastbar":
			displayLastBar();
			break;
			
		case "afterbar":
			displayAfterBar();
			break;
			
		case "copies":
			displayCopies();
			break;
		}
	}

	private void displayToSq() {
		toSqField.setText("" + (bcGui.getToSq() + 1));
	}

	private void displayFromSq() {
		fromSqField.setText("" + (bcGui.getFromSq() + 1));		
	}

	private void displayAfterBar() {
		afterBarField.setText("" + bcGui.getAfterBar());
	}

	private void displayLastBar() {
			lastBarField.setText("" + (bcGui.getLastBar() + 1));
	}

	private void displayFirstBar() {
		firstBarField.setText("" + (bcGui.getFirstBar() + 1));
	}
}