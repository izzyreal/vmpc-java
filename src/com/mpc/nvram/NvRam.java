package com.mpc.nvram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import com.mpc.file.all.Defaults;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.ControlPanel;
import com.mpc.gui.UserDefaults;

public class NvRam {

	public static UserDefaults load() {

		File file = new File(Bootstrap.resPath + "nvram.vmp");
		UserDefaults ud = new UserDefaults();
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				DefaultsParser dp = new DefaultsParser(ud);
				fos.write(dp.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			DefaultsParser dp = new DefaultsParser(file);
			Defaults defaults = dp.getDefaults();
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
		}
		return ud;
	}

	public static void saveUserDefaults() {
		DefaultsParser dp = new DefaultsParser(Bootstrap.getUserDefaults());
		File file = new File(Bootstrap.resPath + "nvram.vmp");
		try {
			if (!file.exists()) file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(dp.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveKnobPositions() {
		File file = new File(Bootstrap.resPath + "knobpositions.vmp");
		try {
			if (!file.exists()) file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ControlPanel cp = Bootstrap.getGui().getMainFrame().getControlPanel();
			byte recordb = (byte) cp.getRecord();
			byte volumeb = (byte) cp.getVolume();
			byte sliderb = (byte) cp.getSlider().getValue();
			byte[] bytes = new byte[]{recordb,volumeb,sliderb};
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static int getMasterLevel() {
		return getKnobPositions().masterLevel;
	}

	public static int getRecordLevel() {
		return getKnobPositions().recordLevel;
	}

	public static int getSlider() {
		return getKnobPositions().slider;
	}

	private static KnobPositions getKnobPositions() {
		return new KnobPositions();
	}
	
	private static class KnobPositions {

		private int recordLevel = 0;
		private int masterLevel = 65;
		private int slider = 64;
		
		private KnobPositions() {
			File file = new File(Bootstrap.resPath + "knobpositions.vmp");
			if (!file.exists()) {
				try {
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] bytes = new byte[3];
					bytes[0] = (byte) recordLevel;
					bytes[1] = (byte) masterLevel;
					bytes[2] = (byte) slider;
					fos.write(bytes);
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					FileInputStream fis = new FileInputStream(file);
					byte[] bytes = new byte[3];
					fis.read(bytes);
					fis.close();
					recordLevel = bytes[0];
					masterLevel = bytes[1];
					slider = bytes[2];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
