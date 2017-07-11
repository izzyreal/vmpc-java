package com.mpc.controls.sequencer;

import java.io.UnsupportedEncodingException;

import com.mpc.sequencer.MpcTrack;

public class EraseAllOffTracksControls extends AbstractSequencerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 4:
			int trackCounter = 0;
			for (MpcTrack t : mpcSequence.getMpcTracks()) {
				if (!((MpcTrack) t).isOn()) {
					try {
						t = new MpcTrack(gui.getMpc(), trackCounter);
						mpcSequence.setTrack(t, trackCounter);
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
				}
				trackCounter++;
			}
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		}
	}
}
