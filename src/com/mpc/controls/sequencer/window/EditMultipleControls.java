package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;

public class EditMultipleControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		Event sEvent = seGui.getSelectedEvent();
		String paramLetter = seGui.getParamLetter();

		switch (i) {
		case 5:
			if (sEvent instanceof NoteEvent && track.getBusNumber() != 0) {
				switch (paramLetter) {
				case "a":
					checkNotes();
					break;
				case "b":
					for (Event event : seGui.getSelectedEvents())
						if (event instanceof NoteEvent)
							((NoteEvent) event).setVariationTypeNumber(seGui.getChangeVariationTypeNumber());
					break;
				case "c":
					for (Event event : seGui.getSelectedEvents())
						if (event instanceof NoteEvent)
							((NoteEvent) event).setVariationValue(seGui.getChangeVariationValue());
					break;
				case "d":
					checkFiveParameters();
					break;
				case "e":
					checkThreeParameters();
					break;
				}
			}
			if (sEvent instanceof NoteEvent && track.getBusNumber() == 0) {
				switch (paramLetter) {
				case "a":
					checkNotes();
					break;
				case "b":
					checkFiveParameters();
					break;
				case "c":
					checkThreeParameters();
					break;
				}
			}
			if (sEvent instanceof ControlChangeEvent) {
				switch (paramLetter) {
				case "a":
					checkFiveParameters();
					break;
				case "b":
					checkThreeParameters();
					break;
				}
			}

			if (sEvent instanceof ProgramChangeEvent || sEvent instanceof ChannelPressureEvent) checkFiveParameters();
			if (sEvent instanceof PolyPressureEvent) {
				switch (paramLetter) {
				case "a":
					checkFiveParameters();
					break;
				case "b":
					checkThreeParameters();
					break;
				}
			}

			mainFrame.openScreen("sequencer_step", "mainpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		Event sEvent = seGui.getSelectedEvent();
		String paramLetter = seGui.getParamLetter();
		if (param.equals("value0")) {

			if (sEvent instanceof NoteEvent && track.getBusNumber() != 0) {

				switch (paramLetter) {

				case "a":
					if (seGui.getChangeNoteToNumber() == 98) return;

					seGui.setChangeNoteToNumber(seGui.getChangeNoteToNumber() + notch);
					break;

				case "b":
					seGui.setChangeVariationTypeNumber(seGui.getChangeVariationTypeNumber() + notch);
					break;

				case "c":
					seGui.setChangeVariationValue(seGui.getChangeVariationValue() + notch);
					break;

				case "d":
				case "e":
					seGui.setEditTypeNumber(seGui.getEditTypeNumber() + notch);
					break;
				}
			}

			if (sEvent instanceof NoteEvent && track.getBusNumber() == 0) {

				switch (paramLetter) {

				case "a":
					seGui.setChangeNoteToNumber(seGui.getChangeNoteToNumber() + notch);
					break;

				case "b":
				case "c":
					seGui.setEditTypeNumber(seGui.getEditTypeNumber() + notch);
					break;

				}
			}

			if (sEvent instanceof ProgramChangeEvent || sEvent instanceof PolyPressureEvent
					|| sEvent instanceof ChannelPressureEvent || sEvent instanceof ControlChangeEvent) {

				seGui.setEditTypeNumber(seGui.getEditTypeNumber() + notch);
			}
		}

		if (param.equals("value1")) seGui.setEditValue(seGui.getEditValue() + 1);

	}

	private void checkThreeParameters() {

		for (Event event : seGui.getSelectedEvents()) {

			if (event instanceof NoteEvent) ((NoteEvent) event).setVelocity(seGui.getEditValue());

			if (event instanceof ControlChangeEvent)
				((ControlChangeEvent) event).setAmount(seGui.getEditValue());

			if (event instanceof PolyPressureEvent) ((PolyPressureEvent) event).setAmount(seGui.getEditValue());
		}
	}

	private void checkFiveParameters() {

		for (Event event : seGui.getSelectedEvents()) {
			if (event instanceof NoteEvent) ((NoteEvent) event).setDuration(seGui.getEditValue());

			if (event instanceof ProgramChangeEvent)
				((ProgramChangeEvent) event).setProgram(seGui.getEditValue());

			if (event instanceof ControlChangeEvent)
				((ControlChangeEvent) event).setController(seGui.getEditValue());

			if (event instanceof ChannelPressureEvent) ((ChannelPressureEvent) event).setAmount(seGui.getEditValue());
			if (event instanceof PolyPressureEvent) ((PolyPressureEvent) event).setNote(seGui.getEditValue());
		}
	}

	private void checkNotes() {
		for (Event event : seGui.getSelectedEvents())
			if (event instanceof NoteEvent) ((NoteEvent) event).setNote(seGui.getChangeNoteToNumber());
	}
}
