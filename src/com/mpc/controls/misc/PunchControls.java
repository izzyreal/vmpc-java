package com.mpc.controls.misc;

public class PunchControls extends AbstractMiscControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("autopunch")) punchGui.setAutoPunch(punchGui.getAutoPunch() + notch);
		checkAllTimes(notch);
	}

	public void function(int i) {
		init();
		switch (i) {
		case 1:
			openMain("trans");
			gui.getTransGui().setBar0(0);
			gui.getTransGui().setBar1(sequencer.getActiveSequence().getLastBar());
			break;
		case 2:
			openMain("2ndseq");
			break;
			
		case 5:
			break;
		}
	}

}
