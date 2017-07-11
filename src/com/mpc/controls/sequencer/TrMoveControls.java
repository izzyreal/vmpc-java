package com.mpc.controls.sequencer;

import com.mpc.sequencer.MpcSequence;

public class TrMoveControls extends AbstractSequencerControls {

	private MpcSequence seq;

	protected void init() {
		super.init();
		seq = sequencer.getSequence(trMoveGui.getSq());
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.startsWith("tr") && notch > 0) trMoveGui.goUp();
		if (param.startsWith("tr") && notch < 0) trMoveGui.goDown();
		if (param.equals("sq")) trMoveGui.setSq(trMoveGui.getSq() + notch);

	}

	public void up() {
		init();
		if (param.startsWith("tr")) trMoveGui.goUp();
	}

	public void down() {
		init();
		if (param.startsWith("tr")) trMoveGui.goDown();
	}

	public void left() {
		init();
		if (gui.getTrMoveGui().isSelected() && param.equals("tr0")) return;
		if (!gui.getTrMoveGui().isSelected() && param.equals("sq")) return;
		super.left();
	}

	public void right() {
		init();
		if (gui.getTrMoveGui().isSelected() && param.equals("tr0")) return;
		if (!gui.getTrMoveGui().isSelected() && param.equals("tr1")) return;
		super.right();
	}

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			gui.getEditSequenceGui().setFromSq(trMoveGui.getSq());
			mainFrame.openScreen("edit", "mainpanel");
			break;
		case 1:
			gui.getBarCopyGui().setFromSq(trMoveGui.getSq());
			mainFrame.openScreen("barcopy", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("user", "mainpanel");
			break;
		case 4:
			if (trMoveGui.isSelected()) trMoveGui.cancel();

			mainFrame.lookupTextField("tr1").grabFocus();
			break;
		case 5:
			if (trMoveGui.isSelected()) {
				trMoveGui.insert(seq);
				mainFrame.lookupTextField("tr1").grabFocus();
			} else {
				trMoveGui.select();
				mainFrame.lookupTextField("tr0").grabFocus();
			}
			break;
		}

	}
}
