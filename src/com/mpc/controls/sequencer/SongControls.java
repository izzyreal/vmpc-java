package com.mpc.controls.sequencer;

import java.math.BigDecimal;

import com.mpc.sequencer.Song;

public class SongControls extends AbstractSequencerControls {

	private int step;
	private Song s;

	protected void init() {
		super.init();
		step = songGui.getOffset() + 1;
		s = sequencer.getSong(songGui.getSelectedSongIndex());
	}

	public void up() {
		init();
		if (param.equals("step1") || param.equals("sequence1") || param.equals("reps1")) {
			if (songGui.getOffset() == -1) return;
			songGui.setOffset(songGui.getOffset() - 1);
			sequencer.setSelectedSequenceIndex(sequencer.getSongSequenceIndex());
			sequencer.setBar(0);
		} else {
			super.up();
		}
	}

	public void openWindow() {
		init();
		if (param.equals("loop")) mainFrame.openScreen("loopsongwindow", "windowpanel");
	}

	public void down() {
		init();
		if (param.equals("step1") || param.equals("sequence1") || param.equals("reps1")) {
			if (step == s.getStepAmount()) return;
			songGui.setOffset(songGui.getOffset() + 1);
			sequencer.setSelectedSequenceIndex(sequencer.getSongSequenceIndex());
			sequencer.setBar(0);
		} else {
			super.down();
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.startsWith("sequence")) {
			if (step > s.getStepAmount() - 1) return;
			int seq = s.getStep(step).getSequence();
			int up = sequencer.getFirstUsedSeqUp(seq + 1);
			System.out.println("seq: " + seq);
			System.out.println("up " + up);
			s.getStep(step).setSequence(notch < 0 ? sequencer.getFirstUsedSeqDown(seq - 1) : up);
			sequencer.setSelectedSequenceIndex(sequencer.getSongSequenceIndex());
			sequencer.setBar(0);

		} else if (param.startsWith("reps")) {
			if (step > s.getStepAmount() - 1) return;
			s.getStep(step).setRepeats(s.getStep(step).getRepeats() + notch);
		} else if (param.equals("song")) {
			songGui.setSelectedSongIndex(songGui.getSelectedSongIndex() + notch);
			songGui.setOffset(-1);
			init();
			if (s.isUsed() && s.getStepAmount() != 0) sequencer.setSelectedSequenceIndex(s.getStep(0).getSequence());
		} else if (param.equals("tempo") && !sequencer.isTempoSourceSequence()) {
			sequencer.setTempo(sequencer.getTempo().add((BigDecimal.valueOf(notch / 10.0))));
		} else if (param.equals("temposource")) {
			sequencer.setTempoSourceSequence(notch > 0);
		} else if (param.equals("loop")) {
			songGui.setLoop(notch > 0);
		}
	}

	public void function(int i) {
		init();
		switch (i) {
		case 4:
			// if (songGui.getOffset() == -1) return;
			s.deleteStep(step);
			break;
		case 5:
			// if (songGui.getOffset() == -1) return;
			s.insertStep(step, s.new Step());
			if (!s.isUsed()) s.setUsed(true);
			break;
		}

	}
}