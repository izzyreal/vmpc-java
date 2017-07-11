package com.mpc.disk;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.mpc.file.pgmreader.Mixer;
import com.mpc.file.pgmreader.Pads;
import com.mpc.file.pgmreader.PgmAllNoteParameters;
import com.mpc.file.pgmreader.PgmHeader;
import com.mpc.file.pgmreader.ProgramFileReader;
import com.mpc.file.pgmreader.ProgramName;
import com.mpc.file.pgmreader.SoundNames;
import com.mpc.file.pgmreader.Slider;
import com.mpc.sampler.MixerChannel;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;

public class PgmToProgramConverter {

	private final ProgramFileReader reader;
	private final Program program;
	private boolean done = false;
	private final List<String> soundNames;

	public PgmToProgramConverter(MpcFile file) {
		program = new Program();

		reader = new ProgramFileReader(file);
		soundNames = new ArrayList<String>();

		SoundNames pgmSoundNames = reader.getSampleNames();
		
		try {
			for (int i = 0; i < reader.getHeader().getNumberOfSamples(); i++)
				soundNames.add(pgmSoundNames.getSampleName(i));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		final PgmHeader pgmHeader = reader.getHeader();
		final ProgramName programName = reader.getProgramName();
		final int numberOfSamples = pgmHeader.getNumberOfSamples();

		try {
			program.setName(programName.getProgramNameASCII());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		setNoteParameters();
		setMixer();
		setSlider();
		done = true;
	}

	void setSlider() {
		Slider slider = reader.getSlider();

		int nn = slider.getMidiNoteAssign() == 0 ? 34 : slider.getMidiNoteAssign();
		program.getSlider().setAssignNote(nn);

		program.getSlider().setAttackHighRange(slider.getAttackHigh());
		program.getSlider().setAttackLowRange(slider.getAttackLow());
		program.getSlider().setControlChange(slider.getControlChange());
		program.getSlider().setDecayHighRange(slider.getDecayHigh());
		program.getSlider().setDecayLowRange(slider.getDecayLow());
		program.getSlider().setFilterHighRange(slider.getFilterHigh());
		program.getSlider().setFilterLowRange(slider.getFilterLow());
		program.getSlider().setTuneHighRange(slider.getTuneHigh());
		program.getSlider().setTuneLowRange(slider.getTuneLow());
	}

	void setNoteParameters() {

		PgmAllNoteParameters pgmNoteParameters = reader.getAllNoteParameters();
		Pads pgmPads = reader.getPads();
		int pmn = 0;
		int nn = 0;
		NoteParameters programNoteParameters = null;

		for (int i = 0; i < 64; i++) {
			pmn = pgmPads.getNote(i);
			nn = pmn == -1 ? 34 : pmn;
			program.getPad(i).setNote(nn);

			programNoteParameters = program.getNoteParameters(i + 35);
			
			programNoteParameters.setAttack(pgmNoteParameters.getAttack(i));
			programNoteParameters.setDecay(pgmNoteParameters.getDecay(i));
			programNoteParameters.setDecayMode(pgmNoteParameters.getDecayMode(i));
			programNoteParameters.setFilterAttack(pgmNoteParameters.getVelEnvToFiltAtt(i));
			programNoteParameters.setFilterDecay(pgmNoteParameters.getVelEnvToFiltDec(i));
			programNoteParameters.setFilterEnvelopeAmount(pgmNoteParameters.getVelEnvToFiltAmt(i));
			programNoteParameters.setFilterFrequency(pgmNoteParameters.getCutoff(i));
			programNoteParameters.setFilterResonance(pgmNoteParameters.getResonance(i));
			programNoteParameters.setMuteAssignA(pgmNoteParameters.getMuteAssign1(i));
			programNoteParameters.setMuteAssignB(pgmNoteParameters.getMuteAssign2(i));
			programNoteParameters.setOptNoteA(pgmNoteParameters.getAlsoPlayUse1(i));
			programNoteParameters.setOptionalNoteB(pgmNoteParameters.getAlsoPlayUse2(i));

			int sampleSelect = pgmNoteParameters.getSampleSelect(i);
			programNoteParameters.setSoundNumberNoLimit(sampleSelect == 255 ? -1 : sampleSelect);
			programNoteParameters.setSliderParameterNumber(pgmNoteParameters.getSliderParameter(i));
			programNoteParameters.setSoundGenMode(pgmNoteParameters.getSoundGenerationMode(i));
			programNoteParameters.setTune(pgmNoteParameters.getTune(i));
			programNoteParameters.setVeloRangeLower(pgmNoteParameters.getVelocityRangeLower(i));
			programNoteParameters.setVeloRangeUpper(pgmNoteParameters.getVelocityRangeUpper(i));
			programNoteParameters.setVelocityToAttack(pgmNoteParameters.getVelocityToAttack(i));
			programNoteParameters.setVelocityToFilterFrequency(pgmNoteParameters.getVelocityToCutoff(i));
			programNoteParameters.setVeloToLevel(pgmNoteParameters.getVelocityToLevel(i));
			programNoteParameters.setVelocityToPitch(pgmNoteParameters.getVelocityToPitch(i));
			programNoteParameters.setVelocityToStart(pgmNoteParameters.getVelocityToStart(i));
			programNoteParameters.setVoiceOverlap(pgmNoteParameters.getVoiceOverlap(i));
		}
			
	}

	void setMixer() {
		Mixer pgmMixer = reader.getMixer();
		Pads pgmPads = reader.getPads();
		int skippedMixerChannels = 0;
		for (int i = 0; i < 64; i++) {

			int pmn = pgmPads.getNote(i);

			if (pmn == -1) {
				program.getPad(i).setNote(34);
			} else {
				program.getPad(i).setNote(pmn);
			}
			if (pmn != -1) {
				int mixindex = pmn - 35 - skippedMixerChannels;  
				MixerChannel mc = program.getPad(i).getMixerChannel();
				mc.setLevel(pgmMixer.getVolume(mixindex));
				mc.setVolumeIndividualOut(pgmMixer.getVolumeIndividual(mixindex));
				mc.setPanning(pgmMixer.getPan(mixindex));
				mc.setOutput(pgmMixer.getOutput(mixindex));
				mc.setFxPath(pgmMixer.getEffectsOutput(mixindex));
			} else {
				skippedMixerChannels++;
			}
		}
	}

	public Program get() {
		if (!done) return null;
		return program;
	}

	public List<String> getSoundNames() {
		if (!done) return null;
		return soundNames;
	}
}
