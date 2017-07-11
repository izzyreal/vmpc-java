package com.mpc.file.all;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mpc.Mpc;
import com.mpc.disk.MpcFile;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.UserDefaults;
import com.mpc.gui.midisync.MidiSyncGui;
import com.mpc.gui.sequencer.StepEditorGui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;

public class AllLoader {

	private final AllParser allParser;
	private List<Sequence> sequences;
	private final List<MpcSequence> mpcSequences;
	private final Mpc mpc;

	public AllLoader(MpcFile file) {
		mpc = Bootstrap.getGui().getMpc();
		mpcSequences = new ArrayList<MpcSequence>();
		allParser = new AllParser(file);
		sequences = allParser.getAllSequences();
		String[] allSeqNames = allParser.getSeqNames().getNames();
		List<Sequence> temp = new ArrayList<Sequence>();
		int counter = 0;
		for (int i = 0; i < 99; i++) {
			if (allSeqNames[i].contains("(Unused)")) {
				temp.add(null);
			} else {
				temp.add(sequences.get(counter++));
			}
		}
		sequences = temp;
		convertSequences();
	}

	public AllLoader(Mpc mpc, MpcFile file) {
		this.mpc = mpc;
		mpcSequences = new ArrayList<MpcSequence>();
		allParser = new AllParser(file);
		sequences = allParser.getAllSequences();

		Defaults defaults = allParser.getDefaults();
		UserDefaults ud = Bootstrap.getUserDefaults();
		ud.setLastBar(defaults.getBarCount() - 1);
		ud.setBus(defaults.getBusses()[0]);

		for (int i = 0; i < 33; i++)
			ud.setDeviceName(i, defaults.getDefaultDevNames()[i]);

		ud.setSequenceName(defaults.getDefaultSeqName());

		String[] defTrackNames = defaults.getDefaultTrackNames();
		for (int i = 0; i < 64; i++)
			ud.setTrackName(i, defTrackNames[i]);

		ud.setDeviceNumber(defaults.getDevices()[0]);

		ud.setTimeSig(defaults.getTimeSigNum(), defaults.getTimeSigDen());
		ud.setPgm(defaults.getPgms()[0]);
		ud.setTempo(new BigDecimal("" + defaults.getTempo() / 10.0));
		ud.setVelo(defaults.getTrVelos()[0]);
		convertSequences();
		mpc.getSequencer().purgeAllSequences();

		int destIndex = 0;
		String[] allSeqNames = allParser.getSeqNames().getNames();

		for (int i = 0; i < mpcSequences.size(); i++) {

			while (allSeqNames[destIndex].contains("(Unused)") && destIndex < 98)
				destIndex++;

			mpc.getSequencer().setSequence(destIndex++, mpcSequences.get(i));
		}

		Sequencer sequencer = allParser.getSequencer();
		mpc.getSequencer().setSelectedSequenceIndex(sequencer.sequence);
		mpc.getSequencer().setSelectedTrackIndex(sequencer.track);
		mpc.getSequencer().setTcValue(sequencer.tc);
		Count count = allParser.getCount();
		SequencerWindowGui swgui = Bootstrap.getGui().getSequencerWindowGui();
		swgui.setCountIn(count.getCountInMode());
		swgui.setAccentVelo(count.getAccentVelo());
		swgui.setNormalVelo(count.getNormalVelo());
		swgui.setClickOutput(count.getClickOutput());
		swgui.setClickVolume(count.getClickVolume());
		swgui.setRate(count.getRate());
		swgui.setMetronomeSound(count.getSound());
		swgui.setInPlay(count.isEnabledInPlay());
		swgui.setInRec(count.isEnabledInRec());
		swgui.setWaitForKey(count.isWaitForKeyEnabled());
		mpc.getSequencer().setCountEnabled(count.isEnabled());

		MidiInput midiInput = allParser.getMidiInput();
		swgui.setReceiveCh(midiInput.getReceiveCh());
		swgui.setFilterType(midiInput.getFilterType());
		int[] trackDests = midiInput.getMultiRecTrackDests();
		for (int i = 0; i < trackDests.length; i++)
			swgui.getMrsLines()[i].setTrack(trackDests[i]);
		swgui.setChPressurePassEnabled(midiInput.isChPressurePassEnabled());
		swgui.setExclusivePassEnabled(midiInput.isExclusivePassEnabled());
		swgui.setMidiFilterEnabled(midiInput.isFilterEnabled());
		mpc.getSequencer().setRecordingModeMulti(midiInput.isMultiRecEnabled());
		swgui.setNotePassEnabled(midiInput.isNotePassEnabled());
		swgui.setPgmChangePassEnabled(midiInput.isPgmChangePassEnabled());
		swgui.setPitchBendPassEnabled(midiInput.isPitchBendPassEnabled());
		swgui.setPolyPressurePassEnabled(midiInput.isPolyPressurePassEnabled());
		swgui.setSustainPedalToDuration(midiInput.isSustainPedalToDurationEnabled());
		Misc misc = allParser.getMisc();
		MidiSyncMisc midiSyncMisc = allParser.getMidiSync();
		StepEditorGui segui = Bootstrap.getGui().getStepEditorGui();
		segui.setAutoStepIncrementEnabled(misc.isAutoStepIncEnabled());
		segui.setTcValueRecordedNotes(misc.getDurationTcPercentage());
		segui.setDurationOfRecordedNotes(misc.isDurationOfRecNotesTc());
		MidiSyncGui msgui = Bootstrap.getGui().getMidiSyncGui();
		msgui.setReceiveMMCEnabled(misc.isInReceiveMMCEnabled());
		msgui.setSendMMCEnabled(midiSyncMisc.isSendMMCEnabled());
		msgui.setModeIn(midiSyncMisc.getInMode());
		msgui.setModeOut(midiSyncMisc.getOutMode());
		msgui.setShiftEarly(midiSyncMisc.getShiftEarly());
		msgui.setFrameRate(midiSyncMisc.getFrameRate());
		msgui.setIn(midiSyncMisc.getInput());
		msgui.setOut(midiSyncMisc.getOutput());

		mpc.getSequencer().setSecondSequenceEnabled(sequencer.secondSeqEnabled);
		mpc.getSequencer().setSecondSequenceIndex(sequencer.secondSeqIndex);

		Bootstrap.getGui().getSongGui().setDefaultSongName(midiSyncMisc.getDefSongName());
		swgui.setTapAvg(misc.getTapAvg());

		Song[] songs = allParser.getSongs();
		for (int i = 0; i < 20; i++)
			mpc.getSequencer().getSong(i).setName(songs[i].name);

	}

	private void convertSequences() {
		int counter = 0;
		for (Sequence as : sequences) {
			if (as == null) {
				mpcSequences.add(null);
				continue;
			}
			System.out.println("sequence " + (counter++));
			MpcSequence mpcSeq = new MpcSequence(mpc, com.mpc.sequencer.Sequencer.defaultTrackNames);
			mpcSeq.setUsed(true);
			mpcSeq.init(as.barCount - 1);
			for (int i = 0; i < as.barCount; i++) {
				int num = as.barList.getBars().get(i).getNumerator();
				int den = as.barList.getBars().get(i).getDenominator();
				mpcSeq.setTimeSignature(i, i, num, den);
			}
			mpcSeq.setName(as.name);
			mpcSeq.getTempoChangeEvents().get(0).setInitialTempo(as.tempo);

			Tracks at = as.tracks;
			for (int i = 0; i < 64; i++) {
				MpcTrack t = (MpcTrack) mpcSeq.getTracks().get(i);
				t.setUsed(at.getStatus(i) != 6);
				t.setName(at.getName(i));
				t.setBusNumber(at.getBus(i));
				t.setProgramChange(at.getPgm(i));
				t.setOn(at.getStatus(i) != 5);
				t.setVelocityRatio(at.getVelo(i));
				mpcSeq.setTrack(t, i);
			}

			for (int j = 0; j < as.getEventAmount(); j++) {
				Event e = as.allEvents.get(j);
				if (e == null) continue;
				int track = e.getTrack();
				System.out.println("Track as pulled from event: " + track);
				if (track < 0) track += 128;
				if (track > 63) track -= 64;
				System.out.println("Track after correction: " + track);
				mpcSeq.getTrack(track).addEvent(e);
			}

			for (int i = 0; i < 32; i++)
				mpcSeq.setDeviceName(i, as.devNames[i]);

			mpcSeq.initMetaTracks();
			mpcSeq.setFirstLoopBar(as.loopFirst);
			mpcSeq.setLastLoopBar(as.loopLast);
			mpcSeq.setLastLoopBar(as.loopLast); // on purpose
			if (as.loopLastEnd) mpcSeq.setLastLoopBar(Integer.MAX_VALUE);
			mpcSeq.setLoopEnabled(as.loop);
			mpcSequences.add(mpcSeq);
		}
	}

	public List<MpcSequence> getSequences() {
		return mpcSequences;
	}
}
