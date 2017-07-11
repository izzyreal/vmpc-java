package com.mpc.controls.sequencer;

import java.math.BigDecimal;

import com.mpc.controls.other.AbstractOtherControls;

public class UserDefaultsControls extends AbstractOtherControls {

	public void function(int i) {
		init();
		switch (i) {

		case 0:
			mainFrame.openScreen("edit", "mainpanel");
			break;
		case 1:
			mainFrame.openScreen("barcopy", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("trmove", "mainpanel");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("tempo")) ud.setTempo(ud.getTempo().add((BigDecimal.valueOf(notch / 10.0))));
		if (param.equals("loop")) ud.setLoop(notch > 0);
		if (param.equals("tsig")) ud.getTimeSig().increase();
		if (param.equals("bars")) ud.setLastBar(ud.getLastBarIndex() + notch);
		if (param.equals("pgm")) ud.setPgm(ud.getPgm() + notch);
		if (param.equals("recordingmode")) ud.setRecordingModeMulti(notch > 0);
		if (param.equals("tracktype")) ud.setBus(ud.getBus() + notch);
		if (param.equals("devicenumber")) ud.setDeviceNumber(ud.getDeviceNumber() + notch);
		if (param.equals("velo")) ud.setVelo(ud.getVeloRatio() + notch);

	}
}