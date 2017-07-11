package com.mpc.file.all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.disk.MpcFile;
import com.mpc.file.Definitions;
import com.mpc.gui.Bootstrap;
import com.mpc.sequencer.MpcSequence;

public class AllParser {

	// Some generic constants used by various classes in this package.

	final static int NAME_LENGTH = 0x10;
	final static int DEV_NAME_LENGTH = 0x08;
	final static int EMPTY_SEQ_LENGTH = 0x2810;
	final static int EVENT_LENGTH = 0x08;

	// Offsets relative to start of file

	final static int HEADER_OFFSET = 0x0000;
	final static int HEADER_LENGTH = 0x0010;

	final static int DEFAULTS_OFFSET = 0x0010;
	public final static int DEFAULTS_LENGTH = 0x06C0;

	final static int UNKNOWN1_OFFSET = 0x06D0;

	final static int SEQUENCER_OFFSET = 0x0710;

	final static int COUNT_OFFSET = 0x0720;
	final static int COUNT_LENGTH = 0x10;

	final static int MIDI_INPUT_OFFSET = 0x0730;

	final static int MIDI_SYNC_OFFSET = 0x0770;

	final static int MISC_OFFSET = 0x0790;

	final static int SEQUENCE_NAMES_OFFSET = 0x0810;

	final static int SONGS_OFFSET = 0x0F06;
	
	final static int SEQUENCES_OFFSET = 0x3846;

	// Loading

	Header header;
	Defaults defaults;
	Sequencer sequencer;
	Count count;
	MidiInput midiInput;
	MidiSyncMisc midiSyncMisc;
	Misc misc;
	SequenceNames seqNames;
	List<Sequence> sequences = new ArrayList<Sequence>();
	Song[] songs = new Song[20];
	
	// saving
	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	public AllParser(MpcFile file) {
		byte[] loadBytes = file.getBytes();
		header = new Header(Arrays.copyOfRange(loadBytes, HEADER_OFFSET, HEADER_OFFSET + HEADER_LENGTH));
		header.verifyFileID();

		defaults = new Defaults(Arrays.copyOfRange(loadBytes, DEFAULTS_OFFSET, DEFAULTS_OFFSET + DEFAULTS_LENGTH));
		sequencer = new Sequencer(Arrays.copyOfRange(loadBytes, SEQUENCER_OFFSET, SEQUENCER_OFFSET + Sequencer.LENGTH));
		count = new Count(Arrays.copyOfRange(loadBytes, COUNT_OFFSET, COUNT_OFFSET + COUNT_LENGTH));
		midiInput = new MidiInput(
				Arrays.copyOfRange(loadBytes, MIDI_INPUT_OFFSET, MIDI_INPUT_OFFSET + MidiInput.LENGTH));
		midiSyncMisc = new MidiSyncMisc(
				Arrays.copyOfRange(loadBytes, MIDI_SYNC_OFFSET, MIDI_SYNC_OFFSET + MidiSyncMisc.LENGTH));
		misc = new Misc(Arrays.copyOfRange(loadBytes, MISC_OFFSET, MISC_OFFSET + Misc.LENGTH));

		seqNames = new SequenceNames(
				Arrays.copyOfRange(loadBytes, SEQUENCE_NAMES_OFFSET, SEQUENCE_NAMES_OFFSET + SequenceNames.LENGTH));

		for (int i=0;i<20;i++){
			int offset = SONGS_OFFSET + (i*Song.LENGTH);
			songs[i] = new Song(Arrays.copyOfRange(loadBytes, offset, offset+Song.LENGTH));
		}
		
		System.out.println("LoadBytes length: " + loadBytes.length);
		sequences = readSequences(Arrays.copyOfRange(loadBytes, SEQUENCES_OFFSET, loadBytes.length));
	}

	List<Sequence> getAllSequences() {
		return sequences;
	}

	Defaults getDefaults() {
		return defaults;
	}

	Sequencer getSequencer() {
		return sequencer;
	}

	Count getCount() {
		return count;
	}

	public MidiInput getMidiInput() {
		return midiInput;
	}

	MidiSyncMisc getMidiSync() {
		return midiSyncMisc;
	}

	public Misc getMisc() {
		return misc;
	}

	public SequenceNames getSeqNames() {
		return seqNames;
	}
	
	public Song[] getSongs() {
		return songs;
	}
	
	private List<Sequence> readSequences(byte[] trimmedSeqsArray) {
		
		final int totalSeqChunkLength = trimmedSeqsArray.length;

		List<Sequence> seqs = new ArrayList<Sequence>();
		
		int eventSegments, currentSeqEnd, read = 0;
		
		for (int i = 0; i < 99; i++) {
			eventSegments = Sequence.getNumberOfEventSegmentsForThisSeq(trimmedSeqsArray);
		
			currentSeqEnd = EMPTY_SEQ_LENGTH + (eventSegments * EVENT_LENGTH);
			
			byte[] currentSeqArray = Arrays.copyOfRange(trimmedSeqsArray, 0, currentSeqEnd);
			Sequence as = new Sequence(currentSeqArray);
			seqs.add(as);
			read += currentSeqEnd;
			int multiplier = ((eventSegments & 1) == 0 ? 0 : 1);
			
			if (totalSeqChunkLength - read >= EMPTY_SEQ_LENGTH - 16) {
				trimmedSeqsArray = Arrays.copyOfRange(trimmedSeqsArray, currentSeqEnd - (multiplier * EVENT_LENGTH), // not
																														// sure
																														// about
																														// subtracting
																														// 2
																														// *
																														// 8
						trimmedSeqsArray.length);
			} else {
				break; // no more seqs left to parse
			}
		}
		
		return seqs;
	}

	/*
	 * Constructor and methods for saving
	 */
	public AllParser(Mpc mpc, String allName) {

		List<byte[]> chunks = new ArrayList<byte[]>();

		chunks.add(new Header().getBytes());

		Defaults defaults = new Defaults(Bootstrap.getUserDefaults());
		chunks.add(defaults.getBytes());

		chunks.add(Definitions.UNKNOWN_CHUNK);

		sequencer = new Sequencer(Bootstrap.getGui());
		chunks.add(sequencer.getBytes());

		count = new Count(Bootstrap.getGui());
		chunks.add(count.getBytes());

		midiInput = new MidiInput(Bootstrap.getGui());
		chunks.add(midiInput.getBytes());

		chunks.add(Definitions.TERMINATOR);

		midiSyncMisc = new MidiSyncMisc(Bootstrap.getGui());
		chunks.add(midiSyncMisc.getBytes());

		misc = new Misc(Bootstrap.getGui());
		chunks.add(misc.getBytes());

		seqNames = new SequenceNames(mpc);
		chunks.add(seqNames.getBytes());
		
		songs = new Song[20];
		
		for (int i=0;i<20;i++) {
			songs[i] = new Song(mpc.getSequencer().getSong(i));
			chunks.add(songs[i].getBytes());
		}
		
		
		List<MpcSequence> usedSeqs = mpc.getSequencer().getUsedSequences();

		for (int i = 0; i < usedSeqs.size(); i++) {
			MpcSequence seq = usedSeqs.get(i);
			Sequence allSeq = new Sequence(seq, (mpc.getSequencer().getUsedSequenceIndexes().get(i).intValue()+1));
			chunks.add(allSeq.getBytes());
		}

		saveBytes = Util.stitchByteArrays(chunks);

	}

	public byte[] getBytes() {
		return saveBytes;
	}

}