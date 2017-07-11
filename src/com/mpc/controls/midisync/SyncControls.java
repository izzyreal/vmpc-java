package com.mpc.controls.midisync;

public class SyncControls extends AbstractMidiSyncControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("in")) midiSyncGui.setIn(midiSyncGui.getIn() + notch);
		if (param.equals("out")) midiSyncGui.setOut(midiSyncGui.getOut() + notch);
		if (param.equals("modein")) midiSyncGui.setModeIn(midiSyncGui.getModeIn() + notch);
		if (param.equals("modeout")) midiSyncGui.setModeOut(midiSyncGui.getModeOut() + notch);
		if (param.equals("receivemmc")) midiSyncGui.setReceiveMMCEnabled(notch > 0);
		if (param.equals("sendmmc")) midiSyncGui.setSendMMCEnabled(notch > 0);
	}

	public void function(int i) {
		init();
		
		switch(i) {
		
		case 1:
//			openMain("dump");
			break;
			
		case 2:
//			openMain("midisw");
			break;
		
		case 3:
//			openMain("ports"); // deprecated screen, replaced by "audio" / "midi"
			break;
			
		}
	}
	
}
