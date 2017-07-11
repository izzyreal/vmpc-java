package com.mpc.gui.sequencer.window;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.gui.sequencer.StepEditorGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class StepWindowObserver implements Observer {

	private String[] timingCorrectNames = new String[] { "OFF", "1/8",
			"1/8(3)", "1/16", "1/16(3)", "1/32", "1/32(3)" };
	
	private String[] eventTypeNames = { "NOTE", "PITCH BEND", "CONTROL CHANGE",
			"PROGRAM CHANGE", "CH PRESSURE", "POLY PRESSURE", "EXCLUSIVE",
			"MIXER" };
	
	private String[] noteVariationParameterNames = { "Tun", "Dcy", "Atk", "Flt" };

	private String[] editTypeNames = { "ADD VALUE", "SUB VALUE", "MULT VAL%",
			"SET TO VAL" };

	private Sequencer sequencer;
	private int trackNum;
	private int seqNum;

	private MpcSequence mpcSequence;
	private MpcTrack track;

	private JTextField tcValueField;
	private JTextField eventtypeField;

	private JTextField editMultiValue0Field;
	private JTextField editMultiValue1Field;
	private JLabel editMultiParam0Label;
	private JLabel editMultiParam1Label;

	private StepEditorGui seqGui;

	private int xPosSingle = 120;
	private int yPosSingle = 50;
	private String[] singleLabels = { "Change note to:", "Variation type:",
			"Variation value:" };
	private int[] xPosDouble = { 120, 144 };
	private int[] yPosDouble = { 45, 63 };
	private String[] doubleLabels = { "Edit type:", "Value:" };
	private Sampler sampler;
	private Program program;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;
	private SamplerGui samplerGui;

	public StepWindowObserver(Mpc mpc, MainFrame mainFrame)
			throws UnsupportedEncodingException {
		this.sequencer = mpc.getSequencer();
		this.sampler = mpc.getSampler();
		
		seqGui = Bootstrap.getGui().getStepEditorGui();
		samplerGui = Bootstrap.getGui().getSamplerGui();
		seqGui.deleteObservers();
		seqGui.addObserver(this);
		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getTrackDrum());
		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());
		sequencer.deleteObservers();
		sequencer.addObserver(this);

		if (mainFrame.getLayeredScreen().getCurrentScreenName()
				.equals("step_tc")) {
			tcValueField = mainFrame.lookupTextField("tcvalue");

			tcValueField.setText(timingCorrectNames[sequencer
					.getTcIndex()]);
		}

		if (mainFrame.getLayeredScreen().getCurrentScreenName()
				.equals("insertevent")) {
			eventtypeField = mainFrame.lookupTextField("eventtype");
			eventtypeField.setText(eventTypeNames[seqGui.getInsertEventType()]);
		}

		if (mainFrame.getLayeredScreen().getCurrentScreenName()
				.equals("editmultiple")) {
			editMultiParam0Label = mainFrame.lookupLabel("value0");
			editMultiParam1Label = mainFrame.lookupLabel("value1");
			editMultiValue0Field = mainFrame.lookupTextField("value0");
			editMultiValue1Field = mainFrame.lookupTextField("value1");
			updateEditMultiple();
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {
		case "timing":
			tcValueField.setText(timingCorrectNames[sequencer
					.getTcIndex()]);
			break;
		case "eventtype":
			eventtypeField.setText(eventTypeNames[seqGui.getInsertEventType()]);
			break;
		case "editmultiple":
			updateEditMultiple();
			break;

		}
	}

	private void updateEditMultiple() {
		if (seqGui.getSelectedEvent() instanceof NoteEvent
				&& track.getBusNumber() != 0) {

			if (seqGui.getParamLetter().equals("a")
					|| seqGui.getParamLetter().equals("b")
					|| seqGui.getParamLetter().equals("c")) {
				editMultiParam1Label.setVisible(false);
				editMultiValue1Field.setVisible(false);

				editMultiParam0Label.setVisible(true);
				editMultiParam0Label.setLocation(xPosSingle, yPosSingle);
				if (seqGui.getParamLetter().equals("a")) {
					editMultiParam0Label.setText(singleLabels[0]);
					editMultiValue0Field.setSize(6 * 6 * 2 + 2, 18);
					editMultiValue0Field.setText(seqGui
							.getChangeNoteToNumber()
							+ "/"
							+ sampler.getPadName(program.getPadNumberFromNote(seqGui
									.getChangeNoteToNumber())));
				}
				if (seqGui.getParamLetter().equals("b")) {
					editMultiParam0Label.setText(singleLabels[1]);
					editMultiValue0Field.setSize(3 * 6 * 2 + 2, 18);
					editMultiValue0Field
							.setText(noteVariationParameterNames[seqGui
									.getChangeVariationTypeNumber()]);
				}
				if (seqGui.getParamLetter().equals("c")) {
					editMultiParam0Label.setText(singleLabels[2]);

					if (seqGui.getChangeVariationTypeNumber() == 0) {
						editMultiValue0Field.setSize(4 * 6 * 2 + 2, 18);
						editMultiValue0Field.setLocation(180,
								editMultiValue0Field.getLocation().y);
						int noteVarValue = (seqGui.getChangeVariationValue() * 2) - 128;
						if (noteVarValue < -120)
							noteVarValue = -120;
						if (noteVarValue > 120)
							noteVarValue = 120;
						if (noteVarValue == 0) {
							editMultiValue0Field.setText(Util.padLeftSpace("0", 4));
						}
						if (noteVarValue < 0) {
							editMultiValue0Field.setText("-"
									+ Util.padLeftSpace("" + Math.abs(noteVarValue),
											3));
						}
						if (noteVarValue > 0) {
							editMultiValue0Field.setText("+"
									+ Util.padLeftSpace("" + noteVarValue, 3));
						}
					}

					if (seqGui.getChangeVariationTypeNumber() == 1
							|| seqGui.getChangeVariationTypeNumber() == 2) {
						int noteVarValue = seqGui.getChangeVariationValue();
						if (noteVarValue > 100)
							noteVarValue = 100;
						editMultiValue0Field.setText(Util.padLeftSpace(""
								+ noteVarValue, 3));
						editMultiValue0Field.setSize(3 * 6 * 2 + 2, 18);
						editMultiValue0Field.setLocation(180 + 12,
								editMultiValue0Field.getLocation().y);
					}

					if (seqGui.getChangeVariationTypeNumber() == 3) {
						editMultiValue0Field.setSize(4 * 6 * 2 + 2, 18);
						editMultiValue0Field.setLocation(180,
								editMultiValue0Field.getLocation().y);
						int noteVarValue = seqGui.getChangeVariationValue() - 50;
						if (noteVarValue > 50)
							noteVarValue = 50;
						if (noteVarValue < 0) {
							editMultiValue0Field.setText("-"
									+ Util.padLeftSpace("" + Math.abs(noteVarValue),
											2));
						}
						if (noteVarValue > 0) {
							editMultiValue0Field.setText("+"
									+ Util.padLeftSpace("" + noteVarValue, 2));
						}
						if (noteVarValue == 0) {
							editMultiValue0Field.setText(Util.padLeftSpace("0", 3));
						}
					}
				}
				editMultiParam0Label.setSize(editMultiParam0Label.getText()
						.length() * 6 * 2 + 2, 18);
				editMultiValue0Field.setVisible(true);
				editMultiValue0Field.setLocation(xPosSingle
						+ editMultiParam0Label.getSize().width, yPosSingle);
			}

			if (seqGui.getParamLetter().equals("d")
					|| seqGui.getParamLetter().equals("e")) {
				updateDouble();
			}

		}

		if (seqGui.getSelectedEvent() instanceof NoteEvent
				&& track.getBusNumber() == 0) {

			if (seqGui.getParamLetter().equals("a")) {
				editMultiParam1Label.setVisible(false);
				editMultiValue1Field.setVisible(false);

				editMultiParam0Label.setVisible(true);
				editMultiParam0Label.setLocation(xPosSingle, yPosSingle);
				editMultiParam0Label.setText(singleLabels[0]);
				editMultiValue0Field.setSize(8 * 6 * 2 + 2, 18);
				editMultiValue0Field.setText(Util.padLeftSpace(
						"" + seqGui.getChangeNoteToNumber(), 3)
						+ "("
						+ Gui.noteNames[seqGui.getChangeNoteToNumber()]
						+ ")");
				editMultiParam0Label.setSize(editMultiParam0Label.getText()
						.length() * 6 * 2 + 2, 18);
				editMultiValue0Field.setVisible(true);
				editMultiValue0Field.setLocation(xPosSingle
						+ editMultiParam0Label.getSize().width, yPosSingle);
			}

			if (seqGui.getParamLetter().equals("b")
					|| seqGui.getParamLetter().equals("c")) {
				updateDouble();
			}

		}

		if (seqGui.getSelectedEvent() instanceof ProgramChangeEvent
				|| seqGui.getSelectedEvent() instanceof PolyPressureEvent
				|| seqGui.getSelectedEvent() instanceof ChannelPressureEvent
				|| seqGui.getSelectedEvent() instanceof ControlChangeEvent) {
			updateDouble();
		}
	}

	private void updateDouble() {
		editMultiParam0Label.setVisible(true);
		editMultiParam1Label.setVisible(true);
		editMultiValue0Field.setVisible(true);
		editMultiValue1Field.setVisible(true);

		editMultiParam0Label.setText(doubleLabels[0]);
		editMultiParam1Label.setText(doubleLabels[1]);

		editMultiParam0Label.setSize(
				editMultiParam0Label.getText().length() * 6 * 2 + 2, 18);
		editMultiParam0Label.setLocation(xPosDouble[0], yPosDouble[0]);

		editMultiParam1Label.setSize(
				editMultiParam1Label.getText().length() * 6 * 2 + 2, 18);
		editMultiParam1Label.setLocation(xPosDouble[1], yPosDouble[1]);

		editMultiValue0Field.setLocation(
				xPosDouble[0] + editMultiParam0Label.getSize().width,
				yPosDouble[0]);
		editMultiValue1Field.setLocation(
				xPosDouble[1] + editMultiParam1Label.getSize().width,
				yPosDouble[1]);

		editMultiValue0Field.setText(editTypeNames[seqGui
				.getEditTypeNumber()]);
		editMultiValue1Field.setText("" + seqGui.getEditValue());

		editMultiValue0Field.setSize(
				editMultiValue0Field.getText().length() * 6 * 2 + 2, 18);
		editMultiValue1Field.setSize(
				editMultiValue1Field.getText().length() * 6 * 2 + 2, 18);
	}
}