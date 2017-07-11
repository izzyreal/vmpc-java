package com.mpc.controls.misc;

public class TransControls extends AbstractMiscControls {

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			openMain("punch");
			break;
		case 2:
			openMain("2ndseq");
			break;

		case 5:
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("tr")) transGui.setTr(transGui.getTr() + notch);
		if (param.equals("transposeamount")) transGui.setAmount(transGui.getAmount() + notch);
		if (param.equals("bar0")) {
			int candidate = transGui.getBar0() + notch;
			if (candidate < 0 || candidate > sequencer.getActiveSequence().getLastBar()) return;
			transGui.setBar0(transGui.getBar0() + notch);
		}
		if (param.equals("bar1")) {
			int candidate = transGui.getBar1() + notch;
			if (candidate < 0 || candidate > sequencer.getActiveSequence().getLastBar()) return;
			transGui.setBar1(transGui.getBar1() + notch);
		}
	}

}
