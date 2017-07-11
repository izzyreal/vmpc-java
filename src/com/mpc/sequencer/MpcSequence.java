package com.mpc.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

import javax.sound.midi.ShortMessage;

import com.mpc.Mpc;
import com.mpc.file.all.AllParser;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.tootextensions.Track;
import com.mpc.tootextensions.TrackContainer;

public class MpcSequence extends Observable implements TrackContainer {

	private SequencerWindowGui swGui;

	private List<MpcTrack> tracks;
	private com.mpc.sequencer.MpcTrack[] metaTracks;

	private List<TempoChangeEvent> tempoChangeEvents = new ArrayList<TempoChangeEvent>();

	@SuppressWarnings("unused")
	private AllParser allFile;

	private String name;
	private String[] deviceNames;
	private boolean loopEnabled;
	private int lastBar;

	private String[] defaultTrackNames;

	private boolean used;

	private long[] barLengths = new long[999];
	private int[] numerators = new int[999];
	private int[] denominators = new int[999];

	private Mpc mpc;

	private boolean tempoChangeOn;

	private long loopStart;
	private long loopEnd;

	private int firstLoopBar;
	private int lastLoopBar;

	private boolean lastLoopBarEnd = true;

	public MpcSequence(Mpc mpc, String[] defaultTrackNames) {
		this.defaultTrackNames = defaultTrackNames;
		tempoChangeOn = true;
		this.mpc = mpc;
		used = false;

		tracks = new ArrayList<MpcTrack>();
		metaTracks = new com.mpc.sequencer.MpcTrack[3];
		loopEnabled = true;
		lastBar = -1;
		try {

			for (int i = 0; i < 64; i++) {
				tracks.add(i, (new com.mpc.sequencer.MpcTrack(mpc, i)));
				((com.mpc.sequencer.MpcTrack) tracks.get(i)).setName(defaultTrackNames[i]);
			}

			metaTracks[0] = new com.mpc.sequencer.MpcTrack(mpc, 64);
			metaTracks[1] = new com.mpc.sequencer.MpcTrack(mpc, 65);
			metaTracks[2] = new com.mpc.sequencer.MpcTrack(mpc, 66);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		metaTracks[0].setName("click");
		metaTracks[1].setName("midiclock");
		metaTracks[2].setName("tempo");

		deviceNames = new String[33];
		for (int i = 0; i < 33; i++)
			deviceNames[i] = Bootstrap.getUserDefaults().getDeviceName(i);

	}

	public void setLoopStart(long l) {
		loopStart = l;
	}

	public long getLoopStart() {
		return loopStart;
	}

	public void setLoopEnd(long l) {
		loopEnd = l;
	}

	public long getLoopEnd() {
		return loopEnd;
	}

	public void setFirstLoopBar(int i) {
		if (i < 0 || i > lastBar) return;
		firstLoopBar = i;
		setChanged();
		notifyObservers("firstloopbar");

		if (i > lastLoopBar) {
			lastLoopBar = i;
			setChanged();
			notifyObservers("lastloopbar");
		}
	}

	public int getFirstLoopBar() {
		return firstLoopBar;
	}

	public void setLastLoopBar(int i) {
		if (i < 0) return;

		if (lastLoopBarEnd) {
			if (i < lastBar) {
				lastLoopBarEnd = false;
				lastLoopBar = lastBar;
				setChanged();
				notifyObservers("lastloopbar");
				return;
			} else {
				return;
			}
		} else {
			if (i > lastBar) {
				lastLoopBarEnd = true;
				setChanged();
				notifyObservers("lastloopbar");
			} else {
				lastLoopBar = i;
				setChanged();
				notifyObservers("lastloopbar");
				if (i < firstLoopBar) {
					firstLoopBar = i;
					setChanged();
					notifyObservers("firstloopbar");
				}
			}
		}

		lastLoopBar = i;
		setChanged();
		notifyObservers("lastloopbar");
	}

	public int getLastLoopBar() {
		if (lastLoopBarEnd) return lastBar;
		return lastLoopBar;
	}

	public void initMetaTracks() {
		createClickTrack();
		createTempoChangeTrack();
		createMidiClockTrack();

		for (int i = 64; i <= 66; i++) {
			if (i > tracks.size() - 1) {
				tracks.add(metaTracks[i - 64]);
			} else {
				tracks.set(i, metaTracks[i - 64]);
			}
		}
	}

	private void createClickTrack() {

		swGui = Bootstrap.getGui().getSequencerWindowGui();
		metaTracks[0].getEvents().clear();

		int bars = getLastBar() + 1;
		int den = 0;
		int denTicks = 0;

		for (int i = 0; i < bars; i++) {
			den = denominators[i];
			denTicks = (int) (96 * (4.0 / den));
			long barStartPos = 0;

			for (int k = 0; k < i; k++)
				barStartPos += barLengths[k];
			switch (swGui.getRate()) {

			case 1:
				denTicks *= 2f / 3;
				break;
			case 2:
				denTicks *= 1f / 2;
				break;
			case 3:
				denTicks *= 1f / 3;
				break;
			case 4:
				denTicks *= 1f / 4;
				break;
			case 5:
				denTicks *= 1f / 6;
				break;
			case 6:
				denTicks *= 1f / 8;
				break;
			case 7:
				denTicks *= 1f / 12;
				break;

			}

			for (int j = 0; j < barLengths[i]; j += denTicks) {
				NoteEvent n = new NoteEvent();
				n.setDuration(1);
				if (j == 0) {
					n.setVelocity(swGui.getAccentVelo());
					n.setNote(swGui.getAccentNote());
				} else {
					n.setVelocity(swGui.getNormalVelo());
					n.setNote(swGui.getNormalNote());
				}
				n.setTick(barStartPos + j);
				metaTracks[0].addEvent(n);
			}
		}
	}

	private void createMidiClockTrack() {
		metaTracks[1].getEvents().clear();

		metaTracks[1].addEvent(new MidiClockEvent(ShortMessage.START));

		for (int i = 0; i < this.getLastTick(); i += 4) {
			MidiClockEvent mcm = new MidiClockEvent(ShortMessage.TIMING_CLOCK);
			mcm.setTick(i);
			metaTracks[1].addEvent(mcm);
		}
	}

	private void createTempoChangeTrack() {
		metaTracks[2].getEvents().clear();
		for (TempoChangeEvent tce : tempoChangeEvents)
			metaTracks[2].getEvents().add(tce);
	}

	public boolean isLoopEnabled() {
		return loopEnabled;
	}

	public void setName(String s) {
		name = s;
	}

	public String getName() {
		if (!used) return "(Unused)";
		return name;
	}

	public void setDeviceName(int i, String s) {
		deviceNames[i] = s;
	}

	public String getDeviceName(int i) {
		return deviceNames[i];
	}

	public void setLastBar(int i) {
		if (i < 0 || i > 998) return;
		lastBar = i;
		setChanged();
		notifyObservers("lastbar");
	}

	public int getLastBar() {
		return lastBar;
	}

	public void setLoopEnabled(Boolean b) {
		loopEnabled = b;
		setChanged();
		notifyObservers("loop");
	}

	public void copyBars(int fromSequence, int firstBar, int lastBar, int toSequence, int afterBar, int copies) {
	}

	public MpcTrack getTrack(int i) {
		return tracks.get(i);
	}

	public void setUsed(boolean b) {
		used = b;
	}

	public boolean isUsed() {
		return used;
	}

	public void init(int barCount) {
		used = true;

		tempoChangeEvents = new ArrayList<TempoChangeEvent>();
		TempoChangeEvent initialTempoChangeEvent = new TempoChangeEvent(Bootstrap.getUserDefaults().getTempo(), this);
		initialTempoChangeEvent.setRatio(1000);
		initialTempoChangeEvent.setStepNumber(0);
		tempoChangeEvents.add(initialTempoChangeEvent);

		loopEnabled = Bootstrap.getUserDefaults().isLoopEnabled();

		for (MpcTrack t : getMpcTracks()) {
			com.mpc.sequencer.MpcTrack track = (com.mpc.sequencer.MpcTrack) t;
			track.setDeviceNumber(Bootstrap.getUserDefaults().getDeviceNumber());
			track.setProgramChange(Bootstrap.getUserDefaults().getPgm());
			track.setBusNumber(Bootstrap.getUserDefaults().getBus());
			track.setVelocityRatio(Bootstrap.getUserDefaults().getVeloRatio());
		}

		setLastBar(barCount);

		setTimeSignature(0, this.getLastBar(), Bootstrap.getUserDefaults().getTimeSig().getNumerator(),
				Bootstrap.getUserDefaults().getTimeSig().getDenominator());
		initMetaTracks();
		initLoop();

		tempoChangeEvents.get(0).setInitialTempo(Bootstrap.getUserDefaults().getTempo());
	}

	public void setTimeSignature(int firstBar, int tsLastBar, int num, int den) {
		SequenceUtil.setTimeSignature(this, firstBar, tsLastBar, num, den);
	}

	public List<MpcTrack> getMpcTracks() {
		return tracks;
	}

	@Override
	public List<Track> getTracks() {
		List<Track> t = new ArrayList<Track>();
		t.addAll(tracks);
		return t;
	}

	public void setTrack(MpcTrack track, int index) {
		tracks.set(index, track);
	}

	public String[] getDeviceNames() {
		return deviceNames;
	}

	public void setDeviceNames(String[] sa) {
		deviceNames = sa;
	}

	public List<TempoChangeEvent> getTempoChangeEvents() {
		return tempoChangeEvents;
	}

	public boolean isTempoChangeOn() {
		return tempoChangeOn;
	}

	public void setTempoChangeOn(boolean b) {
		tempoChangeOn = b;
		setChanged();
		notifyObservers("tempochangeon");
	}

	public long getLastTick() {
		long lastTick = 0;
		for (int i = 0; i < this.getLastBar() + 1; i++) {
			lastTick += barLengths[i];
		}
		return lastTick;
	}

	public TimeSignature getTimeSignature() {
		TimeSignature ts = new TimeSignature();
		int bar = mpc.getSequencer().getCurrentBarNumber();

		ts.setNumerator(numerators[bar]);
		ts.setDenominator(denominators[bar]);
		return ts;
	}

	public void sortTempoChangeEvents() {
		Collections.sort(tempoChangeEvents, new Comparator<TempoChangeEvent>() {
			public int compare(TempoChangeEvent o1, TempoChangeEvent o2) {
				if (o1.getTick() == o2.getTick()) return 0;
				return o1.getTick() < o2.getTick() ? -1 : 1;
			}
		});
		int tceCounter = 0;
		for (TempoChangeEvent tce : tempoChangeEvents) {
			tce.setStepNumber(tceCounter);
			tceCounter++;
		}
	}

	public void sortTracks() {
		Collections.sort(tracks, new Comparator<MpcTrack>() {
			public int compare(MpcTrack o1, MpcTrack o2) {
				if (((com.mpc.sequencer.MpcTrack) o1).getTrackIndex() == ((com.mpc.sequencer.MpcTrack) o2).getTrackIndex())
					return 0;
				return ((com.mpc.sequencer.MpcTrack) o1).getTrackIndex() < ((com.mpc.sequencer.MpcTrack) o2).getTrackIndex()
						? -1 : 1;
			}
		});
	}

	public void purgeAllTracks() {
		for (int i = 0; i < 64; i++) {
			purgeTrack(i);
		}
	}

	public void purgeTrack(int i) {
		try {

			tracks.set(i, new com.mpc.sequencer.MpcTrack(mpc, i));

			((com.mpc.sequencer.MpcTrack) tracks.get(i)).setName(defaultTrackNames[i]);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public int getDenominator(int i) {
		return denominators[i];
	}

	public int getNumerator(int i) {
		return numerators[i];
	}

	public long[] getBarLengths() {
		return barLengths;
	}

	public void deleteBars(int firstBar, int lBar) {
		lBar++;
		int difference = lBar - firstBar;
		lastBar -= difference;

		long oldBarStartPos = 0;
		int barCounter = 0;
		for (long l : barLengths) {
			if (barCounter == lBar) break;
			oldBarStartPos += l;
			barCounter++;
		}

		long newBarStartPos = 0;
		barCounter = 0;
		for (long l : barLengths) {
			if (barCounter == firstBar) break;
			newBarStartPos += l;
			barCounter++;
		}

		long tickDifference = oldBarStartPos - newBarStartPos;

		for (int i = firstBar; i < 999; i++) {
			if (i + difference > 998) break;
			barLengths[i] = barLengths[i + difference];
			numerators[i] = numerators[i + difference];
			denominators[i] = denominators[i + difference];
		}

		for (MpcTrack t : tracks) {
			if (((com.mpc.sequencer.MpcTrack) t).getTrackIndex() == 64
					|| ((com.mpc.sequencer.MpcTrack) t).getTrackIndex() == 65)
				continue;
			for (Event e : ((com.mpc.sequencer.MpcTrack) t).getEvents()) {
				if (e.getTick() >= oldBarStartPos) {
					e.setTick(e.getTick() - tickDifference);
				}
			}
		}

		if (firstLoopBar > lastBar) firstLoopBar = lastBar;

		if (lastLoopBar > lastBar) lastLoopBar = lastBar;
	}

	public void insertBars(int numberOfBars, int afterBar) {
		lastBar += numberOfBars;
		for (int i = afterBar; i < 999; i++) {
			if (i + numberOfBars > 998) break;
			barLengths[i + numberOfBars] = barLengths[i];
			numerators[i + numberOfBars] = numerators[i];
			denominators[i + numberOfBars] = denominators[i];
		}

		for (int i = afterBar; i < afterBar + numberOfBars; i++) {
			barLengths[i] = 384;
			numerators[i] = 4;
			denominators[i] = 4;
		}

		long barStart = 0;
		int barCounter = 0;
		for (long l : barLengths) {
			if (barCounter == afterBar) break;
			barStart += l;
			barCounter++;
		}

		barCounter = 0;
		long newBarStart = 0;
		for (long l : barLengths) {
			if (barCounter == afterBar + numberOfBars) break;
			newBarStart += l;
			barCounter++;
		}

		for (MpcTrack t : tracks) {
			if (((com.mpc.sequencer.MpcTrack) t).getTrackIndex() == 64
					|| ((com.mpc.sequencer.MpcTrack) t).getTrackIndex() == 65)
				continue;
			for (Event e : ((com.mpc.sequencer.MpcTrack) t).getEvents()) {
				if (e.getTick() >= barStart) {
					e.setTick(e.getTick() + (newBarStart - barStart));
				}
			}
		}
		initMetaTracks();
	}

	public void moveTrack(int source, int destination) {
		if (source == destination) return;

		if (source > destination) {
			((com.mpc.sequencer.MpcTrack) tracks.get(source)).setTrackIndex(destination);

			for (int i = destination; i < source; i++) {
				com.mpc.sequencer.MpcTrack t = (com.mpc.sequencer.MpcTrack) tracks.get(i);
				t.setTrackIndex(t.getTrackIndex() + 1);
			}
		}

		if (destination > source) {
			((com.mpc.sequencer.MpcTrack) tracks.get(source)).setTrackIndex(destination);

			for (int i = source + 1; i <= destination; i++) {
				com.mpc.sequencer.MpcTrack t = (com.mpc.sequencer.MpcTrack) tracks.get(i);
				t.setTrackIndex(t.getTrackIndex() - 1);
			}
		}

		sortTracks();
	}

	public boolean isLastLoopBarEnd() {
		return lastLoopBarEnd;
	}

	public void setTempoChangeEvents(List<TempoChangeEvent> tceList) {
		tempoChangeEvents = tceList;
	}

	public int getEventCount() {
		int counter = 0;
		for (MpcTrack t : tracks) {
			if (t.getTrackIndex() > 63) break;
			counter += ((com.mpc.sequencer.MpcTrack) t).getEvents().size();
		}
		return counter;
	}

	public void initLoop() {
		int firstBar = getFirstLoopBar();
		int lastBar = getLastLoopBar() + 1;

		long loopStart = 0;
		long loopEnd = 0;
		for (int i = 0; i < lastBar; i++) {
			if (i < firstBar) {
				loopStart += getBarLengths()[i];
			}
			loopEnd += getBarLengths()[i];
		}

		setLoopStart(loopStart);
		setLoopEnd(loopEnd);
	}

	public int[] getNumerators() {
		return numerators;
	}

	public int[] getDenominators() {
		return denominators;
	}

	public void removeFirstMetronomeClick() {
		tracks.get(64).removeEvent(0);
	}

	@Override
	public int getNoteEventCount() {
		int eventCounter = 0;
		for (int i=0;i<64;i++) {
			eventCounter += tracks.get(i).getNoteEvents().size();
		}
		return eventCounter;
	}
}