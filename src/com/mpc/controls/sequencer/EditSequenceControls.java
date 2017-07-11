package com.mpc.controls.sequencer;

import com.mpc.sequencer.Event;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;

public class EditSequenceControls extends AbstractSequencerControls {

	public void function(int i) {
		init();

		switch (i) {
		case 1:
			gui.getBarCopyGui().setFromSq(editSequenceGui.getFromSq());
			mainFrame.openScreen("barcopy", "mainpanel");
			break;
		case 2:
			gui.getTrMoveGui().setSq(editSequenceGui.getFromSq());
			mainFrame.openScreen("trmove", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("user", "mainpanel");
			break;
		case 5:
			long sourceStart, sourceEnd;
			long destStart;
			sourceStart = editSequenceGui.getTime0();
			sourceEnd = editSequenceGui.getTime1();

			MpcTrack sourceTrack = (MpcTrack) fromSeq.getTrack(editSequenceGui.getTr0());

			if (editSequenceGui.getEditFunctionNumber() == 0) { // copy
				destStart = editSequenceGui.getStartTicks();
				long destOffset = destStart - sourceStart;
				if (!toSeq.isUsed()) toSeq.init(fromSeq.getLastBar());

				MpcTrack destTrack = (MpcTrack) toSeq.getTrack(editSequenceGui.getTr1());
				if (!destTrack.isUsed()) destTrack.setUsed(true);
				if (editSequenceGui.getEditFunctionNumber() == 0) {
					for (Event event : sourceTrack.getEvents()) {
						if (event.getTick() >= sourceEnd) break;
						if (event.getTick() >= sourceStart) {
							Event temp = (Event) event.clone();
							temp.setTick(temp.getTick() + destOffset);
							destTrack.addEvent(temp);
						}
					}

				}
				destTrack.sortEvents();
			}

			if (editSequenceGui.getEditFunctionNumber() == 1) { // duration
				for (Event event : sourceTrack.getEvents()) {
					if (event instanceof NoteEvent) {
						NoteEvent n = (NoteEvent) event;
						if (editSequenceGui.getDurationMode() == 0) {
							n.setDuration(n.getDuration() + editSequenceGui.getDurationValue());
						}

						if (editSequenceGui.getDurationMode() == 1) {
							n.setDuration(n.getDuration() - editSequenceGui.getDurationValue());
						}

						if (editSequenceGui.getDurationMode() == 2) {
							n.setDuration((int) (n.getDuration() * (editSequenceGui.getDurationValue() / 100.0)));
						}

						if (editSequenceGui.getDurationMode() == 3) {
							n.setDuration(editSequenceGui.getDurationValue());
						}
					}
				}
			}

			if (editSequenceGui.getEditFunctionNumber() == 2) { // velocity
				for (Event event : sourceTrack.getEvents()) {
					if (event instanceof NoteEvent) {
						NoteEvent n = (NoteEvent) event;
						if (editSequenceGui.getVelocityMode() == 0) {
							n.setVelocity(n.getVelocity() + editSequenceGui.getVelocityValue());
						}

						if (editSequenceGui.getVelocityMode() == 1) {
							n.setVelocity(n.getVelocity() - editSequenceGui.getVelocityValue());
						}

						if (editSequenceGui.getVelocityMode() == 2) {
							n.setVelocity((int) (n.getVelocity() * (editSequenceGui.getVelocityValue() / 100.0)));
						}

						if (editSequenceGui.getVelocityMode() == 3) {
							n.setVelocity(editSequenceGui.getVelocityValue());
						}
					}
				}
			}

			if (editSequenceGui.getEditFunctionNumber() == 3) { // transpose
																// AIGhT??
				for (Event event : sourceTrack.getEvents()) {
					if (event instanceof NoteEvent) {
						NoteEvent n = (NoteEvent) event;
						n.setNote(n.getNote() + editSequenceGui.getVelocityValue());
					}
				}
			}

			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}

	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("time0")) {
			editSequenceGui.setTime0(editSequenceGui.setBarNumber(
					(editSequenceGui.getBarNumber(fromSeq, editSequenceGui.getTime0())) + notch, fromSeq,
					editSequenceGui.getTime0()));
		}

		if (param.equals("time1")) {
			editSequenceGui.setTime0(editSequenceGui.setBeatNumber(
					(editSequenceGui.getBeatNumber(fromSeq, editSequenceGui.getTime0())) + notch, fromSeq,
					editSequenceGui.getTime0()));
		}

		if (param.equals("time2")) {
			editSequenceGui.setTime0(editSequenceGui.setClockNumber(
					(editSequenceGui.getClockNumber(fromSeq, editSequenceGui.getTime0())) + notch, fromSeq,
					editSequenceGui.getTime0()));
		}

		if (param.equals("time3")) {
			editSequenceGui.setTime1(editSequenceGui.setBarNumber(
					(editSequenceGui.getBarNumber(fromSeq, editSequenceGui.getTime1())) + notch, fromSeq,
					editSequenceGui.getTime1()));
		}

		if (param.equals("time4")) {
			editSequenceGui.setTime1(editSequenceGui.setBeatNumber(
					(editSequenceGui.getBeatNumber(fromSeq, editSequenceGui.getTime1())) + notch, fromSeq,
					editSequenceGui.getTime1()));
		}

		if (param.equals("time5")) {
			editSequenceGui.setTime1(editSequenceGui.setClockNumber(
					(editSequenceGui.getClockNumber(fromSeq, editSequenceGui.getTime1())) + notch, fromSeq,
					editSequenceGui.getTime1()));
		}

		if (param.equals("start0")) {
			editSequenceGui.setStartTicks(editSequenceGui.setBarNumber(
					(editSequenceGui.getBarNumber(toSeq, editSequenceGui.getStartTicks())) + notch, toSeq,
					editSequenceGui.getStartTicks()));
		}

		if (param.equals("start1")) {
			editSequenceGui.setStartTicks(editSequenceGui.setBeatNumber(
					(editSequenceGui.getBeatNumber(toSeq, editSequenceGui.getStartTicks())) + notch, toSeq,
					editSequenceGui.getStartTicks()));
		}

		if (param.equals("start2")) {
			editSequenceGui.setStartTicks(editSequenceGui.setClockNumber(
					(editSequenceGui.getClockNumber(toSeq, editSequenceGui.getStartTicks())) + notch, toSeq,
					editSequenceGui.getStartTicks()));
		}

		if (param.equals("editfunction")) {
			editSequenceGui.setEditFunctionNumber(editSequenceGui.getEditFunctionNumber() + notch);
		}

		if (param.equals("drumnote")) {
			editSequenceGui.setDrumNote(editSequenceGui.getDrumNote() + notch);
		}

		if (param.equals("midinote0")) {
			editSequenceGui.setMidiNote0(editSequenceGui.getMidiNote0() + notch);
		}

		if (param.equals("midinote1")) {
			editSequenceGui.setMidiNote1(editSequenceGui.getMidiNote1() + notch);
		}

		if (param.equals("fromsq")) {
			editSequenceGui.setFromSq(editSequenceGui.getFromSq() + notch);
			fromSeq = sequencer.getSequence(editSequenceGui.getFromSq());
			if (editSequenceGui.getTime1() > fromSeq.getLastTick())
				editSequenceGui.setTime1(fromSeq.getLastTick());
		}

		if (param.equals("tr0")) {
			editSequenceGui.setTr0(editSequenceGui.getTr0() + notch);
		}

		if (param.equals("tosq")) {
			editSequenceGui.setToSq(editSequenceGui.getToSq() + notch);
			toSeq = sequencer.getSequence(editSequenceGui.getToSq());
			if (editSequenceGui.getStartTicks() > toSeq.getLastTick())
				editSequenceGui.setStartTicks(toSeq.getLastTick());
		}

		if (param.equals("tr1")) {
			editSequenceGui.setTr1(editSequenceGui.getTr1() + notch);
		}

		if (param.equals("mode")) {
			if (editSequenceGui.getEditFunctionNumber() == 0) {
				editSequenceGui.setModeMerge(true);
			}
			if (editSequenceGui.getEditFunctionNumber() == 1) {
				editSequenceGui.setDurationMode(editSequenceGui.getDurationMode() + notch);
			}
			if (editSequenceGui.getEditFunctionNumber() == 2) {
				editSequenceGui.setVelocityMode(editSequenceGui.getVelocityMode() + notch);
			}
			if (editSequenceGui.getEditFunctionNumber() == 3) {
				editSequenceGui.setTransposeAmount(editSequenceGui.getTransposeAmount() + notch);
			}
		}

		if (param.equals("copies")) {
			if (editSequenceGui.getEditFunctionNumber() == 0) {
				editSequenceGui.setCopies(editSequenceGui.getCopies() + notch);
			}
			if (editSequenceGui.getEditFunctionNumber() == 1) {
				editSequenceGui.setDurationValue(editSequenceGui.getDurationValue() + notch);
			}
			if (editSequenceGui.getEditFunctionNumber() == 2) {
				editSequenceGui.setVelocityValue(editSequenceGui.getVelocityValue() + notch);
			}
		}
	}
}