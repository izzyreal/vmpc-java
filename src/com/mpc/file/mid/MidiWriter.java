package com.mpc.file.mid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.leff.midi.event.meta.InstrumentName;
import com.leff.midi.event.meta.SmpteOffset;
import com.leff.midi.event.meta.SmpteOffset.FrameRate;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import com.leff.midi.event.meta.TimeSignature;
import com.leff.midi.event.meta.TrackName;
import com.mpc.Util;
import com.mpc.disk.AbstractDisk;
import com.mpc.sequencer.ChannelPressureEvent;
import com.mpc.sequencer.ControlChangeEvent;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.PitchBendEvent;
import com.mpc.sequencer.PolyPressureEvent;
import com.mpc.sequencer.ProgramChangeEvent;
import com.mpc.sequencer.SystemExclusiveEvent;
import com.mpc.sequencer.TempoChangeEvent;

public class MidiWriter {

	private List<NoteOn> noteOffs;
	private List<NoteOff> variations;
	private List<NoteOn> noteOns;
	private List<MidiEvent> miscEvents;
	private MpcSequence mpcSequence;
	private final MidiFile mf;

	public MidiWriter(MpcSequence mpcSequence) {

		this.mpcSequence = mpcSequence;

		mf = new MidiFile();

		MidiTrack meta = new MidiTrack();

		Text seqParams = new Text(0, 0, "LOOP=ON  START=000 END=END TEMPO=ON ");
		meta.insertEvent(seqParams);

		TrackName seqName = new TrackName(0, 0,
				"MPC2000XL 1.00  " + AbstractDisk.padRightSpace(mpcSequence.getName(), 16));
		meta.insertEvent(seqName);
		List<Tempo> tempos = new ArrayList<Tempo>();

		long previousTick = 0;

		for (TempoChangeEvent tce : mpcSequence.getTempoChangeEvents()) {

			float tempo = tce.getTempo().floatValue();

			int mpqn = (int) (60000000.0 / tempo);

			tempos.add(new Tempo(tce.getTick(), tce.getTick() - previousTick, mpqn));

			previousTick = tce.getTick();
		}

		for (Tempo t : tempos)
			meta.insertEvent(t);

		meta.insertEvent(new SmpteOffset(0, 0, FrameRate.FRAME_RATE_25, 0, 0, 0, 0, 0));

		Set<int[]> tSigs = new HashSet<int[]>();

		int tSigTick = 0;
		int[] lastAdded = new int[3];

		for (int i = 0; i < mpcSequence.getLastBar() + 1; i++) {

			int actualTick = tSigTick;

			if (lastAdded[0] == mpcSequence.getNumerator(i) && lastAdded[1] == mpcSequence.getDenominator(i))
				actualTick = lastAdded[2];

			if (tSigs.add(new int[] { mpcSequence.getNumerator(i), mpcSequence.getDenominator(i), actualTick })) {

				lastAdded[0] = mpcSequence.getNumerator(i);
				lastAdded[1] = mpcSequence.getDenominator(i);
				lastAdded[2] = actualTick;

			}

			tSigTick += mpcSequence.getBarLengths()[i];
		}

		previousTick = 0;

		for (int[] ia : tSigs) {

			TimeSignature tSig = new TimeSignature(ia[2], ia[2] - previousTick, ia[0], ia[1], 24, 8);

			meta.insertEvent(tSig);

			previousTick = ia[2];
		}

		meta.setEndOfTrackDelta(mpcSequence.getLastTick());

		mf.addTrack(meta);

		for (MpcTrack t : mpcSequence.getMpcTracks()) {

			MpcTrack mpcTrack = (MpcTrack) t;

			noteOffs = new ArrayList<NoteOn>();
			variations = new ArrayList<NoteOff>();
			noteOns = new ArrayList<NoteOn>();
			miscEvents = new ArrayList<MidiEvent>();

			if (mpcTrack.getTrackIndex() > 63 || !mpcTrack.isUsed()) break;

			MidiTrack mt = new MidiTrack();

			InstrumentName in = new InstrumentName(0, 0, "        ");
			mt.insertEvent(in);
			String trackNumber = Util.padLeft2Zeroes(mpcTrack.getTrackIndex());
			Text text = new Text(0, 0, "TRACK DATA:" + trackNumber + "C0006403  000107   ");
			mt.insertEvent(text);

			TrackName tn = new TrackName(0, 0, AbstractDisk.padRightSpace(mpcTrack.getName(), 16));

			mt.insertEvent(tn);

			for (Event event : mpcTrack.getEvents()) {

				if (event instanceof NoteEvent) {
					NoteEvent e = (NoteEvent) event;
					addNoteOn(new NoteOn(e.getTick(), mpcTrack.getTrackIndex(), e.getNote(), e.getVelocity()));

					noteOffs.add(new NoteOn(e.getTick() + e.getDuration(), mpcTrack.getTrackIndex(), e.getNote(), 0));

					boolean variation = false;

					NoteOff varNoteOff = null;

					int varType = e.getVariationTypeNumber();
					int varVal = e.getVariationValue();

					if (varType == 0 && varVal != 64) variation = true;

					if ((varType == 1 || varType == 2) && varVal != 0) variation = true;

					if (varType == 3 && varVal != 50) variation = true;

					if (variation) varNoteOff = new NoteOff(e.getTick(), mpcTrack.getTrackIndex(), varType, varVal);

					if (varNoteOff != null) variations.add(varNoteOff);
				} else if (event instanceof SystemExclusiveEvent) {
					SystemExclusiveEvent mpcSee = (SystemExclusiveEvent) event;
					byte[] sysExData = new byte[mpcSee.getBytes().length - 1];
					for (int j = 0; j < sysExData.length; j++)
						sysExData[j] = mpcSee.getBytes()[j + 1];
					System.out.println("sysexdata for writing is " + sysExData[0]);
					com.leff.midi.event.SystemExclusiveEvent see = new com.leff.midi.event.SystemExclusiveEvent(240,
							mpcSee.getTick(), sysExData);
					miscEvents.add(see);
				} else if (event instanceof PitchBendEvent) {
					PitchBendEvent mpcPbe = (PitchBendEvent) event;
					byte[] amountBytes = Util.unsignedIntToBytePair(mpcPbe.getAmount() + 8192);
					PitchBend pb = new PitchBend(mpcPbe.getTick(), 1, (int) (amountBytes[0] & 0xFF), (int) (amountBytes[1] & 0xFF));
					pb.setBendAmount(mpcPbe.getAmount()+8192);
					miscEvents.add(pb);
				} else if (event instanceof ChannelPressureEvent) {
					ChannelPressureEvent cpe = (ChannelPressureEvent) event;
					ChannelAftertouch ca = new ChannelAftertouch(cpe.getTick(), 1, cpe.getAmount());
					miscEvents.add(ca);
				} else if (event instanceof PolyPressureEvent) {
					PolyPressureEvent ppe = (PolyPressureEvent) event;
					NoteAftertouch na = new NoteAftertouch(ppe.getTick(), 1, ppe.getNote(), ppe.getAmount());
					miscEvents.add(na);
				} else if (event instanceof ControlChangeEvent) {
					ControlChangeEvent cce = (ControlChangeEvent) event;
					Controller c = new Controller(cce.getTick(), 1, cce.getController(), cce.getAmount());
					miscEvents.add(c);
				} else if (event instanceof ProgramChangeEvent) {
					ProgramChangeEvent pce = (ProgramChangeEvent) event;
					ProgramChange pc = new ProgramChange(pce.getTick(), 1, pce.getProgram());
					miscEvents.add(pc);
				} else if (event instanceof MixerEvent) {
					MixerEvent me = (MixerEvent) event;
					byte[] sysExData = new byte[8];
					sysExData[0] = (byte) 71;
					sysExData[1] = (byte) 0;
					sysExData[2] = (byte) 68;
					sysExData[3] = (byte) 69;
					sysExData[7] = (byte) 247;

					sysExData[4] = (byte) (me.getParameter() + 1);
					sysExData[5] = (byte) me.getPad();
					sysExData[6] = (byte) me.getValue();

					com.leff.midi.event.SystemExclusiveEvent see = new com.leff.midi.event.SystemExclusiveEvent(240, me.getTick(), sysExData);
					miscEvents.add(see);
				}

			}

			for (int i = 0; i < mpcSequence.getLastTick(); i++) {

				for (NoteOn no : noteOffs)
					if (no.getTick() == i) mt.insertEvent(no);

				for (NoteOff var : variations)
					if (var.getTick() == i) mt.insertEvent(var);

				for (NoteOn no : noteOns)
					if (no.getTick() == i) mt.insertEvent(no);

				for (MidiEvent e : miscEvents)
					if (e.getTick() == i) {
						System.out.println("inserting " + e.getClass());
						System.out.println("at tick " + e.getTick());
						mt.insertEvent(e);
					}

			}

			mt = createDeltas(mt);
			mf.addTrack(mt);
		}
		mf.setType(1);
		mf.setResolution(96);
	}

	private void addNoteOn(NoteOn noteOn) {

		for (int i = 0; i < noteOffs.size(); i++) {
			NoteOn no = noteOffs.get(i);

			if (no.getNoteValue() == noteOn.getNoteValue() && no.getTick() > noteOn.getTick())

			no = new NoteOn(noteOn.getTick(), no.getChannel(), no.getNoteValue(), 0);

		}

		noteOns.add(noteOn);
	}

	private MidiTrack createDeltas(MidiTrack mt) {

		MidiEvent pme = null;

		for (MidiEvent me : mt.getEvents()) {

			if (me instanceof NoteOn) {

				if (pme != null) {

					if (me.getTick() != pme.getTick()) {
						me.setDelta(me.getTick() - pme.getTick());
					} else {
						me.setDelta(0);
					}
				}
				pme = me;
			}
		}

		long pmeTick = pme == null ? 0 : pme.getTick();
		mt.setEndOfTrackDelta(mpcSequence.getLastTick() - pmeTick);

		return mt;
	}

	public byte[] getBytes() {
		return mf.getBytes();
	}
}
