package com.mpc;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

import javax.swing.SwingUtilities;

import com.mpc.audiomidi.AudioMidiServices;
import com.mpc.audiomidi.EventHandler;
import com.mpc.audiomidi.MpcMidiInput;
import com.mpc.audiomidi.MpcMidiPorts;
import com.mpc.disk.Disk;
import com.mpc.disk.ProgramLoader;
import com.mpc.disk.SoundLoader;
import com.mpc.disk.Stores;
import com.mpc.file.wav.WavFileException;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.vmpc.MidiGui;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcBasicSoundPlayerChannel;
import com.mpc.tootextensions.MpcMultiMidiSynth;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

import uk.org.toot.audio.basic.tap.TapControls;

public class Mpc extends Observable {

	public static char[] akaiAsciiChar = { ' ', '!', '#', '$', '%', '&', '\'', '(', ')', '-', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '}' };

	public final static String[] akaiAscii = { " ", "!", "#", "$", "%", "&", "'", "(", ")", "-", "0", "1", "2", "3",
			"4", "5", "6", "7", "8", "9", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "_", "a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "}" };

	private Sequencer sequencer;
	private Sampler sampler;
	private AudioMidiServices audioMidiServices;
	private ProgramLoader programLoader;

	MpcMidiInput[] mpcMidiInputs;

	private EventHandler eventHandler;

	private DiskController diskController;

	private MidiGui midiGui;

	public Mpc() {
		diskController = new DiskController();
	}

	public void init() {
		audioMidiServices = new AudioMidiServices(this);
		sampler = new Sampler();
		sampler.init(this);
		sequencer = new Sequencer(this);
		
		audioMidiServices.init();
		
		mpcMidiInputs = new MpcMidiInput[] { new MpcMidiInput(0, this), new MpcMidiInput(1, this) };
		eventHandler = new EventHandler(this);
		
	}

	public Sequencer getSequencer() {
		return sequencer;
	}

	public Sampler getSampler() {
		return sampler;
	}

	public Disk getDisk() {
		return diskController.getDisk();
	}

	public void loadSound(boolean replace) throws InterruptedException, IOException, WavFileException {
		final SoundLoader soundLoader = new SoundLoader(sampler.getSounds(), replace);
		soundLoader.setPreview(true);
		soundLoader.setPartOfProgram(false);
		boolean exists = soundLoader.loadSound(Bootstrap.getGui().getDiskGui().getSelectedFile()) != -1;
		if (exists) {
			Bootstrap.getGui().getDiskGui().removePopup();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						int sleepTime = soundLoader.getSize() / 400;
						if (sleepTime < 300) sleepTime = 300;
						Thread.sleep(sleepTime / 2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Bootstrap.getGui().getDiskGui().removePopup();
					getDisk().setBusy(false);
					getDisk().openLoadASound();
				}
			});
		}
	}

	public void loadProgram() throws InterruptedException, IOException, WavFileException {
		programLoader = new ProgramLoader(this, Bootstrap.getGui().getDiskGui().getSelectedFile(),
				Bootstrap.getGui().getDiskGui().getLoadReplaceSound());
		programLoader.loadProgram();
	}

	public void importLoadedProgram() {
		MpcTrack t = sequencer.getActiveSequence().getTrack(sequencer.getActiveTrackIndex());

		if (Bootstrap.getGui().getDiskGui().getClearProgramWhenLoading()) {
			int pgm = getDrum(t.getBusNumber() - 1).getProgram();
			sampler.getPrograms().set(pgm, programLoader.get());
		} else {
			sampler.getPrograms().add(programLoader.get());
			getDrum(t.getBusNumber() - 1).setProgram(sampler.getProgramCount() - 1);
		}
	}

	public void startMidi() {		
		midiGui = Bootstrap.getGui().getMidiGui();
	}

	public MpcSoundPlayerChannel getDrum(int i) {
		return (MpcSoundPlayerChannel) audioMidiServices.getMms().getChannel(i);
	}

	public MpcBasicSoundPlayerChannel getBasicPlayer() {
		return (MpcBasicSoundPlayerChannel) audioMidiServices.getMms().getChannel(4);
	}

	public AudioMidiServices getAudioMidiServices() {
		return audioMidiServices;
	}

	public Disk getDisk(int i) {
		return diskController.getDisk(i);
	}

	public void initDisks() {
		diskController.initDisks();
	}

	public Stores getStores() {
		return diskController.getStores();
	}

	public int getRawStoresAmount() {
		return diskController.getStores().getRawStores().size();
	}

	public int getJavaStoresAmount() {
		return diskController.getStores().getJavaStores().size();
	}

	public MpcMidiInput getMpcMidiInput(int i) {
		return mpcMidiInputs[i];
	}

	public List<TapControls> getTapControls() {
		return audioMidiServices.getTapControls();
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public MpcMidiPorts getMidiPorts() {
		return audioMidiServices.getMidiPorts();
	}

	public MpcMultiMidiSynth getMms() {
		return audioMidiServices.getMms();
	}

	public MpcSoundPlayerChannel[] getDrums() {
		MpcSoundPlayerChannel[] drums = new MpcSoundPlayerChannel[4];
		for (int i = 0; i < 4; i++)
			drums[i] = (MpcSoundPlayerChannel) audioMidiServices.getMms().getChannel(i);
		return drums;
	}
}