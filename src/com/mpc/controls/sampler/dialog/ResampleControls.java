package com.mpc.controls.sampler.dialog;

import java.io.File;
import java.io.IOException;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.disk.MpcFile;
import com.mpc.disk.SoundLoader;
import com.mpc.file.wav.WavFileException;
import com.mpc.sampler.Sound;

public class ResampleControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("newfs")) soundGui.setNewFs(soundGui.getNewFs() + notch);
		if (param.equals("newbit")) soundGui.setNewBit(soundGui.getNewBit() + notch);
		if (param.equals("quality")) soundGui.setQuality(soundGui.getQuality() + notch);

		if (param.equals("newname")) {
			nameGui.setName(mainFrame.lookupTextField("newname").getText());
			nameGui.setParameterName("newname");
			mainFrame.openScreen("name", "dialogpanel");
		}
	}

	public void function(int i) {
		init();
		switch (i) {

		case 3:
			mainFrame.openScreen("sound", "windowpanel");
			break;
		case 4:
			sampler.resample(soundGui.getSoundIndex(), soundGui.getNewFs(), soundGui.getNewBit());
			SoundLoader sl = new SoundLoader();
			float[] newSampleData = null;
			try {
				newSampleData = sl.getSampleDataFromWav(new MpcFile(new File(System.getProperty("user.home") + "/Mpc/temp/resampled.wav")));
			} catch (IOException | WavFileException e) {
				e.printStackTrace();
			}

			if (soundGui.getNewBit() == 1) newSampleData = sampler.process12Bit(newSampleData);
			if (soundGui.getNewBit() == 2) newSampleData = sampler.process8Bit(newSampleData);
			Sound sound = new Sound(soundGui.getNewFs());
			sound.setSampleData(newSampleData);
			sound.setName(soundGui.getNewName());
			sound.setMono(sampler.getSound(soundGui.getSoundIndex()).isMono());
			sampler.getSounds().add(sound);
			soundGui.setSoundIndex(sampler.getSoundCount() - 1);
			mainFrame.openScreen("sound", "windowpanel");
			break;
		}

	}
}
