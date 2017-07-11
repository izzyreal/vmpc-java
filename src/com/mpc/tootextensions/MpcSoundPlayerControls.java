package com.mpc.tootextensions;

import java.util.concurrent.ConcurrentLinkedQueue;

import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.server.AudioServer;
import uk.org.toot.synth.SynthChannelControls;

public class MpcSoundPlayerControls extends SynthChannelControls

{
	final static int MPC_SOUND_PLAYER_CHANNEL_ID = 8;

	public static String NAME = "MpcSoundPlayer";

	private final MpcSampler sampler;
	private final AudioMixer mixer;
	private final int drumNumber;

	private ConcurrentLinkedQueue<MpcVoice> voices;

	private AudioServer server;
	
	public MpcSoundPlayerControls(MpcSampler sampler, int drumNumber, AudioMixer mixer, ConcurrentLinkedQueue voices, AudioServer server) {
		super(MPC_SOUND_PLAYER_CHANNEL_ID, NAME);
		this.sampler = sampler;
		this.drumNumber = drumNumber;
		this.mixer = mixer;
		this.voices = voices;
		this.server = server;
	}

	public MpcSampler getSampler() {
		return sampler;
	}

	public int getDrumNumber() {
		return drumNumber;
	}

	public AudioMixer getMixer() {
		return mixer;
	}

	public ConcurrentLinkedQueue<MpcVoice> getVoices() {
		return voices;
	}

	public AudioServer getServer() {
		return server;
	}

}