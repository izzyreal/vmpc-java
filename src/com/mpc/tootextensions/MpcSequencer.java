
package com.mpc.tootextensions;

import java.util.Collections;
import java.util.List;

public abstract class MpcSequencer extends Source {

	protected boolean metronomeOnly = false;
	protected TrackContainer metronomeSeq = null;

	/**
	 * @return the List of Sequences
	 */
	public abstract List<TrackContainer> getSequences();

	public int activeSequenceIndex = 0;

	public int getActiveSequenceIndex() {
		return activeSequenceIndex;
	}

	public int currentlyPlayingSequenceIndex = 0;

	public boolean songMode = false;
	private TrackContainer tc;

	/**
	 * Should only be called by the Sequencer. Play events as they become due.
	 * 
	 * @param targetTick
	 *            the tick to play until.
	 */
	@Override
	public void playToTick(long targetTick) {
		// System.out.println("songmode " + songMode);
		int seqIndex = songMode ? getSongSequenceIndex() : currentlyPlayingSequenceIndex;
		// System.out.println("seq index: " + seqIndex);
		tc = metronomeOnly ? metronomeSeq : getSequences().get(seqIndex);
		for (com.mpc.tootextensions.Track trk : tc.getTracks()) {
			while (trk.getNextTick() <= targetTick) {
				trk.playNext();
			}
		}
	}

	/**
	 * Should only be called by the Sequencer. Turn off track outputs on a stop
	 * condition
	 */
	@Override
	protected void stopped() {
		int seqIndex = songMode ? getSongSequenceIndex() : currentlyPlayingSequenceIndex;
		for (com.mpc.tootextensions.Track trk : getSequences().get(seqIndex).getTracks()) {
			trk.off(true);
		}
	}

	@Override
	List<com.mpc.tootextensions.Track> getTracks() {
		return Collections.EMPTY_LIST;
	}

	public abstract int getSongSequenceIndex();

	// public abstract long getPlayStartTick();

	public abstract void move(long l);
}