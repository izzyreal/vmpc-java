package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.EmptyEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PitchBendEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.SystemExclusiveEvent;
import com.mpc.sequencer.TimeSignature;

public class StepEditorObserver implements Observer {

	private String[] viewNames = { "ALL EVENTS", "NOTES", "PITCH BEND", "CTRL:", "PROG CHANGE", "CH PRESSURE",
			"POLY PRESS", "EXCLUSIVE" };

	private MainFrame mainFrame;

	private List<Event> events;
	private Event[] visibleEvents;
	private Sequencer sequencer;
	private int trackNum;
	private int seqNum;

	private MpcSequence sequence;
	private MpcTrack track;
	private TimeSignature timeSig;

	private JTextField viewmodenumberField;
	private JTextField controlnumberField;
	private JTextField fromnoteField;
	private JTextField tonoteField;
	private JTextField barnumberField;
	private JTextField beatnumberField;
	private JTextField clocknumberField;

	private JLabel controlnumberLabel;
	private JLabel fromnoteLabel;
	private JLabel tonoteLabel;

	private Sampler sampler;

	private Gui gui;

	private StepEditorGui stepEditorGui;
	private List<Event> eventsAtCurrentTick;
	private List<EventRow> eventRows;
	private Program program;
	private LayeredScreen slp;

	public StepEditorObserver(Mpc mpc) throws UnsupportedEncodingException {

		gui = Bootstrap.getGui();

		stepEditorGui = gui.getStepEditorGui();
		stepEditorGui.deleteObservers();
		stepEditorGui.addObserver(this);

		this.mainFrame = gui.getMainFrame();
		this.slp = mainFrame.getLayeredScreen();

		sequencer = mpc.getSequencer();
		sampler = mpc.getSampler();

		seqNum = sequencer.getActiveSequenceIndex();
		sequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) sequence.getTrack(trackNum);

		timeSig = sequence.getTimeSignature();

		events = track.getEvents();

		viewmodenumberField = mainFrame.lookupTextField("viewmodenumber");
		controlnumberField = mainFrame.lookupTextField("controlnumber");
		fromnoteField = mainFrame.lookupTextField("fromnote");
		tonoteField = mainFrame.lookupTextField("tonote");
		barnumberField = mainFrame.lookupTextField("barnumber");
		beatnumberField = mainFrame.lookupTextField("beatnumber");
		clocknumberField = mainFrame.lookupTextField("clocknumber");

		controlnumberField.setVisible(false);
		fromnoteField.setVisible(false);
		tonoteField.setVisible(false);

		controlnumberLabel = mainFrame.lookupLabel("controlnumber");
		fromnoteLabel = mainFrame.lookupLabel("fromnote");
		tonoteLabel = mainFrame.lookupLabel("tonote");

		controlnumberLabel.setVisible(false);

		refreshViewModeNotes();
		setViewModeNotesText();

		sequencer.deleteObservers();
		sequencer.addObserver(this);
		sequence.deleteObservers();
		sequence.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);
		timeSig.deleteObservers();
		timeSig.addObserver(this);

		viewmodenumberField.setText("" + viewNames[stepEditorGui.getViewModeNumber()]);

		barnumberField.setText(String.format("%03d", sequencer.getCurrentBarNumber() + 1));
		beatnumberField.setText(String.format("%02d", sequencer.getCurrentBeatNumber() + 1));
		clocknumberField.setText(String.format("%02d", sequencer.getCurrentClockNumber()));

		initVisibleEvents();

		eventRows = new ArrayList<EventRow>();
		for (int i = 0; i < 4; i++) {
			EventRow eventRow = new EventRow(mpc, visibleEvents[i], i);
			if (track.getBusNumber() == 0) eventRow.setMidi(true);
			eventRow.init();
			eventRows.add(eventRow);
			for (JComponent jc : eventRow.getEventRow()) {
				jc.setVisible(false);
			}
		}
		refreshEventRows();
	}

	@Override
	public void update(Observable o, Object arg) {
		track.deleteObservers();
		sequence.deleteObservers();
		timeSig.deleteObservers();

		seqNum = sequencer.getActiveSequenceIndex();
		sequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) sequence.getTrack(trackNum);

		events = track.getEvents();

		timeSig = sequence.getTimeSignature();
		track.addObserver(this);
		sequence.addObserver(this);
		timeSig.addObserver(this);

		switch ((String) arg) {

		case "viewmodestext":
			setViewModeNotesText();
		case "stepviewmodenumber":
			viewmodenumberField.setText("" + viewNames[stepEditorGui.getViewModeNumber()]);
			refreshViewModeNotes();
			setViewModeNotesText();
			break;
		case "stepeditor":
			if (slp.getCurrentScreenName().equals("sequencer_step")) {
				String focus = mainFrame.getFocus(slp.getMainPanel());
				int eventNumber = 0;
				try {
					eventNumber = Integer.parseInt(focus.substring(1, 2));
				} catch (NumberFormatException e) {
					// discard error;
				}
				if (visibleEvents[eventNumber] instanceof NoteEvent) {
					if (track.getBusNumber() != 0) {
						eventRows.get(eventNumber).setDrumNoteEventValues();
					}
					if (track.getBusNumber() == 0) {
						eventRows.get(eventNumber).setMidiNoteEventValues();
					}
				}

				if (visibleEvents[eventNumber] instanceof MixerEvent) {
					eventRows.get(eventNumber).setMixerEventValues();
				}

				if (visibleEvents[eventNumber] instanceof PitchBendEvent
						|| visibleEvents[eventNumber] instanceof ProgramChangeEvent) {
					eventRows.get(eventNumber).setMiscEventValues();
				}

				if (visibleEvents[eventNumber] instanceof ControlChangeEvent) {
					eventRows.get(eventNumber).setControlChangeEventValues();
				}

				if (visibleEvents[eventNumber] instanceof ChannelPressureEvent) {
					eventRows.get(eventNumber).setChannelPressureEventValues();
				}

				if (visibleEvents[eventNumber] instanceof PolyPressureEvent) {
					eventRows.get(eventNumber).setPolyPressureEventValues();
				}

				if (visibleEvents[eventNumber] instanceof SystemExclusiveEvent) {
					eventRows.get(eventNumber).setSystemExclusiveEventValues();
				}

				if (visibleEvents[eventNumber] instanceof EmptyEvent) {
					eventRows.get(eventNumber).setEmptyEventValues();
				}

				slp.getMainPanel().repaint();
			}
			break;
		case "resetstepeditor":
			initVisibleEvents();
			refreshEventRows();
			refreshSelection();
			break;
		case "bar":
			barnumberField.setText(String.format("%03d", sequencer.getCurrentBarNumber() + 1));
			stepEditorGui.setyOffset(0);
			break;
		case "beat":
			beatnumberField.setText(String.format("%02d", sequencer.getCurrentBeatNumber() + 1));
			stepEditorGui.setyOffset(0);
			break;
		case "clock":
			clocknumberField.setText(String.format("%02d", sequencer.getCurrentClockNumber()));
			stepEditorGui.setyOffset(0);
			break;
		case "selection":
			refreshSelection();
			break;
		case "selectionstart":
			slp.drawFunctionBoxes("sequencer_step_selection");
			break;
		case "clearselection":
			slp.drawFunctionBoxes("sequencer_step");
			break;
		}
	}

	private void refreshSelection() {
		int firstEventIndex = stepEditorGui.getSelectionStartIndex();
		int lastEventIndex = stepEditorGui.getSelectionEndIndex();

		if (firstEventIndex != -1) {
			if (firstEventIndex > lastEventIndex) {
				firstEventIndex = stepEditorGui.getSelectionEndIndex();
				lastEventIndex = stepEditorGui.getSelectionStartIndex();
			}
			for (int i = 0; i < 4; i++) {
				EventRow eventRow = eventRows.get(i);
				int absoluteEventNumber = i + stepEditorGui.getyOffset();
				if (absoluteEventNumber >= firstEventIndex && absoluteEventNumber < lastEventIndex + 1) {
					eventRow.setSelected(true);
				} else {
					eventRow.setSelected(false);
				}
			}
		} else {
			for (int i = 0; i < 4; i++) {
				EventRow eventRow = eventRows.get(i);
				eventRow.setSelected(false);
			}
		}
		slp.repaint();
	}

	private void initVisibleEvents() {
		visibleEvents = new Event[4];

		for (Event e : visibleEvents) {
			if (e != null) e.deleteObservers();
		}

		eventsAtCurrentTick = new ArrayList<Event>();

		for (Event event : events) {

			if (event.getTick() == sequencer.getTickPosition()) {

				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 1)
						&& event instanceof NoteEvent) {

					if (track.getBusNumber() != 0) {

						if (stepEditorGui.getFromNotePad() == 34) eventsAtCurrentTick.add(event);

						if (stepEditorGui.getFromNotePad() != 34
								&& stepEditorGui.getFromNotePad() == ((NoteEvent) event).getNote())
							eventsAtCurrentTick.add(event);
					}

					if (track.getBusNumber() == 0) {
						if (((NoteEvent) event).getNote() >= stepEditorGui.getNoteA()
								&& ((NoteEvent) event).getNote() <= stepEditorGui.getNoteB())
							eventsAtCurrentTick.add(event);
					}

				}
				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 2)
						&& event instanceof PitchBendEvent)
					eventsAtCurrentTick.add(event);
				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 3)
						&& event instanceof ControlChangeEvent) {
					if (stepEditorGui.getControlNumber() == -1) {
						eventsAtCurrentTick.add(event);
					}
					if (stepEditorGui.getControlNumber() == ((ControlChangeEvent) event).getController()) {
						eventsAtCurrentTick.add(event);
					}
				}
				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 4)
						&& event instanceof ProgramChangeEvent)
					eventsAtCurrentTick.add(event);
				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 5)
						&& event instanceof ChannelPressureEvent)
					eventsAtCurrentTick.add(event);
				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 6)
						&& event instanceof PolyPressureEvent)
					eventsAtCurrentTick.add(event);
				if ((stepEditorGui.getViewModeNumber() == 0 || stepEditorGui.getViewModeNumber() == 7)
						&& (event instanceof SystemExclusiveEvent || event instanceof MixerEvent))
					eventsAtCurrentTick.add(event);
			}
		}
		eventsAtCurrentTick.add(new EmptyEvent());
		stepEditorGui.setEventsAtCurrentTick(eventsAtCurrentTick);

		visibleEvents = new Event[4];
		int visibleEventCounter = 0;
		int firstVisibleEventIndex = stepEditorGui.getyOffset();
		if (eventsAtCurrentTick.size() != 0) {
			for (int i = 0; i < 4; i++) {
				visibleEvents[visibleEventCounter] = eventsAtCurrentTick.get(i + firstVisibleEventIndex);
				visibleEvents[visibleEventCounter].addObserver(this);
				visibleEventCounter++;
				if (visibleEventCounter > 3 || visibleEventCounter > eventsAtCurrentTick.size() - 1) break;
			}
		}
		stepEditorGui.setVisibleEvents(visibleEvents);
	}

	public void refreshEventRows() {
		for (int i = 0; i < 4; i++) {
			EventRow eventRow = eventRows.get(i);
			eventRow.setEvent(visibleEvents[i]);
			eventRow.init();
			if (visibleEvents[i] == null) {
				for (JComponent jc : eventRow.getEventRow()) {
					jc.setVisible(false);
				}
			}
		}
	}

	public void refreshViewModeNotes() {
		if (stepEditorGui.getViewModeNumber() == 1 && track.getBusNumber() != 0) {
			fromnoteLabel.setVisible(true);
			fromnoteField.setVisible(true);
			fromnoteField.setLocation(134, 0);
			tonoteLabel.setVisible(false);
			tonoteField.setVisible(false);
		}

		if (stepEditorGui.getViewModeNumber() == 1 && track.getBusNumber() == 0) {
			fromnoteLabel.setVisible(true);
			fromnoteField.setVisible(true);
			fromnoteField.setLocation(120, 0);
			fromnoteField.setSize(8 * 6 * 2, 18);

			tonoteField.setSize(8 * 6 * 2, 18);
			tonoteLabel.setVisible(true);
			tonoteField.setVisible(true);
			controlnumberField.setVisible(false);
		}

		if (stepEditorGui.getViewModeNumber() == 3) {
			fromnoteLabel.setVisible(false);
			fromnoteField.setVisible(false);
			tonoteLabel.setVisible(false);
			tonoteField.setVisible(false);
			controlnumberField.setVisible(true);
		}

		if (stepEditorGui.getViewModeNumber() != 1 && stepEditorGui.getViewModeNumber() != 3) {
			fromnoteLabel.setVisible(false);
			fromnoteField.setVisible(false);
			tonoteLabel.setVisible(false);
			tonoteField.setVisible(false);
			controlnumberField.setVisible(false);
		}
	}

	public void setViewModeNotesText() {

		if (stepEditorGui.getViewModeNumber() == 1 && track.getBusNumber() != 0) {

			if (stepEditorGui.getFromNotePad() != 34) {

				fromnoteField.setText("" + stepEditorGui.getFromNotePad() + "/"
						+ sampler.getPadName(program.getPadNumberFromNote(stepEditorGui.getFromNotePad() - 35)));

			} else {

				fromnoteField.setText("ALL");

			}
		}

		if (stepEditorGui.getViewModeNumber() == 1 && track.getBusNumber() == 0) {

			fromnoteField.setText(Util.padLeftSpace("" + stepEditorGui.getNoteA(), 3) + "("
					+ Gui.noteNames[stepEditorGui.getNoteA()] + "\u00D4");

			tonoteField.setText(Util.padLeftSpace("" + stepEditorGui.getNoteB(), 3) + "("
					+ Gui.noteNames[stepEditorGui.getNoteB()] + "\u00D4");
		}

		if (stepEditorGui.getViewModeNumber() == 3) {

			if (stepEditorGui.getControlNumber() == -1) controlnumberField.setText("   -    ALL");

			if (stepEditorGui.getControlNumber() != -1)
				controlnumberField.setText(EventRow.controlNames[stepEditorGui.getControlNumber()]);

		}

		viewmodenumberField.setText("" + viewNames[stepEditorGui.getViewModeNumber()]);

		viewmodenumberField.setSize(viewmodenumberField.getText().length() * 6 * 2 + 2, 18);

	}
}