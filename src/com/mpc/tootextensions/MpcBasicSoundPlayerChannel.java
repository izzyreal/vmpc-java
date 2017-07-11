package com.mpc.tootextensions;

import com.mpc.tootextensions.MpcBasicSoundPlayerControls;

import uk.org.toot.audio.core.AudioControlsChain;
import uk.org.toot.audio.fader.FaderControl;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.mixer.AudioMixerStrip;
import uk.org.toot.audio.mixer.MainMixControls;
import uk.org.toot.control.Control;
import uk.org.toot.synth.SynthChannel;

public class MpcBasicSoundPlayerChannel extends SynthChannel {

	private final MpcSampler sampler;

	private final MpcVoice voice;
	private final AudioMixerStrip mixerStrip;
	private final AudioMixer mixer;

	private final FaderControl fader;

	private MpcSoundOscillatorVariables tempVars;

	public MpcBasicSoundPlayerChannel(MpcBasicSoundPlayerControls controls) {
		sampler = controls.getSampler();

		mixer = controls.getMixer();
		mixerStrip = mixer.getStrip("65");

		AudioControlsChain sc = mixer.getMixerControls().getStripControls("65");
		MainMixControls cc = (MainMixControls) sc.getControls().get(4);
		
		System.out.println("\n\nMpcBasicSoundPlayerChannel sc controls:");
		for (Control c : sc.getControls())
			System.out.println(c.getName());
		System.out.println("MpcBasicSoundPlayerChannel cc controls:");
		for (Control c : cc.getControls())
			System.out.println(c.getName());
		
		fader = (FaderControl) cc.getControls().get(3);
		voice = controls.getVoice();
	}

	@Override
	public void setLocation(String location) {
	}

	// implement midichannel

	@Override
	public void noteOn(int soundNumber, final int velocity) {
		mpcNoteOn(soundNumber, velocity, 0);
	}

	public void mpcNoteOn(int soundNumber, final int velocity, int frameOffset) {
		if (velocity == 0) return;
		tempVars = sampler.getClickSound();
		if (soundNumber == -1) tempVars = null;
		if (soundNumber >= 0) tempVars = sampler.getSound(soundNumber);
		if (soundNumber == -3) tempVars = sampler.getPreviewSound();

		if (tempVars == null) return;

		fader.setValue(soundNumber == -2 ? 200 : 100);
		voice.init(-1, velocity, -1, tempVars, null, 0, 64, -1, -1, frameOffset, soundNumber == -2 ? false : true);
	}

	@Override
	public void noteOff(int note) {
	}

	@Override
	public void allNotesOff() {
	}

	@Override
	public void allSoundOff() {
		voice.startDecay();
	}

	public void connectVoice() {
		try {
			mixerStrip.setInputProcess(voice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}