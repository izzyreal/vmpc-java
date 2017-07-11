package com.mpc.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mpc.Mpc;
import com.mpc.controls.KbMouseController;
import com.mpc.gui.Bootstrap;
import com.mpc.tootextensions.Track;

public class MpcTrack extends Observable implements Track {

	private Mpc mpc;

	private int busNumber;
	private String name;
	private boolean on;
	private int velocityRatio;
	private int programChange;
	private int device;
	private List<Event> events;
	private int trackIndex;
	private boolean used;
	private long relativeTick;

	private int eventIndex;
	private ConcurrentLinkedQueue<NoteEvent> queuedNoteOnEvents;
	private com.mpc.sequencer.Sequencer sequencer;

	private List<NoteEvent> noteOffs;

	private NoteEvent lastAdded;

	private boolean eventAvailable;

	private Event event;

	private boolean multi;

	private boolean delete;

	private Comparator<? super Event> comparator;

	private Comparator<? super NoteEvent> noteComparator;

	private int tcValue;

	private ArrayList<Event> tempEvents;

	public MpcTrack(Mpc mpc, int i) throws UnsupportedEncodingException {
		this.mpc = mpc;
		sequencer = mpc.getSequencer();
		trackIndex = i;
		programChange = 0;
		velocityRatio = 100;
		used = false;
		on = true;
		events = new ArrayList<Event>();
		eventIndex = 0;
		device = 0;
		busNumber = 1;
		queuedNoteOnEvents = new ConcurrentLinkedQueue<NoteEvent>();
		noteOffs = new ArrayList<NoteEvent>();
		comparator = new Comparator<Event>() {
			public int compare(Event o1, Event o2) {
				if (o1.getTick() == o2.getTick()) return 0;
				return o1.getTick() < o2.getTick() ? -1 : 1;
			}
		};
		noteComparator = new Comparator<NoteEvent>() {
			public int compare(NoteEvent o1, NoteEvent o2) {
				if (o1.getNote() == o2.getNote()) return 0;
				return o1.getNote() < o2.getNote() ? -1 : 1;
			}
		};
	}

	protected void move(long tick, long oldTick) {

		if (tick == 0) {
			eventIndex = 0;
			return;
		}

		for (NoteEvent no : noteOffs) {
			no.setTick((no.getTick() - oldTick) + tick);
		}

		if (tick == oldTick) return;
		int startIndex = 0;
		if (tick > oldTick) {
			if (eventIndex == events.size()) return;
			startIndex = eventIndex;
		}

		if (tick < oldTick && eventIndex == 0) return;

		eventIndex = events.size();
		for (int i = startIndex; i < events.size(); i++) {
			if (events.get(i).getTick() >= tick) {
				eventIndex = i;
				break;
			}
		}
	}

	protected void setTrackIndex(int i) {
		trackIndex = i;
	}

	public int getTrackIndex() {
		return trackIndex;
	}

	public void recordNoteOn(NoteEvent n) {
		for (NoteEvent ne : queuedNoteOnEvents) {
			if (ne.getNote() == n.getNote()) {
				NoteEvent nne = new NoteEvent();
				nne.setNote(n.getNote());
				nne.setVelocity(0);
				nne.setTick(sequencer.getTickPosition());
				recordNoteOff(nne);
			}
		}
		if (n.getTick() >= sequencer.getCurrentlyPlayingSequence().getLastTick()) n.setTick(0);
		queuedNoteOnEvents.add(n);
	}

	public void flushNoteCache() {
		queuedNoteOnEvents.clear();
	}

	public void recordNoteOff(NoteEvent n) {
		int note = n.getNote();
		NoteEvent noteOn = null;
		for (NoteEvent noteEvent : queuedNoteOnEvents) {
			if (noteEvent.getNote() == note) {
				noteOn = noteEvent;
				break;
			}
		}
		if (noteOn == null) return;
		if (n.getTick() > noteOn.getTick()) {
			noteOn.setDuration((int) (n.getTick() - noteOn.getTick()));
		} else {
			noteOn.setDuration((int) (sequencer.getLoopEnd() - 1 - noteOn.getTick()));
		}

		addEventRealTime(noteOn);
		eventIndex++;
		queuedNoteOnEvents.remove(noteOn);
	}

	public void setUsed(boolean b) {
		used = b;
		if (used) {
			setChanged();
			notifyObservers("tracknumbername");
		}
	}

	public void setOn(boolean b) {
		on = b;
		setChanged();
		notifyObservers("trackon");
	}

	private void addEventRealTime(Event event) {
		if (events.size() == 0) setUsed(true);
		for (Event temp : events) {
			if (temp.getTick() == event.getTick()) {
				if (temp.getClass().equals(event.getClass())) {
					if (((NoteEvent) temp).getNote() == ((NoteEvent) event).getNote()) {
						((NoteEvent) temp).setDuration(((NoteEvent) event).getDuration());
						((NoteEvent) temp).setVelocity(((NoteEvent) event).getVelocity());
						return;
					}
				}
			}
		}
		tcValue = Bootstrap.getGui().getSequencerWindowGui().getNoteValue();
		if (tcValue > 0 && event instanceof NoteEvent) {
			this.timingCorrect(0, sequencer.getCurrentlyPlayingSequence().getLastBar(), (NoteEvent) event,
					Sequencer.tickValues[tcValue]);
		}
		events.add(event);
		sortEvents();
	}

	public void addEvent(Event event) {
		if (events.size() == 0) setUsed(true);
		events.add(event);
		if (event instanceof NoteEvent) lastAdded = (NoteEvent) event;
		sortEvents();
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public void adjustDurLastEvent(int newDur) {
		if (lastAdded == null) return;
		lastAdded.setDuration(newDur);
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public void addEvent(Event e, MpcSequence mpcSequence) {
		relativeTick = sequencer.getTickPosition();
		if (events.size() == 0) setUsed(true);
		e.setTick(relativeTick);
		events.add(e);
		sortEvents();
	}

	public void addNoteEvent(int note, MpcSequence mpcSequence) {
		relativeTick = sequencer.getTickPosition();
		if (events.size() == 0) setUsed(true);
		NoteEvent n = new NoteEvent();
		n.setNote(note);
		n.setTick(relativeTick);
		events.add(n);
		sortEvents();
	}

	public void removeEvent(int i) {
		events.remove(i);
	}

	public void setVelocityRatio(int i) {
		if (i < 1) i = 1;
		if (i > 200) i = 200;
		velocityRatio = i;
		setChanged();
		notifyObservers("velocityratio");
	}

	public int getVelocityRatio() {
		return velocityRatio;
	}

	public void setProgramChange(int i) {
		if (i < 0 || i > 128) return;
		programChange = i;
		setChanged();
		notifyObservers("programchange");
	}

	public int getProgramChange() {
		return programChange;
	}

	public void setBusNumber(int i) {
		if (i < 0 || i > 4) return;
		busNumber = i;
		setChanged();
		notifyObservers("tracktype");
	}

	public int getBusNumber() {
		return busNumber;
	}

	public void setDeviceNumber(int i) {
		if (i < 0 || i > 32) return;
		device = i;
		setChanged();
		notifyObservers("device");
		setChanged();
		notifyObservers("devicename");
	}

	public int getDevice() {
		return device;
	}

	public Event getEvent(int i) {
		return events.get(i);
	}

	public void setName(String s) {
		name = s;
		setChanged();
		notifyObservers("tracknumbername");
	}

	public String getName() {
		if (!used) return "(Unused)";
		return name;
	}

	public List<Event> getEvents() {
		return events;
	}

	@Override
	public long getNextTick() {
		if (eventIndex > events.size() - 1 && noteOffs.size() == 0) {
			return MAX_TICK;
		}

		eventAvailable = eventIndex < events.size();

		if (noteOffs.size() != 0) {
			for (NoteEvent no : noteOffs) {
				if (eventAvailable) {
					if (no.getTick() < events.get(eventIndex).getTick()) {
						return no.getTick();
					}
				} else {
					return no.getTick();
				}
			}
		}

		if (eventAvailable) {
			return events.get(eventIndex).getTick();
		} else {
			return MAX_TICK;
		}
	}

	@Override
	public void playNext() {
		if (eventIndex > events.size() - 1 && noteOffs.size() == 0) return;

		multi = sequencer.isRecordingModeMulti();

		delete = sequencer.isRecording() && (trackIndex == sequencer.getActiveTrackIndex() || multi)
				&& (trackIndex < 64);

		if (sequencer.isOverDubbing() && KbMouseController.eraseIsPressed
				&& (trackIndex == sequencer.getActiveTrackIndex() || multi) && trackIndex < 64)
			delete = true;

		for (NoteEvent no : noteOffs) {
			if (eventIndex > events.size() - 1 || no.getTick() < events.get(eventIndex).getTick()) {
				if (!delete) mpc.getEventHandler().handle(no, this);
				noteOffs.remove(no);
				return;
			}
		}

		event = events.get(eventIndex);

		if (event instanceof NoteEvent) {
			((NoteEvent) event).setTrack(trackIndex);
		}

		if (delete) {
			events.remove(event);
			return;
		}

		mpc.getEventHandler().handle(event, this);
		
		if (event instanceof NoteEvent) {
			NoteEvent ne = (NoteEvent) event;
			if (ne.getVelocity() > 0 && ne.getDuration() >= 0) {
				NoteEvent noteOff = ne.getNoteOff();
				noteOff.setDuration(0);
				noteOff.setNote(ne.getNote());
				noteOff.setTrack(ne.getTrack());
				int dur = ne.getDuration();
				if (dur < 1) dur = 1;
				noteOff.setTick(ne.getTick() + dur);
				noteOff.setVelocity(0);
				noteOffs.add(noteOff);
				Collections.sort(noteOffs, comparator);
			}
		}

		if (!(event instanceof NoteEvent && ((NoteEvent) event).getVelocity() == 0)) eventIndex++;
	}

	@Override
	public void off(boolean stop) {
		// eventIndex = 0;
	}

	public boolean isOn() {
		return on;
	}

	public boolean isUsed() {
		return used || events.size() > 0;
	}

	void setEventIndex(int i) {
		if (i < 0 || i > events.size() - 1) return;
		eventIndex = i;
	}

	public List<Event> getEventRange(long startTick, long endTick) {
		tempEvents = new ArrayList<Event>();
		for (Event e : events)
			if (e.getTick() >= startTick && e.getTick() <= endTick) tempEvents.add(e);
		return tempEvents;
	}

	public void correctTimeRange(long startPos, long endPos, int stepLength) {
		if (sequencer == null) sequencer = mpc.getSequencer();
		MpcSequence s = sequencer.getActiveSequence();
		long accumBarLengths = 0;
		int fromBar = 0;
		int toBar = 0;

		for (int i = 0; i < 999; i++) {
			accumBarLengths += s.getBarLengths()[i];
			if (accumBarLengths >= startPos) {
				fromBar = i;
				break;
			}
		}

		for (int i = 0; i < 999; i++) {
			accumBarLengths += s.getBarLengths()[i];
			if (accumBarLengths > endPos) {
				toBar = i;
				break;
			}
		}

		for (Event event : events) {
			if (event instanceof NoteEvent) {
				if (event.getTick() >= endPos) break;

				if (event.getTick() >= startPos && event.getTick() < endPos)
					timingCorrect(fromBar, toBar, (NoteEvent) event, stepLength);
			}
		}
		removeDoubles();
		sortEvents();
	}

	private void timingCorrect(int fromBar, int toBar, NoteEvent noteEvent, int stepLength) {
		long accumBarLengths = 0;
		long previousAccumBarLengths = 0;
		int barNumber = 0;
		int numberOfSteps = 0;

		MpcSequence s = sequencer.getActiveSequence();

		long segmentStart = 0;
		long segmentEnd = 0;

		for (int i = 0; i < 999; i++) {

			if (i < fromBar) segmentStart += s.getBarLengths()[i];

			if (i <= toBar) {
				segmentEnd += s.getBarLengths()[i];
			} else {
				break;
			}
		}

		for (int i = 0; i < 999; i++) {
			accumBarLengths += s.getBarLengths()[i];
			if (noteEvent.getTick() < accumBarLengths && noteEvent.getTick() >= previousAccumBarLengths) {
				barNumber = i;
				break;
			}

			previousAccumBarLengths = accumBarLengths;
		}

		for (int i = 1; i < 1000; i++) {
			if (s.getBarLengths()[barNumber] - (i * stepLength) < 0) {
				numberOfSteps = i - 1;
				break;
			}
		}

		long currentBarStart = 0;
		for (int i = 0; i < barNumber; i++)
			currentBarStart += s.getBarLengths()[i];

		for (int i = 0; i <= numberOfSteps; i++) {
			long stepStart = ((i - 1) * stepLength) + (stepLength / 2);
			long stepEnd = (i * stepLength) + (stepLength / 2);

			if (noteEvent.getTick() - currentBarStart >= stepStart
					&& noteEvent.getTick() - currentBarStart <= stepEnd) {

				noteEvent.setTick((i * stepLength) + currentBarStart);

				if (noteEvent.getTick() >= segmentEnd) noteEvent.setTick(segmentStart);
				break;
			}
		}
	}

	public void removeDoubles() {
		int eventCounter = 0;
		List<Integer> deleteIndexList = new ArrayList<Integer>();

		List<Integer> notesAtTick = new ArrayList<Integer>();
		long lastTick = -100;
		for (Event e : events) {
			if (e instanceof NoteEvent) {
				if (lastTick != e.getTick()) notesAtTick = new ArrayList<Integer>();
				if (!notesAtTick.contains((Integer) ((NoteEvent) e).getNote())) {
					notesAtTick.add((Integer) ((NoteEvent) e).getNote());
				} else {
					deleteIndexList.add(eventCounter);
				}
				lastTick = e.getTick();
			}
			eventCounter++;
		}

		Collections.reverse(deleteIndexList);

		for (Integer i : deleteIndexList)
			events.remove(i.intValue());
	}

	public void sortEvents() {
		Collections.sort(events, comparator);
	}

	public List<NoteEvent> getNoteEvents() {
		List<NoteEvent> noteEvents = new ArrayList<NoteEvent>();
		for (Event e : events)
			if (e instanceof NoteEvent) noteEvents.add((NoteEvent) e);
		return noteEvents;
	}

	private List<NoteEvent> getNoteEventsAtTick(long tick) {
		List<NoteEvent> noteEvents = new ArrayList<NoteEvent>();
		for (NoteEvent ne : getNoteEvents())
			if (ne.getTick() == tick) noteEvents.add(ne);
		return noteEvents;
	}

	public void sortEventsByNotePerTick() {
		for (NoteEvent ne : getNoteEvents())
			sortEventsOfTickByNote(getNoteEventsAtTick(ne.getTick()));
	}

	private void sortEventsOfTickByNote(List<NoteEvent> noteEvents) {
		Collections.sort(noteEvents, noteComparator);
	}

	public void setEvents(List<Event> l) {
		events = l;
	}

	public void swing(int noteValue, int percentage, int[] noteRange, List<Event> list) {
		if (noteValue != 1 && noteValue != 3) return;
		int base = 48;
		if (noteValue == 3) base = 24;
		for (Event e : list) {
			if (e instanceof NoteEvent) {
				if ((e.getTick() + base) % (base * 2) == 0) {
					if (((NoteEvent) e).getNote() >= noteRange[0] && ((NoteEvent) e).getNote() <= noteRange[1]) {
						e.setTick((long) (e.getTick() + ((percentage - 50) * (4.0 / 100.0) * (base / 2.0))));
					}
				}
			}
		}
		events = list;
	}

	public void shiftTiming(boolean later, int amount, long lastTick, List<Event> list) {
		if (!later) amount *= -1;
		for (Event e : list) {
			e.setTick(e.getTick() + amount);
			if (e.getTick() < 0) e.setTick(0);
			if (e.getTick() > lastTick) e.setTick(lastTick);
		}
		events = list;
	}

	int getEventIndex() {
		return eventIndex;
	}

	public String getActualName() {
		return name;
	}
}
