package com.mpc.audiomidi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A Properties class
 */
public class TootProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File file;

	public TootProperties(File path) {
		try {
			load(new BufferedInputStream(new FileInputStream(file = new File(path, "toot.cfg"))));
		} catch (IOException ioe) {
			// ok if no default properties file
		}
	}

	public void store() {
		try {
			store(new FileOutputStream(file), null);
		} catch (IOException ioe) {
			System.out.println("Failed to store configuration " + file.getPath());
		}
	}

	public String getProperty(String key) {
		// first see if there's a default demo property
		String defaultProperty = super.getProperty(key);
		if (defaultProperty == null) {
			// if there isn't a default just use system property
			return System.getProperty(key);
		}
		// if there is a default use it with system property
		return System.getProperty(key, defaultProperty);
	}

	public String getProperty(String key, String def) {
		String property = getProperty(key);
		return property == null ? def : property;
	}
}
