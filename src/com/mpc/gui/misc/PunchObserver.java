package com.mpc.gui.misc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sequencer.EditSequenceGui;
import com.mpc.sequencer.MpcSequence;

public class PunchObserver implements Observer {

	private PunchGui punchGui;

	private JTextField autoPunchField;
	private JTextField time0Field;
	private JTextField time1Field;
	private JTextField time2Field;
	private JTextField time3Field;
	private JTextField time4Field;
	private JTextField time5Field;

	private MpcSequence mpcSequence;

	private String[] autoPunchNames = { "PUNCH IN ONLY", "PUNCH OUT ONLY", "PUNCH IN OUT" };

	public PunchObserver(MainFrame mainFrame) {

		punchGui = Bootstrap.getGui().getPunchGui();
		punchGui.deleteObservers();
		punchGui.addObserver(this);

		autoPunchField = mainFrame.lookupTextField("autopunch");
		time0Field = mainFrame.lookupTextField("time0");
		time1Field = mainFrame.lookupTextField("time1");
		time2Field = mainFrame.lookupTextField("time2");
		time3Field = mainFrame.lookupTextField("time3");
		time4Field = mainFrame.lookupTextField("time4");
		time5Field = mainFrame.lookupTextField("time5");

		mpcSequence = Bootstrap.getGui().getMpc().getSequencer().getActiveSequence();

		displayAutoPunch();
		displayTime();
		displayBackground();
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {

		case "autopunch":
			displayAutoPunch();
			displayTime();
			displayBackground();
			break;
		case "time":
			displayTime();
			break;
		}

	}

	private void displayAutoPunch() {
		autoPunchField.setText(autoPunchNames[punchGui.getAutoPunch()]);
	}

	private void displayTime() {

		for (int i = 0; i < 3; i++) {
			Bootstrap.getGui().getMainFrame().lookupTextField("time" + i).setVisible(punchGui.getAutoPunch() != 1);
			Bootstrap.getGui().getMainFrame().lookupLabel("time" + i).setVisible(punchGui.getAutoPunch() != 1);
			Bootstrap.getGui().getMainFrame().lookupTextField("time" + (i + 3))
					.setVisible(punchGui.getAutoPunch() != 0);
			Bootstrap.getGui().getMainFrame().lookupLabel("time" + (i + 3)).setVisible(punchGui.getAutoPunch() != 0);
		}

		Bootstrap.getGui().getMainFrame().lookupLabel("time3").setVisible(punchGui.getAutoPunch() == 2);
		
		time0Field.setText(Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, punchGui.getTime0()) + 1));
		time1Field.setText(Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, punchGui.getTime0()) + 1));
		time2Field.setText(Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, punchGui.getTime0())));
		time3Field.setText(Util.padLeft3Zeroes(EditSequenceGui.getBarNumber(mpcSequence, punchGui.getTime1()) + 1));
		time4Field.setText(Util.padLeft2Zeroes(EditSequenceGui.getBeatNumber(mpcSequence, punchGui.getTime1()) + 1));
		time5Field.setText(Util.padLeft2Zeroes(EditSequenceGui.getClockNumber(mpcSequence, punchGui.getTime1())));
	}

	private void displayBackground() {
		String bgName = "punch";
		if (punchGui.getAutoPunch() == 1) {
			bgName = "punchout";
		} else if (punchGui.getAutoPunch() == 2) {
			bgName = "punchinout";
		}
		Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentBackground().setBackgroundName(bgName);
	}

}
