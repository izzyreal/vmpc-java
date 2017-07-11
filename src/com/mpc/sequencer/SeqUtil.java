package com.mpc.sequencer;

import java.math.BigDecimal;

import com.mpc.gui.Bootstrap;

public class SeqUtil {

	public static long getTickFromBar(int i, MpcSequence s, long position) {
		if (i < 0) return 0;
		int difference = i - getBarFromTick(s, position);
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position + (difference * denTicks * 4) > s.getLastTick()) {
			position = s.getLastTick();
		} else {
			position = position + (difference * denTicks * 4);
		}
		return position;
	}

	public static long setBeat(int i, MpcSequence s, long position) {
		if (i < 0) i = 0;
		int difference = i - getBeat(s, position);
		int num = s.getTimeSignature().getNumerator();
		if (i >= num) return position;
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position + (difference * denTicks) > s.getLastTick()) {
			position = s.getLastTick();
		} else {
			position = position + (difference * denTicks);
		}
		return position;
	}

	public static long setClockNumber(int i, MpcSequence s, long position) {
		if (i < 0) i = 0;
		int difference = i - getClockNumber(s, position);
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (i > denTicks - 1) return position;
		if (position + difference > s.getLastTick()) {
			position = s.getLastTick();
		} else {
			position = position + difference;
		}
		return position;
	}

	public static int getBarFromTick(MpcSequence s, long position) {
		int num = s.getTimeSignature().getNumerator();
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position == 0) return 0;
		int bar = (int) Math.floor(position / (denTicks * num));
		return bar;
	}

	public static int getBeat(MpcSequence s, long position) {
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position == 0) return 0;
		int beat = (int) Math.floor(position / (denTicks));
		beat = beat % den;
		return beat;
	}

	public static int getClockNumber(MpcSequence s, long position) {
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (position == 0) return 0;
		int clock = (int) (position % (denTicks));
		return clock;
	}

	private static double secondsPerTick(BigDecimal tempo) {
		return 60.0 / tempo.doubleValue() / 96.0;
	}

	private static double ticksPerSecond(BigDecimal tempo) {
		double bps = tempo.doubleValue() / 60.0;
		return bps * 96.0;
	}

	public static double ticksToFrames(double ticks, BigDecimal tempo) {
		return (ticks * secondsPerTick(tempo) * 44100.0);
	}

	static double framesToTicks(double d, BigDecimal tempo) {
		return (d / 44100.0) * (ticksPerSecond(tempo));
	}

	public static double sequenceFrameLength(MpcSequence seq, long firstTick, long lastTick) {
		double result = 0;
		long lastTceTick = firstTick;
		int tceSize = seq.getTempoChangeEvents().size();
		TempoChangeEvent lastTce = null;
		for (int i = 0; i < tceSize - 1; i++) {
			TempoChangeEvent nextTce = seq.getTempoChangeEvents().get(i + 1);
			if (firstTick > nextTce.getTick()) continue;
			if (lastTick < nextTce.getTick()) {
				lastTce = nextTce;
				break;
			}
			TempoChangeEvent tce = seq.getTempoChangeEvents().get(i);
			result += ticksToFrames(nextTce.getTick() - lastTceTick, tce.getTempo());
			lastTceTick = nextTce.getTick();
		}
		if (lastTce == null) lastTce = seq.getTempoChangeEvents().get(0);
		result += ticksToFrames(lastTick - lastTce.getTick(), lastTce.getTempo());
		return (long) Math.ceil(result);
	}

	public static long loopFrameLength(MpcSequence seq) {
		return (long) sequenceFrameLength(seq, seq.getLoopStart(), seq.getLoopEnd());
	}

	public static long songFrameLength(Song song) {
		double result = 0;
		int steps = song.getStepAmount();
		for (int i = 0; i < steps; i++) {
			for (int j = 0; j < song.getStep(i).getRepeats(); j++) {
				MpcSequence seq = Bootstrap.getGui().getMpc().getSequencer().getSequence(song.getStep(i).getSequence());
				result += sequenceFrameLength(seq, 0, seq.getLastTick());
			}
		}
		return (long) result;
	}

}
