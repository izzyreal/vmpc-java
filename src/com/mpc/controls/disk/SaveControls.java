package com.mpc.controls.disk;

import com.mpc.Util;
import com.mpc.sequencer.MpcSequence;

public class SaveControls extends AbstractDiskControls {

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			mainFrame.openScreen("load", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("format", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("setup", "mainpanel");
			break;
		case 5:
			if (disk == null) return;
			switch (diskGui.getType()) {
			case 0:
				nameGui.setName("ALL_SEQ_SONG1");
				mainFrame.openScreen("saveallfile", "windowpanel");
				break;
			case 1:
				MpcSequence seq = sequencer.getActiveSequence();
				if (!seq.isUsed()) return;
				nameGui.setName(sequencer.getActiveSequence().getName());
				mainFrame.openScreen("saveasequence", "windowpanel");
				break;
			case 2:
				nameGui.setName("ALL_PGMS");
				mainFrame.openScreen("saveapsfile", "windowpanel");
				break;
			case 3:
				nameGui.setName(Util.getFileName(program.getName()));
				mainFrame.openScreen("saveaprogram", "windowpanel");
				break;
			case 4:
				nameGui.setName(sampler.getSoundName(soundGui.getSoundIndex()));
				mainFrame.openScreen("saveasound", "windowpanel");
				break;
			}
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("type")) diskGui.setType(diskGui.getType() + notch);

		if (param.equals("file")) {
			switch (diskGui.getType()) {
			case 0:
			case 2:
				return;
			case 1:
				sequencer.setSelectedSequenceIndex(sequencer.getActiveSequenceIndex() + notch);
				break;
			case 3:
				int nr = ((com.mpc.sequencer.MpcTrack) sequencer.getActiveSequence().getMpcTracks()
						.get(sequencer.getActiveTrackIndex())).getBusNumber();
				sampler.setDrumBusProgramNumber(nr, sampler.getDrumBusProgramNumber(nr) + notch);
				break;
			case 4:
				gui.getSoundGui().setSoundIndex(gui.getSoundGui().getSoundIndex() + notch);
				break;
			case 5:
				break;

			}

		}
	}
}
