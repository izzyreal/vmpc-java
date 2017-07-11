package com.mpc.controls.sampler;

public class SndParamsControls extends AbstractSamplerControls {

	@Override
	public void openWindow() {
		init();
		if (param.equals("snd")) {
			soundGui.setSoundIndex(soundGui.getSoundIndex());
			soundGui.setPreviousScreenName("params");
			mainFrame.openScreen("sound", "windowpanel");
		}
	}

	@Override
	public void function(int f) {
		init();
		switch (f) {
		case 0:
			mainFrame.openScreen("trim", "mainpanel");
			break;
		case 1:
			mainFrame.openScreen("loop", "mainpanel");
			break;
		case 2:
			mainFrame.openScreen("zone", "mainpanel");
			break;
		case 3:
			sampler.sort();
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
		int notch_inc = getNotch(increment);
		
		if (param.equals("playx")) soundGui.setPlayX(soundGui.getPlayX() + notch_inc);
		if (param.equals("snd") && notch_inc > 0) sampler.setSoundGuiNextSound();
		if (param.equals("snd") && notch_inc < 0) sampler.setSoundGuiPrevSound();
		if (param.equals("level")) sound.setLevel(sound.getSndLevel() + notch_inc);
		if (param.equals("tune")) sound.setTune(sound.getTune() + notch_inc);
		if (param.equals("beat")) sound.setNumberOfBeats(sound.getBeatCount() + notch_inc);
	}
}
