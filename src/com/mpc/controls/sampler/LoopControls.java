package com.mpc.controls.sampler;

import com.mpc.gui.components.MpcTextField;

public class LoopControls extends AbstractSamplerControls {

	protected void init() {
		super.init();
		typableParams = new String[]{ "to", "endlengthvalue" };		
	}
	
	public void openWindow() {
		init();
		if (param.equals("snd")) {
			soundGui.setSoundIndex(soundGui.getSoundIndex());
			soundGui.setPreviousScreenName("loop");
			mainFrame.openScreen("sound", "windowpanel");
		}

		if (param.equals("to")) mainFrame.openScreen("looptofine", "windowpanel");
		if (param.equals("endlengthvalue")) mainFrame.openScreen("loopendfine", "windowpanel");
	}

	public void function(int f) {
		init();
		switch (f) {

		case 0:
			mainFrame.openScreen("trim", "mainpanel");
			break;
		case 1:
			sampler.sort();
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
			gui.getEditSoundGui().setPreviousScreenName("loop");
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
		int notch_inc = getNotch(increment);
		int soundInc = getSoundIncrement(notch_inc);

		final int oldLoopLength = sound.getEnd() - sound.getLoopTo();
		final boolean loopFix = zoomGui.isLoopLngthFix();
		MpcTextField mtf = (MpcTextField) mainFrame.lookupTextField(param);

		if (mtf.isSplit())
			soundInc = increment >= 0 ? splitInc[mtf.getActiveSplit() - 1] : -splitInc[mtf.getActiveSplit() - 1];

		if (param.equals("to")) {
			if (loopFix && sound.getLoopTo() + soundInc + oldLoopLength > sound.getLastFrameIndex()) return;
			sound.setLoopTo(sound.getLoopTo() + soundInc);
			if (loopFix) sound.setEnd(sound.getLoopTo() + oldLoopLength);
		}

		if (param.equals("endlengthvalue")) {
			if (loopFix && sound.getEnd() + soundInc - oldLoopLength < 0) return;
			sound.setEnd(sound.getEnd() + soundInc);
			if (loopFix) sound.setLoopTo(sound.getEnd() - oldLoopLength);
		}

		if (param.equals("playx")) soundGui.setPlayX(soundGui.getPlayX() + notch_inc);
		if (param.equals("loop")) sampler.setLoopEnabled(soundGui.getSoundIndex(), notch_inc > 0);
		if (param.equals("endlength")) soundGui.setEndSelected(notch_inc > 0);
		if (param.equals("snd") && increment > 0) sampler.setSoundGuiNextSound();
		if (param.equals("snd") && increment < 0) sampler.setSoundGuiPrevSound();
	}

	@Override
	public void setSlider(int i) {
		init();
		final int oldLength = sound.getEnd() - sound.getLoopTo();
		final boolean lengthFix = zoomGui.isSmplLngthFix();
		int candidatePos = (int) ((i / 124.0) * sound.getLastFrameIndex());
		int maxPos = 0;
		switch (param) {
		case "to":
			maxPos = lengthFix ? sound.getLastFrameIndex() - oldLength : sound.getLastFrameIndex();
			if (candidatePos > maxPos) candidatePos = maxPos;
			sound.setLoopTo(candidatePos);
			if (lengthFix) sound.setEnd(sound.getLoopTo() + oldLength);
			break;
		case "endlengthvalue":
			maxPos = lengthFix ? oldLength : 0;
			if (candidatePos < maxPos) candidatePos = maxPos;
			sound.setEnd(candidatePos);
			if (lengthFix) sound.setLoopTo(sound.getEnd() - oldLength);
			break;
		}
	}

	@Override
	public void left() {
		super.splitLeft();
	}

	@Override
	public void right() {
		super.splitRight();
	}

	public void pressEnter() {
		init();
		if (!isTypable()) return;
		MpcTextField mtf = mainFrame.lookupTextField(param);
		if (!mtf.isTypeModeEnabled()) return;
		int candidate = mtf.enter();
		final int oldLength = sound.getEnd() - sound.getLoopTo();
		final boolean lengthFix = zoomGui.isLoopLngthFix();
		if (candidate != Integer.MAX_VALUE) {
			if (param.equals("to")) {
				if (lengthFix && candidate + oldLength > sound.getLastFrameIndex()) return;
				sound.setLoopTo(candidate);
				if (lengthFix) sound.setEnd(sound.getLoopTo() + oldLength);
			}
			
			if (param.equals("endlengthvalue")) {
				if (lengthFix && candidate - oldLength < 0) return;
				sound.setEnd(candidate);
				if (lengthFix) sound.setLoopTo(sound.getEnd() - oldLength);
			}
		}
	}
}