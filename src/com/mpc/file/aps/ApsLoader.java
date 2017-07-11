package com.mpc.file.aps;

import java.util.ArrayList;
import java.util.List;

import com.mpc.Mpc;
import com.mpc.disk.AbstractDisk;
import com.mpc.disk.JavaDisk;
import com.mpc.disk.MpcFile;
import com.mpc.disk.SoundLoader;
import com.mpc.gui.Bootstrap;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;
import com.mpc.tootextensions.ConcreteMixParameters;
import com.mpc.tootextensions.MpcMixParameters;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class ApsLoader implements Runnable {
	List<Program> programs = new ArrayList<Program>();
	private MpcFile file;

	public ApsLoader(MpcFile file) {
		this.file = file;
	}

	public void load() {
		new Thread(this).start();
		Bootstrap.getGui().getMpc().getDisk().setBusy(true);
	}

	@Override
	public void run() {
		ApsParser apsParser = new ApsParser(file);
		Mpc mpc = Bootstrap.getGui().getMpc();
		for (int i = 0; i < apsParser.getSoundNames().size(); i++) {

			String ext = "snd";

			MpcFile soundFile = null;
			String soundFileName = apsParser.getSoundNames().get(i).replaceAll(" ", "");

			for (MpcFile f : mpc.getDisk().getFiles())
				if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".SND")) soundFile = f;

			if (soundFile == null) {
				for (MpcFile f : mpc.getDisk().getFiles())
					if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".WAV")) soundFile = f;

				ext = "wav";
			}

			if (soundFile != null) {
				loadSound(soundFileName, ext, soundFile, mpc, false, i);
			} else {
				if (mpc.getDisk() instanceof JavaDisk) { // making sure akai
															// 16.3 files can be
															// found when using
															// non raw disk
															// access
					ext = "snd";
					soundFileName = soundFileName.substring(0, 8);
					for (MpcFile f : mpc.getDisk().getFiles())
						if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".SND")) soundFile = f;

					if (soundFile == null) {
						for (MpcFile f : mpc.getDisk().getFiles())
							if (f.getName().replaceAll(" ", "").equalsIgnoreCase(soundFileName + ".WAV")) soundFile = f;

						ext = "wav";
					}
				}
				if (soundFile == null) {
					// notfound(soundFileName, ext);
					System.out.println("aps loader couldn't find " + soundFileName + "." + ext);
				}
			}
		}

		List<ApsProgram> apsPrograms = apsParser.getPrograms();
		for (ApsProgram p : apsPrograms) {
			Program newProgram = new Program();
			newProgram.setName(p.getName());
			for (int i = 0; i < 64; i++) {
				int padnn = p.getAssignTable().get()[i];
				newProgram.getPad(i).setNote(padnn);
				ConcreteMixParameters mixParameters = p.getMixParameters(i + 35);
				newProgram.getPad(i).getMixerChannel().setFxPath(mixParameters.getFxPath());
				newProgram.getPad(i).getMixerChannel().setLevel(mixParameters.getLevel());
				newProgram.getPad(i).getMixerChannel().setPanning(mixParameters.getPanning());
				newProgram.getPad(i).getMixerChannel().setVolumeIndividualOut(mixParameters.getVolumeIndividualOut());
				newProgram.getPad(i).getMixerChannel().setFxSendLevel(mixParameters.getFxSendLevel());
				newProgram.getPad(i).getMixerChannel().setOutput(mixParameters.getOutput());
				NoteParameters np = newProgram.getNoteParameters(i + 35);
				ApsNoteParameters anp = p.getNoteParameters(i + 35);
				np.setSoundNumber(anp.getSoundNumber());
				np.setTune(anp.getTune());
				np.setVoiceOverlap(anp.getVoiceOverlap());
				np.setDecayMode(anp.getDecayMode());
				np.setAttack(anp.getAttack());
				np.setDecay(anp.getDecay());
				np.setFilterAttack(anp.getVelocityToFilterAttack());
				np.setFilterDecay(anp.getVelocityToFilterDecay());
				np.setFilterEnvelopeAmount(anp.getVelocityToFilterAmount());
				np.setFilterFrequency(anp.getCutoffFrequency());
				np.setFilterResonance(anp.getResonance());
				np.setMuteAssignA(anp.getMute1());
				np.setMuteAssignB(anp.getMute2());
				np.setOptNoteA(anp.getAlsoPlay1());
				np.setOptionalNoteB(anp.getAlsoPlay2());
				np.setSliderParameterNumber(anp.getSliderParameter());
				np.setSoundGenMode(anp.getSoundGenerationMode());
				np.setVelocityToStart(anp.getVelocityToStart());
				np.setVelocityToAttack(anp.getVelocityToAttack());
				np.setVelocityToFilterFrequency(anp.getVelocityToFilterFrequency());
				np.setVeloToLevel(anp.getVelocityToLevel());
				np.setVeloRangeLower(anp.getVelocityRangeLower());
				np.setVeloRangeUpper(anp.getVelocityRangeUpper());
				np.setVelocityToPitch(anp.getVelocityToPitch());
			}

			newProgram.getSlider().setAttackHighRange(p.getSlider().getAttackHigh());
			newProgram.getSlider().setAttackLowRange(p.getSlider().getAttackLow());
			newProgram.getSlider().setControlChange(p.getSlider().getProgramChange());
			newProgram.getSlider().setDecayHighRange(p.getSlider().getDecayHigh());
			newProgram.getSlider().setDecayLowRange(p.getSlider().getDecayLow());
			newProgram.getSlider().setFilterHighRange(p.getSlider().getFilterHigh());
			newProgram.getSlider().setFilterLowRange(p.getSlider().getFilterLow());
			newProgram.getSlider().setAssignNote(p.getSlider().getNote());
			newProgram.getSlider().setTuneHighRange(p.getSlider().getTuneHigh());
			newProgram.getSlider().setTuneLowRange(p.getSlider().getTuneLow());

			programs.add(newProgram);

		}

		mpc.getSampler().getPrograms().clear();
		mpc.getSampler().getPrograms().addAll(programs);

		for (int i = 0; i < 4; i++) {
			ApsMixer m = apsParser.getDrumMixers()[i];
			MpcSoundPlayerChannel drum = mpc.getDrum(i);
			for (int j = 0; j < 64; j++) {
				MpcMixParameters apsmp = m.getMixVariables(j + 35);
				MpcMixParameters drummp = drum.getMixParameters()[j];
				drummp.setFxPath(apsmp.getFxPath());
				drummp.setLevel(apsmp.getLevel());
				drummp.setPanning(apsmp.getPanning());
				drummp.setVolumeIndividualOut(apsmp.getVolumeIndividualOut());
				drummp.setOutput(apsmp.getOutput());
				drummp.setFxSendLevel(apsmp.getFxSendLevel());
			}
			drum.setProgram(apsParser.getDrumConfiguration(i).getProgram());
			drum.setReceivePgmChange(apsParser.getDrumConfiguration(i).getReceivePgmChange());
			drum.setReceiveMidiVolume(apsParser.getDrumConfiguration(i).getReceiveMidiVolume());
		}

		Bootstrap.getGui().getMixerSetupGui()
				.setCopyPgmMixToDrumEnabled(apsParser.getGlobalParameters().copyPgmMixToDrum());
		Bootstrap.getGui().getMixerSetupGui().setFxDrum(apsParser.getGlobalParameters().getFxDrum());
		Bootstrap.getGui().getMixerSetupGui()
				.setIndivFxSourceDrum(apsParser.getGlobalParameters().isIndivFxSourceDrum());
		Bootstrap.getGui().getMixerSetupGui()
				.setStereoMixSourceDrum(apsParser.getGlobalParameters().isStereoMixSourceDrum());
		Bootstrap.getGui().getSamplerGui().setPadToIntSound(apsParser.getGlobalParameters().isPadToIntSoundEnabled());
		Bootstrap.getGui().getDiskGui().removePopup();
		Bootstrap.getGui().getMainFrame().openScreen("load", "mainpanel");
		mpc.getDisk().setBusy(false);
	}

	private void loadSound(String soundFileName, String ext, MpcFile soundFile, Mpc mpc, boolean replace,
			int loadSoundIndex) {

		SoundLoader sl = new SoundLoader(mpc.getSampler().getSounds(), replace);
		sl.setPartOfProgram(true);
		try {
			sl.loadSound(soundFile);
			showPopup(soundFileName, ext, soundFile.length());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showPopup(final String name, final String ext, final int sampleSize) {
		Bootstrap.getGui().getDiskGui().openPopup(AbstractDisk.padRightSpace(name, 16), ext);
		Bootstrap.getGui().getMainFrame().getLayeredScreen().repaint();
		if (Bootstrap.getGui().getMpc().getDisk() instanceof JavaDisk) {
			try {
				int sleepTime = sampleSize / 400;
				if (sleepTime < 300) sleepTime = 300;
				Thread.sleep(sleepTime / 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
