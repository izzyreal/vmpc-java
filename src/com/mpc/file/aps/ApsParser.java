/*
 *  This class is used to load or save an MPC2000XL APS file.
 */

package com.mpc.file.aps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mpc.Mpc;
import com.mpc.disk.MpcFile;
import com.mpc.sampler.Sampler;

class ApsParser {

	final static int HEADER_OFFSET = 0;
	final static int HEADER_LENGTH = 4;

	// repeat soundAmount times
	final static int SOUND_NAME_LENGTH = 17;

	final static int PAD_LENGTH1 = 2; // 0x18 0x00
	final static int APS_NAME_LENGTH = 17;
	final static int PARAMETERS_LENGTH = 8;
	final static int TABLE_LENGTH = 64;
	final static int PAD_LENGTH2 = 7; // 0x04 0x00 0x88 0x01 0x40 0x00 0x06

	// repeat 4x (one for each DRUM bus)
	final static int MIXER_LENGTH = 384;
	final static int DRUM_CONFIG_LENGTH = 9;
	final static int DRUM_PAD_LENGTH = 3; // 0x40 0x00 0x06

	final static int PAD_LENGTH3 = 15; // 0x04 0x1E 0x00

	// repeat for each program
	final static int PROGRAM_LENGTH = 2350;
	final static int PROGRAM_PAD_LENGTH = 4;

	// finalize program with 2 instead of 4 bytes
	final static int LAST_PROGRAM_PAD_LENGTH = 2;

	// Some generic values
	static final byte NAME_TERMINATOR = 0x00;

	// For reading and writing
	final int programCount;

	// For reading
	ApsHeader header;
	ApsSoundNames soundNames;
	ApsName apsName;
	ApsGlobalParameters globalParameters;
	ApsAssignTable maTable;
	ApsMixer[] drumMixers = new ApsMixer[4];
	ApsDrumConfiguration[] drumConfigurations = new ApsDrumConfiguration[4];

	List<ApsProgram> programs = new ArrayList<ApsProgram>();

	// For saving

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	ApsParser(MpcFile file) {
		byte[] loadBytes = file.getBytes();
		header = new ApsHeader(Arrays.copyOfRange(loadBytes, HEADER_OFFSET, HEADER_OFFSET + HEADER_LENGTH));

		final int soundNamesEnd = HEADER_LENGTH + (header.getSoundAmount() * SOUND_NAME_LENGTH);

		soundNames = new ApsSoundNames(Arrays.copyOfRange(loadBytes, HEADER_OFFSET + HEADER_LENGTH, soundNamesEnd));

		programCount = (loadBytes.length - 1689 - (soundNames.get().size() * 17)) / PROGRAM_LENGTH;

		final int nameEnd = soundNamesEnd + PAD_LENGTH1 + APS_NAME_LENGTH;
		final int nameOffset = soundNamesEnd + PAD_LENGTH1;
		apsName = new ApsName(Arrays.copyOfRange(loadBytes, nameOffset, nameEnd));

		final int parametersEnd = nameEnd + PARAMETERS_LENGTH;
		globalParameters = new ApsGlobalParameters(Arrays.copyOfRange(loadBytes, nameEnd, parametersEnd));

		final int maTableEnd = parametersEnd + TABLE_LENGTH;
		maTable = new ApsAssignTable(Arrays.copyOfRange(loadBytes, parametersEnd, maTableEnd));

		final int drum1MixerOffset = maTableEnd + PAD_LENGTH2;

		for (int i = 0; i < 4; i++) {
			int offset = drum1MixerOffset + (i * (MIXER_LENGTH + DRUM_CONFIG_LENGTH + DRUM_PAD_LENGTH));
			drumMixers[i] = new ApsMixer(Arrays.copyOfRange(loadBytes, offset, offset + MIXER_LENGTH));
			drumConfigurations[i] = new ApsDrumConfiguration(
					Arrays.copyOfRange(loadBytes, offset + MIXER_LENGTH, offset + MIXER_LENGTH + DRUM_CONFIG_LENGTH));
		}

		final int firstProgramOffset = drum1MixerOffset + ((MIXER_LENGTH + DRUM_CONFIG_LENGTH) * 4) + PAD_LENGTH3;
		for (int i = 0; i < programCount; i++) {
			int offset = firstProgramOffset + (i * (PROGRAM_LENGTH + PROGRAM_PAD_LENGTH));
			programs.add(new ApsProgram(Arrays.copyOfRange(loadBytes, offset, offset + 2350)));
		}

	}

	List<String> getSoundNames() {
		return soundNames.get();
	}

	boolean isHeaderValid() {
		return header.isValid();
	}

	String getApsName() {
		return apsName.get();
	}

	List<ApsProgram> getPrograms() {
		return programs;
	}

	ApsMixer[] getDrumMixers() {
		return drumMixers;
	}

	ApsDrumConfiguration getDrumConfiguration(int i) {
		return drumConfigurations[i];
	}

	ApsGlobalParameters getGlobalParameters() {
		return globalParameters;
	}

	/*
	 * Constructor and methods for saving
	 */

	ApsParser(Mpc mpc, String apsNameString) {
		List<byte[]> chunks = new ArrayList<byte[]>();
		programCount = mpc.getSampler().getProgramCount();
		final int soundCount = mpc.getSampler().getSoundCount();

		ApsHeader header = new ApsHeader(soundCount);
		chunks.add(header.getBytes());

		ApsSoundNames soundNames = new ApsSoundNames(mpc.getSampler());
		chunks.add(soundNames.getBytes());

		chunks.add(new byte[] { 0x18, 0x00 });

		ApsName apsName = new ApsName(apsNameString);
		chunks.add(apsName.getBytes());

		ApsGlobalParameters parameters = new ApsGlobalParameters(mpc);
		chunks.add(parameters.getBytes());

		ApsAssignTable masterTable = new ApsAssignTable(Sampler.masterPadAssign);

		chunks.add(masterTable.getBytes());

		chunks.add(new byte[] { 0x04, 0x00, (byte) (0x88 & 0xFF), 0x01, 0x40, 0x00, 0x06 }); // TODO
																								// Check
																								// actual
																								// pad
																								// bytes.

		for (int i = 0; i < 4; i++) {
			ApsMixer mixer = new ApsMixer(mpc.getSampler().getDrumMixer(i));
			ApsDrumConfiguration drumConfig = new ApsDrumConfiguration(mpc.getSampler().getDrumBusProgramNumber(i + 1),
					mpc.getSampler().getDrum(i).receivesPgmChange(), mpc.getSampler().getDrum(i).receivesMidiVolume());
			chunks.add(mixer.getBytes());
			if (i < 3) {
				chunks.add(drumConfig.getBytes());
			} else {
				byte[] ba = drumConfig.getBytes();
				byte[] ba1 = new byte[7];
				for (int j = 0; j < 7; j++)
					ba1[j] = ba[j];
				chunks.add(ba1);
			}
		}

		chunks.add(new byte[] { 0x01, 0x7F, 0x00, 0x00, 0x07, 0x04, 0x1E, 0x00 });

		for (int i = 0; i < programCount; i++) {
			ApsProgram program = new ApsProgram(mpc.getSampler().getProgram(i));
			chunks.add(program.getBytes());
			if (i != programCount - 1) {
				chunks.add(new byte[] { 0x01, 0x00, 0x07, 0x04, 0x1E, 0x00 });
			} else {
				chunks.add(new byte[] { (byte) (0xFF & 0xFF), (byte) (0xFF & 0xFF) });				
			}
		}

		int totalSize = 0;
		for (byte[] ba : chunks)
			totalSize += ba.length;
		saveBytes = new byte[totalSize];
		int counter = 0;
		for (byte[] ba : chunks)
			for (byte b : ba)
				saveBytes[counter++] = b;
	}

	byte[] getBytes() {
		return saveBytes;
	}

}
