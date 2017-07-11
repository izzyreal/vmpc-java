package com.mpc.controls.sequencer;

import java.math.BigDecimal;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.components.MpcTextField;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.TimeSignature;

public class SequencerControls extends AbstractSequencerControls {

	protected void init() {
		super.init();
		typableParams = new String[] { "tempo", "now0", "now1", "now2", "velo" };
	}

	public void pressEnter() {
		init();
		if (!isTypable()) return;
		MpcTextField mtf = mainFrame.lookupTextField(param);
		if (!mtf.isTypeModeEnabled()) return;
		int candidate = mtf.enter();
		if (candidate != Integer.MAX_VALUE) {
			if (param.equals("now0")) {
				sequencer.setBar(candidate-1);
				ls.setLastFocus("sequencer_step", "viewmodenumber");
			}
			if (param.equals("now1")) {
				sequencer.setBeat(candidate-1);
				ls.setLastFocus("sequencer_step", "viewmodenumber");
			}

			if (param.equals("now2")) {
				sequencer.setClock(candidate);
				ls.setLastFocus("sequencer_step", "viewmodenumber");
			}

			if (param.equals("tempo")) sequencer.setTempo(BigDecimal.valueOf(candidate / 10.0));

			if (param.equals("velo")) track.setVelocityRatio(candidate);

		}
	}

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			mainFrame.openScreen("sequencer_step", "mainpanel");
			break;
		case 1:
			editSequenceGui.setTime1(mpcSequence.getLastTick());
			mainFrame.openScreen("edit", "mainpanel");
			break;
		case 2:
			track.setOn(!track.isOn());
			break;
		case 3:
			sequencer.setSoloEnabled(!sequencer.isSoloEnabled());
			break;
		case 4:
			sequencer.trackDown();
			break;
		case 5:
			sequencer.trackUp();
			break;
		}

	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("devicenumber")) track.setDeviceNumber(track.getDevice() + notch);
		if (param.equals("tr") && notch > 0) sequencer.trackUp();
		if (param.equals("tr") && notch < 0) sequencer.trackDown();
		if (param.equals("tracktype")) {
			track.setBusNumber(track.getBusNumber() + notch);
			String lastFocus = ls.getLastFocus("sequencer_step");
			if (lastFocus != null && lastFocus.length() == 2) {
				int eventNumber = Integer.parseInt(lastFocus.substring(1, 2));
				if (seGui.getVisibleEvents()[eventNumber] instanceof NoteEvent) {
					if (track.getBusNumber() == 0) {
						if (lastFocus.startsWith("d") || lastFocus.startsWith("e")) {
							ls.setLastFocus("sequencer_step", "a" + eventNumber);
						}
					}
				}
			}

			lastFocus = ls.getLastFocus("edit");
			if (lastFocus != null && lastFocus.startsWith("midinote")) {
				if (track.getBusNumber() != 0) ls.setLastFocus("edit", "drumnote");

			}
			if (lastFocus != null && lastFocus.startsWith("drumnote")) {
				if (track.getBusNumber() == 0) ls.setLastFocus("edit", "midinote0");
			}
		}

		if (param.equals("pgm")) track.setProgramChange(track.getProgramChange() + notch);
		if (param.equals("velo")) track.setVelocityRatio(track.getVelocityRatio() + notch);
		if (param.equals("timing")) sequencer.setTcValue(sequencer.getTcIndex() + notch);
		if (param.equals("sq")) {
			if (sequencer.isPlaying()) {
//				int sq = sequencer.getCurrentlyPlayingSequenceIndex();
//				int nextSq = notch > 0 ? sequencer.getFirstUsedSeqUp(sq) : sequencer.getFirstUsedSeqDown(sq);
				sequencer.setNextSq(sequencer.getCurrentlyPlayingSequenceIndex() + notch);
			} else {
				sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex() + notch);
			}
		}

		if (param.equals("nextsq")) sequencer.setNextSq(sequencer.getNextSq() + notch);
		if (param.equals("bars")) {
			gui.getSequencerWindowGui().setNewBars(sequencer.getActiveSequence().getLastBar());
			mainFrame.openScreen("changebars2", "windowpanel");
		}
		if (param.equals("now0")) {
			sequencer.setBar(sequencer.getCurrentBarNumber() + notch);
			ls.setLastFocus("sequencer_step", "viewmodenumber");
		}
		if (param.equals("now1")) {
			sequencer.setBeat(sequencer.getCurrentBeatNumber() + notch);
			ls.setLastFocus("sequencer_step", "viewmodenumber");
		}

		if (param.equals("now2")) {
			sequencer.setClock(sequencer.getCurrentClockNumber() + notch);
			ls.setLastFocus("sequencer_step", "viewmodenumber");
		}

		if (param.equals("tempo")) {
			sequencer.setTempo(sequencer.getTempo().add((BigDecimal.valueOf(notch / 10.0))));
		}

		if (param.equals("tsig")) {
			TimeSignature ts = new TimeSignature();
			ts.setNumerator(mpcSequence.getTimeSignature().getNumerator());
			ts.setDenominator(mpcSequence.getTimeSignature().getDenominator());
			gui.getSequencerWindowGui().setNewTimeSignature(ts);
			mainFrame.openScreen("changetsig", "windowpanel");

		}

		if (param.equals("temposource")) sequencer.setTempoSourceSequence(notch > 0);
		if (param.equals("count")) sequencer.setCountEnabled(notch > 0);
		if (param.equals("loop")) mpcSequence.setLoopEnabled(notch > 0);
		if (param.equals("recordingmode")) sequencer.setRecordingModeMulti(notch > 0);
		if (param.equals("on")) track.setOn(notch > 0);

	}

	public void openWindow() {
		init();
		if (param.equals("sq")) mainFrame.openScreen("sequence", "windowpanel");
		if (param.startsWith("now")) mainFrame.openScreen("timedisplay", "windowpanel");
		if (param.startsWith("tempo")) mainFrame.openScreen("tempochange", "windowpanel");
		if (param.equals("timing")) {
			gui.getSequencerWindowGui().setNoteValue(sequencer.getTcIndex());
			mainFrame.openScreen("timingcorrect", "windowpanel");
		}

		if (param.equals("tsig")) {
			TimeSignature ts = new TimeSignature();
			ts.setNumerator(mpcSequence.getTimeSignature().getNumerator());
			ts.setDenominator(mpcSequence.getTimeSignature().getDenominator());
			gui.getSequencerWindowGui().setNewTimeSignature(ts);
			mainFrame.openScreen("changetsig", "windowpanel");
		}

		if (param.equals("count")) mainFrame.openScreen("countmetronome", "windowpanel");
		if (param.equals("loop")) mainFrame.openScreen("loopbarswindow", "windowpanel");
		if (param.equals("tr")) mainFrame.openScreen("track", "windowpanel");
		if (param.equals("on")) mainFrame.openScreen("eraseallofftracks", "windowpanel");
		if (param.equals("pgm")) mainFrame.openScreen("transmitprogramchanges", "windowpanel");
		if (param.equals("recordingmode")) mainFrame.openScreen("multirecordingsetup", "windowpanel");
		if (param.equals("tracktype")) mainFrame.openScreen("midiinput", "windowpanel");
		if (param.equals("devicenumber")) mainFrame.openScreen("midioutput", "windowpanel");
		if (param.equals("bars")) mainFrame.openScreen("changebars", "windowpanel");

		if (param.equals("velo")) {
			Bootstrap.getGui().getSequencerWindowGui().setTime0(0);
			Bootstrap.getGui().getSequencerWindowGui().setTime1(mpcSequence.getLastTick());
			mainFrame.openScreen("editvelocity", "windowpanel");
		}

	}

	public void left() {
		init();
		if (sequencer.getNextSq() != -1) return;
		super.left();
	}

	public void right() {
		init();
		if (sequencer.getNextSq() != -1) return;
		super.right();
		if (!mpcSequence.isUsed()) {
			mpcSequence.init(Bootstrap.getUserDefaults().getLastBarIndex());
			sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex());
		}
	}

	public void up() {
		init();
		if (sequencer.getNextSq() != -1) return;
		super.up();
	}

	public void down() {
		init();
		if (sequencer.getNextSq() != -1) return;
		if (!mpcSequence.isUsed()) {
			mpcSequence.init(Bootstrap.getUserDefaults().getLastBarIndex());
			sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex());
		}
		super.down();
	}
}