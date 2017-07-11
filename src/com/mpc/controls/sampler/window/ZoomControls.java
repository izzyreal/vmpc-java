package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;

public class ZoomControls extends AbstractSamplerControls {

	private int sampleLength = 0;

	protected void init() {
		super.init();
		sampleLength = sound.getSampleData().length;
		if (!sound.isMono()) sampleLength /= 2;
	}

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 1:
			zoomGui.setZoomLevel(zoomGui.getZoomLevel() + 1);
			break;
		case 2:
			zoomGui.setZoomLevel(zoomGui.getZoomLevel() - 1);
			break;
		case 4:
			sampler.setPreviewSound(sound);
			int[] zone = { soundGui.getZoneStart(soundGui.getZoneNumber()),
					soundGui.getZoneEnd(soundGui.getZoneNumber()) };
			sampler.playX(soundGui.getPlayX(), zone);
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		int startEndLength = (int) (sound.getEnd() - sound.getStart());
		int loopLength = (int) (sound.getEnd() - sound.getLoopTo());

		if (param.equals("looplngth")) zoomGui.setLoopLngthFix(notch > 0);

		if (param.equals("lngth")) {
			if (sound.getEnd() + notch > sampleLength) return;
			sound.setEnd(sound.getEnd() + notch);
		}
		if (param.equals("to")) {
			if (!zoomGui.isLoopLngthFix() && sound.getEnd() - (sound.getLoopTo() + notch) < 0) return;
			int highestLoopTo = sampleLength - 1;
			if (zoomGui.isLoopLngthFix()) {
				highestLoopTo -= loopLength;
				if (sound.getLoopTo() + notch > highestLoopTo) return;
			}
			sound.setLoopTo(sound.getLoopTo() + notch);
			if (zoomGui.isLoopLngthFix()) sound.setEnd(sound.getLoopTo() + loopLength);
		}

		if (param.equals("start")) {
			if (!csn.contains("zone")) {
				int highestStartPos = sampleLength - 1;
				int length = sound.getEnd() - sound.getStart();
				if (zoomGui.isSmplLngthFix()) {
					highestStartPos -= startEndLength;
					if (sound.getStart() + notch > highestStartPos) return;
				}
				sound.setStart(sound.getStart() + notch);
				if (zoomGui.isSmplLngthFix()) sound.setEnd(sound.getStart() + length);
			} else {
				soundGui.setZoneStart(soundGui.getZoneNumber(),
						soundGui.getZoneStart(soundGui.getZoneNumber()) + notch);
			}
		}

		if (param.equals("end") && csn.equals("endfine")) {
			sound.setEnd(sound.getEnd() + notch);
			if (zoomGui.isSmplLngthFix()) sound.setStart(sound.getEnd() - startEndLength);
		}

		if (param.equals("end") && csn.equals("loopendfine")) {
			sound.setEnd(sound.getEnd() + notch);
			if (zoomGui.isLoopLngthFix()) sound.setLoopTo(sound.getEnd() - loopLength);
		}

		if (param.equals("end") && csn.equals("zoneendfine"))
			soundGui.setZoneEnd(soundGui.getZoneNumber(), soundGui.getZoneEnd(soundGui.getZoneNumber()) + notch);
		if (param.equals("smpllngth")) zoomGui.setSmplLngthFix(notch > 0);
		if (param.equals("playx")) soundGui.setPlayX(soundGui.getPlayX() + 1);

	}
}