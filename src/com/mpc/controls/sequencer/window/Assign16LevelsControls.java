package com.mpc.controls.sequencer.window;

import com.mpc.controls.sequencer.AbstractSequencerControls;

public class Assign16LevelsControls extends AbstractSequencerControls {

	public void function(int i) {
		super.function(i);
		switch(i) {
		case 4:
			sequencerGui.setSixteenLevelsEnabled(true);
			mainFrame.getLedPanel().setSixteenLevels(true);
			mainFrame.openScreen(ls.getPreviousScreenName(), ls.getPreviousPanel().getName());
			break;
		}
		
	}
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("note")) sequencerGui.setNote(sequencerGui.getNote() + notch);
		if (param.equals("param")) sequencerGui.setParameter(sequencerGui.getParameter() + notch);
		if (param.equals("type")) sequencerGui.setType(sequencerGui.getType() + notch);
		if (param.equals("originalkeypad"))
			sequencerGui.setOriginalKeyPad(sequencerGui.getOriginalKeyPad() + notch);
	}

}
