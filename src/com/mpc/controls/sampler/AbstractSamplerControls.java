package com.mpc.controls.sampler;

import com.mpc.controls.AbstractControls;
import com.mpc.controls.KbMouseController;
import com.mpc.gui.components.MpcTextField;
import com.mpc.gui.sampler.SoundGui;
import com.mpc.gui.sampler.window.EditSoundGui;
import com.mpc.gui.sampler.window.SamplerWindowGui;
import com.mpc.gui.sampler.window.ZoomGui;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Pad;
import com.mpc.sampler.Sampler;
import com.mpc.sampler.Sound;
import com.mpc.sequencer.MpcTrack;

public abstract class AbstractSamplerControls extends AbstractControls {

	protected ZoomGui zoomGui;
	protected SoundGui soundGui;
	protected Sound sound;
	protected SamplerWindowGui swGui;

	protected EditSoundGui editSoundGui;
	protected Pad lastPad;
	protected NoteParameters lastNp;
	
	protected boolean splittable = false;
	protected int[] splitInc = { 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1 };
	
	protected void init() {
		super.init();
		zoomGui = gui.getZoomGui();
		soundGui = gui.getSoundGui();
		editSoundGui = gui.getEditSoundGui();
		sampler = mpc.getSampler();
		sequencer = mpc.getSequencer();
		samplerGui = gui.getSamplerGui();
		swGui = gui.getSamplerWindowGui();
		if (sampler.getSoundCount() != 0) sound = sampler.getSound(soundGui.getSoundIndex());

		// if (csn.equals("programassign")) {
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		// } else {
		// mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getTrackDrum());
		// }

		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());
		if (csn.equals("programassign")) {
			lastPad = Sampler.getLastPad(program);
			int note = lastPad.getNote();
			lastNp = program.getNoteParameters(note);
		} else {
			lastNp = Sampler.getLastNp(program);
		}
		
		splittable = param.equals("st") || param.equals("end") || param.equals("to") || param.equals("endlengthvalue"); 
	}

	protected int getSoundIncrement(int notch_inc) {
		int soundInc = notch_inc;
		if (Math.abs(notch_inc) != 1) soundInc *= (int) Math.ceil(sound.getLastFrameIndex() / 15000.0);
		return soundInc;
	}

	protected void checkProgramReferences() {
		MpcTrack t = (MpcTrack) sequencer.getSequence(sequencer.getActiveSequenceIndex())
				.getTrack(sequencer.getActiveTrackIndex());

		for (int i = 0; i < 4; i++) {
			if (sampler.getDrumBusProgramNumber(t.getBusNumber()) > sampler.getProgramCount() - 1) {
				sampler.setDrumBusProgramNumber(t.getBusNumber(), sampler.getProgramCount() - 1);
			}

		}
	}

	protected void splitLeft() {
		init();
		MpcTextField mtf = (MpcTextField) mainFrame.lookupTextField(param);
		if (KbMouseController.shiftIsPressed) {
			if (splittable) {
				if (!mtf.isSplit()) {
					mtf.setSplit(true);
				} else {
					mtf.setActiveSplit(mtf.getActiveSplit() - 1);
				}
			}
		} else {
			super.left();
		}
	}

	protected void splitRight() {
		init();
		MpcTextField mtf = (MpcTextField) mainFrame.lookupTextField(param);
		if (KbMouseController.shiftIsPressed) {
			if (splittable) {
				if (mtf.isSplit()) if (!mtf.setActiveSplit(mtf.getActiveSplit() + 1)) mtf.setSplit(false);
			}
		} else {
			super.right();
		}
	}

	
}