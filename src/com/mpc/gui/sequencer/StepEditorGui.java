package com.mpc.gui.sequencer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.mpc.gui.MainFrame;
import com.mpc.sequencer.EmptyEvent;
import com.mpc.sequencer.Event;

public class StepEditorGui extends Observable {

	private MainFrame mainFrame;

	private int viewModeNumber = 0; /* 0 = ALL EVENTS */
	/* 1 = NOTES */
	/* 2 = PITCH BEND */
	/* 3 = CTRL */
	/* 4 = PROG CHANGE */
	/* 5 = CH PRESSURE */
	/* 6 = POLY PRESS */
	/* 7 = EXCLUSIVE */
	private int noteA = 0;
	/* 0-127 = note n */
	private int noteB = 127;
	/* 0-127 = note n */
	private int controlNumber = -1; /* -1 = ALL */
	/* 0-127 = ctrl n */
	private boolean autoStepIncrementEnabled = false; /* NO / YES */
	private boolean durationOfRecordedNotes = false; /* 0 = AS PLAYED, 1 = TC VALUE PERCENTAGE */
	/* 1 = TC VALUE */
	private int tcValueRecordedNotes = 100; /* 0 - 100 % */
	private int yOffset = 0; /*
							 * Keeps track of the vertical offset of the event
							 * list
							 */
	private int selectedEventNumber = 0; /*
										 * Selected event out of the 4 listed in
										 * step editor
										 */

	private int insertEventType = 0; /*
									 * Selected event type upon inserting in
									 * step editor
									 */
	private int editTypeNumber = 0;
	private int changeNoteToNumber = 35;
	private int changeVariationTypeNumber = 0;
	private int changeVariationValue = 0;
	private int editValue = 0;

	private Event[] visibleEvents;
	private List<Event> eventsAtCurrentTick;
	private int fromNotePad = 34;

	private int selectionStartIndex = -1;
	private int selectionEndIndex = -1;

	private List<Event> placeHolder;
	private Event selectedEvent;
	private String selectedParameterLetter;
	private List<Event> selectedEvents;

	private boolean durationTcPercentageEnabled;

	public StepEditorGui(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public int getInsertEventType() {
		return insertEventType;
	}

	public void setInsertEventType(int i) {
		if (i < 0 || i > 7)
			return;
		insertEventType = i;
		setChanged();
		notifyObservers("eventtype");
	}

	public int getViewModeNumber() {
		return viewModeNumber;
	}

	public void setViewModeNumber(int i) {
		if (i < 0 || i > 7)
			return;
		this.viewModeNumber = i;
		setChanged();
		notifyObservers("stepviewmodenumber");
		setyOffset(0);
	}

	public int getNoteA() {
		return noteA;
	}

	public void setNoteA(int i) {
		if (i < 0 || i > 127)
			return;
		this.noteA = i;
		if (i > noteB)
			setNoteB(i);
		setChanged();
		notifyObservers("viewmodestext");
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public int getNoteB() {
		return noteB;
	}

	public void setNoteB(int i) {
		if (i < 0 || i > 127)
			return;
		this.noteB = i;
		if (i < noteA)
			setNoteA(i);
		setChanged();
		notifyObservers("viewmodestext");
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public int getControlNumber() {
		return controlNumber;
	}

	public void setControlNumber(int i) {
		if (i < -1 || i > 127)
			return;
		this.controlNumber = i;
		setChanged();
		notifyObservers("viewmodestext");
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public boolean isAutoStepIncrementEnabled() {
		return autoStepIncrementEnabled;
	}

	public void setAutoStepIncrementEnabled(boolean b) {
		this.autoStepIncrementEnabled = b;
		setChanged();
		notifyObservers("autostepincrement");
	}

	public boolean getDurationOfRecordedNotes() {
		return durationOfRecordedNotes;
	}

	public void setDurationOfRecordedNotes(boolean b) {
		this.durationOfRecordedNotes = b;
		setChanged();
		notifyObservers("durationofrecordednotes");
	}

	public int getTcValueRecordedNotes() {
		return tcValueRecordedNotes;
	}

	public void setTcValueRecordedNotes(int i) {
		if (i < 0 || i > 100)
			return;
		this.tcValueRecordedNotes = i;
		setChanged();
		notifyObservers("tcvaluerecordednotes");
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int i) {
		if (i < 0)
			return;
		this.yOffset = i;
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public int getSelectedEventNumber() {
		return selectedEventNumber;
	}

	public void setSelectedEventNumber(int i) {
		this.selectedEventNumber = i;
	}

	public void setVisibleEvents(Event[] e) {
		visibleEvents = e;
	}

	public Event[] getVisibleEvents() {
		return visibleEvents;
	}

	public void setEventsAtCurrentTick(List<Event> l) {
		eventsAtCurrentTick = l;
	}

	public List<Event> getEventsAtCurrentTick() {
		return eventsAtCurrentTick;
	}

	public void setFromNotePad(int i) {
		
		if (i < 34 || i > 98)
			return;
		
		fromNotePad = i;
		setChanged();
		notifyObservers("viewmodestext");
		setChanged();
		notifyObservers("resetstepeditor");
	}

	public int getFromNotePad() {
		return fromNotePad;
	}

	public void setSelectionStartIndex(int i) {
		
		if (eventsAtCurrentTick.get(i) instanceof EmptyEvent)
			return;
		
		selectionStartIndex = i;
		selectionEndIndex = i;
		setChanged();
		notifyObservers("selectionstart");
		setChanged();
		notifyObservers("selection");
	}

	public void clearSelection() {
		selectionStartIndex = -1;
		selectionEndIndex = -1;
		setChanged();
		notifyObservers("clearselection");
		setChanged();
		notifyObservers("selection");
	}

	public int getSelectionStartIndex() {
		return selectionStartIndex;
	}

	public void setSelectionEndIndex(int i) {
		
		if (i == -1)
			return;
		
		if (eventsAtCurrentTick.get(i) instanceof EmptyEvent)
			return;
		
		selectionEndIndex = i;
		setChanged();
		notifyObservers("selection");
	}

	public int getSelectionEndIndex() {
		return selectionEndIndex;
	}

	public void setSelectedEvents() {
		
		selectedEvents = new ArrayList<Event>();
		
		int firstEventIndex = selectionStartIndex;
		int lastEventIndex = selectionEndIndex;

		if (firstEventIndex > lastEventIndex) {
			firstEventIndex = selectionEndIndex;
			lastEventIndex = selectionStartIndex;
		}

		for (int i = firstEventIndex; i < lastEventIndex + 1; i++)
			selectedEvents.add(eventsAtCurrentTick.get(i));
	}

	public List<Event> getSelectedEvents() {
		return selectedEvents;
	}

	public void setPlaceHolder(List<Event> l) {
		placeHolder = l;
	}

	public List<Event> getPlaceHolder() {
		return placeHolder;
	}

	public void checkSelection() {
		
		String focus = mainFrame.getFocus(mainFrame.getLayeredScreen()
				.getMainPanel());

		if (focus != null && focus.length() == 2) {
			int eventNumber = Integer.parseInt(focus.substring(1, 2));

			int visibleEventCounter = 0;
			int firstSelectedVisibleEventIndex = -1;
			int selectedEventCounter = 0;

			for (SelectedEventBar seb : mainFrame.getLayeredScreen()
					.getSelectedEventBarsStepEditor()) {
				if (seb.isVisible()) {
					if (firstSelectedVisibleEventIndex == -1)
						firstSelectedVisibleEventIndex = visibleEventCounter;
					selectedEventCounter++;
				}
				visibleEventCounter++;
			}

			if (firstSelectedVisibleEventIndex != -1) {
				int lastSelectedVisibleEventIndex = firstSelectedVisibleEventIndex
						+ selectedEventCounter - 1;

				if (!(visibleEvents[eventNumber] instanceof EmptyEvent)) {
					if (eventNumber < firstSelectedVisibleEventIndex
							|| eventNumber > lastSelectedVisibleEventIndex) {
						clearSelection();
					}
				}
			}
		} else {
			clearSelection();
		}
	}

	public int getEditTypeNumber() {
		return editTypeNumber;
	}

	public void setEditTypeNumber(int se_editTypeNumber) {
		this.editTypeNumber = se_editTypeNumber;
		setChanged();
		notifyObservers("editmultiple");
	}

	public void setSelectedEvent(Event event) {
		selectedEvent = event;
	}

	public Event getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedParameterLetter(String substring) {
		selectedParameterLetter = substring;
	}

	public String getParamLetter() {
		return selectedParameterLetter;
	}

	public int getChangeNoteToNumber() {
		return changeNoteToNumber;
	}

	public void setChangeNoteToNumber(int i) {
		if (i < 0 || i > 127)
			return;
		this.changeNoteToNumber = i;
		setChanged();
		notifyObservers("editmultiple");
	}

	public int getChangeVariationTypeNumber() {
		return changeVariationTypeNumber;
	}

	public void setChangeVariationTypeNumber(int i) {
		if (i < 0 || i > 3)
			return;
		this.changeVariationTypeNumber = i;
		setChanged();
		notifyObservers("editmultiple");
	}

	public int getChangeVariationValue() {
		return changeVariationValue;
	}

	public void setChangeVariationValue(int i) {
		if (i < 0 || i > 128)
			return;
		if (changeVariationTypeNumber != 0 && i > 100)
			i = 100;
		this.changeVariationValue = i;
		setChanged();
		notifyObservers("editmultiple");
	}

	public int getEditValue() {
		return editValue;
	}

	public void setEditValue(int i) {
		if (i < 0 || i > 127)
			return;
		this.editValue = i;
		setChanged();
		notifyObservers("editmultiple");
	}

	public boolean isDurationTcPercentageEnabled() {
		return durationTcPercentageEnabled;
	}
}