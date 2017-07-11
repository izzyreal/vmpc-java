package com.mpc.gui.sequencer.window;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sequencer.SequencerGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.Sequencer;

public class Assign16LevelsObserver implements Observer {

	private final static String[] PARAM_NAMES = { "VELOCITY", "NOTE VAR" };
	public final static String[] TYPE_NAMES = { "TUNING", "DECAY", "ATTACK", "FILTER" };

	private JTextField noteField;
	private JTextField paramField;
	private JTextField typeField;
	private JTextField originalKeyPadField;

	private SequencerGui sGui;
	private JLabel typeLabel;
	private JLabel originalKeyPadLabel;

	public Assign16LevelsObserver(MainFrame mainFrame) {

		sGui = Bootstrap.getGui().getSequencerGui();
		sGui.deleteObservers();
		sGui.addObserver(this);

		noteField = mainFrame.lookupTextField("note");
		paramField = mainFrame.lookupTextField("param");
		typeField = mainFrame.lookupTextField("type");
		typeLabel = mainFrame.lookupLabel("type");
		originalKeyPadField = mainFrame.lookupTextField("originalkeypad");
		originalKeyPadLabel = mainFrame.lookupLabel("originalkeypad");

		displayNote();
		displayParameter();
		if (sGui.getParameter() == 1) {
			displayType();
			displayOriginalKeyPad();
		}
	}

	private void displayNote() {
		Sequencer seq = Bootstrap.getGui().getMpc().getSequencer();
		Sampler sampler = Bootstrap.getGui().getMpc().getSampler();
		int nn = sGui.getNote();
		int track = seq.getActiveTrackIndex();
		int pgmNumber = sampler.getDrumBusProgramNumber(seq.getActiveSequence().getTrack(track).getBusNumber());
		Program program = sampler.getProgram(pgmNumber);
		int pn = program.getPadNumberFromNote(nn);
		int sn = program.getNoteParameters(nn).getSndNumber();
		String soundName = sn == -1 ? "OFF" : sampler.getSoundName(sn);
		noteField.setText(nn + "/" + sampler.getPadName(pn) + "-" + soundName);
	}

	private void displayParameter() {
		paramField.setText(PARAM_NAMES[sGui.getParameter()]);
		typeField.setVisible(sGui.getParameter() == 1);
		typeLabel.setVisible(sGui.getParameter() == 1);
		originalKeyPadField.setVisible(sGui.getParameter() == 1 && sGui.getType() == 0);
		originalKeyPadLabel.setVisible(sGui.getParameter() == 1 && sGui.getType() == 0);
	}

	private void displayType() {
		typeField.setText(TYPE_NAMES[sGui.getType()]);
		originalKeyPadField.setVisible(sGui.getType() == 0);
		originalKeyPadLabel.setVisible(sGui.getType() == 0);		
	}

	private void displayOriginalKeyPad() {
		originalKeyPadField.setText(StringUtils.leftPad("" + (sGui.getOriginalKeyPad()+1), 2));
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "note":
			displayNote();
			break;
		case "parameter":
			displayParameter();
			if (sGui.getParameter() == 1) {
				displayType();
				displayOriginalKeyPad();
			}
			break;
		case "type":
			displayType();
			break;
		case "originalkeypad":
			displayOriginalKeyPad();
			break;

		}

	}

}
