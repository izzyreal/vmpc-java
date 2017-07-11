package com.mpc.controls.sampler;

import com.mpc.Mpc;
import com.mpc.sampler.Sampler;
import com.mpc.sampler.Sound;

public class PgmParamsControls extends AbstractSamplerControls {
	public void function(int i) {
		init();
		switch (i) {
		case 0:
			mainFrame.openScreen("programassign", "mainpanel");
			break;
		case 1:
			mainFrame.openScreen("selectdrum", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("drum", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("purge", "mainpanel");
			break;
		case 4:
			gui.getSamplerWindowGui().setAutoChromAssSnd(Sampler.getLastNp(program).getSndNumber());
			int letterNumber = sampler.getProgramCount() + 21;

			String newName = "NewPgm-" + Mpc.akaiAscii[letterNumber];
			gui.getSamplerWindowGui().setNewName(newName);
			samplerGui.setPrevScreenName(csn);
			mainFrame.openScreen("autochromaticassignment", "windowpanel");
			break;
		case 5:
			// TODO Play sound of currently selected note number (not pad)
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("tune")) lastNp.setTune(lastNp.getTune() + notch);
		if (param.equals("dcymd")) lastNp.setDecayMode(lastNp.getDecayMode() + notch);

		if (param.equals("voiceoverlap")) {
			Sound s = sampler.getSound(lastNp.getSndNumber());
			if (s != null && s.isLoopEnabled()) return;
			lastNp.setVoiceOverlap(lastNp.getVoiceOverlap() + notch);
		}

		if (param.equals("reson")) lastNp.setFilterResonance(lastNp.getFilterResonance() + notch);
		if (param.equals("freq")) lastNp.setFilterFrequency(lastNp.getFilterFrequency() + notch);
		if (param.equals("decay")) lastNp.setDecay(lastNp.getDecay() + notch);
		if (param.equals("attack")) lastNp.setAttack(lastNp.getAttack() + notch);
		if (param.equals("pgm")) mpcSoundPlayerChannel.setProgram(mpcSoundPlayerChannel.getProgram() + notch);
		if (param.equals("note")) {
			int candidate = samplerGui.getNote() + notch;
			if (candidate > 34) samplerGui.setPadAndNote(samplerGui.getPad(), candidate);
		}

	}

	public void openWindow() {
		init();
		switch (param) {

		case "pgm":
			samplerGui.setPrevScreenName(csn);
			mainFrame.openScreen("program", "windowpanel");
			break;

		case "note":

			int pn = mpcSoundPlayerChannel.getProgram();
			int nn = samplerGui.getNote();

			gui.getSamplerWindowGui().setProg0(pn);
			gui.getSamplerWindowGui().setNote0(nn);
			gui.getSamplerWindowGui().setProg1(pn);
			gui.getSamplerWindowGui().setNote1(nn);

			samplerGui.setPrevScreenName(csn);
			mainFrame.openScreen("copynoteparameters", "windowpanel");
			break;

		case "attack":
		case "decay":
		case "dcymd":
			mainFrame.openScreen("velocitymodulation", "windowpanel");
			break;

		case "freq":
		case "reson":
			mainFrame.openScreen("veloenvfilter", "windowpanel");
			break;

		case "tune":
			mainFrame.openScreen("velopitch", "windowpanel");
			break;

		case "voiceoverlap":
			mainFrame.openScreen("muteassign", "windowpanel");
			break;

		}

	}
}
