package com.mpc.tootextensions;

import static uk.org.toot.control.id.ProviderId.TOOT_PROVIDER_ID;

import uk.org.toot.synth.MidiSynth;
import uk.org.toot.synth.SynthControls;
import uk.org.toot.synth.spi.SynthServiceProvider;

public class MpcMultiSynthServiceProvider extends SynthServiceProvider {
	public MpcMultiSynthServiceProvider() {
		super(TOOT_PROVIDER_ID, "Toot Software", MpcMultiSynthControls.NAME, "0.1");
		String name = MpcMultiSynthControls.NAME;
		addControls(MpcMultiSynthControls.class, MpcMultiSynthControls.ID, name, "", "0.1");
		add(MpcMultiMidiSynth.class, name, "", "0.1");

	}

	public MidiSynth createSynth(SynthControls c) {
		if (c instanceof MpcMultiSynthControls) {
			return new MpcMultiMidiSynth((MpcMultiSynthControls) c);
		}
		return null;
	}

}
