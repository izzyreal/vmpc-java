package com.mpc.sequencer;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.ShortMessage;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.file.all.AllParser;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.sequencer.Song.Step;
import com.mpc.tootextensions.Track;
import com.mpc.tootextensions.TrackContainer;

import uk.org.toot.audio.server.NonRealTimeAudioServer;

public class Sequencer extends com.mpc.tootextensions.MpcSequencer {

	private MpcSequence[] sequences;
	private Song[] songs;
	public static int[] tickValues = { 0, 48, 32, 24, 16, 12, 8 };

	private SequencerWindowGui swGui;
	private Thread tickDisplay;

//	private AllParser allFile;

	private String defaultSongName;
	private String defaultSequenceName;
	private String[] defaultDeviceNames;
	// private int timingCorrectIndex;
	private int timeDisplayStyle;
	private boolean recordingModeMulti;
	private int frameRate;
	private boolean countEnabled;
	private boolean soloEnabled;
	private boolean tempoSourceSequence;
	public static String[] defaultTrackNames;
	int activeTrackIndex;
	BigDecimal tempo;
	private boolean countingIn;
	private Mpc mpc;
	private CircularFifoBuffer taps = new CircularFifoBuffer(4);
	private CircularFifoBuffer slaveTempos = new CircularFifoBuffer(200);
	private long reposition = -1;
	private long position;

	private long lastTap;

	int nextsq = -1;
	static boolean endOfSong;
	static int repeats;
	private boolean secondSequenceEnabled;
	private int secondSequenceIndex;

	boolean overdubbing;
	boolean recording;

	private MpcSequence undoPlaceHolder;
	private boolean lastRecordingActive;
	private long playStartTick;
	private BigDecimal previousTempo = new BigDecimal("0.0");
	private long recordStartTick;

	public Sequencer(Mpc mpc) {
		this.mpc = mpc;
		songs = new Song[20];

		defaultTrackNames = new String[64];
		for (int i = 0; i < 64; i++) {
			defaultTrackNames[i] = ("Track-" + (String.format("%02d", +(i + 1))));
		}

		defaultSequenceName = Bootstrap.getUserDefaults().getSequenceName();
		defaultDeviceNames = new String[33];
		for (int i = 0; i < 33; i++)
			defaultDeviceNames[i] = Bootstrap.getUserDefaults().getDeviceName(i);

		recordingModeMulti = Bootstrap.getUserDefaults().isRecordingModeMulti();
		activeTrackIndex = 0;
		soloEnabled = false;
		tempoSourceSequence = true;
		// timingCorrectIndex = 3;
		countEnabled = true;

		recording = false;

		tempo = Bootstrap.getUserDefaults().getTempo();
	}

	public void init(SequencerWindowGui swGui) {
		this.swGui = swGui;
		purgeAllSequences();
		// tootSeq.setSource(this);
		// tootSeq.setClocksPerQuarter(Bootstrap.getGui().getMidiSyncGui().getModeIn()
		// != 0 ? 24 : 0);

		for (int i = 0; i < 20; i++) {
			songs[i] = new Song(this);
			songs[i].setName("Song" + Util.padLeft2Zeroes(i + 1));
		}
	}

	public void setTempo(BigDecimal i) {
		if (i.doubleValue() < 30.0 || i.doubleValue() > 300.0) return;
		TempoChangeEvent tce = getCurrentTempoChangeEvent();
		BigDecimal newPlayTempo = null;
		if (tempoSourceSequence) {
			if (tce.getTick() == 0) {
				tce.setInitialTempo(i);
				newPlayTempo = i;
			} else {
				BigDecimal initialTempo = tce.getInitialTempo();
				double ratio = i.doubleValue() / initialTempo.doubleValue();
				tce.setRatio((int) (ratio * 1000.0));
				newPlayTempo = tce.getTempo();
			}
		} else {
			tempo = i;
			newPlayTempo = i;
		}

		if (isPlaying()) setPlayTempo(newPlayTempo.floatValue());
		setChanged();
		notifyObservers("tempo");
	}

	public BigDecimal getTempo() {
		if (!isPlaying() && !getActiveSequence().isUsed()) return tempo;
		if (tempoSourceSequence) return getCurrentTempoChangeEvent().getTempo();
		return tempo;
	}

	public boolean isTempoSourceSequence() {
		return tempoSourceSequence;
	}

	public void setTempoSourceSequence(boolean b) {
		tempoSourceSequence = b;
		setChanged();
		notifyObservers("temposource");
		setChanged();
		notifyObservers("tempo");
	}

	public boolean isRecordingOrOverdubbing() {
		return recording || overdubbing;
	}

	public boolean isRecording() {
		return recording;
	}

	public boolean isSoloEnabled() {
		return soloEnabled;
	}

	public void setSoloEnabled(boolean b) {
		soloEnabled = b;
		setChanged();
		notifyObservers("soloenabled");
	}

	//public AllParser getAllFile() {
//		return allFile;
	//}

	public MpcSequence getSequence(int i) {
		return sequences[i];
	}

	public String getDefaultSongName() {
		return defaultSongName;
	}

	public void setDefaultSongName(String s) {
		defaultSongName = s;
	}

	public String getDefaultSequenceName() {
		return defaultSequenceName;
	}

	public void setDefaultSequenceName(String s) {
		defaultSequenceName = s;
	}

	public void setSelectedSequenceIndex(int i) {
		if (i < 0 || i > 98) return;
		activeSequenceIndex = i;
		if (!isPlaying()) {
			position = 0;
			setChanged();
			notifyObservers("now");
		}
		setChanged();
		notifyObservers("seqnumbername");
		setChanged();
		notifyObservers("timesignature");
		setChanged();
		notifyObservers("numberofbars");
		setChanged();
		notifyObservers("tempo");
		notifyTrack();
	}

	public String getDefaultDeviceName(int i) {
		return defaultDeviceNames[i];
	}

	public void setDefaultDeviceName(int i, String s) {
		defaultDeviceNames[i] = s;
	}

	public void setTcValue(int i) {
		// if (i < 0 || i > 6) return;
		// timingCorrectIndex = i;
		Bootstrap.getGui().getSequencerWindowGui().setNoteValue(i);
		setChanged();
		notifyObservers("timing");
	}

	public boolean isCountEnabled() {
		return countEnabled;
	}

	public void setCountEnabled(boolean b) {
		countEnabled = b;
		setChanged();
		notifyObservers("count");
	}

	public int getTcIndex() {
		return Bootstrap.getGui().getSequencerWindowGui().getNoteValue();
	}

	public void setTimeDisplayStyle(int i) {
		timeDisplayStyle = i;
	}

	public int getTimeDisplayStyle() {
		return timeDisplayStyle;
	}

	public void setRecordingModeMulti(Boolean b) {
		recordingModeMulti = b;
		setChanged();
		notifyObservers("recordingmode");
	}

	public void setFrameRate(int i) {
		frameRate = i;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public boolean isRecordingModeMulti() {
		return recordingModeMulti;
	}

	public int getActiveTrackIndex() {
		return activeTrackIndex;
	}

	public void trackUp() {
		if (activeTrackIndex == 63) return;
		activeTrackIndex++;
		notifyTrack();
	}

	public void trackDown() {
		if (activeTrackIndex == 0) return;
		activeTrackIndex--;
		notifyTrack();
	}

	public boolean isPlaying() {
		if (mpc.getAudioMidiServices().isDisabled() || mpc.getAudioMidiServices().getFrameSequencer() == null) return false;
		return mpc.getAudioMidiServices().getFrameSequencer().isRunning();
	}

	private void play(boolean fromStart) {
		if (isPlaying()) return;
		endOfSong = false;
		repeats = 0;
		Song currentSong = songs[Bootstrap.getGui().getSongGui().getSelectedSongIndex()];
		Step currentStep = null;
		if (songMode) {
			if (!currentSong.isUsed()) return;
			if (fromStart) Bootstrap.getGui().getSongGui().setOffset(-1);
			if (Bootstrap.getGui().getSongGui().getOffset() + 1 > currentSong.getStepAmount() - 1) return;
			int step = Bootstrap.getGui().getSongGui().getOffset() + 1;
			if (step > currentSong.getStepAmount()) step = currentSong.getStepAmount() - 1;
			currentStep = currentSong.getStep(step);
		}

		move(position);
		currentlyPlayingSequenceIndex = activeSequenceIndex;
		if (!countEnabled || swGui.getCountInMode() == 0 || (swGui.getCountInMode() == 1 && recording == false)) {
			if (fromStart) move(0);
		}

		if (countEnabled && !songMode) {
			if (swGui.getCountInMode() == 2 || (swGui.getCountInMode() == 1 && recording == true)) {
				move(getActiveSequence().getLoopStart());
				startCountingIn();
			}
		}

		MpcSequence s = getActiveSequence();
		if (!songMode) {
			if (!s.isUsed()) return;
			s.initLoop();
			if (recording || overdubbing) {
				undoPlaceHolder = copySequence(s);
				lastRecordingActive = true;
				recordStartTick = getTickPosition();
				Bootstrap.getGui().getMainFrame().getLedPanel().setUndoSeq(lastRecordingActive);
			}

		}
		setBpm(getTempo().floatValue());

		Bootstrap.getGui().getMainFrame().getLedPanel().setPlay(true);

		// try {
		// if (Bootstrap.getGui().getMidiSyncGui().getModeOut() != 0)
		// mpc.getEventHandler()
		// .handle(new MidiClockEvent(ShortMessage.START), new
		// com.mpc.sequencer.MpcTrack(mpc, 999));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }

		mpc.getAudioMidiServices().getFrameSequencer().start(this);
		if (mpc.getAudioMidiServices().isBouncePrepared()) {
			mpc.getAudioMidiServices().startBouncing();
		} else {
			((NonRealTimeAudioServer) mpc.getAudioMidiServices().getAudioServer()).setRealTime(true);
		}
		setChanged();
		notifyObservers("play");
		tickDisplay = new Thread(new TickDisplay());
		tickDisplay.start();
	}

	public void undoSeq() {
		if (isPlaying()) return;
		if (undoPlaceHolder == null) return;
		MpcSequence s = copySequence(undoPlaceHolder);
		undoPlaceHolder = copySequence(sequences[activeSequenceIndex]);
		// sequences[selectedSequenceIndex] = copySequence(s);
		sequences[activeSequenceIndex] = s;
		lastRecordingActive = !lastRecordingActive;
		Bootstrap.getGui().getMainFrame().getLedPanel().setUndoSeq(lastRecordingActive);
	}

	public void clearUndoSeq() {
		// System.out.println("clear undo");
		if (isPlaying()) return;
		undoPlaceHolder = null;
		lastRecordingActive = false;
		Bootstrap.getGui().getMainFrame().getLedPanel().setUndoSeq(false);
	}

	public void playFromStart() {
		if (isPlaying()) return;
		play(true);
	}

	public void play() {
		if (isPlaying()) return;
		play(false);
	}

	public void rec() {
		if (isPlaying()) return;
		recording = true;
		play(false);
	}

	public void recFromStart() {
		if (isPlaying()) return;
		recording = true;
		play(true);
	}

	public void overdub() {
		if (isPlaying()) return;
		overdubbing = true;
		play(false);
	}

	public void switchRecordToOverDub() {
		if (!isRecording()) return;
		recording = false;
		overdubbing = true;
		Bootstrap.getGui().getMainFrame().getLedPanel().setOverDub(true);
		Bootstrap.getGui().getMainFrame().getLedPanel().setRec(false);
	}

	public void overdubFromStart() {
		if (isPlaying()) return;
		overdubbing = true;
		play(true);
	}

	public void stop() {
		if (!isPlaying()) {
			if (position != 0) setBar(0);
			return;
		}
		try {
			mpc.getEventHandler().handle(new MidiClockEvent(ShortMessage.STOP),
					new com.mpc.sequencer.MpcTrack(mpc, 999));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		long pos = getTickPosition();

		mpc.getAudioMidiServices().getFrameSequencer().stop();
		if (recording || overdubbing) getCurrentlyPlayingSequence().getTrack(activeTrackIndex).correctTimeRange(0,
				getCurrentlyPlayingSequence().getLastTick(), tickValues[getTcIndex()]);

		boolean notifynextsq = false;
		if (nextsq != -1) {
			notifynextsq = true;
			nextsq = -1;
			Bootstrap.getGui().getMainFrame().lookupTextField("sq").grabFocus();
		}

		recording = false;
		overdubbing = false;
		if (pos > getActiveSequence().getLastTick()) pos = getActiveSequence().getLastTick();
		move(pos);
		mpc.getSampler().stopAllVoices();

		if (notifynextsq) {
			setChanged();
			notifyObservers("nextsqoff");
		}
		notifyTimeDisplay();

		if (endOfSong) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Bootstrap.getGui().getSongGui().setOffset(Bootstrap.getGui().getSongGui().getOffset() + 1);
				}
			});
		}

		new Thread() {
			public void run() {
				Util.sleep(100);
				mpc.getAudioMidiServices().stopBouncing();
				((NonRealTimeAudioServer) mpc.getAudioMidiServices().getAudioServer()).setRealTime(true);
			}
		}.start();
		Bootstrap.getGui().getMainFrame().getLedPanel().setOverDub(false);
		Bootstrap.getGui().getMainFrame().getLedPanel().setPlay(false);
		Bootstrap.getGui().getMainFrame().getLedPanel().setRec(false);
		setChanged();
		notifyObservers("stop");
	}

	public boolean isCountingIn() {
		return countingIn;
	}

	public void setCountingIn(boolean b) {
		countingIn = b;
	}

	public void startCountingIn() {
		countingIn = true;
	}

	private class TickDisplay implements Runnable {

		@Override
		public void run() {
			int updateCounter = 0;
			long previousTickPos = -1;
			while (isPlaying()) {
				if (getTickPosition() != previousTickPos) {
					previousTickPos = getTickPosition();
					updateCounter++;
					if (updateCounter == 6) {
						if (!countingIn) {
							notifyTimeDisplay();
							Sequencer.this.notify("timesignature");
							Sequencer.this.notify("numberofbars");
							Sequencer.this.notify("loop");

						}
						updateCounter = 0;
					}
				}
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void notifyTrack() {
		setChanged();
		notifyObservers("tracknumbername");
		setChanged();
		notifyObservers("trackon");
		setChanged();
		notifyObservers("programchange");
		setChanged();
		notifyObservers("velocityratio");
		setChanged();
		notifyObservers("tracktype");
		setChanged();
		notifyObservers("device");
		setChanged();
		notifyObservers("devicename");
	}

	public void setSequence(int i, MpcSequence s) {
		sequences[i] = s;
	}

	public void purgeAllSequences() {
		sequences = new MpcSequence[99];
		for (int i = 0; i < 99; i++) {
			sequences[i] = new MpcSequence(mpc, defaultTrackNames);
			sequences[i].setName(defaultSequenceName.trim() + String.format("%02d", i + 1));
		}

		activeSequenceIndex = 0;
	}

	public void copySequence(int sq0, int sq1) {
		sequences[sq1] = copySequence(sequences[sq0]);
		sequences[sq1].initLoop();
	}

	private MpcSequence copySequence(MpcSequence source) {
		MpcSequence copy = new MpcSequence(mpc, defaultTrackNames);
		copy.init(source.getLastBar());

		copySequenceParameters(source, copy);

		for (int i = 0; i < 64; i++)
			copy.setTrack(copyTrack(source.getTrack(i)), i);

		return copy;
	}

	public void copySequenceParameters(int sq0, int sq1) {
		copySequenceParameters(sequences[sq0], sequences[sq1]);
	}

	private void copySequenceParameters(MpcSequence source, MpcSequence dest) {
		dest.setName(source.getName());
		dest.setTempoChangeEvents(source.getTempoChangeEvents());
		dest.setLoopEnabled(source.isLoopEnabled());
		dest.setUsed(source.isUsed());
		dest.setDeviceNames(source.getDeviceNames());
	}

	public void copyTrack(int tr0, int tr1, int sq0, int sq1) {
		com.mpc.sequencer.MpcTrack track0 = (com.mpc.sequencer.MpcTrack) sequences[sq0].getTrack(tr0);
		com.mpc.sequencer.MpcTrack track1 = null;
		try {
			track1 = new com.mpc.sequencer.MpcTrack(mpc, tr1);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		for (Event event : track0.getEvents()) {
			Event clone = (Event) event.clone();
			track1.getEvents().add(clone);
		}
		copyTrackParameters(tr0, tr1, sq0, sq1);
		sequences[sq1].setTrack(track1, tr1);
	}

	private MpcTrack copyTrack(MpcTrack source) {
		MpcTrack copy = null;
		try {
			copy = new MpcTrack(mpc, source.getTrackIndex());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		for (Event e : source.getEvents()) {
			Event clone = (Event) e.clone();
			copy.getEvents().add(clone);
		}

		copyTrackParameters(source, copy);
		return copy;
	}

	public void copyTrackParameters(int tr0, int tr1, int sq0, int sq1) {
		copyTrackParameters(sequences[sq0].getTrack(tr0), sequences[sq1].getTrack(tr1));
	}

	private void copyTrackParameters(MpcTrack source, MpcTrack dest) {
		dest.setUsed(source.isUsed());
		dest.setOn(source.isOn());
		dest.setDeviceNumber(source.getDevice());
		dest.setBusNumber(source.getBusNumber());
		dest.setVelocityRatio(source.getVelocityRatio());
		dest.setProgramChange(source.getProgramChange());
		dest.setName(source.getName());
	}

	public String getDefaultTrackName(int i) {
		return defaultTrackNames[i];
	}

	public void setDefaultTrackName(String s, int i) {
		defaultTrackNames[i] = s;
	}

	public int getCurrentBarNumber() {
		MpcSequence s = isPlaying() ? getCurrentlyPlayingSequence() : getActiveSequence();
		long pos = getTickPosition();
		if (pos == s.getLastTick()) return s.getLastBar() + 1;
		long index = pos;
		if (isPlaying() && !countingIn) index = this.getTickPosition();

		if (index == 0) return 0;

		long[] barLengths = s.getBarLengths();
		int barCounter = 0;
		long tickCounter = 0;

		for (int i = 0; i < 999; i++) {
			if (i > s.getLastBar()) i = 0;
			tickCounter += barLengths[i];
			if (tickCounter > index) {
				barCounter = i;
				break;
			}
		}
		return barCounter;
	}

	public int getCurrentBeatNumber() {
		MpcSequence s = isPlaying() ? getCurrentlyPlayingSequence() : getActiveSequence();
		long pos = getTickPosition();
		if (pos == s.getLastTick()) return 0;
		long index = pos;

		if (isPlaying() && !countingIn) {
			index = this.getTickPosition();
			if (index > s.getLastTick()) {
				index %= s.getLastTick();
			}
		}

		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));

		if (index == 0) return 0;

		long barStartPos = 0;
		int barCounter = 0;

		for (long l : s.getBarLengths()) {
			if (barCounter == getCurrentBarNumber()) break;
			barStartPos += l;
			barCounter++;
		}

		int beatCounter = (int) Math.floor((index - barStartPos) / denTicks);
		return beatCounter;
	}

	public int getCurrentClockNumber() {
		MpcSequence s = isPlaying() ? getCurrentlyPlayingSequence() : getActiveSequence();
		long pos = getTickPosition();
		if (pos == s.getLastTick()) return 0;
		long index = pos;

		if (isPlaying() && !countingIn) {
			index = getTickPosition();
			if (index > s.getLastTick()) {
				index %= s.getLastTick();
			}
		}

		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));

		if (index == 0) return 0;

		int barCounter = 0;
		long clock = index;

		for (long l : s.getBarLengths()) {
			if (barCounter == getCurrentBarNumber()) break;
			clock -= l;
			barCounter++;
		}

		for (int i = 0; i < getCurrentBeatNumber(); i++) {
			clock -= denTicks;
		}
		return (int) clock;
	}

	public void setBar(int i) {
		if (isPlaying()) return;
		if (i < 0) return;

		MpcSequence s = getActiveSequence();
		if (i > s.getLastBar() + 1) return;
		if (s.getLastBar() == 998 & i > 998) return;
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (i != s.getLastBar() + 1) {
			s.getTimeSignature().setNumerator(s.getNumerator(i));
			s.getTimeSignature().setDenominator(s.getDenominator(i));
		}

		long[] barLengths = s.getBarLengths();
		int currentClock = getCurrentClockNumber();
		int currentBeat = getCurrentBeatNumber();
		long pos = 0;
		int barCounter = 0;

		for (long l : barLengths) {
			if (barCounter == i) break;
			pos += l;
			barCounter++;
		}

		pos += currentBeat * denTicks;
		pos += currentClock;
		if (pos > s.getLastTick()) pos = s.getLastTick();

		move(pos);
		setChanged();
		notifyObservers("timesignature");
		setBeat(0);
		setClock(0);
	}

	public void setBeat(int i) {
		if (isPlaying()) return;
		if (i < 0) return;
		MpcSequence s = getActiveSequence();
		long pos = getTickPosition();
		if (pos == s.getLastTick()) return;
		int difference = i - getCurrentBeatNumber();
		int num = s.getTimeSignature().getNumerator();
		if (i >= num) return;
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		pos += difference * denTicks;
		move(pos);
	}

	public void setClock(int i) {
		if (isPlaying()) return;
		if (i < 0) return;
		MpcSequence s = getActiveSequence();
		long pos = getTickPosition();
		if (pos == s.getLastTick()) return;
		int difference = i - getCurrentClockNumber();
		int den = s.getTimeSignature().getDenominator();
		int denTicks = (int) (96 * (4.0 / den));
		if (i > denTicks - 1) return;
		if (pos + difference > s.getLastTick()) return;
		pos = pos + difference;
		move(pos);
	}

	public long getLoopEnd() {
		return getActiveSequence().getLoopEnd();
	}

	public TempoChangeEvent getCurrentTempoChangeEvent() {
		int index = -1;
		MpcSequence s = getActiveSequence();
		for (TempoChangeEvent tce : s.getTempoChangeEvents()) {
			if (getTickPosition() >= tce.getTick()) {
				index++;
			} else {
				break;
			}
		}
		if (index == -1) index++;
		return getActiveSequence().getTempoChangeEvents().get(index);
	}

	public MpcSequence getActiveSequence() {
		if (songMode && songs[Bootstrap.getGui().getSongGui().getSelectedSongIndex()].getStepAmount() != 0)
			return sequences[getSongSequenceIndex() >= 0 ? getSongSequenceIndex() : activeSequenceIndex];
		return sequences[activeSequenceIndex];
	}

	public int getUsedSequenceCount() {
		return getUsedSequences().size();
	}

	public List<MpcSequence> getUsedSequences() {
		List<MpcSequence> usedSeqs = new ArrayList<MpcSequence>();
		for (MpcSequence s : sequences)
			if (s.isUsed()) usedSeqs.add(s);
		return usedSeqs;
	}

	public List<Integer> getUsedSequenceIndexes() {
		List<Integer> usedSeqs = new ArrayList<Integer>();
		for (int i = 0; i < 99; i++) {
			MpcSequence s = sequences[i];
			if (s.isUsed()) usedSeqs.add(new Integer(i));
		}
		return usedSeqs;
	}

	public void goToPreviousEvent() {
		MpcTrack t = (MpcTrack) getActiveSequence().getTrack(getActiveTrackIndex());
		if (t.getEventIndex() == 0) {
			setBar(0);
			return;
		}

		if (t.getEventIndex() >= t.getEvents().size() - 1
				&& t.getEvent(t.getEvents().size() - 1).getTick() < position) {
			t.setEventIndex(t.getEvents().size() - 1);
			move(t.getEvent(t.getEventIndex()).getTick());
			return;
		}

		Event event;
		Event prev;
		while (t.getEventIndex() > 0) {
			event = t.getEvent(t.getEventIndex());
			prev = t.getEvent(t.getEventIndex() - 1);
			if (prev.getTick() == event.getTick()) {
				t.setEventIndex(t.getEventIndex() - 1);
			} else {
				break;
			}
		}

		t.setEventIndex(t.getEventIndex() - 1);
		while (t.getEventIndex() > 0) {
			event = t.getEvent(t.getEventIndex());
			prev = t.getEvent(t.getEventIndex() - 1);
			if (prev.getTick() == event.getTick()) {
				t.setEventIndex(t.getEventIndex() - 1);
			} else {
				break;
			}
		}
		move(t.getEvents().get(t.getEventIndex()).getTick());
	}

	public void goToNextEvent() {
		MpcTrack t = (MpcTrack) getActiveSequence().getTrack(getActiveTrackIndex());
		if (t.getEvents().size() == 0) {
			if (position != getActiveSequence().getLastTick()) {
				move(getActiveSequence().getLastTick());
			}
			return;
		}
		final int eventCount = t.getEvents().size();
		if (position == getActiveSequence().getLastTick()) return;

		if (t.getEventIndex() >= eventCount - 1 && position >= t.getEvent(eventCount - 1).getTick()) {
			move(getActiveSequence().getLastTick());
			return;
		}

		if (t.getEvent(t.getEventIndex()).getTick() > position) {
			move(t.getEvent(t.getEventIndex()).getTick());
			return;
		}

		Event event;
		Event next;

		if (t.getEvent(t.getEventIndex()).getTick() == position) {
			while (t.getEventIndex() < eventCount - 2) {
				event = t.getEvent(t.getEventIndex());
				next = t.getEvent(t.getEventIndex() + 1);
				if (next.getTick() == event.getTick()) {
					t.setEventIndex(t.getEventIndex() + 1);
				} else {
					break;
				}
			}
		}

		t.setEventIndex(t.getEventIndex() + 1);

		while (t.getEventIndex() < eventCount - 2) {
			event = t.getEvent(t.getEventIndex());
			next = t.getEvent(t.getEventIndex() + 1);
			if (next.getTick() == event.getTick()) {
				t.setEventIndex(t.getEventIndex() + 1);
			} else {
				break;
			}
		}

		move(t.getEvents().get(t.getEventIndex()).getTick());
	}

	private void notifyTimeDisplay() {
		setChanged();
		notifyObservers("bar");
		setChanged();
		notifyObservers("beat");
		setChanged();
		notifyObservers("clock");
	}

	public void goToPreviousStep() {
		int stepSize = tickValues[getTcIndex()];
		long pos = getTickPosition();
		int stepAmt = (int) Math.ceil(getActiveSequence().getLastTick() / stepSize) + 1;
		long[] stepGrid = new long[stepAmt];
		for (int i = 0; i < stepGrid.length; i++)
			stepGrid[i] = i * stepSize;
		int currentStep = 0;
		for (long l : stepGrid) {
			if (pos <= l) break;
			currentStep++;
		}
		if (currentStep == 0) return;
		currentStep--;
		move(currentStep * stepSize);
	}

	public void goToNextStep() {
		int stepSize = tickValues[getTcIndex()];
		long pos = getTickPosition();
		long[] stepGrid = new long[(int) Math.ceil(getActiveSequence().getLastTick() / stepSize)];
		for (int i = 0; i < stepGrid.length; i++)
			stepGrid[i] = i * stepSize;
		int currentStep = -1;
		for (long l : stepGrid) {
			if (pos < l) break;
			currentStep++;
		}
		if (currentStep == stepGrid.length) return;
		currentStep++;
		move(currentStep * stepSize);
	}

	public void tap() {
		if (isPlaying()) return;
		if (System.nanoTime() - lastTap > (2000 * 1000000)) taps = new CircularFifoBuffer(4);
		lastTap = System.nanoTime();
		taps.add(System.nanoTime());
		long accum = 0;
		Iterator<Long> il = taps.iterator();
		List<Long> tapsLong = new ArrayList<Long>();
		while (il.hasNext())
			tapsLong.add(il.next());
		for (int i = 0; i < tapsLong.size() - 1; i++) {
			long l0 = tapsLong.get(i);
			long l1 = tapsLong.get(i + 1);
			accum += l1 - l0;
		}
		if (accum == 0) return;
		double tempo = (60000.0 * 1000000.0) / (accum / (tapsLong.size() - 1));
		tempo = Math.floor(tempo * 10) / 10;
		setTempo(new BigDecimal("" + tempo));
	}

	@Override
	public int getResolution() {
		return 96;
	}

	@Override
	public List<TrackContainer> getSequences() {
		List<TrackContainer> seqs = new ArrayList<TrackContainer>();
		List<MpcSequence> l = new ArrayList<MpcSequence>();
		for (MpcSequence s : sequences)
			l.add(s);
		seqs.addAll(l);
		return seqs;
	}

	public void move(long tick) {
		long oldTick = getTickPosition();
		reposition = tick;
		position = tick;
		playStartTick = tick;
		MpcSequence s = (isPlaying() ? getCurrentlyPlayingSequence() : getActiveSequence());

		if (!isPlaying() && songMode) s = sequences[getSongSequenceIndex()];
		if (s.isUsed()) for (Track t : s.getTracks()) {
			if (!((MpcTrack) t).isUsed()) continue;
			((com.mpc.sequencer.MpcTrack) t).move(tick, oldTick);
		}
		notifyTimeDisplay();
		if (getTempo() != previousTempo) {
			previousTempo = getTempo();
			setChanged();
			notifyObservers("tempo");
		}
	}

	public void setPlayTempo(float tempo) {
		this.setBpm(tempo);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setChanged();
				notifyObservers("playtempo");
			}
		});
	}

	public long getTickPosition() {
		// for song mode add previous sequences accum tick length
		if (isPlaying()) {
			return mpc.getAudioMidiServices().getFrameSequencer().getTickPosition();
		}
		return position;
	}

	public MpcSequence getCurrentlyPlayingSequence() {
		int songseq = songMode ? songs[Bootstrap.getGui().getSongGui().getSelectedSongIndex()]
				.getStep(Bootstrap.getGui().getSongGui().getOffset() + 1).getSequence() : -1;
		return sequences[songMode ? songseq : currentlyPlayingSequenceIndex];
	}

	public void setSelectedTrackIndex(int i) {
		activeTrackIndex = i;
		setChanged();
		notifyObservers("selectedtrackindex");
	}

	public int getCurrentlyPlayingSequenceIndex() {
		int songseq = songMode ? songs[Bootstrap.getGui().getSongGui().getSelectedSongIndex()]
				.getStep(Bootstrap.getGui().getSongGui().getOffset() + 1).getSequence() : -1;
		return songMode ? songseq : currentlyPlayingSequenceIndex;
	}

	public int getNextSq() {
		return nextsq;
	}

	public int getFirstUsedSeqDown(int from) {
		int result = -1;
		for (int i = from; i >= 0; i--) {
			if (sequences[i].isUsed()) {
				result = i;
				break;
			}
		}
		return result;
	}

	public int getFirstUsedSeqUp(int from) {
		int result = -1;
		for (int i = from; i < 99; i++) {
			if (sequences[i].isUsed()) {
				result = i;
				break;
			}
		}
		return result;
	}

	public void setNextSq(int i) {
		boolean firstnotify = nextsq == -1;
		boolean up = i > nextsq;
		if (firstnotify) up = i > currentlyPlayingSequenceIndex;
		int result = up ? getFirstUsedSeqUp(i) : getFirstUsedSeqDown(i);

		if (result == -1) return;
		nextsq = result;
		System.out.println("nextsq: " + nextsq);
		if (firstnotify) {
			setChanged();
			notifyObservers("nextsq");
		} else {
			setChanged();
			notifyObservers("nextsqvalue");
		}
	}

	public void setNextSqPad(int i) {
		if (!sequences[i].isUsed()) {
			nextsq = -1;
			setChanged();
			notifyObservers("nextsqoff");
			return;
		}
		boolean firstnotify = nextsq == -1;
		nextsq = i;
		if (firstnotify) {
			setChanged();
			notifyObservers("nextsq");
		} else {
			setChanged();
			notifyObservers("nextsqvalue");
		}
	}

	public Song getSong(int i) {
		return songs[i];
	}

	public void setSong(int i, Song s) {
		songs[i] = s;
	}

	public boolean isSongModeEnabled() {
		return songMode;
	}

	public void setSongModeEnabled(boolean b) {
		songMode = b;
	}

	@Override
	public int getSongSequenceIndex() {
		Song song = songs[Bootstrap.getGui().getSongGui().getSelectedSongIndex()];
		int step = Bootstrap.getGui().getSongGui().getOffset() + 1;
		if (step > song.getStepAmount() - 1) step = song.getStepAmount() - 1;
		return song.getStep(step).getSequence();
	}

	public boolean isSecondSequenceEnabled() {
		return secondSequenceEnabled;
	}

	public void setSecondSequenceEnabled(boolean b) {
		secondSequenceEnabled = b;
	}

	public int getSecondSequenceIndex() {
		return secondSequenceIndex;
	}

	public void setSecondSequenceIndex(int i) {
		secondSequenceIndex = i;
	}

	public void flushTrackNoteCache() {
		for (MpcTrack t : getCurrentlyPlayingSequence().getMpcTracks())
			t.flushNoteCache();
	}

	public void storeActiveSequenceInPlaceHolder() {
		undoPlaceHolder = copySequence(sequences[activeSequenceIndex]);
		lastRecordingActive = true;
		Bootstrap.getGui().getMainFrame().getLedPanel().setUndoSeq(lastRecordingActive);
	}

	public boolean isOverDubbing() {
		return overdubbing;
	}

	public long getPlayStartTick() {
		return playStartTick;
	}

	void notify(String s) {
		setChanged();
		notifyObservers(s);
	}

	public void setRecording(boolean b) {
		recording = b;
	}

	public void setOverdubbing(boolean b) {
		overdubbing = b;
	}

	public void playMetronomeTrack() {
		if (isPlaying() || metronomeOnly) return;
		metronomeOnly = true;
		MpcSequence metroSeq = new MpcSequence(mpc, defaultTrackNames);
		metroSeq.init(8);
		metroSeq.setTimeSignature(0, 3, getActiveSequence().getNumerator(getCurrentBarNumber()),
				getActiveSequence().getDenominator(getCurrentBarNumber()));
		metroSeq.getTempoChangeEvents().get(0).setInitialTempo(getTempo());
		metroSeq.removeFirstMetronomeClick();
		metronomeSeq = metroSeq;	
		mpc.getAudioMidiServices().getFrameSequencer().startMetronome(this);
	}
	
	public void stopMetronomeTrack() {
		if (!metronomeOnly) return;
		metronomeOnly = false;
		mpc.getAudioMidiServices().getFrameSequencer().stop();
	}

}