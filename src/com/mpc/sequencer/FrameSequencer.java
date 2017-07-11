package com.mpc.sequencer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mpc.gui.Bootstrap;

import uk.org.toot.audio.server.AudioClient;

public class FrameSequencer implements AudioClient {

	private FrameClock clock;

	private boolean running = false;
	private long previousTick = -1;
	Sequencer sequencer;
	private NoteRepeatProcessor nrp;

	private double targetTick;
	private long prevTSegmentTicks;

	private boolean metronome;

	private double bufferStartTick;

	private HashSet<Long> ticks;

	private MpcSequence seq;

	public void start(Sequencer sequencer) {
		metronome = false;
		ticks = new HashSet<Long>();
		previousTick = -1;
		this.sequencer = sequencer;
		clock = new FrameClock(sequencer.getTempo());
		clock.previousTempoTick = sequencer.getPlayStartTick();
		nrp = new NoteRepeatProcessor(this);
		running = true;
	}

	void move(long movePos, double targetTickCum) {
		sequencer.move(movePos);
		targetTick += targetTickCum;
		ticks.clear();
		ticks.add((long) Math.floor(targetTick));
		nrp.process(ticks);
		clock.endTSegment(sequencer.getTempo(), prevTSegmentTicks, (long) Math.floor(targetTick));
		previousTick = (long) Math.floor(targetTick);
	}

	void checkTempo(double tick) {
		if (sequencer.getTempo().doubleValue() != clock.frameTempo.doubleValue()) {
			clock.endTSegment(sequencer.getTempo(), prevTSegmentTicks, (long) Math.floor(tick));
		}
	}

	@Override
	public void work(int nFrames) {
		
		if (running) {

			bufferStartTick = clock.getTargetTick(0);
			targetTick = clock.getTargetTick(nFrames);
			prevTSegmentTicks = (long) (Math.floor(targetTick)) - clock.previousTempoTick;

			if ((long) Math.floor(targetTick) == previousTick) {
				checkTempo(bufferStartTick);
				clock.clock(nFrames);
				return;
			}

			if (!metronome) {

				ticks.clear();

				for (int l = 0; l < nFrames; l += 1) {
					ticks.add(new Long((long) Math.floor(clock.getTargetTick(l))));
				}

				nrp.process(ticks);

				checkTempo(bufferStartTick);

				seq = sequencer.getCurrentlyPlayingSequence();

				if (sequencer.isCountingIn()) {
					long start = seq.getBarLengths()[seq.getFirstLoopBar()];
					Long l = (long) Math.floor(targetTick);
					if (l.longValue() >= start - 1) {
						sequencer.playToTick(start - 1);
						move(seq.isLoopEnabled() ? seq.getLoopStart() : 0,
								seq.isLoopEnabled() ? seq.getLoopStart() - start : -start);
						sequencer.setCountingIn(false);
					} else {
						sequencer.playToTick(l);
						previousTick = (long) Math.floor(targetTick);
						clock.clock(nFrames);
						return;
					}
				}

				previousTick = (long) Math.floor(targetTick);

				if (targetTick >= seq.getLastTick() - 1 && !sequencer.isSongModeEnabled()) {

					if (sequencer.getNextSq() != -1) {
						sequencer.playToTick(seq.getLastTick());
						seq = checkNextSq(seq);

						move(0, -seq.getLastTick());

						sequencer.playToTick(previousTick);
						clock.clock(nFrames);
						return;
					}
				}

				if (sequencer.isSongModeEnabled()) {

					if (targetTick >= seq.getLastTick() - 1) {

						Sequencer.repeats++;
						Song song = sequencer.getSong(Bootstrap.getGui().getSongGui().getSelectedSongIndex());
						int step = Bootstrap.getGui().getSongGui().getOffset() + 1;

						if (step == song.getStepAmount() - 1 && Sequencer.repeats == song.getStep(step).getRepeats()) {
							if (!Bootstrap.getGui().getSongGui().isLoopEnabled()) {
								sequencer.playToTick(seq.getLastTick() - 1);
								Sequencer.endOfSong = true;
								sequencer.stop();
								sequencer.move(seq.getLastTick());
								return;
							} else {
								sequencer.playToTick(seq.getLastTick() - 1);
								Bootstrap.getGui().getSongGui().setOffset(-1);
								move(0, -seq.getLastTick());
							}
						} else {
							sequencer.playToTick(seq.getLastTick() - 1);
							if (Sequencer.repeats == song.getStep(step).getRepeats()) {
								Sequencer.repeats = 0;
								Bootstrap.getGui().getSongGui()
										.setOffset(Bootstrap.getGui().getSongGui().getOffset() + 1);
							}
							move(0, -seq.getLastTick());
						}
					}

				} else if (seq.isLoopEnabled() && targetTick >= seq.getLoopEnd() - 1) {
					sequencer.playToTick(seq.getLoopEnd() - 1);
					move(seq.getLoopStart(), seq.getLoopStart() - seq.getLoopEnd());

					if (sequencer.isRecording()) {
						sequencer.switchRecordToOverDub();
					}

				} else if (targetTick >= seq.getLastTick() - 1) {

					sequencer.playToTick(seq.getLastTick() - 1);
					sequencer.stop();
					sequencer.move(seq.getLastTick());
					return;
				}
			}

			sequencer.playToTick((long) Math.floor(targetTick));
			clock.clock(nFrames);

		}

	}

	@Override
	public void setEnabled(boolean enabled) {

	}

	public int getEventFrameOffset(long tick) {
		return tick == -1 ? 0 : clock.getEventFrameOffset(tick);
	}

	private class FrameClock {

		private long frameCounter;
		private BigDecimal frameTempo;

		private List<Double> tempoAreasFrames;

		private long previousTempoTick = 0;

		private double accum;

		private int offset;

		private FrameClock(BigDecimal startTempo) {
			frameCounter = 0l;
			frameTempo = startTempo;
			tempoAreasFrames = new ArrayList<Double>();
		}

		public double getTargetTick(int nFrames) {
			return SeqUtil.framesToTicks(frameCounter + nFrames - getTempoAreaStartFrame(), frameTempo)
					+ previousTempoTick;
		}

		private double getTempoAreaStartFrame() {
			accum = 0;
			for (Double d : tempoAreasFrames)
				accum += d.doubleValue();
			return accum;
		}

		protected void clock(int nFrames) {
			frameCounter += nFrames;
		}

		protected void endTSegment(BigDecimal newTempo, long previousSegmentLengthTicks, long currentTick) {
			tempoAreasFrames.add(SeqUtil.ticksToFrames(previousSegmentLengthTicks, frameTempo));
			this.previousTempoTick = currentTick;
			frameTempo = newTempo;
			sequencer.notify("tempo");
		}

		int getEventFrameOffset(long tick) {
			offset = (int) (((long) Math
					.floor(SeqUtil.ticksToFrames(tick - previousTempoTick, frameTempo) + getTempoAreaStartFrame()))
					- frameCounter);
			return offset;
		}

		double getTickPosition() {
			return getTargetTick(0) >= 0 ? getTargetTick(0) : 0;
		}
	}

	public void stop() {
		if (!running) return;
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public long getTickPosition() {
		if (sequencer.isCountingIn()) return 0;
		return (long) (Math.floor(clock.getTickPosition()));
	}

	private MpcSequence checkNextSq(MpcSequence s) {
		if (sequencer.nextsq != -1) {
			sequencer.currentlyPlayingSequenceIndex = sequencer.nextsq;
			sequencer.activeSequenceIndex = sequencer.nextsq;
			sequencer.nextsq = -1;
			s = sequencer.getCurrentlyPlayingSequence();
			sequencer.notify("nextsqoff");
			sequencer.notify("seqnumbername");
		}
		return s;
	}

	public void startMetronome(Sequencer sequencer) {
		this.sequencer = sequencer;
		metronome = true;
		previousTick = -1;
		this.sequencer = sequencer;
		clock = new FrameClock(sequencer.getTempo());
		clock.previousTempoTick = 0;
		running = true;
	}
}