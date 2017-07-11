package com.mpc.tootextensions;

import uk.org.toot.synth.SynthChannel;
import uk.org.toot.synth.SynthChannelControls;
import uk.org.toot.synth.spi.TootSynthChannelServiceProvider;

/**
 * This class provides the services of vMPC's toot-compliant synthchannels.
 * 
 * @author iz
 */
public class MpcSoundPlayerChannelsServiceProvider extends TootSynthChannelServiceProvider {

	final static int MPC_SOUND_PLAYER_CHANNEL_ID = 8;
	final static int MPC_BASIC_SOUND_PLAYER_CHANNEL_ID = 9;

	public MpcSoundPlayerChannelsServiceProvider() {

		super("Toot Synth Channels", "0.2");

		String name;
		name = MpcSoundPlayerControls.NAME;
		addControls(MpcSoundPlayerControls.class, MPC_SOUND_PLAYER_CHANNEL_ID, name, "Akai Sample Player", "0.1");
		add(MpcSoundPlayerChannel.class, name, "MpcSoundPlayer", "0.1");

		name = MpcBasicSoundPlayerControls.NAME;
		addControls(MpcBasicSoundPlayerControls.class, MPC_BASIC_SOUND_PLAYER_CHANNEL_ID, name,
				"Akai Basic Sample Player", "0.1");
		add(MpcBasicSoundPlayerChannel.class, name, "MpBasicSoundPlayer", "0.1");

	}

	public SynthChannel createSynthChannel(SynthChannelControls c) {

		if (c instanceof MpcSoundPlayerControls) {
			return new MpcSoundPlayerChannel((MpcSoundPlayerControls) c);
		} else if (c instanceof MpcBasicSoundPlayerControls) {
			return new MpcBasicSoundPlayerChannel((MpcBasicSoundPlayerControls) c);
		}

		return null;
	}
}
