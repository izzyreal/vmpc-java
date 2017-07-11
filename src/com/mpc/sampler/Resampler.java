package com.mpc.sampler;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.mpc.gui.Bootstrap;

public class Resampler {

	private int rate = 0;
	private File inputFile;
	
	public Resampler(Sampler sampler, Sound sound, int rate) {
		this.rate = rate;
		String oldName = sound.getName();
		sound.setName("resample");
		Bootstrap.getGui().getMpc().getDisk().writeWavToTemp(sound);
		sound.setName(oldName);
		inputFile = new File(System.getProperty("user.home") + "/Mpc/temp/resample.wav");
	}
	
	public void resample() throws Exception {

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);
                AudioFormat sourceFormat =  audioInputStream.getFormat();


                AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        (float) rate,
                        sourceFormat.getSampleSizeInBits(),
                        sourceFormat.getChannels(),
                        sourceFormat.getFrameSize(),
                        sourceFormat.getFrameRate(),
                        sourceFormat.isBigEndian());

                AudioInputStream inputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
                AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, new File(System.getProperty("user.home") + "/Mpc/temp/resampled.wav"));
    }
}