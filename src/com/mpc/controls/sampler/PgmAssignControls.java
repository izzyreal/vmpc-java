package com.mpc.controls.sampler;

import com.mpc.Mpc;
import com.mpc.sampler.Sampler;

public class PgmAssignControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			mainFrame.openScreen("selectdrum", "mainpanel");
			break;
		case 1:
			mainFrame.openScreen("programparams", "mainpanel");
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

		if (param.equals("padassign")) samplerGui.setPadAssignMaster(i > 0);
		if (param.equals("pgm")) mpcSoundPlayerChannel.setProgram(mpcSoundPlayerChannel.getProgram() + notch);

		if (param.equals("pad")) {
			int candidate = samplerGui.getPad() + notch;
			if (candidate < 0 || candidate > 63) return;
			int nextNN = program.getPad(candidate).getNote();
			samplerGui.setPadAndNote(candidate, nextNN);
		}

		if (param.equals("padnote")) {
			lastPad.setNote(lastPad.getNote() + notch);
			int candidate = lastPad.getNote();
			if (candidate > 34) samplerGui.setPadAndNote(samplerGui.getPad(), candidate);
		}

		if (param.equals("note")) {
			int candidate = samplerGui.getNote() + notch;
			if (candidate > 34) samplerGui.setPadAndNote(samplerGui.getPad(), candidate);
		}

		if (param.equals("snd")) {
			lastNp.setSoundNumber(sampler.getNextSoundIndex(lastNp.getSndNumber(), notch > 0));
			if (sampler.getSound(lastNp.getSndNumber()) != null) {
				if (sampler.getSound(lastNp.getSndNumber()).isMono()) lastPad.getMixerChannel().setStereo(false);
				if (sampler.getSound(lastNp.getSndNumber()).isLoopEnabled()) lastNp.setVoiceOverlap(2);
			}
		}
		
		if (param.equals("mode")) lastNp.setSoundGenMode(lastNp.getSoundGenerationMode() + notch);

		if (param.equals("velocityrangelower")) lastNp.setVeloRangeLower(lastNp.getVelocityRangeLower() + notch);

		if (param.equals("velocityrangeupper")) lastNp.setVeloRangeUpper(lastNp.getVelocityRangeUpper() + notch);

		if (param.equals("optionalnotenumbera")) lastNp.setOptNoteA(lastNp.getOptionalNoteA() + notch);

		if (param.equals("optionalnotenumberb")) lastNp.setOptionalNoteB(lastNp.getOptionalNoteB() + notch);
	}

	public void openWindow() {
		init();
		if (param.equals("pgm")) {
			samplerGui.setPrevScreenName("programassign");
			mainFrame.openScreen("program", "windowpanel");
		}

		if (param.equals("pad") || param.equals("padnote")) mainFrame.openScreen("assignmentview", "windowpanel");
		if (param.equals("padassign")) mainFrame.openScreen("initpadassign", "windowpanel");
		if (param.equals("note")) {
			int pn = mpcSoundPlayerChannel.getProgram();
			int nn = samplerGui.getNote();
			gui.getSamplerWindowGui().setProg0(pn);
			gui.getSamplerWindowGui().setNote0(nn);
			gui.getSamplerWindowGui().setProg1(pn);
			gui.getSamplerWindowGui().setNote1(nn);
			mainFrame.openScreen("copynoteparameters", "windowpanel");
		}

		if (param.equals("snd")) {
			int sn = Sampler.getLastNp(program).getSndNumber();
			if (sn != -1) {
				gui.getSoundGui().setSoundIndex(sn);
				gui.getSoundGui().setPreviousScreenName("programassign");
				mainFrame.openScreen("sound", "windowpanel");
			}

		}
	}
}
