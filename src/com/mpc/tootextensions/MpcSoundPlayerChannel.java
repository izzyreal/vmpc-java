package com.mpc.tootextensions;

import java.util.concurrent.ConcurrentLinkedQueue;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioBuffer.MetaInfo;
import uk.org.toot.audio.core.AudioControlsChain;
import uk.org.toot.audio.mixer.AudioMixer;
import uk.org.toot.audio.mixer.AudioMixerStrip;
import uk.org.toot.audio.mixer.MainMixControls;
import uk.org.toot.audio.mixer.MixControls.PanControl;
import uk.org.toot.audio.server.AudioServer;
import uk.org.toot.control.CompoundControl;
import uk.org.toot.synth.SynthChannel;

public class MpcSoundPlayerChannel extends SynthChannel implements MpcDrum {

	private final MpcSampler sampler;
	private final AudioMixer mixer;

	private static ConcurrentLinkedQueue<MpcVoice> unusedVoices;
	private final static ConcurrentLinkedQueue<MpcVoice> voices = new ConcurrentLinkedQueue<MpcVoice>();

	private int programNumber;
	private boolean receivePgmChange = true;
	private boolean receiveMidiVolume = true;
	private AudioBuffer.MetaInfo info;

	private ConcreteMixParameters[] mixParametersArray;

	private final int index;

	private MpcMixerInterconnection[] mixerConnections;
	private MpcProgram program;
	private int padNumber;
	private MpcNoteParameters np;
	private int soundNumber;
	private MpcVoice voice;
	private MpcSoundOscillatorVariables vars;
	private MpcMixParameters mmp;
	private AudioControlsChain audioControlsChain;
	private MainMixControls mainMixControls;
	private MpcFaderControl faderControl;
	private int auxBus;
	private CompoundControl compoundControl;
	private AudioServer server;

	public MpcSoundPlayerChannel(MpcSoundPlayerControls controls) {

		index = controls.getDrumNumber();
		sampler = controls.getSampler();
		mixer = controls.getMixer();
		server = controls.getServer();
		
		
		if (index == 0) {
			unusedVoices = controls.getVoices();
		}

		mixParametersArray = new ConcreteMixParameters[64];
		for (int i = 0; i < 64; i++)
			mixParametersArray[i] = new ConcreteMixParameters();

	}

	// implement mpc drum
	@Override
	public void setProgram(int i) {
		if (i < 0 || i > sampler.getProgramCount() - 1) return;
		programNumber = i;
		setChanged();
		notifyObservers("pgm");
	}

	@Override
	public int getProgram() {
		return programNumber;
	}

	@Override
	public boolean receivesPgmChange() {
		return receivePgmChange;
	}

	@Override
	public void setReceivePgmChange(boolean b) {
		receivePgmChange = b;
		setChanged();
		notifyObservers("receivepgmchange");
	}

	@Override
	public boolean receivesMidiVolume() {
		return receiveMidiVolume;
	}

	@Override
	public void setReceiveMidiVolume(boolean b) {
		receiveMidiVolume = b;
		setChanged();
		notifyObservers("receivemidivolume");
	}

	@Override
	public void setLocation(String location) {
		info = new AudioBuffer.MetaInfo("soundplayerchannel", location);
	}

	// implement midichannel

	@Override
	public void noteOn(int note, final int velo) {
		mpcNoteOn(-1, note, velo, 0, 64, 0);
	}

	public void mpcNoteOn(final int track, final int note, final int velo, final int varType, final int varValue,
			final int frameOffset) {
		// System.out.println("note " + note + " velo " + velo);
		if (note < 35 || note > 98) return;
		if (velo == 0) return;
		program = sampler.getProgram(programNumber);
		padNumber = program.getPadNumberFromNote(note);
		np = program.getNoteParameters(note);

		checkForMutes(np);

		soundNumber = np.getSndNumber();

		if (soundNumber == -1) return;

		if (unusedVoices.size() == 0) return;

		voice = unusedVoices.poll();
		voices.add(voice);
		vars = sampler.getSound(soundNumber);

		mmp = program.getPadMixer(padNumber);

		audioControlsChain = mixer.getMixerControls().getStripControls("" + (voice.getStripNumber()));
		mainMixControls = (MainMixControls) audioControlsChain.getControls().get(4);
		((PanControl) mainMixControls.getControls().get(0)).setValue((float) ((mmp.getPanning()) / 100.0));
		((MpcFaderControl) mainMixControls.getControls().get(3)).setValue(mmp.getLevel());

		audioControlsChain = mixer.getMixerControls().getStripControls("" + (voice.getStripNumber() + 32));
		mainMixControls = (MainMixControls) audioControlsChain.getControls().get(4);

		if (mmp.getOutput() > 0) {
			if (vars.isMono()) {
				if (mmp.getOutput() % 2 == 1) {
					mixerConnections[voice.getStripNumber() - 1].setLeftEnabled(true);
					mixerConnections[voice.getStripNumber() - 1].setRightEnabled(false);
				} else {
					mixerConnections[voice.getStripNumber() - 1].setLeftEnabled(false);
					mixerConnections[voice.getStripNumber() - 1].setRightEnabled(true);
				}
			} else {
				mixerConnections[voice.getStripNumber() - 1].setLeftEnabled(true);
				mixerConnections[voice.getStripNumber() - 1].setRightEnabled(true);
			}
		}

		/*
		 * // set strip volume to -inf
		 */

		faderControl = (MpcFaderControl) mainMixControls.getControls().get(3);
		if (faderControl.getValue() != 0) faderControl.setValue(0);

		/*
		 * set aux send to indiv vol
		 */
		auxBus = (int) (Math.ceil((mmp.getOutput() - 2) / 2.0));

		for (int i = 0; i < 4; i++) {
			compoundControl = (CompoundControl) audioControlsChain.getControls().get(i);
			if (i == auxBus) {
				((MpcFaderControl) compoundControl.getControls().get(2)).setValue(mmp.getVolumeIndividualOut());
			} else {
				((MpcFaderControl) compoundControl.getControls().get(2)).setValue(0);
			}
		}

		// TapControls tc = (TapControls) sc.getControls().get(6);
		// tc.getBuffer().setMetaInfo(new MetaInfo("" + track));

		stopPad(padNumber, 1);

		voice.init(track, velo, padNumber, vars, np, varType, varValue, note, index, frameOffset, true);
	}

	private void checkForMutes(MpcNoteParameters np) {

		if (np.getMuteAssignA() != 34 || np.getMuteAssignB() != 34) {

			for (MpcVoice v : voices) {
				if (v.getMuteInfo() == null) continue;
				if (v.getMuteInfo().muteMe(np.getMuteAssignA(), index)
						|| v.getMuteInfo().muteMe(np.getMuteAssignB(), index)) {
					v.startDecay();
				}
			}
		}
	}

	private void stopPad(int p, int o) {
		for (MpcVoice v : voices) {
			if (v.getPadNumber() == p && v.getVoiceOverlap() == o && !v.isDecaying() && index == v.getMuteInfo().getDrum()) {
				v.startDecay();
				break;
			}
		}
	}

	@Override
	public void noteOff(int note) {
		if (note < 35 || note > 98) return;
		stopPad(sampler.getProgram(programNumber).getPadNumberFromNote(note), 2);
	}

	@Override
	public void allNotesOff() {
	}

	@Override
	public void allSoundOff() {
		for (MpcVoice v : voices) // Real MPC only kills voices from note off
									// pads.
			v.startDecay();
	}

	public void connectVoices() {
		mixerConnections = new MpcMixerInterconnection[32];
		Object[] voiceArray = unusedVoices.toArray();
		try {
			for (int j = 0; j < 32; j++) {

				AudioMixerStrip ams1 = mixer.getStrip("" + (j + 1));
				ams1.setInputProcess((MpcVoice) voiceArray[j]);

				MpcMixerInterconnection mi = new MpcMixerInterconnection("con" + j, server);
				ams1.setDirectOutputProcess(mi.getInputProcess());

				AudioMixerStrip ams2 = mixer.getStrip("" + (j + 1 + 32));
				ams2.setInputProcess(mi.getOutputProcess());
				mixerConnections[j] = mi;

				((MpcVoice) voiceArray[j]).setParent(this);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void kill(MpcVoice mpcVoice) {
		unusedVoices.add(mpcVoice);
		voices.remove(mpcVoice);
	}

	public MetaInfo getInfo() {
		return info;
	}

	public MpcMixParameters[] getMixParameters() {
		return mixParametersArray;
	}

	public int getDrumNumber() {
		return index;
	}

	public void cleanupVoices() {
		unusedVoices.clear();
		voices.clear();
	}

	public void mpcNoteOff(int note, int frameOffset) {
		if (note < 35 || note > 98) return;
		stopPad(sampler.getProgram(programNumber).getPadNumberFromNote(note), 2, frameOffset);
	}

	private void stopPad(int p, int o, int offset) {
		for (MpcVoice v : voices) {
			if (v.getPadNumber() == p && v.getVoiceOverlap() == o && !v.isDecaying() && index == v.getMuteInfo().getDrum()) {
				v.startDecay(offset);
				break;
			}
		}
	}
}