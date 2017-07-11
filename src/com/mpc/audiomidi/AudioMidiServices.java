package com.mpc.audiomidi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;

import com.frinika.toot.javasoundmultiplexed.MultiplexedJavaSoundAudioServer;
import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.vmpc.AudioObserver;
import com.mpc.nvram.NvRam;
import com.mpc.sequencer.FrameSequencer;
import com.mpc.sequencer.Voice;
import com.mpc.tootextensions.DummyAudioProcess;
import com.mpc.tootextensions.MpcBasicSoundPlayerControls;
import com.mpc.tootextensions.MpcFaderControl;
import com.mpc.tootextensions.MpcMixerControls;
import com.mpc.tootextensions.MpcMixerControlsFactory;
import com.mpc.tootextensions.MpcMultiMidiSynth;
import com.mpc.tootextensions.MpcMultiSynthControls;
import com.mpc.tootextensions.MpcSoundPlayerControls;
import com.synthbot.jasiohost.AsioDriver;

import uk.org.toot.audio.basic.tap.TapControls;
import uk.org.toot.audio.core.AudioControlsChain;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.mixer.MixerControlsFactory;
import uk.org.toot.audio.server.ASIOAudioServer;
import uk.org.toot.audio.server.AudioServer;
import uk.org.toot.audio.server.AudioServerConfiguration;
import uk.org.toot.audio.server.AudioServerServices;
import uk.org.toot.audio.server.CompoundAudioClient;
import uk.org.toot.audio.server.IOAudioProcess;
import uk.org.toot.audio.server.JavaSoundAudioServer;
import uk.org.toot.audio.server.NonRealTimeAudioServer;
import uk.org.toot.audio.server.PortAudioServer;
import uk.org.toot.audio.server.TimedAudioServer;
import uk.org.toot.audio.system.DefaultAudioSystem;
import uk.org.toot.audio.system.MixerConnectedAudioSystem;
import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.midi.core.ConnectedMidiSystem;
import uk.org.toot.midi.core.DefaultConnectedMidiSystem;
import uk.org.toot.midi.core.LegacyDevices;
import uk.org.toot.service.ServiceDescriptor;
import uk.org.toot.service.ServiceVisitor;
import uk.org.toot.swingui.audioui.serverui.AudioServerUIServices;
import uk.org.toot.synth.SynthRack;
import uk.org.toot.synth.SynthRackControls;

public class AudioMidiServices extends Observable {

	private boolean disabled;
	private boolean bouncePrepared;
	private boolean bouncing;

	protected TootProperties properties;

	protected AudioServer rtServer;
	protected AudioServer offlineServer;
	protected AudioServerConfiguration serverConfig;

	protected DefaultAudioSystem audioSystem;
	protected ConnectedMidiSystem midiSystem;

	protected AudioMixer mixer;
	protected MpcMixerControls mainMixerControls;
	List<AudioControlsChain> mainMixerStripChains;

	protected AudioServerConfiguration serverSetup;

	private MpcMultiMidiSynth mms;
	private MpcMidiPorts mpcMidiPorts;
	private List<TapControls> tapControls;
	private Mpc mpc;

	private List<String> serverNames;
	private List<String> modelNames;
	private String serverName;

	private List<String> inputNames;
	private List<String> outputNames;

	private int selectedServer = 0;

	private int[] selectedInputs = { 0, -1 }; // [0] RECORD IN, [1] DIGITAL IN
	private int[] selectedOutputs = { 0, 1, -1, -1, -1 };

	private List<ExportAudioProcessAdapter> exportProcesses;

	/*
	 * These processes are for the virtual hardware i/o
	 */

	private IOAudioProcess[] inputProcesses;
	private IOAudioProcess[] outputProcesses;

	private FrameSequencer frameSequencer;

	private List<Integer> availableBufferSizes;
	private HashSet<AsioDriver> usedAsioDrivers = new HashSet<AsioDriver>();

	private int[] oldPrograms;

	public AudioMidiServices(Mpc mpc) {
		this.mpc = mpc;
		properties = new TootProperties(new File(System.getProperty("user.home") + "/Mpc/"));
		setupMidi();
		initServerNames();
	}

	public void init() {
		String lastUsed = properties.getProperty("server");
		serverName = lastUsed;
		int index = -1;
		for (int i = 0; i < serverNames.size(); i++) {
			if (serverNames.get(i).equals(lastUsed)) {
				index = i;
				break;
			}
		}
		setActiveServer(index);
	}

	private List<String> initServerNames() {
		serverNames = new ArrayList<String>();
		modelNames = new ArrayList<String>();
		AudioServerServices.scan();
		AudioServerServices.accept(new ServiceVisitor() {
			public void visitDescriptor(ServiceDescriptor d) {
				serverNames.add(d.getName());
				modelNames.add(d.getDescription());
			}
		}, AudioServer.class);
		return serverNames;
	}

	private void refreshInputNames() {
		inputNames = new ArrayList<String>();
		for (String s : rtServer.getAvailableInputNames()) {
			if (s.contains("Primary Sound") && s.contains("Driver")) continue;
			inputNames.add(s);
		}

		if (isAsio()) inputNames = getOneThird(inputNames);
	}

	private void refreshOutputNames() {
		outputNames = new ArrayList<String>();
		for (String s : rtServer.getAvailableOutputNames()) {
			if (s.contains("Primary Sound") && s.contains("Driver")) continue;
			outputNames.add(s);
		}
		if (isAsio()) outputNames = getOneThird(outputNames);
	}

	private void setupServer(String serverName) throws Exception {

		try {
			createServer(serverName);
		} catch (Exception e) {
			throw e;
		}

		offlineServer = new NonRealTimeAudioServer(rtServer);

		if (offlineServer instanceof NonRealTimeAudioServer) {
			System.out.println("non rt server created.");
		}

		serverConfig = AudioServerServices.createServerConfiguration(rtServer);
		if (serverConfig != null) {
			serverConfig.applyProperties(properties);
			serverConfig.addObserver(new Observer() {
				public void update(Observable obs, Object obj) {
					serverConfig.mergeInto(properties);
					properties.store();
				}
			});
		}
		refreshInputNames();
		refreshOutputNames();
	}

	private void setupMidi() {
		midiSystem = new DefaultConnectedMidiSystem();
		LegacyDevices.installPlatformPorts(midiSystem);
	}

	private void createServer(String serverName) throws Exception {
		try {
			rtServer = AudioServerServices.createServer(serverName);
			System.out.println("\n\nServer " + rtServer.getClass());
			availableBufferSizes = getAvailableBufferSizes();

			serverSetup = AudioServerServices.createServerSetup(rtServer);
		} catch (Exception ae) {
			throw ae;
		}
	}

	private List<Integer> getAvailableBufferSizes() {
		if (!isAsio()) return Collections.EMPTY_LIST;
		ASIOAudioServer asioServer = getAsioAudioServer();
		List<Integer> sizes = new ArrayList<Integer>();
		int size = asioServer.getMinSize();
		System.out.println("Granularity: " + asioServer.getBufferGranularity());
		if (asioServer.getBufferGranularity() == -1) {
			int index = (int) (Math.log(size) / Math.log(2));
			System.out.println("start index: " + index);
			do {
				size = (int) Math.pow(2.0, index++);
				sizes.add(size);
				System.out.println("added size " + size);

			} while (size < asioServer.getMaxSize());
		} else {
			sizes.add(size);
		}
		return sizes;

	}

	private void setupMixer() {
		try {
			mainMixerControls = new MpcMixerControls("Mixer", 1f);
			
			int nMixerChans = intProperty("mixer.chans", 66);

			mainMixerControls.createAuxBusControls("AUX#1", ChannelFormat.STEREO);
			mainMixerControls.createAuxBusControls("AUX#2", ChannelFormat.STEREO);
			mainMixerControls.createAuxBusControls("AUX#3", ChannelFormat.STEREO);
			mainMixerControls.createAuxBusControls("AUX#4", ChannelFormat.STEREO);

			MixerControlsFactory.createBusStrips(mainMixerControls, "L-R", ChannelFormat.STEREO,
					intProperty("mixer.returns", 2));

			mainMixerStripChains = MpcMixerControlsFactory.createRefChannelStrips(mainMixerControls, nMixerChans);

			mixer = new AudioMixer(mainMixerControls, offlineServer);

			audioSystem = new MixerConnectedAudioSystem(mixer);
			audioSystem.setAutoConnect(false);

			setMasterLevel(NvRam.getMasterLevel());
			setGroupLevel(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setGroupLevel(int i) {

		for (int j = 1; j <= 4; j++) {
			AudioControlsChain sc1 = mainMixerControls.getStripControls("AUX#" + j);
			CompoundControl cc1 = (CompoundControl) sc1.getControls().get(0);
			((MpcFaderControl) cc1.getControls().get(2)).setValue(i);
			if (j == 1) {
				System.out.println("sc1 controls:");
				for (Control c : sc1.getControls())
					System.out.println(c.getName());
				System.out.println("cc1 controls:");
				for (Control c : cc1.getControls())
					System.out.println(c.getName() + ", id:" + c.getId() + ", class " + c.getClass());
			}
		}

	}

	private void autoAssignIO() {

		selectedInputs = new int[] { 0, -1 };
		selectedOutputs = new int[] { 0, 1, -1, -1, -1 };

		inputProcesses = new IOAudioProcess[2];
		outputProcesses = new IOAudioProcess[5];

		try {

			for (int i = 0; i < inputNames.size(); i++) {
				if (i > 1) break;
				inputProcesses[i] = rtServer.openAudioInput(inputNames.get(i), AudioObserver.inNames[i]);
				if (inputProcesses[i] != null) selectedInputs[i] = i;
			}

			int candidateInputIndex;

			if (Bootstrap.getGui() != null) {
				candidateInputIndex = Bootstrap.getGui().getSamplerGui().getInput();
				if (candidateInputIndex > inputNames.size()) candidateInputIndex = 0;
			} else {
				candidateInputIndex = 0;
			}

			for (int i = 0; i < outputNames.size(); i++) {
				if (i > 4) break;
				// if (i > 1) break;
				outputProcesses[i] = rtServer.openAudioOutput(outputNames.get(i), AudioObserver.outNames[i]);
				if (outputProcesses[i] != null) {
					selectedOutputs[i] = i;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public DefaultAudioSystem getAudioSystem() {
//		return audioSystem;
//	}

	public AudioMixer getMixer() {
		return mixer;
	}

	public AudioServer getAudioServer() {
		return offlineServer;
	}

	public ConnectedMidiSystem getMidiSystem() {
		return midiSystem;
	}

	public void setMasterLevel(int i) {
		AudioControlsChain sc = mainMixerControls.getStripControls("L-R");
		CompoundControl cc = (CompoundControl) sc.getControls().get(0);
		((MpcFaderControl) cc.getControls().get(2)).setValue(i);
	}

//	public List<AudioControlsChain> getChains() {
//		return mainMixerStripChains;
//	}

	protected String property(String key) {
		return properties.getProperty(key);
	}

	protected int intProperty(String key, int def) {
		String prop = property(key);
		return prop == null ? def : Integer.parseInt(prop);
	}

	public List<String> getInputNames() {
		return inputNames;
	}

	public List<String> getOutputNames() {
		return outputNames;
	}

	public void saveServerSetupProperties() {
		if (serverSetup != null) {
			serverSetup.applyProperties(properties);
			serverSetup.addObserver(new Observer() {
				public void update(Observable obs, Object obj) {
					serverSetup.mergeInto(properties);
					properties.store();
				}
			});
		}

	}

	public void saveServerProperties() {
		if (disabled) return;
		properties.put("server", serverName);
		properties.store();
	}

	public String getLastUsedServerName() {
		return serverName;
	}

	private void showSetupDialog() {
		AudioServerUIServices.showSetupDialog(rtServer, serverSetup);
	}

	public void startTapControls() {

		// tapControls = new ArrayList<TapControls>();
		// for (AudioControlsChain chain : getChains()) {
		// TapControls tc = new TapControls();
		// tc.reference(1);
		// tc.setEnabled(true);
		// tapControls.add(tc);
		// chain.insert(tc, null);
		// }

	}

	public List<TapControls> getTapControls() {
		return tapControls;
	}

	public MpcMultiMidiSynth getMms() {
		return mms;
	}

	private void createMpcSoundPlayerChannels() {
		MpcMultiSynthControls msc = new MpcMultiSynthControls();
		SynthRackControls synthRackControls = new SynthRackControls(1);
		SynthRack synthRack = new SynthRack(synthRackControls, getMidiSystem(), audioSystem);
		synthRackControls.setSynthControls(0, msc);
		mms = (MpcMultiMidiSynth) synthRack.getMidiSynth(0);
		ConcurrentLinkedQueue<Voice> voices = new ConcurrentLinkedQueue<Voice>();
		for (int i = 1; i <= 32; i++) {
			voices.add(new Voice(i, false));
		}

		for (int i = 0; i < 4; i++) {
			MpcSoundPlayerControls m = new MpcSoundPlayerControls(mpc.getSampler(), i, mixer, voices, rtServer);
			msc.setChannelControls(i, m);
		}

		MpcBasicSoundPlayerControls m = new MpcBasicSoundPlayerControls(mpc.getSampler(), getMixer(),
				new Voice(65, true));
		msc.setChannelControls(4, m);
	}

	public void connectVoices() {
		mpc.getDrums()[0].connectVoices();
		mpc.getBasicPlayer().connectVoice();
	}

	public MpcMidiPorts getMidiPorts() {
		return mpcMidiPorts;
	}

	public String getServerNameAndModel(int i) {
		if (serverNames.get(i).contains("JavaSound") || serverNames.get(i).contains("ASIO")) return serverNames.get(i);
		return serverNames.get(i) + " (" + modelNames.get(i) + ")";
	}

	public int getActiveServerIndex() {
		int result = -1;
		for (int i = 0; i < serverNames.size(); i++)
			if (serverNames.get(i).equals(serverName)) {
				result = i;
				break;
			}
		return result;
	}

	public void setSelectedServer(int i) {
		if (i < 0 || i > serverNames.size() - 1) return;
		selectedServer = i;
		setChanged();
		notifyObservers("selectedserver");
	}

	public void create(String serverName, int bufferSize) throws Exception {
		try {
			setupServer(serverName);
			setupMixer();
			autoAssignIO();
			createMpcSoundPlayerChannels();
			connectVoices();
			mpcMidiPorts = new MpcMidiPorts(getMidiSystem(), mpc);
			mpcMidiPorts.setMidiIn1(-1);
			mpcMidiPorts.setMidiIn2(-1);
			mpcMidiPorts.setMidiOutA(-1);
			mpcMidiPorts.setMidiOutB(-1);

			mpc.getSampler().setInput(inputProcesses[0]);

			// for (int i = 0; i < 32; i++) {
			// AudioControlsChain sc =
			// mixer.getMixerControls().getStripControls("" + (i + 32 + 1));
			// }
			mixer.getStrip("66").setInputProcess(mpc.getSampler().getAudioOutputs().get(0));

			initializeDiskWriter();

			// audioStreamSequencer = new AudioStreamSequencer();
			frameSequencer = new FrameSequencer();

			CompoundAudioClient cac = new CompoundAudioClient();
			cac.add(frameSequencer);
			cac.add(mixer);
			cac.add(mpc.getSampler());
			getAudioServer().setClient(cac);

			if (isAsio()) {
				usedAsioDrivers.add(getAsioAudioServer().getDriver());
				if (bufferSize != -1) {
					getAsioAudioServer().setBufferSize(bufferSize);
				}
			} else if (isCoreAudio()) {
				if (bufferSize != -1) {
					getCoreAudioServer().setBufferSize(bufferSize);
				}
			}
			if (oldPrograms != null) {
				for (int i = 0; i < 4; i++)
					mpc.getDrum(i).setProgram(oldPrograms[i]);
			}
			rtServer.start();
		} catch (Exception e) {
			throw e;
		}
	}

	public PortAudioServer getCoreAudioServer() {
		return (PortAudioServer) rtServer;
	}

	public TimedAudioServer getTimedAudioServer() {
		return (TimedAudioServer) rtServer;
	}

	public boolean isAsio() {
		return rtServer instanceof ASIOAudioServer;
	}

	public boolean isCoreAudio() {
		return rtServer instanceof PortAudioServer;
	}

	public boolean isJava() {
		return rtServer instanceof JavaSoundAudioServer || rtServer instanceof MultiplexedJavaSoundAudioServer;
	}

	public void setJavaLatency(int ms) {

	}

	private void initializeDiskWriter() throws Exception {
		exportProcesses = new ArrayList<ExportAudioProcessAdapter>();

		AudioFormat format = new AudioFormat(44100, 16, 2, true, false);

		ExportAudioProcessAdapter diskWriter = null;
		if (outputProcesses[0] != null) {
			diskWriter = new ExportAudioProcessAdapter(outputProcesses[0], format, "diskwriter");
		} else {
			diskWriter = new ExportAudioProcessAdapter(new DummyAudioProcess(), format, "diskwriter");
		}

		exportProcesses.add(diskWriter);
		mixer.getMainBus().setOutputProcess(diskWriter);

		for (int i = 1; i <= 4; i++) {
			if (outputProcesses[i] != null) {
				diskWriter = new ExportAudioProcessAdapter(outputProcesses[i], format, "diskwriter");
			} else {
				diskWriter = new ExportAudioProcessAdapter(new DummyAudioProcess(), format, "diskwriter");
			}
			mixer.getStrip("AUX#" + i).setDirectOutputProcess(diskWriter);
			exportProcesses.add(diskWriter);
		}
	}

	public void destroyServices() {
		if (disabled) return;
		oldPrograms = new int[4];

		for (int i = 0; i < 4; i++)
			oldPrograms[i] = mpc.getDrum(i).getProgram();

		cleanupVoices();
		mms.closeAudio();
		mms.closeMidi();
		audioSystem.removeAudioDevice(mms);
		audioSystem.close();
		mixer.close();
		disconnectIO();
		rtServer.stop();
		mpcMidiPorts.close();
	}

	private void cleanupVoices() {
		mpc.getDrum(0).cleanupVoices();
	}

	public boolean setActiveServer(int i) {
		if (i == -1) {
			disable();
			return false;
		}
		// System.out.println("Creating server with index " + i + ", name " +
		// serverNames.get(i));
		int oldServerIndex = getActiveServerIndex();
		// if (serverNames.get(i).equals(serverName)) return true;
		if (rtServer != null) destroyServices();
		boolean firstAttemptSuccess = false;
		try {
			create(serverNames.get(i), -1);
			firstAttemptSuccess = true;
		} catch (Exception e) {
			firstAttemptSuccess = false;
			e.printStackTrace();
		}

		if (!firstAttemptSuccess) {
			if (oldServerIndex >= 0) {
				try {
					create(serverNames.get(oldServerIndex), -1);
					return false;
				} catch (Exception e1) {
					disable();
					return false;
				}
			} else {
				disable();
				return false;
			}
		}

		disabled = false;
		serverName = serverNames.get(i);
		setChanged();
		notifyObservers("selectedserver");
		return true;
	}

	private void disconnectIO() {

		for (int j = 0; j < 2; j++) {
			if (inputProcesses[j] == null) continue;
			rtServer.closeAudioInput(inputProcesses[j]);
		}

		for (int j = 0; j < 5; j++) {
			if (outputProcesses[j] == null) continue;
			rtServer.closeAudioOutput(outputProcesses[j]);
		}

	}

	public int getSelectedServer() {
		return selectedServer;
	}

	public void prepareBouncing(DirectToDiskSettings settings) {
		String[] indivFileNames = { "L-R", "1-2", "3-4", "5-6", "7-8" };
		for (int i = 0; i < 5; i++) {
			ExportAudioProcessAdapter eapa = exportProcesses.get(i);
			eapa.prepare(new File(Bootstrap.home + "/Mpc/recordings/" + indivFileNames[i]), settings.lengthInFrames);
		}
		bouncePrepared = true;
	}

	public void startBouncing() {
		if (!bouncePrepared) return;
		for (ExportAudioProcessAdapter eapa : exportProcesses)
			eapa.start();
		bouncePrepared = false;
		bouncing = true;
	}

	public void stopBouncing() {
		if (!bouncing) return;
		for (ExportAudioProcessAdapter eapa : exportProcesses)
			((ExportAudioProcessAdapter) eapa).stop();
		for (ExportAudioProcessAdapter eapa : exportProcesses)
			((ExportAudioProcessAdapter) eapa).writeWav();
		Bootstrap.getGui().getMpc().getSequencer().stop();
		Bootstrap.getGui().getMainFrame().openScreen("recordingfinished", "windowpanel");
		bouncing = false;
	}

	public FrameSequencer getFrameSequencer() {
		return frameSequencer;
	}

	public boolean isBouncePrepared() {
		return bouncePrepared;
	}

	public boolean isBouncing() {
		return bouncing;
	}

	private void disable() {
		// retain parameters of mpcSoundPlayerChannels, mixers
		disabled = true;
		offlineServer = null;
		rtServer = null;
		mixer = null;

		serverConfig = null;

		audioSystem = null;

		mainMixerControls = null;
		mainMixerStripChains = null;

		serverSetup = null;

		mms = null;
		mpcMidiPorts = null;
		tapControls = null;

		inputNames = null;
		outputNames = null;

		selectedInputs = new int[] { 0, -1 };
		selectedOutputs = new int[] { 0, 1, -1, -1, -1 };
	}

	public boolean isDisabled() {
		return disabled;
	}

	public IOAudioProcess getAudioInput(int input) {
		return inputProcesses[input];
	}

	public ASIOAudioServer getAsioAudioServer() {
		return (ASIOAudioServer) rtServer;
	}

	public void setBufferSize(int size) {
		destroyServices();
		try {
			create(serverName, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getBufferSize() {
		return rtServer.getOutputLatencyFrames();
	}

	/*
	 * ASIO only methods
	 */

	public void increaseBufferSize() {
		int candidate = getBufferSizeIndex() + 1;
		if (candidate >= availableBufferSizes.size()) return;
		setBufferSize(availableBufferSizes.get(candidate));
	}

	public void decreaseBufferSize() {
		int candidate = getBufferSizeIndex() - 1;
		if (candidate < 0) return;
		setBufferSize(availableBufferSizes.get(candidate));
	}

	private int getBufferSizeIndex() {
		int counter = 0;
		for (Integer i : availableBufferSizes) {
			if (i.intValue() == this.getBufferSize()) return counter;
			counter++;
		}
		return -1;
	}

	public void unloadAsioDrivers() {
		Iterator<AsioDriver> drivers = usedAsioDrivers.iterator();

		while (drivers.hasNext()) {
			AsioDriver driver = drivers.next();
			try {
				driver.exit();
				driver.shutdownAndUnloadDriver();
				System.out.println("Closed " + driver.getName());
			} catch (Exception e) {
				System.out.println(driver.getName() + " already closed.");
			}
		}
	}
	
	private List<String> getOneThird(List<String> list) {
		List<String> temp = new ArrayList<String>();
		for (int i = 0; i < list.size() / 3; i++)
			temp.add(list.get(i));
		return temp;
	}
}