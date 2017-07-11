package com.mpc.controls.sampler;

import com.mpc.gui.components.MpcTextField;

public class TrimControls extends AbstractSamplerControls {

	protected void init() {
		super.init();
		typableParams = new String[]{ "st", "end" };		
	}

	@Override
	public void openWindow() {
		init();
		if (param.equals("snd")) {
			soundGui.setSoundIndex(soundGui.getSoundIndex());
			soundGui.setPreviousScreenName("trim");
			mainFrame.openScreen("sound", "windowpanel");
		}

		if (param.equals("st")) mainFrame.openScreen("startfine", "windowpanel");
		if (param.equals("end")) mainFrame.openScreen("endfine", "windowpanel");
	}

	@Override
	public void function(int f) {
		init();
		switch (f) {

		case 0:
			sampler.sort();
			break;
		case 1:
			mainFrame.openScreen("loop", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("zone", "mainpanel");
			break;
		case 3:
			mainFrame.openScreen("params", "mainpanel");
			break;
		case 4:
			if (sampler.getSoundCount() == 0) return;
			String newSampleName = sampler.getSoundName(soundGui.getSoundIndex());
			newSampleName = newSampleName.replaceAll("\\s+$", "");
			newSampleName = sampler.addOrIncreaseNumber(newSampleName);
			gui.getEditSoundGui().setNewName(newSampleName);
			gui.getEditSoundGui().setPreviousScreenName("trim");
			mainFrame.openScreen("editsound", "windowpanel");
			break;
		case 5:
			sampler.setPreviewSound(sound);
			int[] zone = { soundGui.getZoneStart(soundGui.getZoneNumber()),
					soundGui.getZoneEnd(soundGui.getZoneNumber()) };
			sampler.playX(soundGui.getPlayX(), zone);
			break;
		}
	}

	@Override
	public void turnWheel(int increment) {
		init();
		if (param == null) return;
		final int oldLength = sound.getEnd() - sound.getStart();
		final boolean lengthFix = zoomGui.isSmplLngthFix();

		int notch = getNotch(increment);
		int soundInc = getSoundIncrement(notch);
		MpcTextField mtf = (MpcTextField) mainFrame.lookupTextField(param);
		if (mtf.isSplit()) {
			soundInc = increment >= 0 ? splitInc[mtf.getActiveSplit() - 1] : -splitInc[mtf.getActiveSplit() - 1];
		}
		if (param.equals("st")) {
			if (lengthFix && sound.getStart() + soundInc + oldLength > sound.getLastFrameIndex()) return;
			sound.setStart(sound.getStart() + soundInc);
			if (lengthFix) sound.setEnd(sound.getStart() + oldLength);
		}

		if (param.equals("end")) {
			if (lengthFix && sound.getEnd() + soundInc - oldLength < 0) return;
			sound.setEnd(sound.getEnd() + soundInc);
			if (lengthFix) sound.setStart(sound.getEnd() - oldLength);
		}

		if (param.equals("view")) soundGui.setView(soundGui.getView() + notch);
		if (param.equals("playx")) soundGui.setPlayX(soundGui.getPlayX() + notch);
		if (param.equals("snd") && notch > 0) sampler.setSoundGuiNextSound();
		if (param.equals("snd") && notch < 0) sampler.setSoundGuiPrevSound();
	}

	@Override
	public void setSlider(int i) {
		init();
		final int oldLength = sound.getEnd() - sound.getStart();
		final boolean lengthFix = zoomGui.isSmplLngthFix();
		int candidatePos = (int) ((i / 124.0) * sound.getLastFrameIndex());
		int maxPos = 0;
		switch (param) {
		case "st":
			maxPos = lengthFix ? sound.getLastFrameIndex() - oldLength : sound.getLastFrameIndex();
			if (candidatePos > maxPos) candidatePos = maxPos;
			sound.setStart(candidatePos);
			if (lengthFix) sound.setEnd(sound.getStart() + oldLength);
			break;
		case "end":
			maxPos = lengthFix ? oldLength : 0;
			if (candidatePos < maxPos) candidatePos = maxPos;
			sound.setEnd(candidatePos);
			if (lengthFix) sound.setStart(sound.getEnd() - oldLength);
			break;
		}
	}

	public void left() {
		super.splitLeft();
	}

	public void right() {
		super.splitRight();
	}

	public void pressEnter() {
		init();
		if (!isTypable()) return;
		MpcTextField mtf = mainFrame.lookupTextField(param);
		if (!mtf.isTypeModeEnabled()) return;
		int candidate = mtf.enter();
		final int oldLength = sound.getEnd() - sound.getStart();
		final boolean lengthFix = zoomGui.isSmplLngthFix();
		if (candidate != Integer.MAX_VALUE) {
			if (param.equals("st")) {
				if (lengthFix && candidate + oldLength > sound.getLastFrameIndex()) return;
				sound.setStart(candidate);
				if (lengthFix) sound.setEnd(sound.getStart() + oldLength);
			}
			if (param.equals("end")) {
				if (lengthFix && candidate - oldLength < 0) return;
				sound.setEnd(candidate);
				if (lengthFix) sound.setStart(sound.getEnd() - oldLength);
			}

		}
	}
}