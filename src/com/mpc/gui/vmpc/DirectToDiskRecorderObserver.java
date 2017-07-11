package com.mpc.gui.vmpc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sequencer.EditSequenceGui;
import com.mpc.sequencer.MpcSequence;

public class DirectToDiskRecorderObserver implements Observer {

	private String[] recordNames = { "SEQUENCE", "LOOP", "CUSTOM RANGE", "SONG", "JAM" };

	private JTextField recordField;
	private JTextField sqField;
	private JTextField songField;
	private JTextField time0Field;
	private JTextField time1Field;
	private JTextField time2Field;
	private JTextField time3Field;
	private JTextField time4Field;
	private JTextField time5Field;

	private JTextField outputFolderField;

	private JTextField offlineField;
	private JTextField splitLRField;

	private DirectToDiskRecorderGui d2dRecorderGui;

	public DirectToDiskRecorderObserver(Mpc mpc, MainFrame mainFrame) {

		d2dRecorderGui = Bootstrap.getGui().getD2DRecorderGui();
		d2dRecorderGui.deleteObservers();
		d2dRecorderGui.addObserver(this);

		recordField = mainFrame.lookupTextField("record");
		sqField = mainFrame.lookupTextField("sq");
		songField = mainFrame.lookupTextField("song");
		time0Field = mainFrame.lookupTextField("time0");
		time1Field = mainFrame.lookupTextField("time1");
		time2Field = mainFrame.lookupTextField("time2");
		time3Field = mainFrame.lookupTextField("time3");
		time4Field = mainFrame.lookupTextField("time4");
		time5Field = mainFrame.lookupTextField("time5");

		outputFolderField = mainFrame.lookupTextField("outputfolder");

		offlineField = mainFrame.lookupTextField("offline");
		splitLRField = mainFrame.lookupTextField("splitlr");

		displayRecord();
		displaySq();
		displaySong();
		displayTime();
		displayOutputFolder();
		displayOffline();
		displaySplitLR();
	}

	private void displaySong() {
		songField.setVisible(d2dRecorderGui.getRecord() == 3);
		Bootstrap.getGui().getMainFrame().lookupLabel("song").setVisible(d2dRecorderGui.getRecord() == 3);
		if (d2dRecorderGui.getRecord() != 3) return;
		int song = d2dRecorderGui.getSong();
		songField.setText(Util.padLeft2Zeroes(song + 1) + "-"
				+ Bootstrap.getGui().getMpc().getSequencer().getSong(song).getName());
	}

	private void displayOffline() {
		offlineField.setText(d2dRecorderGui.isOffline() ? "YES" : "NO");
	}

	private void displaySplitLR() {
		splitLRField.setText(d2dRecorderGui.isSplitLR() ? "YES" : "NO");
	}

	private void displayOutputFolder() {
		outputFolderField.setText(d2dRecorderGui.getOutputfolder());
	}

	private void displayRecord() {
		recordField.setText(recordNames[d2dRecorderGui.getRecord()]);
	}

	private void displaySq() {
		sqField.setVisible(d2dRecorderGui.getRecord() >= 0 && d2dRecorderGui.getRecord() <= 2);
		Bootstrap.getGui().getMainFrame().lookupLabel("sq")
				.setVisible(d2dRecorderGui.getRecord() >= 0 && d2dRecorderGui.getRecord() <= 2);
		int seq = d2dRecorderGui.getSq();
		sqField.setText(Util.padLeft2Zeroes(seq + 1) + "-"
				+ Bootstrap.getGui().getMpc().getSequencer().getSequence(seq).getName());
	}

	private void displayTime() {

		for (int i = 0; i < 6; i++) {
			Bootstrap.getGui().getMainFrame().lookupTextField("time" + i).setVisible(d2dRecorderGui.getRecord() == 2);
			Bootstrap.getGui().getMainFrame().lookupLabel("time" + i).setVisible(d2dRecorderGui.getRecord() == 2);
		}

		if (d2dRecorderGui.getRecord() != 2) {
			return;
		}

		MpcSequence mpcSequence = Bootstrap.getGui().getMpc().getSequencer().getSequence(d2dRecorderGui.getSq());
		time0Field
				.setText(Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, d2dRecorderGui.getTime0()) + 1));
		time1Field.setText(
				Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, d2dRecorderGui.getTime0()) + 1));
		time2Field.setText(Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, d2dRecorderGui.getTime0())));
		time3Field
				.setText(Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, d2dRecorderGui.getTime1()) + 1));
		time4Field.setText(
				Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, d2dRecorderGui.getTime1()) + 1));
		time5Field.setText(Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, d2dRecorderGui.getTime1())));
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		// System.out.println("Observable: " + arg0.getClass());
		// System.out.println("String: " + ((String) arg1));

		switch ((String) arg1) {
		case "offline":
			displayOffline();
			break;
		case "splitlr":
			displaySplitLR();
			break;
		case "outputfolder":
			displayOutputFolder();
			break;
		case "time":
			displayTime();
			break;
		case "record":
			displayRecord();
			displaySq();
			displaySong();
			displayTime();
			break;
		case "sq":
			displaySq();
			break;
		case "song":
			displaySong();
			break;
		}
	}

}
