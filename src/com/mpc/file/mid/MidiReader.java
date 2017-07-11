package com.mpc.file.mid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ChannelAftertouch;
import com.leff.midi.event.Controller;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteAftertouch;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.PitchBend;
import com.leff.midi.event.ProgramChange;
import com.leff.midi.event.SystemExclusiveEvent;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TimeSignature;
import com.leff.midi.event.meta.TrackName;
import com.mpc.Mpc;
import com.mpc.disk.MpcFile;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PitchBendEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.TempoChangeEvent;

public class MidiReader {

	private MidiFile midiFile;
	private MpcSequence mpcSequence;
	private Mpc mpc;

	public MidiReader(Mpc mpc, MpcFile file, MpcSequence mpcSequence) {

		this.mpc = mpc;

		this.midiFile = null;

		this.mpcSequence = mpcSequence;
		mpcSequence.setUsed(true);
		try {
			midiFile = new MidiFile(file.getFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public MpcSequence getSequence() throws UnsupportedEncodingException {

		List<MidiTrack> midiTracks = midiFile.getTracks();

		int lengthInTicks = (int) (midiFile.getLengthInTicks() + midiTracks.get(0).getEndOfTrackDelta());

		List<TimeSignature> timeSignatures = new ArrayList<TimeSignature>();
		List<Tempo> tempoChanges = new ArrayList<Tempo>();

		int firstLoopBar = -1;
		int lastLoopBar = -1;

		for (MidiEvent me : midiTracks.get(0).getEvents()) {

			if (me instanceof Text) {
				String text = (((Text) me).getText());
				if (text.contains("LOOP=ON")) mpcSequence.setLoopEnabled(true);
				if (text.contains("LOOP=OFF")) mpcSequence.setLoopEnabled(false);
				if (text.contains("TEMPO=ON")) mpc.getSequencer().setTempoSourceSequence(true);
				if (text.contains("TEMPO=OFF")) mpc.getSequencer().setTempoSourceSequence(false);
				firstLoopBar = Integer.parseInt(text.substring(15, 18));
				lastLoopBar = -1;
				if (isInteger(text.substring(23, 26))) lastLoopBar = Integer.parseInt(text.substring(23, 26));
			}

			if (me instanceof TrackName) {
				String sequenceName = ((TrackName) me).getTrackName().substring(16);
				mpcSequence.setName(sequenceName);
			}

			if (me instanceof Tempo) tempoChanges.add((Tempo) me);
			if (me instanceof TimeSignature) timeSignatures.add((TimeSignature) me);
		}

		String initialTempoString = Float.toString(tempoChanges.get(0).getBpm());

		int length = initialTempoString.indexOf(".") + 2;
		BigDecimal initialTempoBd = new BigDecimal(initialTempoString.substring(0, length));

		mpcSequence.getTempoChangeEvents().get(0).setInitialTempo(initialTempoBd);

		if (!mpc.getSequencer().isTempoSourceSequence()) mpc.getSequencer().setTempo(initialTempoBd);

		for (int i = 1; i < tempoChanges.size(); i++) {

			TempoChangeEvent tce = new TempoChangeEvent(initialTempoBd, mpcSequence);

			float ratio = (float) (tempoChanges.get(i).getBpm() / initialTempoBd.floatValue());

			tce.setRatio((int) (ratio * 1000.0));
			tce.setStepNumber(i);
			tce.setTick(tempoChanges.get(i).getTick());
			mpcSequence.getTempoChangeEvents().add(tce);
		}

		if (timeSignatures.size() == 1) lengthInTicks = (int) midiTracks.get(0).getEndOfTrackDelta();

		int accumLength = 0;
		int barCounter = 0;

		for (int i = 0; i < timeSignatures.size(); i++) {
			TimeSignature current = timeSignatures.get(i);

			TimeSignature next = null;

			if (timeSignatures.size() > i + 1) next = timeSignatures.get(i + 1);

			if (next != null) {

				while (accumLength < next.getTick()) {

					mpcSequence.setTimeSignature(barCounter, barCounter, current.getNumerator(),
							current.getRealDenominator());

					accumLength += mpcSequence.getBarLengths()[barCounter++];
				}
			} else {

				while (accumLength < lengthInTicks) {

					mpcSequence.setTimeSignature(barCounter, barCounter, current.getNumerator(),
							current.getRealDenominator());

					accumLength += mpcSequence.getBarLengths()[barCounter++];
				}
			}
		}

		System.out.println("Barcounter: " + barCounter);
		mpcSequence.setLastBar(barCounter - 1);
		mpcSequence.setFirstLoopBar(firstLoopBar);

		if (lastLoopBar == -1) {
			mpcSequence.setLastLoopBar(mpcSequence.getLastBar());
			mpcSequence.setLastLoopBar(mpcSequence.getLastLoopBar() + 1);
		} else {
			mpcSequence.setLastLoopBar(lastLoopBar);
		}

		NoteEvent nVariation = null;

		for (int i = 1; i < midiTracks.size(); i++) {

			MidiTrack mt = midiTracks.get(i);

			List<NoteOn> noteOffs = new ArrayList<NoteOn>();
			List<NoteEvent> noteOns = new ArrayList<NoteEvent>();

			MpcTrack track = new MpcTrack(mpc, i - 1);
			track.setUsed(true);

			for (MidiEvent me : mt.getEvents()) {

				if (me instanceof NoteOff) { // MPC2000XL encodes its Note
												// Variation attributes in the
												// form of NoteOffs

					nVariation = new NoteEvent();
					nVariation.setVariationTypeNumber(((NoteOff) me).getNoteValue());
					nVariation.setVariationValue(((NoteOff) me).getVelocity());

				}

				if (me instanceof NoteOn) {

					if (((NoteOn) me).getVelocity() == 0) {
						if (getNumberOfNotes(((NoteOn) me).getNoteValue(),
								noteOns) > getNumberOfNoteOns(((NoteOn) me).getNoteValue(), noteOffs)) {
							noteOffs.add((NoteOn) me);
						}

					} else {

						if (getNumberOfNotes(((NoteOn) me).getNoteValue(),
								noteOns) > getNumberOfNoteOns(((NoteOn) me).getNoteValue(), noteOffs)) {

							NoteOn noteOff = new NoteOn(((NoteOn) me).getTick(), 0, (((NoteOn) me).getNoteValue()), 0);
							noteOffs.add(noteOff);
						}

						NoteEvent ne = new NoteEvent(((NoteOn) me).getNoteValue());

						ne.setTick(((NoteOn) me).getTick());
						ne.setVelocity(((NoteOn) me).getVelocity());

						if (nVariation != null) {
							ne.setVariationTypeNumber(nVariation.getVariationTypeNumber());
							ne.setVariationValue(nVariation.getVariationValue());
							nVariation = null;
						} else {
							ne.setVariationValue(64);
						}

						noteOns.add(ne);
					}
				}
			}

			for (NoteEvent noteOn : noteOns) {

				int indexCandidate = -1;
				long tickCandidate = 999999999;

				for (int k = 0; k < noteOffs.size(); k++) {

					NoteOn noteOff = noteOffs.get(k);

					if (noteOff.getNoteValue() == noteOn.getNote() && noteOff.getTick() >= noteOn.getTick()) {

						if (noteOff.getTick() < tickCandidate) {

							tickCandidate = noteOff.getTick();
							indexCandidate = k;
							break;

						}
					}
				}

				if (indexCandidate != -1) {
					noteOn.setDuration((int) (noteOffs.get(indexCandidate).getTick() - noteOn.getTick()));
					noteOffs.remove(indexCandidate);

				} else {
					noteOn.setDuration(24);
				}
				track.addEvent(noteOn);

			}

			for (MidiEvent me : mt.getEvents()) {

				if (me instanceof SystemExclusiveEvent) {

					SystemExclusiveEvent sysEx = (SystemExclusiveEvent) me;
					byte[] sysExData = sysEx.getData();
					System.out.println("sysexdata length " + sysExData.length);
					System.out.println("sysex byte " + (int) (sysExData[0] & 0xFF));
					if (sysExData.length == 8 && sysExData[0] == (byte) 71 && sysExData[1] == (byte) 0
							&& sysExData[2] == (byte) 68 && sysExData[3] == (byte) 69 && sysExData[7] == (byte) 247) {

						MixerEvent mixerEvent = new MixerEvent();

						mixerEvent.setTick(sysEx.getTick());
						mixerEvent.setParameter((int) (sysExData[4]) - 1);
						mixerEvent.setPadNumber((int) sysExData[5]);
						mixerEvent.setValue((int) sysExData[6]);

						track.addEvent(mixerEvent);

					} else {
						sysExData = new byte[sysEx.getData().length + 1];
						sysExData[0] = (byte) 240;
						for (int j = 0; j < sysEx.getData().length; j++)
							sysExData[j + 1] = sysEx.getData()[j];

						com.mpc.sequencer.SystemExclusiveEvent see = new com.mpc.sequencer.SystemExclusiveEvent();
						see.setTick(sysEx.getTick());
						see.setBytes(sysExData);
						track.addEvent(see);
					}
				}

				if (me instanceof NoteAftertouch) {

					NoteAftertouch na = (NoteAftertouch) me;
					PolyPressureEvent ppe = new PolyPressureEvent();

					ppe.setTick(na.getTick());
					ppe.setNote(na.getNoteValue());
					ppe.setAmount(na.getAmount());

					track.addEvent(ppe);
				}

				if (me instanceof ChannelAftertouch) {

					ChannelAftertouch ca = (ChannelAftertouch) me;
					ChannelPressureEvent cpe = new ChannelPressureEvent();

					cpe.setTick(ca.getTick());
					cpe.setAmount(ca.getAmount());

					track.addEvent(cpe);
				}

				if (me instanceof ProgramChange) {

					ProgramChange programChange = (ProgramChange) me;
					ProgramChangeEvent pce = new ProgramChangeEvent();

					pce.setTick(programChange.getTick());
					pce.setProgram(programChange.getProgramNumber() + 1);

					track.addEvent(pce);
				}

				if (me instanceof TrackName) track.setName(((TrackName) me).getTrackName());

				if (me instanceof Controller) {

					Controller controller = (Controller) me;
					ControlChangeEvent cce = new ControlChangeEvent();

					cce.setController(controller.getControllerType());
					cce.setAmount(controller.getValue());
					cce.setTick(controller.getTick());

					track.addEvent(cce);
				}

				if (me instanceof PitchBend) {

					PitchBend pb = (PitchBend) me;
					PitchBendEvent pbe = new PitchBendEvent();

					pbe.setAmount(pb.getBendAmount() - 8192);

					track.addEvent(pbe);

				}
			}

			mpcSequence.getMpcTracks().set(i - 1, track);
		}

		mpcSequence.initMetaTracks();
		return mpcSequence;
	}

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private int getNumberOfNoteOns(int noteValue, List<NoteOn> allNotes) {
		List<NoteOn> oneNote = new ArrayList<NoteOn>();
		for (NoteOn no : allNotes)
			if (no.getNoteValue() == noteValue) oneNote.add(no);
		return oneNote.size();
	}

	private int getNumberOfNotes(int noteValue, List<NoteEvent> allNotes) {
		List<NoteEvent> oneNote = new ArrayList<NoteEvent>();
		for (NoteEvent ne : allNotes)
			if (ne.getNote() == noteValue) oneNote.add(ne);
		return oneNote.size();
	}
}