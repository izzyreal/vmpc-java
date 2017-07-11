// Copyright (C) 2015 Izmael.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package com.mpc.tootextensions;

import uk.org.toot.synth.synths.multi.MultiSynthControls;

public class MpcMultiSynthControls extends MultiSynthControls {
	final static int MPC_MULTI_SYNTH_ID = 5;

	public final static int ID = MPC_MULTI_SYNTH_ID;
	public final static String NAME = "MpcMultiSynth";

	@Override
	public String getName() {
		return NAME;
	}
}