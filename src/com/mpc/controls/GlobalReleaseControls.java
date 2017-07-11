package com.mpc.controls;

import java.awt.event.KeyEvent;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcSoundPlayerChannel;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;

public class GlobalReleaseControls implements ReleaseControls {

	protected Gui gui;
	protected SamplerGui samplerGui;
	protected MainFrame mainFrame;
	protected LayeredScreen ls;
	protected String csn;
	protected Mpc mpc;
	protected Sampler sampler;
	protected Sequencer sequencer;
	protected MpcTrack track;
	protected MpcSoundPlayerChannel mpcSoundPlayerChannel;
	protected Program program;
	protected KbMouseController kbmc;
	protected int bank;

	protected void init() {
		gui = Bootstrap.getGui();
		samplerGui = gui.getSamplerGui();
		mainFrame = gui.getMainFrame();
		ls = mainFrame.getLayeredScreen();
		csn = ls.getCurrentScreenName();
		mpc = gui.getMpc();
		sampler = mpc.getSampler();
		sequencer = mpc.getSequencer();
		track = sequencer.getActiveSequence().getTrack(sequencer.getActiveTrackIndex());
		int drum = track.getBusNumber() - 1;
		if (drum >= 0 && !mpc.getAudioMidiServices().isDisabled()) {
			mpcSoundPlayerChannel = sampler.getDrum(drum);
			program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());
		}
		
		kbmc = (KbMouseController) gui.getKb();
		bank = samplerGui.getBank();
	}

	public void keyEvent(KeyEvent e) {
		init();
		int kc = e.getKeyCode();
		if (kc == KbMapping.goTo) KbMouseController.goToIsPressed = false;

		if (kc == KbMapping.rec) {
			KbMouseController.recIsPressed = false;
			if (sequencer.isRecording()) return;
			mainFrame.getLedPanel().setRec(false);
		}

		if (kc == KbMapping.overdub) {
			KbMouseController.overdubIsPressed = false;
			if (sequencer.isOverDubbing()) return;
			mainFrame.getLedPanel().setOverDub(false);
		}

		if (csn.equals("trackmute") && kc == KeyEvent.VK_F6 && !sequencer.isSoloEnabled()) {
			mainFrame.getLayeredScreen().removeCurrentBackground();
			mainFrame.getLayeredScreen().setCurrentBackground("trackmute");
			KbMouseController.f6IsPressed = false;
			return;
		}

		if (KbMouseController.getPressedPad(e) != -1) {
			int pressedPad = KbMouseController.getPressedPad(e);
			pad(pressedPad);
		}

		if (csn.equals("step_tc") || csn.equals("sequencer_step")) new StepEditorKbRelease().stepEditorKbRelease(e);

		if ((kc == KeyEvent.VK_F6 || kc == KeyEvent.VK_F5) && !sequencer.isPlaying() && !csn.equals("sequencer"))
			sampler.stopAllVoices();
	}

	public void pad(int i) {
		init();
		KbMouseController.pressedPads.remove(new Integer(i));
		if (csn.equals("loadasound")) return;
		int note = track.getBusNumber() > 0 ? program.getPad(i + (bank * 16)).getNote() : i + (bank * 16) + 35;
		generateNoteOff(note);
		if (csn.equals("sequencer_step")) {
			int newDur = (int) mpc.getAudioMidiServices().getFrameSequencer().getTickPosition();
			sequencer.stopMetronomeTrack();
			track.adjustDurLastEvent(newDur);
		}
	}

	private void generateNoteOff(int nn) {
		init();
		if (sequencer.isRecordingOrOverdubbing()) {
			NoteEvent n = new NoteEvent();
			n.setNote(nn);
			n.setVelocity(0);
			n.setTick(sequencer.getTickPosition());
			track.recordNoteOff(n);
		}

		NoteEvent noteEvent = new NoteEvent(nn);
		noteEvent.setVelocity(0);
		noteEvent.setDuration(0);
		noteEvent.setTick(-1);
		mpc.getEventHandler().handle(noteEvent, track);
	}

	public void overDub() {
		KbMouseController.overdubIsPressed = false;
		init();
		mainFrame.getLedPanel().setOverDub(sequencer.isOverDubbing());
	}

	public void rec() {
		KbMouseController.recIsPressed = false;
		init();
		mainFrame.getLedPanel().setRec(sequencer.isRecording());
	}

	public void tap() {
		KbMouseController.tapIsPressed = false;
		if (sequencer.isRecordingOrOverdubbing()) sequencer.flushTrackNoteCache();
	}

	public void shift() {
		KbMouseController.shiftIsPressed = false;
	}

	public void erase() {
		KbMouseController.eraseIsPressed = false;
	}
}