package com.mpc.controls.sequencer.window;

import java.math.BigDecimal;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.TempoChangeEvent;

public class TempoChangeControls extends AbstractSequencerControls {
	private TempoChangeEvent previous;
	private TempoChangeEvent current;
	private TempoChangeEvent next;
	private int yPos;

	public void left() {
		init();
		if (param.length() == 2) {
			if (param.startsWith("a")) {
				mainFrame.lookupTextField("tempochange").grabFocus();
				return;
			}
		}
		super.left();
	}

	public void right() {
		init();
		if (param.length() == 2) {
			if (param.startsWith("f")) {
				mainFrame.lookupTextField("initialtempo").grabFocus();
				return;
			}
		}
		super.right();
	}

	public void function(int j) {
		super.function(j);
		int yPos = -1;
		if (param.length() == 2) yPos = Integer.parseInt(param.substring(1, 2));

		switch (j) {
		case 1:
			if (yPos + swGui.getTempoChangeOffset() >= mpcSequence.getTempoChangeEvents().size()) return;
			if (mpcSequence.getTempoChangeEvents().get(swGui.getTempoChangeOffset() + yPos).getStepNumber() == 0)
				return;
			mpcSequence.getTempoChangeEvents().remove(swGui.getTempoChangeOffset() + yPos);
			mpcSequence.sortTempoChangeEvents();
			if (swGui.getTempoChangeOffset() + yPos == mpcSequence.getTempoChangeEvents().size())
				swGui.setTempoChangeOffset(swGui.getTempoChangeOffset() - 1);
			mainFrame.openScreen("tempochange", "windowpanel");
			mainFrame.lookupTextField("a" + yPos).grabFocus();
			break;
		case 2:
			int nowDetected = -1;

			for (int i = 0; i < mpcSequence.getTempoChangeEvents().size(); i++) {
				if (mpcSequence.getTempoChangeEvents().get(i).getTick() == sequencer.getTickPosition()) {
					nowDetected = i;
					break;
				}
			}

			if (nowDetected == -1) {
				TempoChangeEvent tce = new TempoChangeEvent(mpcSequence.getTempoChangeEvents().get(0).getInitialTempo(),
						mpcSequence);
				tce.setTick(sequencer.getTickPosition());
				mpcSequence.getTempoChangeEvents().add(tce);
				mpcSequence.sortTempoChangeEvents();
			} else {
				if (nowDetected > swGui.getTempoChangeOffset() + 3 || nowDetected < swGui.getTempoChangeOffset()) {
					swGui.setTempoChangeOffset(nowDetected);
				}
				System.out.println("now detected " + nowDetected);
				System.out.println("tempoChangeOffset " + swGui.getTempoChangeOffset());
				mainFrame.lookupTextField(param.substring(0, 1) + (nowDetected - swGui.getTempoChangeOffset()))
						.grabFocus();

			}
			break;
		case 3:
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		case 4:
			TempoChangeEvent tce = new TempoChangeEvent(mpcSequence.getTempoChangeEvents().get(0).getInitialTempo(),
					mpcSequence);
			if (mpcSequence.getTempoChangeEvents().size() == 1) {
				tce.setTick(mpcSequence.getLastTick());
				tce.setStepNumber(1);
			}

			if (mpcSequence.getTempoChangeEvents().size() > 1) {
				if (param.length() != 2) return;
				if (yPos + swGui.getTempoChangeOffset() == 0) {
					if (current.getTick() == 1) return;
					tce.setTick(next.getTick() - 1);
				}
				if (yPos + swGui.getTempoChangeOffset() > 0) {
					if (current.getTick() - 1 == previous.getTick()) return;
					tce.setTick(current.getTick() - 1);
				}

			}
			mpcSequence.getTempoChangeEvents().add(tce);
			mpcSequence.sortTempoChangeEvents();
			mainFrame.openScreen("tempochange", "windowpanel");
			break;
		}
	}

	public void init() {
		super.init();
		yPos = -1;
		if (param.length() == 2) yPos = Integer.parseInt(param.substring(1, 2));
		previous = null;
		current = null;
		next = null;

		if (param.length() == 2) {
			int nextPosition = yPos + swGui.getTempoChangeOffset() + 1;
			if (mpcSequence.getTempoChangeEvents().size() > nextPosition) {
				next = mpcSequence.getTempoChangeEvents().get(nextPosition);
			}
			int currentPosition = yPos + swGui.getTempoChangeOffset();

			if (currentPosition > mpcSequence.getTempoChangeEvents().size() - 1) return; // //!!!!

			current = mpcSequence.getTempoChangeEvents().get(currentPosition);
			int previousPosition = yPos + swGui.getTempoChangeOffset() - 1;
			if (previousPosition >= 0) {
				previous = mpcSequence.getTempoChangeEvents().get(previousPosition);
			}
		}
	}

	public void turnWheel(int j) {
		init();
		int notch = getNotch(j);
		if (param.equals("tempochange")) mpcSequence.setTempoChangeOn(notch > 0);

		if (param.equals("initialtempo")) {
			TempoChangeEvent tce = mpcSequence.getTempoChangeEvents().get(0);
			if (notch > 0) tce.setInitialTempo(tce.getInitialTempo().add((BigDecimal.valueOf(notch / 10.0))));
			if (notch < 0)
				tce.setInitialTempo(tce.getInitialTempo().subtract((BigDecimal.valueOf(Math.abs(notch) / 10.0))));
		}

		for (int i = 0; i < 3; i++) {

			if (param.equals("b" + i)) {
				if (notch > 0) {
					swGui.getVisibleTempoChanges()[i].plusOneBar(mpcSequence.getTimeSignature().getNumerator(),
							mpcSequence.getTimeSignature().getDenominator(), next);
				} else {
					swGui.getVisibleTempoChanges()[i].minusOneBar(mpcSequence.getTimeSignature().getNumerator(),
							mpcSequence.getTimeSignature().getDenominator(), previous);
				}
			}
			if (param.equals("c" + i)) {
				if (notch > 0) {
					swGui.getVisibleTempoChanges()[i].plusOneBeat(mpcSequence.getTimeSignature().getDenominator(),
							next);
				} else {
					swGui.getVisibleTempoChanges()[i].minusOneBeat(mpcSequence.getTimeSignature().getDenominator(),
							previous);
				}
			}
			if (param.equals("d" + i)) {
				if (notch > 0) {
					swGui.getVisibleTempoChanges()[i].plusOneClock(next);
				} else {
					swGui.getVisibleTempoChanges()[i].minusOneClock(previous);
				}
			}
			if (param.equals("e" + i)) {
				swGui.getVisibleTempoChanges()[i].setRatio(swGui.getVisibleTempoChanges()[i].getRatio() + notch);
			}
		}

	}

	public void down() {
		init();
		if (param.equals("tempochange")) mainFrame.lookupTextField("e0").grabFocus();
		if (param.equals("initialtempo")) mainFrame.lookupTextField("f0").grabFocus();

		if (param.length() == 2) {
			if ((yPos == 1 && swGui.getVisibleTempoChanges()[1] == null)
					|| (yPos == 2 && swGui.getVisibleTempoChanges()[2] == null)) {
				return;
			}

			if (yPos == 1 && swGui.getVisibleTempoChanges()[2] == null) {
				mainFrame.lookupTextField("a2").grabFocus();
				return;
			}

			if (yPos == 2) {
				if (swGui.getTempoChangeOffset() + yPos + 1 == mpcSequence.getTempoChangeEvents().size()
						&& !param.startsWith("a")) {
					mainFrame.lookupTextField("a2").grabFocus();
				}
				swGui.setTempoChangeOffset(swGui.getTempoChangeOffset() + 1);
				return;
			}
			mainFrame.lookupTextField(param.substring(0, 1) + (yPos + 1)).grabFocus();
		}
	}

	public void up() {
		init();
		if (param.length() == 2) {
			if (yPos == 0) {
				if (swGui.getTempoChangeOffset() == 0) {
					if (param.equals("e0")) {
						mainFrame.lookupTextField("tempochange").grabFocus();
					}

					if (param.equals("f0")) {
						mainFrame.lookupTextField("initialtempo").grabFocus();
					}
					return;
				}
				swGui.setTempoChangeOffset(swGui.getTempoChangeOffset() - 1);
				return;
			}
			mainFrame.lookupTextField(param.substring(0, 1) + (yPos - 1)).grabFocus();
			return;
		}
	}

}
