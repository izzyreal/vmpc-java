package com.mpc.tootextensions;

import static uk.org.toot.audio.mixer.MixerControlsIds.GROUP_STRIP;

import java.util.ArrayList;
import java.util.List;

import uk.org.toot.audio.core.AudioControlsChain;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.mixer.MixerControls;
import uk.org.toot.audio.mixer.MixerControlsFactory;
import uk.org.toot.audio.mixer.MixerControlsIds;

public class MpcMixerControlsFactory extends MixerControlsFactory {

	public static List<AudioControlsChain> createRefChannelStrips(MixerControls mixerControls, int nchannels) {
		List<AudioControlsChain> result = new ArrayList<AudioControlsChain>();
		ChannelFormat mainFormat = mixerControls.getMainBusControls().getChannelFormat();
		for (int i = 0; i < nchannels; i++) {
			result.add(mixerControls.createStripControls(MixerControlsIds.CHANNEL_STRIP, i, String.valueOf(1 + i),
					mainFormat));
		}
		return result;
	}

	public static List<AudioControlsChain> createRefGroupStrips(MixerControls mixerControls, int ngroups) {
		List<AudioControlsChain> result = new ArrayList<AudioControlsChain>();
		ChannelFormat mainFormat = mixerControls.getMainBusControls().getChannelFormat();
		for (int i = 0; i < ngroups; i++) {
			result.add(mixerControls.createStripControls(GROUP_STRIP, i, String.valueOf((char) ('A' + i)), mainFormat));
		}
		return result;
	}
}
