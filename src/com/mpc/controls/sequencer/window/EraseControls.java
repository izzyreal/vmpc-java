package com.mpc.controls.sequencer.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;

public class EraseControls extends AbstractSequencerControls {

	private String[] eventClassNames = { "com.mpc.sequencer.NoteEvent", "com.mpc.sequencer.PitchBendEvent",
			"com.mpc.sequencer.ControlChangeEvent", "com.mpc.sequencer.ProgramChangeEvent",
			"com.mpc.sequencer.ChannelPressureEvent", "com.mpc.sequencer.PolyPressureEvent",
			"com.mpc.sequencer.SystemExclusiveEvent" };

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("track")) gui.getEraseGui().setTrack(gui.getEraseGui().getTrack() + notch);
		this.checkAllTimesAndNotes(notch);
		if (param.equals("erase")) gui.getEraseGui().setErase(gui.getEraseGui().getErase() + notch);
		if (param.equals("type")) gui.getEraseGui().setType(gui.getEraseGui().getType() + notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		case 4:
			MpcSequence seq = sequencer.getActiveSequence();
			int startIndex = gui.getEraseGui().getTrack() - 1;
			int lastIndex = gui.getEraseGui().getTrack() - 1;
			if (startIndex < 0) {
				startIndex = 0;
				lastIndex = 63;
			}
			int erase = gui.getEraseGui().getErase();
			int type = gui.getEraseGui().getType();
			boolean midi = samplerGui.getTrackDrum() == -1;
			int notea = midi ? swGui.getMidiNote0() : swGui.getDrumNote();
			int noteb = midi ? swGui.getMidiNote1() : -1;
			for (int j = startIndex; j < lastIndex + 1; j++) {
				List<Integer> removalIndices = new ArrayList<Integer>();
				MpcTrack t = seq.getTrack(j);
				for (int k = 0; k < t.getEvents().size(); k++) {
					Event e = t.getEvent(k);
					if (e.getTick() >= swGui.getTime0() && e.getTick() <= swGui.getTime1()) {
						switch (erase) {
						case 0:
							if (e instanceof NoteEvent) {
								int nn = ((NoteEvent) e).getNote();
								if (midi && nn >= notea && nn <= noteb) {
									removalIndices.add(k);
								}
								if (!midi && (notea <= 34 || notea == nn)) {
									removalIndices.add(k);
								}
							} else {
								removalIndices.add(k);
							}
							break;
						case 1:
							String excludeClass = eventClassNames[type];
							if (!e.getClass().getName().equals(excludeClass)) {
								if (e instanceof NoteEvent) {
									int nn = ((NoteEvent) e).getNote();
									if (midi && nn >= notea && nn <= noteb) {
										removalIndices.add(k);
									}
									if (!midi && (notea > 34 && notea != nn)) {
										removalIndices.add(k);
									}
								} else {
									removalIndices.add(k);
								}
							}
							break;
						case 2:
							String includeClass = eventClassNames[type];
							
							if (e.getClass().getName().equals(includeClass)) {
								if (e instanceof NoteEvent) {
									int nn = ((NoteEvent) e).getNote();
									if (midi && nn >= notea && nn <= noteb) {
										removalIndices.add(k);
									}
									if (!midi && (notea <= 34 || notea == nn)) {
										removalIndices.add(k);
									}
								} else {
									removalIndices.add(k);
								}
							}
							break;
						}

					}
				}
				Collections.sort(removalIndices);
				Collections.reverse(removalIndices);
				for (Integer integer : removalIndices) {
					t.getEvents().remove(integer.intValue());
				}
			}

			mainFrame.openScreen("sequencer", "mainpanel");
			break;

		}
	}
}
