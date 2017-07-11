package com.mpc.file.aps;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mpc.file.pgmwriter.Pads;
import com.mpc.tootextensions.ConcreteMixParameters;
import com.mpc.tootextensions.MpcMixParameters;
import com.mpc.tootextensions.MpcProgram;

class ApsProgram {

	// offsets within this particular chunk
	final static int NAME_OFFSET = 0;
	final static int NAME_LENGTH = 16;
	final static int PADDING0_LENGTH = 1;
	final static int SLIDER_OFFSET = 17;
	final static int SLIDER_LENGTH = 10;
	final static int PADDING1_LENGTH = 5;
	final static int NOTE_PARAMETERS_OFFSET = 32;
	final static int NOTE_PARAMETERS_LENGTH = 26; // repeat 64x
	final static int PADDING2_LENGTH = 7;
	final static int MIXER_OFFSET = 1697;
	final static int MIXER_END = 2081;
	final static int PADDING3_LENGTH = 3;
	final static int ASSIGN_TABLE_OFFSET = 2084;
	final static int ASSIGN_TABLE_LENGTH = 64;

	// loading
	String name;
	ApsSlider slider;
	ApsNoteParameters[] noteParameters = new ApsNoteParameters[64];
	ApsMixer mixer;
	ApsAssignTable assignTable;

	// saving
	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	ApsProgram(byte[] loadBytes) {

		try {
			name = new String(Arrays.copyOfRange(loadBytes, NAME_OFFSET, NAME_OFFSET + NAME_LENGTH), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		slider = new ApsSlider(Arrays.copyOfRange(loadBytes, SLIDER_OFFSET, SLIDER_OFFSET + SLIDER_LENGTH));

		for (int i = 0; i < 64; i++) {
			int offset = NOTE_PARAMETERS_OFFSET + (i * NOTE_PARAMETERS_LENGTH);
			noteParameters[i] = new ApsNoteParameters(
					Arrays.copyOfRange(loadBytes, offset, offset + NOTE_PARAMETERS_LENGTH));
		}
		mixer = new ApsMixer(Arrays.copyOfRange(loadBytes, MIXER_OFFSET, MIXER_END));
		assignTable = new ApsAssignTable(
				Arrays.copyOfRange(loadBytes, ASSIGN_TABLE_OFFSET, ASSIGN_TABLE_OFFSET + ASSIGN_TABLE_LENGTH));
	}

	ApsNoteParameters getNoteParameters(int note) {
		return noteParameters[note - 35];
	}

	ApsMixer getMixer() {
		return mixer;
	}

	ApsAssignTable getAssignTable() {
		return assignTable;
	}

	ApsSlider getSlider() {
		return slider;
	}

	String getName() {
		return name;
	}

	/*
	 * Constructor and methods for writing
	 */

	ApsProgram(MpcProgram program) {

		List<byte[]> byteList = new ArrayList<byte[]>();

		final String name = StringUtils.rightPad(program.getName(), 16);
		byteList.add(Arrays.copyOfRange(name.getBytes(), 0, 16));

		byteList.add(new byte[] { 0x00 });

		ApsSlider slider = new ApsSlider(program.getSlider());
		byteList.add(slider.getBytes());

		byteList.add(new byte[] { 0x23, 0x40, 0x00, 0x1A, 0x00 });

		for (int i = 0; i < 64; i++) {
			ApsNoteParameters np = new ApsNoteParameters(program.getNoteParameters(i + 35));
			byteList.add(np.getBytes());
		}

		byteList.add(new byte[] { 0x06 });

		MpcMixParameters[] mixParametersArray = new MpcMixParameters[64];
		for (int i = 0; i < 64; i++)
			mixParametersArray[i] = program.getPadMixer(i);
		ApsMixer mixer = new ApsMixer(mixParametersArray);
		byteList.add(mixer.getBytes());

		byteList.add(new byte[] { 0x00, 0x40, 0x00 });

		int[] assignTable = new int[64];
		for (int i = 0; i < 64; i++)
			assignTable[i] = program.getNoteFromPad(i);
		ApsAssignTable table = new ApsAssignTable(assignTable);
		byteList.add(table.getBytes());

		byteList.add(Pads.getPgmPadding());

		int totalSize = 0;
		for (byte[] ba : byteList)
			totalSize += ba.length;

		saveBytes = new byte[totalSize];
		int counter = 0;
		for (byte[] ba : byteList)
			for (byte b : ba)
				saveBytes[counter++] = b;
	}

	byte[] getBytes() {
		return saveBytes;
	}

	ConcreteMixParameters getMixParameters(int note) {
		return mixer.getMixVariables(note);
	}
}