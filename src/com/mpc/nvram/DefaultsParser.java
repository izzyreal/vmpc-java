package com.mpc.nvram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import com.mpc.file.all.AllParser;
import com.mpc.file.all.Defaults;
import com.mpc.gui.UserDefaults;

public class DefaultsParser {

	/*
	 * MPC uses NVRAM for its main screen user defaults in (presumably). To
	 * emulate this particular aspect of MPC's persistance, the user defaults
	 * portion of the ALL file is stored to a file that is unique to VMPC. The
	 * user typically never interacts directly with this file, only via the GUI.
	 */

	/*
	 * Attributes for loading
	 */

	Defaults defaults;

	/*
	 * Attributes for saving
	 */

	byte[] saveBytes;

	/*
	 * Constructor and methods for loading
	 */

	public DefaultsParser(File file) {
		byte[] data = null;
		try {
			data = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		defaults = new Defaults(Arrays.copyOfRange(data, 0, AllParser.DEFAULTS_LENGTH));
	}

	public Defaults getDefaults() {
		return defaults;
	}

	/*
	 * Constructor and methods for saving
	 */

	public DefaultsParser(UserDefaults ud) {
		Defaults defaults = new Defaults(ud);
		saveBytes = defaults.getBytes();
	}

	public byte[] getBytes() {
		return saveBytes;
	}

}
