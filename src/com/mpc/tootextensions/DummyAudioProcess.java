// Copyright (C) 2007 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org.uk/LICENSE_1_0.txt)

package com.mpc.tootextensions;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.SimpleAudioProcess;

/**
 *  A dummy audio process that does exactly nothing.
 */
public class DummyAudioProcess extends SimpleAudioProcess {
    /**
     * @link aggregation
     * @supplierCardinality 1 
     */
    public int processAudio(AudioBuffer buffer) {
    	return AUDIO_OK;
    }
}
