package com.mpc.gui.sequencer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.components.HorizontalBar;
import com.mpc.gui.components.MpcTextField;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.EmptyEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PitchBendEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.SystemExclusiveEvent;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class EventRow {

	private Gui gui = Bootstrap.getGui();

	private String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };

	private String[] noteVarParamNames = { "Tun", "Dcy", "Atk", "Flt" };

	private String[] drumNoteEventLabels = { ">N: ", "", ":", "D:", "V:" };
	private int[] drumNoteEventSizes = { 6, 3, 4, 4, 3 };
	private int[] drumNoteEventXPos = { 0, 66, 85, 120, 162 };

	private String[] midiNoteEventLabels = { ">Note:", "D:", "V:" };
	private int[] midiNoteEventSizes = { 8, 4, 3 };
	private int[] midiNoteEventXPos = { 0, 98, 144 };

	private String[] miscEventLabels = { ">BEND          :", ":" };

	private int[] miscEventSizes = { 5, 0 };

	private int[] miscEventXPos = { 0, 168 };

	private String[] polyPressureEventLabels = { ">POLY PRESSURE :", ":" };
	private int[] polyPressureEventSizes = { 8, 3 };
	private int[] polyPressureEventXPos = { 0, 168 };

	private String[] sysexEventLabels = { ">Exclusive:", "" };
	private int[] sysexEventSizes = { 2, 2 };
	private int[] sysexEventXPos = { 0, 81 };

	private String[] emptyEventLabels = { "" };
	private int[] emptyEventSizes = { 1 };
	private int[] emptyEventXPos = { 6 };

	private String[] channelPressureEventLabels = { ">CH PRESSURE   :            :" };

	private int[] channelPressureEventSizes = { 3 };
	private int[] channelPressureEventXPos = { 0 };

	private boolean selected = false;

	static String[] controlNames = { "BANK SEL MSB", "MOD WHEEL", "BREATH CONT", "03", "FOOT CONTROL", "PORTA TIME",
			"DATA ENTRY", "MAIN VOLUME", "BALANCE", "09", "PAN", "EXPRESSION", "EFFECT 1", "EFFECT 2", "14", "15",
			"GEN.PUR. 1", "GEN.PUR. 2", "GEN.PUR. 3", "GEN.PUR. 4", "20", "21", "22", "23", "24", "25", "26", "27",
			"28", "29", "30", "31", "BANK SEL LSB", "MOD WHEL LSB", "BREATH LSB", "35", "FOOT CNT LSB", "PORT TIME LS",
			"DATA ENT LSB", "MAIN VOL LSB", "BALANCE LSB", "41", "PAN LSB", "EXPRESS LSB", "EFFECT 1 LSB",
			"EFFECT 2 MSB", "46", "47", "GEN.PUR.1 LS", "GEN.PUR.2 LS", "GEN.PUR.3 LS", "GEN.PUR.4 LS", "52", "53",
			"54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "SUSTAIN PDL", "PORTA PEDAL", "SOSTENUTO",
			"SOFT PEDAL", "LEGATO FT SW", "HOLD 2", "SOUND VARI", "TIMBER/HARMO", "RELEASE TIME", "ATTACK TIME",
			"BRIGHTNESS", "SOUND CONT 6", "SOUND CONT 7", "SOUND CONT 8", "SOUND CONT 9", "SOUND CONT10", "GEN.PUR. 5",
			"GEN.PUR. 6", "GEN.PUR. 7", "GEN.PUR. 8", "PORTA CNTRL", "85", "86", "87", "88", "89", "90", "EXT EFF DPTH",
			"TREMOLO DPTH", "CHORUS DEPTH", " DETUNE DEPTH", "PHASER DEPTH", "DATA INCRE", "DATA DECRE", "NRPN LSB",
			"NRPN MSB", "RPN LSB", "RPN MSB", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111",
			"112", "113", "114", "115", "116", "117", "118", "119", "ALL SND OFF", "RESET CONTRL", "LOCAL ON/OFF",
			"ALL NOTE OFF", "OMNI OFF", "OMNI ON", "MONO MODE ON", "POLY MODE ON" };
	private String[] controlChangeEventLabels = { ">CONTROL CHANGE:", ":" }; // also
																				// used
																				// for
																				// PROGRAM
																				// CHANGE
	private int[] controlChangeEventSizes = { 12, 3 };
	private int[] controlChangeEventXPos = { 0, 168 };

	private String[] mixerParamNames = { "STEREO LEVEL", "STEREO PAN  ", "FXsend LEVEL", "INDIV LEVEL" };
	private String[] mixerEventLabels = { ">", "N:", "L:" };
	private int[] mixerEventSizes = { 12, 6, 3 };
	private int[] mixerEventXPos = { 0, 96, 162 };

	private ArrayList<JComponent> eventRow;

	private boolean midi;
	private Event event;

	private HorizontalBar horizontalBar;
	private SelectedEventBar selectedEventBar;

	private MpcTextField[] tfArray;
	private JLabel[] labelArray;

	private Sampler sampler;

	private MpcSoundPlayerChannel mpcSoundPlayerChannel;

	private Program program;
	private int rowNumber;
	private SamplerGui samplerGui;

	public EventRow(Mpc mpc, Event e, int rowNumber) {

		samplerGui = gui.getSamplerGui();

		sampler = mpc.getSampler();
		this.mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getTrackDrum());
		this.program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		midi = false;
		event = e;
		this.rowNumber = rowNumber;

		eventRow = new ArrayList<JComponent>();
		horizontalBar = gui.getMainFrame().getLayeredScreen().getHorizontalBarsStepEditor()[rowNumber];
		selectedEventBar = gui.getMainFrame().getLayeredScreen().getSelectedEventBarsStepEditor()[rowNumber];
		eventRow.add(horizontalBar);
		eventRow.add(selectedEventBar);
		initLabelsAndFields();
	}

	public void setMidi(boolean b) {
		midi = b;
	}

	public void init() {

		if (event instanceof NoteEvent) {

			if (midi) {
				setLabelTexts(midiNoteEventLabels);
				setSizeAndLocation(midiNoteEventXPos, midiNoteEventSizes);
				setMidiNoteEventValues();
			}

			if (!midi) {
				setLabelTexts(drumNoteEventLabels);
				setSizeAndLocation(drumNoteEventXPos, drumNoteEventSizes);
				setDrumNoteEventValues();
			}

		}

		if (event instanceof EmptyEvent) {
			setLabelTexts(emptyEventLabels);
			setSizeAndLocation(emptyEventXPos, emptyEventSizes);
			setEmptyEventValues();
		}

		if (event instanceof PitchBendEvent) {
			setLabelTexts(miscEventLabels);
			setSizeAndLocation(miscEventXPos, miscEventSizes);
			setMiscEventValues();
		}

		if (event instanceof ProgramChangeEvent) {
			setLabelTexts(miscEventLabels);
			setSizeAndLocation(miscEventXPos, miscEventSizes);
			setMiscEventValues();
		}

		if (event instanceof ControlChangeEvent) {
			setLabelTexts(controlChangeEventLabels);
			setSizeAndLocation(controlChangeEventXPos, controlChangeEventSizes);
			setControlChangeEventValues();
		}

		if (event instanceof ChannelPressureEvent) {
			setLabelTexts(channelPressureEventLabels);
			setSizeAndLocation(channelPressureEventXPos, channelPressureEventSizes);
			setChannelPressureEventValues();
		}

		if (event instanceof PolyPressureEvent) {
			setLabelTexts(polyPressureEventLabels);
			setSizeAndLocation(polyPressureEventXPos, polyPressureEventSizes);
			setPolyPressureEventValues();
		}

		if (event instanceof SystemExclusiveEvent) {
			setLabelTexts(sysexEventLabels);
			setSizeAndLocation(sysexEventXPos, sysexEventSizes);
			setSystemExclusiveEventValues();
		}

		if (event instanceof MixerEvent) {
			setLabelTexts(mixerEventLabels);
			setSizeAndLocation(mixerEventXPos, mixerEventSizes);
			setMixerEventValues();
		}

		if (tfArray != null) {
			for (int i = 0; i < tfArray.length; i++) {
				eventRow.add(labelArray[i]);
				eventRow.add(tfArray[i]);
			}
		}
	}

	public void setEmptyEventValues() {
		tfArray[0].setVisible(true);
		labelArray[0].setVisible(true);
		tfArray[0].setText(" ");
		horizontalBar.setVisible(false);
		tfArray[1].setVisible(false);
		tfArray[2].setVisible(false);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[1].setVisible(false);
		labelArray[2].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setSystemExclusiveEventValues() {
		if (event == null) return;
		SystemExclusiveEvent see = (SystemExclusiveEvent) event;
		for (int i = 0; i < 2; i++) {
			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);
		}
		tfArray[0].setText(Util.padLeft2Zeroes(Integer.toHexString(see.getByteA()).toUpperCase()));
		tfArray[1].setText(Util.padLeft2Zeroes(Integer.toHexString(see.getByteB()).toUpperCase()));
		horizontalBar.setVisible(false);
		tfArray[2].setVisible(false);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[2].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setPolyPressureEventValues() {
		if (event == null) return;
		PolyPressureEvent ppe = (PolyPressureEvent) event;
		for (int i = 0; i < 2; i++) {
			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);
		}
		tfArray[0].setText(
				Util.padLeftSpace("" + ppe.getNote(), 3) + "(" + Gui.noteNames[ppe.getNote()] + ")");
		tfArray[1].setText(Util.padLeftSpace("" + ppe.getAmount(), 3));
		horizontalBar.setValue(ppe.getAmount());
		horizontalBar.setVisible(true);
		tfArray[2].setVisible(false);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[2].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setChannelPressureEventValues() {
		if (event == null) return;
		ChannelPressureEvent cpe = (ChannelPressureEvent) event;
		tfArray[0].setVisible(true);
		labelArray[0].setVisible(true);
		tfArray[0].setText(Util.padLeftSpace("" + cpe.getAmount(), 3));
		horizontalBar.setValue(cpe.getAmount());
		horizontalBar.setVisible(true);
		tfArray[1].setVisible(false);
		tfArray[2].setVisible(false);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[1].setVisible(false);
		labelArray[2].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setControlChangeEventValues() {
		if (event == null) return;
		ControlChangeEvent cce = (ControlChangeEvent) event;
		for (int i = 0; i < 2; i++) {
			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);
		}
		tfArray[0].setText(controlNames[cce.getController()]);
		tfArray[1].setText(Util.padLeftSpace("" + cce.getAmount(), 3));
		horizontalBar.setValue(cce.getAmount());
		horizontalBar.setVisible(true);
		tfArray[2].setVisible(false);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[2].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setMiscEventValues() {
		if (event == null) return;
		int parameterValue = 0;

		if (event instanceof PitchBendEvent) {
			parameterValue = ((PitchBendEvent) event).getAmount();
		}

		if (event instanceof ProgramChangeEvent) {
			parameterValue = ((ProgramChangeEvent) event).getProgram();
			labelArray[0].setText(">PROGRAM CHANGE:");
			tfArray[0].setSize(3 * 6 * 2 + 2, 18);
		}

		for (int i = 0; i < 2; i++) {
			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);
		}
		tfArray[0].setText(Util.padLeftSpace("" + parameterValue, 3));
		if (event instanceof PitchBendEvent) {
			if (parameterValue > 0) {
				tfArray[0].setText("+" + Util.padLeftSpace("" + parameterValue, 4));
			}
			if (parameterValue < 0) {
				tfArray[0].setText("-" + Util.padLeftSpace("" + Math.abs(parameterValue), 4));
			}
			if (parameterValue == 0) {
				tfArray[0].setText("    0");
			}
		}
		horizontalBar.setVisible(false);
		tfArray[1].setVisible(false);
		tfArray[2].setVisible(false);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[2].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setMixerEventValues() {
		if (event == null) return;

		MixerEvent me = (MixerEvent) event;

		for (int i = 0; i < 3; i++) {

			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);

		}

		tfArray[0].setText(mixerParamNames[me.getParameter()]);
		int nn = program.getPad(me.getPad()).getNote();
		tfArray[1].setText((nn == 34 ? "--" : nn) + "/" + sampler.getPadName(me.getPad()));

		if (me.getParameter() == 1) {

			labelArray[2].setText("P:");
			String panning = "L";

			if (me.getValue() > 50) panning = "R";

			tfArray[2].setText(panning + Util.padLeftSpace("" + Math.abs(me.getValue() - 50), 2));
			if (me.getValue() == 50) {
				tfArray[2].setText("0  ");
			}
		} else {
			labelArray[2].setText("L:");
			tfArray[2].setText(Util.padLeftSpace("" + me.getValue(), 3));
		}
		
		horizontalBar.setValue((int) (me.getValue() * 1.27));
		horizontalBar.setVisible(true);
		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	public void setDrumNoteEventValues() {

		if (event == null) return;

		NoteEvent ne = (NoteEvent) event;

		for (int i = 0; i < 5; i++) {

			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);

		}

		if (ne.getNote() < 35 || ne.getNote() > 98) {

			tfArray[0].setText("--/OFF");

		} else {

			tfArray[0].setText(
					ne.getNote() + "/" + sampler.getPadName(program.getPadNumberFromNote(ne.getNote())));

		}

		tfArray[1].setText(noteVarParamNames[ne.getVariationTypeNumber()]);

		if (ne.getVariationTypeNumber() == 0) {

			tfArray[2].setSize(4 * 6 * 2 + 2, 18);
			tfArray[2].setLocation(180, tfArray[2].getLocation().y);

			int noteVarValue = (ne.getVariationValue() * 2) - 128;

			if (noteVarValue < -120) noteVarValue = -120;

			if (noteVarValue > 120) noteVarValue = 120;

			if (noteVarValue == 0) tfArray[2].setText(Util.padLeftSpace("0", 4));

			if (noteVarValue < 0) tfArray[2].setText("-" + Util.padLeftSpace("" + Math.abs(noteVarValue), 3));

			if (noteVarValue > 0) tfArray[2].setText("+" + Util.padLeftSpace("" + noteVarValue, 3));
		}

		if (ne.getVariationTypeNumber() == 1 || ne.getVariationTypeNumber() == 2) {

			int noteVarValue = ne.getVariationValue();

			if (noteVarValue > 100) noteVarValue = 100;

			tfArray[2].setText(Util.padLeftSpace("" + noteVarValue, 3));
			tfArray[2].setSize(3 * 6 * 2 + 2, 18);
			tfArray[2].setLocation(180 + 12, tfArray[2].getLocation().y);
		}

		if (ne.getVariationTypeNumber() == 3) {

			tfArray[2].setSize(4 * 6 * 2 + 2, 18);
			tfArray[2].setLocation(180, tfArray[2].getLocation().y);

			int noteVarValue = ne.getVariationValue() - 50;

			if (noteVarValue > 50) noteVarValue = 50;

			if (noteVarValue < 0) tfArray[2].setText("-" + Util.padLeftSpace("" + Math.abs(noteVarValue), 2));

			if (noteVarValue > 0) tfArray[2].setText("+" + Util.padLeftSpace("" + noteVarValue, 2));

			if (noteVarValue == 0) tfArray[2].setText(Util.padLeftSpace("0", 3));
		}

		tfArray[3].setText(Util.padLeftSpace("" + ne.getDuration(), 4));
		tfArray[4].setText(Util.padLeftSpace("" + ne.getVelocity(), 3));

		horizontalBar.setValue(ne.getVelocity());
		horizontalBar.setVisible(true);
	}

	public void setMidiNoteEventValues() {

		if (event == null) return;

		NoteEvent ne = (NoteEvent) event;

		for (int i = 0; i < 3; i++) {
			tfArray[i].setVisible(true);
			labelArray[i].setVisible(true);
		}

		tfArray[0].setText(
				Util.padLeftSpace("" + ne.getNote(), 3) + "(" + Gui.noteNames[ne.getNote()] + ")");
		tfArray[1].setText(Util.padLeftSpace("" + ne.getDuration(), 4));
		tfArray[2].setText("" + ne.getVelocity());

		horizontalBar.setValue(ne.getVelocity());
		horizontalBar.setVisible(true);

		tfArray[3].setVisible(false);
		tfArray[4].setVisible(false);
		labelArray[3].setVisible(false);
		labelArray[4].setVisible(false);
	}

	private void initLabelsAndFields() {

		tfArray = new MpcTextField[5];
		labelArray = new JLabel[5];

		for (int i = 0; i < 5; i++) {

			tfArray[i] = (MpcTextField) gui.getMainFrame().lookupTextField(letters[i] + rowNumber);

			labelArray[i] = gui.getMainFrame().lookupLabel(letters[i] + rowNumber);

		}

		horizontalBar.setVisible(false);
		setColors();
	}

	private void setColors() {

		for (int i = 0; i < 5; i++) {

			if (selected) {

				selectedEventBar.setVisible(true);
				labelArray[i].setForeground(Bootstrap.lcdOff);
				labelArray[i].setOpaque(true);

				if (tfArray[i].hasFocus()) {

					tfArray[i].setForeground(Bootstrap.lcdOn);
					tfArray[i].setBackground(Bootstrap.lcdOff);

				} else {

					tfArray[i].setForeground(Bootstrap.lcdOff);
					tfArray[i].setBackground(Bootstrap.lcdOn);

				}
			} else {

				selectedEventBar.setVisible(false);
				labelArray[i].setForeground(Bootstrap.lcdOn);
				labelArray[i].setOpaque(false);

				if (tfArray[i].hasFocus()) {

					tfArray[i].setForeground(Bootstrap.lcdOff);
					tfArray[i].setBackground(Bootstrap.lcdOn);

				} else {

					tfArray[i].setForeground(Bootstrap.lcdOn);
					tfArray[i].setBackground(Bootstrap.lcdOff);
				}
			}
		}
	}

	private void setLabelTexts(String[] labels) {
		for (int i = 0; i < labels.length; i++)
			labelArray[i].setText(labels[i]);
	}

	private void setSizeAndLocation(int[] xPosArray, int[] sizeArray) {

		Border empty = new EmptyBorder(3, 2, 1, -3);

		for (int i = 0; i < xPosArray.length; i++) {
			tfArray[i].setSize(sizeArray[i] * 6 * 2 + 2, 18);
			labelArray[i].setSize(labelArray[i].getText().length() * 6 * 2, 18);
			labelArray[i].setLocation(xPosArray[i] * 2 - 2, 22 + (rowNumber * 18));
			labelArray[i].setBorder(empty);

			tfArray[i].setLocation((xPosArray[i] * 2) + (labelArray[i].getText().length() * 6 * 2) - 2,
					22 + (rowNumber * 18));
		}
	}

	public List<JComponent> getEventRow() {
		return eventRow;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public void setSelected(boolean b) {
		selected = b;
		setColors();
	}

	public boolean isSelected() {
		return selected;
	}
}