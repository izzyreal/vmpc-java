package com.mpc.controls.sampler;

import com.mpc.gui.components.MpcTextField;

public class ZoneControls extends AbstractSamplerControls {

	protected void init() {
		super.init();
		this.typableParams = new String[] { "st", "end" };
	}

	@Override
	public void openWindow() {
		init();
		if (param.equals("snd")) {
			soundGui.setSoundIndex(soundGui.getSoundIndex());
			soundGui.setPreviousScreenName("zone");
			mainFrame.openScreen("sound", "windowpanel");
		}

		if (param.equals("zone")) {
			soundGui.setPreviousNumberOfZones(soundGui.getNumberOfZones());
			mainFrame.openScreen("numberofzones", "windowpanel");
		}

		if (param.equals("st")) mainFrame.openScreen("zonestartfine", "windowpanel");
		if (param.equals("end")) mainFrame.openScreen("zoneendfine", "windowpanel");
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
			sampler.sort();
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
			gui.getEditSoundGui().setPreviousScreenName("zone");
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
		MpcTextField mtf = (MpcTextField) mainFrame.lookupTextField(param);

		if (mtf.isSplit())
			soundInc = increment >= 0 ? splitInc[mtf.getActiveSplit() - 1] : -splitInc[mtf.getActiveSplit() - 1];

		int zone = soundGui.getZoneNumber();

		if (param.equals("st")) soundGui.setZoneStart(zone, soundGui.getZoneStart(zone) + soundInc);
		if (param.equals("end")) soundGui.setZoneEnd(zone, soundGui.getZoneEnd(zone) + soundInc);

		if (param.equals("zone")) soundGui.setZone(soundGui.getZoneNumber() + notch_inc);
		if (param.equals("playx")) soundGui.setPlayX(soundGui.getPlayX() + notch_inc);
		if (param.equals("snd") && increment > 0) sampler.setSoundGuiNextSound();
		if (param.equals("snd") && increment < 0) sampler.setSoundGuiPrevSound();
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
		if (candidate != Integer.MAX_VALUE) {
			int zone = soundGui.getZoneNumber();
			if (param.equals("st")) soundGui.setZoneStart(zone, candidate);
			if (param.equals("end")) soundGui.setZoneEnd(zone, candidate);
		}
	}
}