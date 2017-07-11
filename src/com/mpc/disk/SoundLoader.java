package com.mpc.disk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mpc.file.sndreader.SndReader;
import com.mpc.file.wav.MpcWavFile;
import com.mpc.file.wav.WavFileException;
import com.mpc.gui.Bootstrap;
import com.mpc.sampler.Sound;

public class SoundLoader {

	private boolean partOfProgram;
	private MpcFile soundFile;

	private List<Sound> sounds;

	private boolean preview;
	private final boolean replace;
	private float rateToTuneBase = (float) Math.pow(2, (1.0 / 12.0));

	private int size;

	public SoundLoader() {
		replace = false;
	}

	public SoundLoader(List<Sound> sounds, boolean replace) {
		this.replace = replace;
		this.sounds = sounds;
		partOfProgram = false;
	}

	public void setPartOfProgram(boolean b) {
		partOfProgram = b;
	}

	public int loadSound(MpcFile f) throws InterruptedException, IOException, WavFileException {
		soundFile = f;
		String soundFileName = soundFile.getName();
		int periodIndex = soundFileName.lastIndexOf('.');
		String extension = "";
		String soundName = "";
		boolean mono = false;
		int sampleRate = 0;
		int start = 0;
		int end = 0;
		int loopTo = 0;
		int level = 100;
		int tune = 0;
		boolean loopEnabled = false;
		int beats = 4;

		if (periodIndex != -1) {
			extension = soundFileName.substring(periodIndex + 1, soundFileName.length());
			soundFileName = soundFileName.substring(0, periodIndex);
			soundName = soundFileName;
		}

		if (!partOfProgram) Bootstrap.getGui().getDiskGui().openPopup(soundFileName, extension);

		float[] fa = null;

		if (extension.equalsIgnoreCase("wav")) {
			try {
				fa = getSampleDataFromWav(soundFile);
			} catch (IOException | WavFileException e) {
				e.printStackTrace();
			}

			size = 0;
			try {
				size = getSampleDataFromWav(soundFile).length;
				end = size;
			} catch (IOException | WavFileException e1) {
				e1.printStackTrace();
			}

			MpcWavFile wavFile = null;
			try {
				wavFile = MpcWavFile.openWavFile(soundFile.getFile());
			} catch (IOException | WavFileException e) {
				e.printStackTrace();
			}

			int numChannels = wavFile.getNumChannels();
			if (numChannels == 1) {
				mono = true;
			} else {
				end /= 2;
			}

			sampleRate = (int) wavFile.getSampleRate();

			loopTo = end;

			float tuneFactor = (float) (sampleRate / 44100.0);

			tune = (int) Math.floor(logOfBase(tuneFactor, rateToTuneBase) * 10.0);

			if (tune < -120) tune = -120;

			if (tune > 120) tune = 120;

		}

		if (extension.equalsIgnoreCase("snd")) {
			SndReader sndReader = new SndReader(soundFile);

			fa = sndReader.getSampleData();

			size = fa.length;

			mono = sndReader.isMono();
			start = sndReader.getStart();
			end = sndReader.getEnd();
			loopTo = end - sndReader.getLoopLength();
			sampleRate = sndReader.getSampleRate();
			soundName = sndReader.getName();
			loopEnabled = sndReader.isLoopEnabled();
			level = sndReader.getLevel();
			tune = sndReader.getTune();
			System.out.println("tune " + tune);
			beats = sndReader.getNumberOfBeats();
		}

		Sound sound = new Sound(sampleRate);

		sound.setSampleData(fa);
		sound.setName(soundName);
		sound.setMono(mono);
		sound.setStart(start);
		sound.setEnd(end);
		sound.setLoopTo(loopTo);
		sound.setLoopEnabled(loopEnabled);
		sound.setLevel(level);
		sound.setTune(tune);
		sound.setNumberOfBeats(beats);

		int exists = Bootstrap.getGui().getMpc().getSampler().checkExists(sound.getName());
		if (!preview) {
			if (exists == -1) {
				sounds.add(sound);
				if (partOfProgram) return sounds.size() - 1;
			} else {
				if (replace) {
					sound.setMemoryIndex(exists);
					sounds.set(exists, sound);
				}
				if (partOfProgram) 
					return exists;
			}
		} else {
			Bootstrap.getGui().getMpc().getSampler().setPreviewSound(sound);
			return exists;
		}
		return -1;
	}

	public float[] getSampleDataFromWav(MpcFile soundFile) throws IOException, WavFileException {
		MpcWavFile wavFile = MpcWavFile.openWavFile(soundFile.getFile());
		int numChannels = wavFile.getNumChannels();
		double[] buffer = new double[100 * numChannels];
		int framesRead = 0;
		List<Float> l = new ArrayList<Float>();
		do {
			framesRead = wavFile.readFrames(buffer, 100);
			for (int s = 0; s < framesRead * numChannels; s++) {
				l.add((float) buffer[s]);
			}
		} while (framesRead != 0);

		float[] fa = new float[l.size()];

		for (int j = 0; j < l.size(); j++) {
			fa[j] = l.get(j);
		}
		return fa;
	}

	public void setPreview(boolean b) {
		preview = b;
	}

	public double logOfBase(float num, float base) {
		return Math.log(num) / Math.log(base);
	}

	public int getSize() {
		return size;
	}
}