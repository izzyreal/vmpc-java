package com.mpc.sampler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.lang3.StringUtils;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.disk.MpcFile;
import com.mpc.disk.SoundLoader;
import com.mpc.file.wav.WavFileException;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.UserDefaults;
import com.mpc.gui.sampler.SoundGui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.sequencer.NoteEvent;
import com.mpc.tootextensions.MpcMixParameters;
import com.mpc.tootextensions.MpcSampler;
import com.mpc.tootextensions.MpcSoundOscillatorVariables;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.server.AudioClient;
import uk.org.toot.audio.server.IOAudioProcess;
import uk.org.toot.audio.system.AudioDevice;
import uk.org.toot.audio.system.AudioInput;
import uk.org.toot.audio.system.AudioOutput;

public class Sampler extends Observable implements MpcSampler, AudioClient, AudioDevice {

	// private final static int BOUNCE_TRACK_LIMIT = 16;

	private final static int VU_BUFFER_SIZE = 100;

	private int vuCounter = 0;
	private float[] monitorBufferL;
	private float[] monitorBufferR;
	private float[] vuBufferL = new float[VU_BUFFER_SIZE];
	private float[] vuBufferR = new float[VU_BUFFER_SIZE];
	private float[] recordBufferL;
	private float[] recordBufferR;
	private int vuSampleCounter;
	private boolean recording;
	private int recordFrame;

	private IOAudioProcess input;
	private IOAudioProcess inputSwap;

	private CircularFifoBuffer preRecBufferL = new CircularFifoBuffer(4410);
	private CircularFifoBuffer preRecBufferR = new CircularFifoBuffer(4410);
	private MonitorOutput monitorOutput;

	private List<Sound> sounds = new ArrayList<Sound>();
	private List<Program> programs = new ArrayList<Program>();
	private int soundSortingType = 0; // 0 = memory 1 = name 2 = size

	private String[] padNames = new String[64];
	private String[] abcd = { "A", "B", "C", "D" };

	private float[] clickSample;
	private Sound clickSound;
	private Sound previewSound;
	private String[] sortNames = { "MEMORY", "NAME", "SIZE" };
	public static int[] initMasterPadAssign;
	public static int[] masterPadAssign;
	public static int[] autoChromaticAssign;

	private boolean armed;

	private float peakL;
	private float peakR;
	private float levelL;
	private float levelR;

	private Mpc mpc;

	public Sampler() {
		monitorOutput = new MonitorOutput();
		UserDefaults ud = new UserDefaults();
		initMasterPadAssign = ud.getPadNotes();
	}

	private class MonitorOutput implements AudioOutput {

		boolean closed = false;

		@Override
		public String getName() {
			return "monitor";
		}

		@Override
		public void open() throws Exception {
			closed = false;
		}

		@Override
		public int processAudio(AudioBuffer buffer) {
			// if (closed) return AUDIO_SILENCE;
			if (monitorBufferL == null || Bootstrap.getGui() == null
					|| Bootstrap.getGui().getSamplerGui().getMonitor() == 0
					|| !Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentScreenName().equals("sample")
					|| closed) {
				buffer.makeSilence();
				return AUDIO_SILENCE;
			}

			int mode = Bootstrap.getGui().getSamplerGui().getMode();
			int[] leftPairs = buffer.getChannelFormat().getLeft();
			int[] rightPairs = buffer.getChannelFormat().getRight();
			float[] left = buffer.getChannel(leftPairs[0]);
			float[] right = buffer.getChannel(rightPairs[0]);
			for (int i = 0; i < buffer.getSampleCount(); i++) {
				left[i] = mode != 1 ? monitorBufferL[i] : monitorBufferR[i];
				right[i] = mode != 0 ? monitorBufferR[i] : monitorBufferL[i];
			}
			return AUDIO_OK;
		}

		@Override
		public void close() throws Exception {
			closed = true;
		}

		@Override
		public String getLocation() {
			return "sampler";
		}

	}

	@Override
	public void work(int nFrames) {

		if (Bootstrap.getGui() == null || (input == null && inputSwap == null)) return;
		if (!Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentScreenName().equals("sample")) return;

		boolean arm = false;
		AudioBuffer recordBuffer = new AudioBuffer("record", 2, nFrames, 44100);

		if (inputSwap != null) {
			input = inputSwap;
			inputSwap = null;
		}
		
		input.processAudio(recordBuffer);

		int[] leftPairs = recordBuffer.getChannelFormat().getLeft();
		int[] rightPairs = recordBuffer.getChannelFormat().getRight();
		monitorBufferL = recordBuffer.getChannel(leftPairs[0]);
		monitorBufferR = recordBuffer.getChannel(rightPairs[0]);

		for (int i = 0; i < nFrames; i++) {
			monitorBufferL[i] *= Bootstrap.getGui().getMainFrame().getControlPanel().getRecord() / 100.0;
			monitorBufferR[i] *= Bootstrap.getGui().getMainFrame().getControlPanel().getRecord() / 100.0;

			if (armed && Math.abs(monitorBufferL[i]) > (Bootstrap.getGui().getSamplerGui().getThreshold() + 64) / 64.0)
				arm = true;

			if (recording) {
				recordBufferL[recordFrame] = monitorBufferL[i];
				recordBufferR[recordFrame++] = monitorBufferR[i];
				if (recordFrame == recordBufferL.length) stopRecording();
			} else {
				preRecBufferL.add(monitorBufferL[i]);
				preRecBufferR.add(monitorBufferR[i]);
			}

			if (monitorBufferL[i] > 0 && vuCounter++ == 5) {
				vuCounter = 0;
				if (monitorBufferL[i] > peakL) peakL = monitorBufferL[i];
				if (monitorBufferR[i] > peakR) peakR = monitorBufferR[i];

				vuBufferL[vuSampleCounter] = monitorBufferL[i] < 0.01 ? 0 : monitorBufferL[i];
				vuBufferR[vuSampleCounter++] = monitorBufferL[i] < 0.01 ? 0 : monitorBufferL[i];

				if (vuSampleCounter == VU_BUFFER_SIZE) {
					vuSampleCounter = 0;
					float highestl = 0;
					for (float fl : vuBufferL)
						if (fl > highestl) highestl = fl;

					float highestr = 0;
					for (float fl : vuBufferR)
						if (fl > highestr) highestr = fl;

					levelL = highestl;
					levelR = highestr;
					Bootstrap.getGui().getSamplerGui().notify("vumeter");
					vuBufferL = new float[VU_BUFFER_SIZE];
					vuBufferR = new float[VU_BUFFER_SIZE];
				}
			}
		}
		if (arm) {
			arm = false;
			armed = false;
			record();
		}
	}

	public void init(Mpc mpc) {

		this.mpc = mpc;
		Program program = new Program();
		program.setName("NewPgm-A");
		programs.add(program);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 16; j++)
				padNames[(i * 16) + j] = abcd[i] + (Util.padLeft2Zeroes(j + 1));

		SoundLoader sl = new SoundLoader();

		try {
			clickSample = sl.getSampleDataFromWav(new MpcFile(new File(Bootstrap.resPath + "/click.wav")));
			clickSound = new Sound();
			clickSound.setSampleData(clickSample);
			clickSound.setMono(true);
			clickSound.setLevel(100);

		} catch (IOException | WavFileException e) {
			e.printStackTrace();
		}

		masterPadAssign = initMasterPadAssign;

		autoChromaticAssign = new int[64];
		for (int i = 0; i < 64; i++)
			autoChromaticAssign[i] = i;
	}

	public void playMetronome(NoteEvent event, int framePos) {
		SequencerWindowGui swGui = Bootstrap.getGui().getSequencerWindowGui();
		int soundNumber = -2;
		if (swGui.getMetronomeSound() != 0) {
			int program = mpc.getDrum(swGui.getMetronomeSound() - 1).getProgram();
			boolean accent = event.getVelocity() == swGui.getAccentVelo();
			soundNumber = programs.get(program)
					.getNoteParameters(accent ? swGui.getAccentNote() : swGui.getNormalNote()).getSndNumber();
		}
		mpc.getBasicPlayer().mpcNoteOn(soundNumber, event.getVelocity(), framePos);
	}

	public void playPreviewSample(int start, int end, int loopTo, int overlapMode) {
		Sound s = copySound(previewSound);
		s.setStart(start);
		s.setEnd(end);
		s.setLoopTo(loopTo);
		Sound placeHolder = previewSound;
		previewSound = s;
		mpc.getBasicPlayer().noteOn(-3, 127);
		previewSound = placeHolder;
	}

	@Override
	public Program getProgram(int programNumber) {
		if (programs.size() == 0) return null;
		return programs.get(programNumber);
	}

	public int getProgramCount() {
		return programs.size();
	}

	public void addProgram(Program p) {
		programs.add(p);
	}

	public List<Sound> getSounds() {
		return sounds;
	}

	@Override
	public int getSoundCount() {
		return sounds.size();
	}

	public String getSoundName(int i) {
		return sounds.get(i).getName();
	}

	public String getPadName(int i) {
		return padNames[i];
	}

	public List<Program> getPrograms() {
		return programs;
	}

	public float[] getClickSample() {
		return clickSample;
	}

	@Override
	public Sound getSound(int sampleNumber) {
		if (sampleNumber == -1) return null;
		if (sampleNumber >= sounds.size()) return null;
		return sounds.get(sampleNumber);
	}

	public void setPreviewSound(Sound sound) {
		previewSound = sound;
	}

	public Sound getPreviewSound() {
		return previewSound;
	}

	public void setLoopEnabled(int sampleIndex, boolean b) {
		sounds.get(sampleIndex).setLoopEnabled(b);
		if (b == false) return;
		for (Program p : programs) {
			for (int i = 0; i < 64; i++) {
				if (p.getNoteParameters(i + 35).getSndNumber() == sampleIndex) {
					p.getNoteParameters(i + 35).setVoiceOverlap(2);
				}
			}
		}
	}

	public void trimSample(int sampleNumber, long start, long end) {
		Sound s = sounds.get(sampleNumber);
		if (!s.isMono()) {
			start *= 2;
			end *= 2;
		}
		float[] newSampleData = new float[(int) (end - start)];
		for (int i = 0; i < (end - start); i++) {
			newSampleData[i] = s.getSampleData()[(int) (i + start)];
		}
		s.setSampleData(newSampleData);
	}

	public void sort() {
		soundSortingType++;
		if (soundSortingType > 2) soundSortingType = 0;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Bootstrap.getGui().getMainFrame().popupPanel("Sort by " + sortNames[soundSortingType], 85);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							Thread.sleep(70);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Bootstrap.getGui().getMainFrame().removePopup();
					}
				});
			}
		});

	}

	public int getSampleIndexName(int index) {
		int newIndex = 0;
		for (int i = 0; i < sounds.size(); i++) {
			if (sortSamplesByName().get(i).getMemoryIndex() == index) {
				newIndex = i;
				break;
			}
		}
		return newIndex;
	}

	public int getSampleIndexSize(int index) {
		int newIndex = -1;
		for (int i = 0; i < sounds.size(); i++) {
			if (sortSamplesBySize().get(i).getMemoryIndex() == index) {
				newIndex = i;
				break;
			}
		}
		return newIndex;
	}

	private List<Sound> sortSamplesByMemoryIndex(List<Sound> list) {
		Collections.sort(list, new Comparator<Sound>() {
			@Override
			public int compare(Sound s1, Sound s2) {
				if (s1.getMemoryIndex() > s2.getMemoryIndex()) return 1;
				if (s1.getMemoryIndex() < s2.getMemoryIndex()) return -1;
				return 0;
			}
		});
		return list;
	}

	private List<Sound> sortSamplesByName() {
		List<Sound> tempSamples = new ArrayList<Sound>();
		tempSamples.addAll(sounds);

		Collections.sort(tempSamples, new Comparator<Sound>() {
			public int compare(Sound o1, Sound o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return tempSamples;
	}

	private List<Sound> sortSamplesBySize() {
		List<Sound> tempSamples = new ArrayList<Sound>();
		tempSamples.addAll(sounds);
		Collections.sort(tempSamples, new Comparator<Sound>() {
			public int compare(Sound o1, Sound o2) {
				if (o1.getSampleData().length == o2.getSampleData().length) return 0;
				return o1.getSampleData().length < o2.getSampleData().length ? -1 : 1;
			}
		});
		return tempSamples;
	}

	public void deleteSample(int sampleIndex) {
		sounds.remove(sampleIndex);
		for (int i = sampleIndex; i < sounds.size(); i++)
			sounds.get(i).setMemoryIndex(sounds.get(i).getMemoryIndex() - 1);
		for (Program p : programs) {
			for (NoteParameters n : p.getNotesParameters()) {
				if (n.getSndNumber() == sampleIndex) n.setSoundNumber(-1);
				if (n.getSndNumber() > sampleIndex) n.setSoundNumber(n.getSndNumber() - 1);
			}
		}
	}

	public int getSoundSortingType() {
		return soundSortingType;
	}

	public void deleteAllSamples() {
		sounds = new ArrayList<Sound>();
		for (Program p : programs) {
			for (NoteParameters n : p.getNotesParameters())
				n.setSoundNumber(-1);
		}
	}

	public void resample(int soundNumber, int newFs, int quality) {
		Resampler resampler = new Resampler(this, sounds.get(soundNumber), newFs);
		try {
			resampler.resample();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float[] process12Bit(float[] fa) {
		float[] newSampleData = new float[fa.length];
		for (int j = 0; j < newSampleData.length; j++) {
			if (fa[j] != 0f) {
				short fShort = (short) (fa[j] * 32768.0);
				if (fa[j] > 0.9999999f) fShort = 32767;
				String bin = Integer.toBinaryString(0xFFFF & fShort);
				bin = StringUtils.leftPad(bin, 15, '0');
				bin = bin.substring(0, bin.length() - 4) + "0000";
				short newShort = (short) Integer.parseInt(bin, 2);
				newSampleData[j] = (float) (newShort / 32768.0);
			} else {
				newSampleData[j] = 0;
			}
		}
		return newSampleData;
	}

	public float[] process8Bit(float[] fa) {
		float[] newSampleData = new float[fa.length];
		for (int j = 0; j < newSampleData.length; j++) {
			if (fa[j] != 0f) {
				byte fByte = (byte) (fa[j] * 128.0);
				newSampleData[j] = (float) (fByte / 128.0);
			} else {
				newSampleData[j] = 0;
			}
		}
		return newSampleData;
	}

	public Sound createZone(Sound source, int start, int end, int endMargin) {
		int overlap = (int) (endMargin * (source.getSampleRate() / 1000.0));
		if (!source.isMono()) {
			start *= 2;
			end *= 2;
			overlap *= 2;
		}

		if (overlap > end - start) overlap = end - start;
		Sound zone = new Sound(source.getSampleRate());
		int zoneLength = end - start + overlap;
		float[] zoneData = new float[zoneLength];
		for (int i = 0; i < zoneLength; i++)
			zoneData[i] = source.getSampleData()[i + start];
		zone.setSampleData(zoneData);
		zone.setMono(source.isMono());
		return zone;
	}

	public void stopAllVoices() {
		if (mpc.getAudioMidiServices().isDisabled()) return;
		mpc.getBasicPlayer().allSoundOff();
		for (MpcSoundPlayerChannel m : mpc.getDrums())
			m.allSoundOff();
	}

	public void playX(int playXMode, int[] zone) {

		Sound copy = new Sound(previewSound.getSampleRate());
		copy.setName(previewSound.getName());
		copy.setLoopEnabled(previewSound.isLoopEnabled());
		copy.setSampleData(previewSound.getSampleData());
		copy.setMono(previewSound.isMono());
		copy.setEnd(previewSound.getEnd());
		copy.setStart(previewSound.getStart());
		copy.setLoopTo(previewSound.getLoopTo());
		previewSound = copy;
		int start = 0;
		int end = previewSound.getSampleData().length;

		if (!previewSound.isMono()) end /= 2;

		int fullEnd = end;
		if (playXMode == 1) {
			start = zone[0];
			end = zone[1];
		}

		if (playXMode == 2) end = (int) previewSound.getStart();
		if (playXMode == 3) end = (int) previewSound.getLoopTo();
		if (playXMode == 4) {
			start = (int) previewSound.getEnd();
			end = fullEnd;
		}
		playPreviewSample(start, end, -1, 2);
	}

	public long getFreeSampleSpace() {
		long freeSpace = 32 * 1024 * 1000;
		for (Sound s : sounds)
			freeSpace -= s.getSampleData().length * 2;
		return freeSpace;
	}

	private int getLastInt(String s) {
		int offset = s.length();
		for (int i = s.length() - 1; i >= 0; i--) {
			char c = s.charAt(i);
			if (Character.isDigit(c)) {
				offset--;
			} else {
				if (offset == s.length()) return Integer.MIN_VALUE;
				return Integer.parseInt(s.substring(offset));
			}
		}
		return Integer.parseInt(s.substring(offset));
	}

	public String addOrIncreaseNumber(String s) {
		if (s.matches("^.*\\d$")) {
			int lastInt = getLastInt(s);
			s = s.substring(0, s.length() - ("" + lastInt).length());
			lastInt++;
			if (s.length() + ("" + lastInt).length() > 16) s = s.substring(0, (16 - ("" + lastInt).length()));

			s += "" + lastInt;
		} else {
			s += "" + 1;
		}

		for (int i = 0; i < getSoundCount(); i++) {
			if (getSoundName(i).equals(s)) {
				s = addOrIncreaseNumber(s);
				break;
			}
		}
		return s;
	}

	public static Pad getLastPad(Program program) {
		int lastValidPad = Bootstrap.getGui().getSamplerGui().getPad();
		if (lastValidPad == -1) lastValidPad = Bootstrap.getGui().getSamplerGui().getPrevPad();
		return program.getPad(lastValidPad);
	}

	public static NoteParameters getLastNp(Program program) {
		int lastValidNote = Bootstrap.getGui().getSamplerGui().getNote();
		if (lastValidNote == 34) lastValidNote = Bootstrap.getGui().getSamplerGui().getPrevNote();
		return program.getNoteParameters(lastValidNote);

	}

	public int getUnusedSampleAmount() {
		Set<Sound> newSamples = new HashSet<Sound>();
		for (Program p : programs)
			for (NoteParameters nn : p.getNotesParameters())
				if (nn.getSndNumber() != -1) newSamples.add(sounds.get(nn.getSndNumber()));
		return sounds.size() - newSamples.size();
	}

	public void purge() {
		Set<Sound> newSamples = new HashSet<Sound>();

		for (Program p : programs)
			for (NoteParameters nn : p.getNotesParameters())
				if (nn.getSndNumber() != -1) newSamples.add(sounds.get(nn.getSndNumber()));

		List<Sound> newSampleList = new ArrayList<Sound>();
		newSampleList.addAll(newSamples);
		newSampleList = sortSamplesByMemoryIndex(newSampleList);
		sounds = newSampleList;
	}

	public static float[] mergeToStereo(float[] fa0, float[] fa1) {
		int newSampleLength = fa0.length * 2;
		if (fa1.length > fa0.length) newSampleLength = fa1.length * 2;

		float[] newSampleData = new float[newSampleLength];

		int k = 0;
		for (int i = 0; i < newSampleLength; i = i + 2) {
			if (fa0.length > k) {
				newSampleData[i] = fa0[k];
			} else {
				newSampleData[i] = 0f;
			}

			if (fa1.length > k) {
				newSampleData[i + 1] = fa1[k++];
			} else {
				newSampleData[i + 1] = 0f;
			}
		}
		return newSampleData;
	}

	public void setDrumBusProgramNumber(int busNumber, int programNumber) {
		mpc.getDrums()[busNumber - 1].setProgram(programNumber);
	}

	public int getDrumBusProgramNumber(int busNumber) {
		return mpc.getDrums()[busNumber - 1].getProgram();
	}

	public MpcSoundPlayerChannel getDrum(int i) {
		return mpc.getDrum(i);
	}

	@Override
	public MpcSoundOscillatorVariables getClickSound() {
		return clickSound;
	}

	public void arm() { // arm for recording -- input over threshold triggers
						// start
		if (recording) return;
		if (armed) {
			armed = false;
			record();
			return;
		}
		Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentBackground()
				.setBackgroundName("waitingforinputsignal");
		armed = true;
	}

	private void record() {
		int recSize = Bootstrap.getGui().getSamplerGui().getTime() * 4410;
		Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentBackground().setBackgroundName("recording");
		recordBufferL = new float[recSize];
		recordBufferR = new float[recSize];
		recordFrame = 0;
		recording = true;
	}

	public void stopRecordingEarlier() {
		int stopFrameIndex = recordFrame;
		stopRecordingBasic();
		Sound s = previewSound;
		Sound ns = new Sound(44100);
		float[] sampleData = s.getSampleData();
		float[] newSampleData = new float[s.isMono() ? stopFrameIndex : stopFrameIndex * 2];
		for (int i = 0; i < newSampleData.length; i++)
			newSampleData[i] = sampleData[i];
		ns.setSampleData(newSampleData);
		ns.setMono(s.isMono());
		ns.setName(s.getName());
		previewSound = ns;
		Bootstrap.getGui().getMainFrame().openScreen("keeporretry", "windowpanel");
	}

	private void stopRecording() {
		stopRecordingBasic();
		Bootstrap.getGui().getMainFrame().openScreen("keeporretry", "windowpanel");
	}

	private void stopRecordingBasic() {
		int counter = 0;
		Sound s = new Sound(44100);
		s.setName(this.addOrIncreaseNumber("sound000"));
		float[] sampleDataL = new float[preRecBufferL.size() + recordBufferL.length];
		float[] sampleDataR = new float[preRecBufferR.size() + recordBufferR.length];
		Iterator<Float> il = preRecBufferL.iterator();
		Iterator<Float> ir = preRecBufferR.iterator();
		int preRecCounter = 0;
		while (il.hasNext()) {
			while (preRecCounter != 4410 - (Bootstrap.getGui().getSamplerGui().getPreRec() * 44.1))
				preRecCounter++;
			sampleDataL[counter++] = il.next();
		}
		for (float f : recordBufferL)
			sampleDataL[counter++] = f;
		counter = 0;
		preRecCounter = 0;
		while (ir.hasNext()) {
			while (preRecCounter != 4410 - (Bootstrap.getGui().getSamplerGui().getPreRec() * 44.1))
				preRecCounter++;
			sampleDataR[counter++] = ir.next();
		}
		for (float f : recordBufferR)
			sampleDataR[counter++] = f;
		int mode = Bootstrap.getGui().getSamplerGui().getMode();
		if (mode == 2) {
			s.setSampleData(mergeToStereo(sampleDataL, sampleDataR));
			s.setMono(false);
		} else {
			s.setSampleData(mode == 0 ? sampleDataL : sampleDataR);
			s.setMono(true);
		}
		setPreviewSound(s);
		recording = false;
	}

	@Override
	public void setEnabled(boolean enabled) {
	}

	@Override
	public String getName() {
		return "sampler";
	}

	@Override
	public List<AudioInput> getAudioInputs() {
		return Collections.emptyList();
	}

	@Override
	public List<AudioOutput> getAudioOutputs() {
		List<AudioOutput> list = new ArrayList<AudioOutput>();
		list.add(monitorOutput);
		return list;
	}

	@Override
	public void closeAudio() {
		try {
			monitorOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getPeakL() {
		return (int) (peakL * 34.0);
	}

	public int getPeakR() {
		return (int) (peakR * 34.0);
	}

	public int getLevelL() {
		return (int) (levelL * 34.0);
	}

	public int getLevelR() {
		return (int) (levelR * 34.0);
	}

	public void resetPeak() {
		peakL = 0;
		peakR = 0;
	}

	public boolean isArmed() {
		return armed;
	}

	public boolean isRecording() {
		return recording;
	}

	public void unArm() {
		armed = false;
		setSampleBackground();
	}

	public void cancelRecording() {
		recording = false;
		setSampleBackground();
	}

	private void setSampleBackground() {
		Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentBackground().setBackgroundName("sample");
	}

	public int checkExists(String soundName) {
		for (int i = 0; i < getSoundCount(); i++)
			if (getSound(i).getName().replaceAll(" ", "").equalsIgnoreCase(soundName.replaceAll(" ", ""))) return i;
		return -1;
	}

	public int getNextSoundIndex(int j, boolean up) {
		int inc = up ? 1 : -1;
		if (getSoundSortingType() == 0) return j + inc;

		if (getSoundSortingType() == 1) {

			int nextIndex = getSampleIndexName(j) + inc;
			if (nextIndex > getSoundCount() - 1) return j;

			for (int i = 0; i < getSoundCount(); i++)
				if (getSampleIndexName(i) == nextIndex) return i;

		} else if (getSoundSortingType() == 2) {

			int nextIndex = getSampleIndexSize(j) + inc;
			if (nextIndex > getSoundCount() - 1) return j;

			for (int i = 0; i < getSoundCount(); i++)
				if (getSampleIndexSize(i) == nextIndex) return i;

		}
		return j;
	}

	public void setSoundGuiPrevSound() {
		SoundGui soundGui = Bootstrap.getGui().getSoundGui();
		soundGui.setSoundIndex(getNextSoundIndex(soundGui.getSoundIndex(), false));
	}

	public void setSoundGuiNextSound() {
		SoundGui soundGui = Bootstrap.getGui().getSoundGui();
		soundGui.setSoundIndex(getNextSoundIndex(soundGui.getSoundIndex(), true));
	}

	public MpcMixParameters[] getDrumMixer(int i) {
		return mpc.getDrums()[i].getMixParameters();
	}

	public Sound copySound(Sound sound) {
		Sound newSound = new Sound(sound.getSampleRate());
		newSound.setLoopEnabled(sound.isLoopEnabled());
		newSound.setSampleData(sound.getSampleData());
		newSound.setMono(sound.isMono());
		newSound.setEnd(sound.getEnd());
		newSound.setStart(sound.getStart());
		newSound.setLoopTo(sound.getLoopTo());
		return newSound;
	}

	public void setInput(IOAudioProcess input) {
		inputSwap = input;
	}
}