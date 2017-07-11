package com.mpc.tootextensions;

import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.modules.amplifier.AmplifierControls;

public class MpcBasicSoundPlayerControls extends SynthChannelControls

{
	final static int MPC_BASIC_SOUND_PLAYER_CHANNEL_ID = 9;
	public static String NAME = "MpcBasicSoundPlayer";

	public final static int OSC_OFFSET = 0x00;
	public final static int AMP_OFFSET = 0x38;
	public final static int AMPENV_OFFSET = 0x40;

	private AmplifierControls amplifierControls;

	private MpcSoundOscillatorControls msoc;

	private final MpcSampler sampler;
	private final AudioMixer mixer;

	private MpcEnvelopeControls envelopeControl;

	private MpcVoice voice;
	
	public MpcBasicSoundPlayerControls(MpcSampler sampler, AudioMixer mixer, MpcVoice voice) {
		super(MPC_BASIC_SOUND_PLAYER_CHANNEL_ID, NAME);
		this.sampler = sampler;
		this.mixer = mixer;
		this.voice = voice;
		/*
		msoc = new MpcSoundOscillatorControls(0, "Osc",
				OSC_OFFSET);
		amplifierControls = new AmplifierControls(0, "Amplifier", AMP_OFFSET);
		
		ControlRow row1 = new ControlRow();
		row1.add(msoc);
		row1.add(amplifierControls);
		add(row1);

		envelopeControl = new MpcEnvelopeControls(0, "AmpEnv", AMPENV_OFFSET);

		ControlRow row3 = new ControlRow();
		row3.add(envelopeControl);
		add(row3);
		*/

	}
	public MpcSampler getSampler() {
		return sampler;
	}
	public AudioMixer getMixer() {
		return mixer;
	}
	public MpcEnvelopeControls getEnvControl() {
		return envelopeControl;
	}
	public MpcVoice getVoice() {
		return voice;
	}

}