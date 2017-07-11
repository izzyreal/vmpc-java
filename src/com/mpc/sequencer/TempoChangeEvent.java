package com.mpc.sequencer;

import java.math.BigDecimal;

public class TempoChangeEvent extends Event {

	private int ratio;

	private int stepNumber;

	private BigDecimal initialTempo;

	private MpcSequence parent;
	
	public TempoChangeEvent(BigDecimal initialTempo, MpcSequence parent) {
		this.initialTempo = initialTempo;
		ratio = 1000;
		this.parent = parent;
	}

//	public TempoChangeEvent() {
//		if (initialTempo == null) return;
//		ratio = 1000;
//	}

	public void plusOneBar(int numerator, int denominator, TempoChangeEvent next) {
		if (stepNumber == 0) return;
		tick = (long) (super.tick + (numerator * (96 * (4.0 / denominator))));
		if (tick > parent.getLastTick()) tick = parent.getLastTick();
		if (next != null) {
			if (tick >= next.getTick()) {
				tick = next.getTick() - 1;
			}
		}
		setChanged();
		notifyObservers("tempochange");
	}

	public void minusOneBar(int numerator, int denominator, TempoChangeEvent previous) {
		if (stepNumber == 0) return;
		tick = (long) (tick - (numerator * (96 * (4.0 / denominator))));
		if (tick < 0) tick = 0;
		if (previous != null) {
			if (tick <= previous.getTick()) {
				tick = previous.getTick() + 1;
			}
		}
		setChanged();
		notifyObservers("tempochange");
	}

	public void plusOneBeat(int denominator, TempoChangeEvent next) {
		if (stepNumber == 0) return;
		tick = (long) (tick + 96 * (4.0 / denominator));
		if (tick > parent.getLastTick()) tick = parent.getLastTick();
		if (next != null) {
			if (tick >= next.getTick()) {
				tick = next.getTick() - 1;
			}
		}
		setChanged();
		notifyObservers("tempochange");
	}

	public void minusOneBeat(int denominator, TempoChangeEvent previous) {
		if (stepNumber == 0) return;
		tick = (long) (tick - 96 * (4.0 / denominator));
		if (tick < 0) tick = 0;
		if (previous != null) {
			if (tick <= previous.getTick()) {
				tick = previous.getTick() + 1;
			}
		}
		setChanged();
		notifyObservers("tempochange");
	}

	public void plusOneClock(TempoChangeEvent next) {
		if (stepNumber == 0) return;
		if (next != null) {
			if (tick == next.getTick() - 1) return;
		}
		tick++;
		if (tick > parent.getLastTick()) tick = parent.getLastTick();
		setChanged();
		notifyObservers("tempochange");
	}

	public void minusOneClock(TempoChangeEvent previous) {
		if (stepNumber == 0) return;
		if (previous != null) {
			if (tick == previous.getTick() + 1) return;
		}
		tick--;
		if (tick < 0) tick = 0;
		setChanged();
		notifyObservers("tempochange");
	}

	public void setRatio(int i) {
		if (i < 100 || i > 9998) return;
		ratio = i;
		setChanged();
		notifyObservers("tempochange");
	}

	public int getRatio() {
		return ratio;
	}

	public void setStepNumber(int i) {
		if (i < 0) return;
		stepNumber = i;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public int getBar(int n, int d) {
		int barLength = (int) (96 * (4.0 / d) * n);
		int bar = (int) (tick / barLength);
		return bar;
	}

	public int getBeat(int n, int d) {
		int beatLength = (int) (96 * (4.0 / d));
		int beat = ((int) (tick / beatLength)) % n;
		return beat;
	}

	public int getClock(int n, int d) {
		int beatLength = (int) (96 * (4.0 / d));
		int clock = (int) (tick % beatLength);
		return clock;
	}

	public BigDecimal getTempo() {
//		if (stepNumber == 0) return initialTempo;
		double tempoDouble = initialTempo.doubleValue() * (ratio / 1000.0);
		String str = Double.toString(tempoDouble);
		int length = str.indexOf(".") + 2;
		BigDecimal tempo = new BigDecimal(str.substring(0, length));
		if (tempo.doubleValue() < 30.0) tempo = new BigDecimal("30.0");
		if (tempo.doubleValue() > 300.0) tempo = new BigDecimal("300.0");
		return tempo;
	}

	public BigDecimal getInitialTempo() {
		return initialTempo;
	}

	public void setInitialTempo(BigDecimal bd) {
		if (bd.doubleValue() < 30.0) bd = new BigDecimal("30.0");
		if (bd.doubleValue() > 300.0) bd = new BigDecimal("300.0");
		initialTempo = bd;
		setChanged();
		notifyObservers("initialtempo");
	}
}