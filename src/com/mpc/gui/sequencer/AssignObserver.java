package com.mpc.gui.sequencer;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sequencer.window.Assign16LevelsObserver;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sampler.Slider;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;

public class AssignObserver implements Observer {

	private Slider slider;
	private JTextField assignNoteField;
	private JTextField parameterField;
	private JTextField highRangeField;
	private JTextField lowRangeField;
	private JTextField assignNvField;
	private Sampler sampler;
	private Program program;

	public AssignObserver(MainFrame mainFrame) {
		sampler = Bootstrap.getGui().getMpc().getSampler();
		Sequencer sequencer = Bootstrap.getGui().getMpc().getSequencer();
		MpcSequence seq = sequencer.getActiveSequence();

		program = sampler.getProgram(
				sampler.getDrumBusProgramNumber(seq.getTrack(sequencer.getActiveTrackIndex()).getBusNumber()));

		slider = program.getSlider();
		slider.deleteObservers();
		slider.addObserver(this);

		assignNoteField = mainFrame.lookupTextField("assignnote");
		parameterField = mainFrame.lookupTextField("parameter");
		highRangeField = mainFrame.lookupTextField("highrange");
		lowRangeField = mainFrame.lookupTextField("lowrange");
		assignNvField = mainFrame.lookupTextField("assignnv");

		displayAssignNote();
		displayParameter();
		displayHighRange();
		displayLowRange();
		displayAssignNv();

	}

	private void displayAssignNote() {
		int nn = slider.getNote();
		if (nn == 34) {
			assignNoteField.setText("OFF");
			return;
		}
		String padName = sampler.getPadName(program.getPadNumberFromNote(nn));
		int sn = program.getNoteParameters(nn).getSndNumber();
		String soundName = sn == -1 ? "OFF" : sampler.getSoundName(sn);
		assignNoteField.setText(nn + "/" + padName + "-" + soundName);
	}

	private void displayParameter() {
		parameterField.setText(Assign16LevelsObserver.TYPE_NAMES[slider.getParameter()]);
	}

	private void displayHighRange() {
		int hr = 0;
		switch (slider.getParameter()) {
		case 0:
			hr = slider.getTuneHighRange();
			break;
		case 1:
			hr = slider.getDecayHighRange();
			break;
		case 2:
			hr = slider.getAttackHighRange();
			break;
		case 3:
			hr = slider.getFilterHighRange();
			break;
		}
		highRangeField.setText(StringUtils.leftPad("" + hr, 3));
	}

	private void displayLowRange() {
		int lr = 0;
		switch (slider.getParameter()) {
		case 0:
			lr = slider.getTuneLowRange();
			break;
		case 1:
			lr = slider.getDecayLowRange();
			break;
		case 2:
			lr = slider.getAttackLowRange();
			break;
		case 3:
			lr = slider.getFilterLowRange();
			break;
		}
		lowRangeField.setText(StringUtils.leftPad("" + lr, 3));
	}

	private void displayAssignNv() {
		String assignNvString = slider.getControlChange() == 0 ? "OFF"
				: StringUtils.leftPad("" + slider.getControlChange(), 3);
		assignNvField.setText(assignNvString);
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((String) arg) {
		case "assignnote":
			displayAssignNote();
			break;
		case "parameter":
			displayParameter();
			displayHighRange();
			displayLowRange();
			break;
		case "highrange":
			displayHighRange();
			break;
		case "lowrange":
			displayLowRange();
			break;
		case "controlchange":
			displayAssignNv();
			break;
		}

	}

}
