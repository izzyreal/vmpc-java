package com.mpc.sequencer;

import java.util.Observable;

public class TimeSignature extends Observable {

	// private int denominatorLength;
	// private double numeratorLength;

	private double numerator;
	private int denominator;

	public TimeSignature() {
		// denominatorLength = AllFile.sequence.getDenominatorLength();
		// numeratorLength = AllFile.sequence.getNumeratorLength();
		// numerator = numeratorLength / denominatorLength;
		// denominator = 32 / (denominatorLength/12);
		numerator = 4;
		denominator = 4;
	}

	public void setNumerator(int i) {
		numerator = i;
		setChanged();
		notifyObservers("timesignature");
		setChanged();
		notifyObservers("beat");
	}

	public int getNumerator() {
		return (int) numerator;
	}

	public void setDenominator(int i) {
		denominator = i;
		setChanged();
		notifyObservers("timesignature");
		setChanged();
		notifyObservers("beat");
	}

	public int getDenominator() {
		return denominator;
	}

	public void increase() {
		switch (getDenominator()) {

		case 4: 

		case 8:

		case 16:
			if (getNumerator() != 16) {
				setNumerator(getNumerator() + 1);
				break;
			} else {
				setNumerator(1);
				setDenominator(getDenominator() * 2);
				break;
			}

		case 32:
			if (getNumerator() != 32) {
				setNumerator(getNumerator() + 1);
				break;
			}
		}
	}

	public void decrease() {
		switch (getDenominator()) {

		case 4:
			if (getNumerator() != 1) {
				setNumerator(getNumerator() - 1);
			}
			break;

		case 8:

		case 16:
	
		case 32:
			if (getNumerator() == 1) {
				setNumerator(16);
				setDenominator(getDenominator() / 2);
				break;
			} else {
				setNumerator(getNumerator() - 1);
				break;
			}
		}
	}
}