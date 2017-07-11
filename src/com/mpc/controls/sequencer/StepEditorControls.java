package com.mpc.controls.sequencer;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mpc.command.CopySelectedNote;
import com.mpc.command.CopySelectedNotes;
import com.mpc.command.RemoveEvents;
import com.mpc.controls.KbMouseController;
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

public class StepEditorControls extends AbstractSequencerControls {

	private Event[] visibleEvents;

	protected void init() {
		super.init();
		visibleEvents = seGui.getVisibleEvents();
	}

	public void function(int i) {
		init();
		switch (i) {

		case 0:
			mainFrame.openScreen("step_tc", "windowpanel");
			break;
		case 1:
			if (seGui.getSelectionStartIndex() != -1) {
				CopySelectedNotes command = new CopySelectedNotes();
				command.execute();
			} else if (seGui.getSelectionStartIndex() == -1 && param.length() == 2) {
				CopySelectedNote command = new CopySelectedNote(param);
				command.execute();
			}
			break;
		case 2:
			if (seGui.getSelectionStartIndex() != -1) {
				RemoveEvents command = new RemoveEvents(track);
				command.execute();
				mainFrame.getLayeredScreen().setLastFocus("sequencer_step", "a0");
				mainFrame.openScreen("sequencer_step", "mainpanel");
			} else if (param.length() == 2) {
				int eventNumber = Integer.parseInt(param.substring(1, 2));
				if (!(visibleEvents[eventNumber] instanceof EmptyEvent)) {

					track.getEvents().remove(visibleEvents[eventNumber]);
					mainFrame.openScreen("sequencer_step", "mainpanel");
				}
			}
			break;
		case 3:
			if (seGui.getSelectionEndIndex() == -1) {
				mainFrame.openScreen("insertevent", "windowpanel");
			} else {
				int eventNumber = Integer.parseInt(param.substring(1, 2));
				String eventLetter = param.substring(0, 1);
				if (visibleEvents[eventNumber] instanceof PitchBendEvent
						|| visibleEvents[eventNumber] instanceof MixerEvent
						|| visibleEvents[eventNumber] instanceof SystemExclusiveEvent)
					return;

				if (visibleEvents[eventNumber] instanceof PolyPressureEvent && eventLetter.equals("a")) return;
				if (visibleEvents[eventNumber] instanceof ControlChangeEvent && eventLetter.equals("a")) return;
				if (visibleEvents[eventNumber] instanceof NoteEvent && track.getBusNumber() != 0) {
					switch (eventLetter) {
					case "a":
						seGui.setChangeNoteToNumber(((NoteEvent) visibleEvents[eventNumber]).getNote());
						break;
					case "b":
						seGui.setChangeVariationTypeNumber(
								((NoteEvent) visibleEvents[eventNumber]).getVariationTypeNumber());
						break;
					case "c":
						seGui.setChangeVariationTypeNumber(
								((NoteEvent) visibleEvents[eventNumber]).getVariationTypeNumber());
						seGui.setChangeVariationValue(((NoteEvent) visibleEvents[eventNumber]).getVariationValue());
						break;
					case "d":
						seGui.setEditValue(((NoteEvent) visibleEvents[eventNumber]).getDuration());
						break;
					case "e":
						seGui.setEditValue(((NoteEvent) visibleEvents[eventNumber]).getVelocity());
						break;
					}
				}

				if (visibleEvents[eventNumber] instanceof NoteEvent && track.getBusNumber() == 0) {

					switch (eventLetter) {
					case "a":
						seGui.setChangeNoteToNumber(((NoteEvent) visibleEvents[eventNumber]).getNote());
						break;
					case "b":
						seGui.setEditValue(((NoteEvent) visibleEvents[eventNumber]).getDuration());
						break;
					case "c":
						seGui.setEditValue(((NoteEvent) visibleEvents[eventNumber]).getVelocity());
						break;
					}
				}

				if (visibleEvents[eventNumber] instanceof ProgramChangeEvent) {
					seGui.setEditValue(((ProgramChangeEvent) visibleEvents[eventNumber]).getProgram());
				}

				if (visibleEvents[eventNumber] instanceof ChannelPressureEvent) {
					seGui.setEditValue(((ChannelPressureEvent) visibleEvents[eventNumber]).getAmount());
				}

				if (visibleEvents[eventNumber] instanceof PolyPressureEvent) {
					seGui.setEditValue(((PolyPressureEvent) visibleEvents[eventNumber]).getAmount());
				}

				if (visibleEvents[eventNumber] instanceof ControlChangeEvent) {
					seGui.setEditValue(((ControlChangeEvent) visibleEvents[eventNumber]).getAmount());
				}

				seGui.setSelectedEvent(visibleEvents[eventNumber]);
				seGui.setSelectedEvents();
				seGui.setSelectedParameterLetter(eventLetter);
				mainFrame.openScreen("editmultiple", "windowpanel");
				break;
			}

		case 4:
			if (seGui.getPlaceHolder() != null) mainFrame.openScreen("pasteevent", "windowpanel");
			break;

		case 5:
			if (seGui.getSelectionStartIndex() != -1) seGui.clearSelection();
			if (seGui.getSelectionStartIndex() == -1)

			{
				if (param.length() == 2) {
					int eventNumber = Integer.parseInt(param.substring(1, 2));
					if (!(seGui.getVisibleEvents()[eventNumber] instanceof EmptyEvent)) {
						Event clone = (Event) seGui.getVisibleEvents()[eventNumber].clone();
						clone.setTick(-1);
						if (clone instanceof NoteEvent && track.getBusNumber() != 0)
							((NoteEvent) clone).setNote(((NoteEvent) clone).getNote());
						mpc.getEventHandler().handle(clone, track);
					}
				}
			}
			break;
		}

	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("viewmodenumber")) seGui.setViewModeNumber(seGui.getViewModeNumber() + notch);
		if (param.equals("barnumber")) sequencer.setBar(sequencer.getCurrentBarNumber() + notch);
		if (param.equals("beatnumber")) sequencer.setBeat(sequencer.getCurrentBeatNumber() + notch);
		if (param.equals("clocknumber")) sequencer.setClock(sequencer.getCurrentClockNumber() + notch);
		if (param.equals("tcvalue")) sequencer.setTcValue(sequencer.getTcIndex() + notch);

		if (param.equals("fromnote")) {
			if (track.getBusNumber() != 0) seGui.setFromNotePad(seGui.getFromNotePad() + notch);
			if (track.getBusNumber() == 0) seGui.setNoteA(seGui.getNoteA() + notch);
		}

		if (param.equals("tonote")) seGui.setNoteB(seGui.getNoteB() + notch);
		if (param.equals("controlnumber")) seGui.setControlNumber(seGui.getControlNumber() + notch);

		if (param.length() == 2) {
			int eventNumber = Integer.parseInt(param.substring(1, 2));

			if (visibleEvents[eventNumber] instanceof SystemExclusiveEvent) {

				SystemExclusiveEvent event = (SystemExclusiveEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setByteA(event.getByteA() + notch);
				if (param.startsWith("b")) event.setByteB(event.getByteB() + notch);
			}

			if (visibleEvents[eventNumber] instanceof ChannelPressureEvent) {
				ChannelPressureEvent event = (ChannelPressureEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setAmount(event.getAmount() + notch);
			}

			if (visibleEvents[eventNumber] instanceof PolyPressureEvent) {
				PolyPressureEvent event = (PolyPressureEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setNote(event.getNote() + notch);
				if (param.startsWith("b")) event.setAmount(event.getAmount() + notch);
			}

			if (visibleEvents[eventNumber] instanceof ControlChangeEvent) {
				ControlChangeEvent event = (ControlChangeEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setController(event.getController() + notch);
				if (param.startsWith("b")) event.setAmount(event.getAmount() + notch);
			}

			if (visibleEvents[eventNumber] instanceof ProgramChangeEvent) {
				ProgramChangeEvent event = (ProgramChangeEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setProgram(event.getProgram() + notch);
			}

			if (visibleEvents[eventNumber] instanceof PitchBendEvent) {
				PitchBendEvent event = (PitchBendEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setAmount(event.getAmount() + notch);
			}

			if (visibleEvents[eventNumber] instanceof MixerEvent) {
				MixerEvent event = (MixerEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setParameter(event.getParameter() + notch);
				if (param.startsWith("b")) event.setPadNumber(event.getPad() + notch);
				if (param.startsWith("c")) event.setValue(event.getValue() + notch);
			}

			if (visibleEvents[eventNumber] instanceof NoteEvent && track.getBusNumber() == 0) {
				NoteEvent event = (NoteEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) event.setNote(event.getNote() + notch);
				if (param.startsWith("b")) event.setDuration(event.getDuration() + notch);
				if (param.startsWith("c")) event.setVelocity(event.getVelocity() + notch);
			}

			if (visibleEvents[eventNumber] instanceof NoteEvent && track.getBusNumber() != 0) {
				NoteEvent event = (NoteEvent) visibleEvents[eventNumber];
				if (param.startsWith("a")) {
					if (event.getNote() + notch > 98) return;
					if (event.getNote() < 35) {
						event.setNote(35);
						return;
					}
					if (event.getNote() > 98) {
						event.setNote(98);
						return;
					}
					event.setNote(event.getNote() + notch);
				}

				if (param.startsWith("b")) event.setVariationTypeNumber(event.getVariationTypeNumber() + notch);
				if (param.startsWith("c")) event.setVariationValue(event.getVariationValue() + notch);
				if (param.startsWith("d")) event.setDuration(event.getDuration() + notch);
				if (param.startsWith("e")) event.setVelocity(event.getVelocity() + notch);
			}
		}
	}

	public void prevStepEvent() {
		init();
		if (KbMouseController.goToIsPressed) sequencer.goToPreviousEvent();
		if (!KbMouseController.goToIsPressed) sequencer.goToPreviousStep();
	}

	public void nextStepEvent() {
		init();
		if (KbMouseController.goToIsPressed) sequencer.goToNextEvent();
		if (!KbMouseController.goToIsPressed) sequencer.goToNextStep();
	}

	public void prevBarStart() {
		init();
		if (KbMouseController.goToIsPressed) sequencer.setBar(0);
		if (!KbMouseController.goToIsPressed) sequencer.setBar(sequencer.getCurrentBarNumber() - 1);
	}

	public void nextBarEnd() {
		init();
		if (KbMouseController.goToIsPressed) sequencer.setBar(sequencer.getActiveSequence().getLastBar() + 1);
		if (!KbMouseController.goToIsPressed) sequencer.setBar(sequencer.getCurrentBarNumber() + 1);
	}

	public void keyEvent(KeyEvent e) {
		init();
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			if (param.length() == 2) {
				int eventNumber = Integer.parseInt(param.substring(1, 2));
				seGui.setSelectionStartIndex(eventNumber + seGui.getyOffset());
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_DOWN && mainFrame.lookupTextField("a0").isVisible()
				&& param.equals("viewmodenumber")) {
			mainFrame.setFocus("a0", ls.getMainPanel());
			return;
		}

		if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) && param.length() == 2) {

			String src = param;
			String srcLetter = src.substring(0, 1);
			int srcNumber = Integer.parseInt(src.substring(1, 2));

			int increment = 0;
			if (e.getKeyCode() == KeyEvent.VK_DOWN)

			{

				if (srcNumber == 3) {
					if (seGui.getyOffset() + 4 == seGui.getEventsAtCurrentTick().size()) return;
					seGui.setyOffset(seGui.getyOffset() + 1);
					if (e.isShiftDown() && !(visibleEvents[3] instanceof EmptyEvent)) {
						seGui.setSelectionEndIndex(srcNumber + seGui.getyOffset());
					}
					return;
				}
				increment = 1;
			}

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				if (!e.isShiftDown() && srcNumber == 0 && seGui.getyOffset() == 0) {
					seGui.clearSelection();
					mainFrame.lookupTextField("viewmodenumber").grabFocus();
					return;
				}

				if (srcNumber == 0 && seGui.getyOffset() != 0) {
					seGui.setyOffset(seGui.getyOffset() - 1);
					if (e.isShiftDown()) seGui.setSelectionEndIndex(srcNumber + seGui.getyOffset());
				}
				increment = -1;
			}

			String destination = srcLetter + (srcNumber + increment);
			if (srcNumber + increment != -1) {
				if (!(e.isShiftDown() && (visibleEvents[srcNumber + increment] instanceof EmptyEvent))) {
					JTextField tf = mainFrame.lookupTextField(destination);
					if (tf != null && tf.isVisible()) tf.grabFocus();
				}
			}

			if (e.isShiftDown()) seGui.setSelectionEndIndex(srcNumber + increment + seGui.getyOffset());
			if (!e.isShiftDown()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						seGui.checkSelection();
					}
				});
			}
			return;
		}
	}
}