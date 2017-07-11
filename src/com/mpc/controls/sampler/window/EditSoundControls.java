package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Pad;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sampler.Sound;
import com.mpc.sampler.TimeStretch;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;

public class EditSoundControls extends AbstractSamplerControls {

	public void turnWheel(int j) {
		init();
		int notch = getNotch(j);
		if (param.equals("edit")) editSoundGui.setEdit(editSoundGui.getEdit() + notch);

		if (param.equals("variable0")) {

			if (editSoundGui.getEdit() == 2 || editSoundGui.getEdit() == 7) {

				gui.getNameGui().setName(mainFrame.lookupTextField("variable0").getText());
				gui.getNameGui().setParameterName("newname");
				mainFrame.openScreen("name", "dialogpanel");
			}
		}

		if (param.equals("variable0") && editSoundGui.getEdit() == 3)

		editSoundGui.setInsertSndNr(editSoundGui.getInsertSndNr() + notch);

		if (param.equals("variable1")) editSoundGui.setTimeStretchRatio(editSoundGui.getTimeStretchRatio() + notch);

		if (param.equals("variable2"))
			editSoundGui.setTimeStretchPresetNumber(editSoundGui.getTimeStretchPresetNumber() + notch);

		if (param.equals("variable3")) editSoundGui.setTimeStretchAdjust(editSoundGui.getTimeStretchAdjust() + notch);
		if (param.equals("endmargin")) editSoundGui.setEndMargin(editSoundGui.getEndMargin() + notch);

		if (param.equals("createnewprogram")) editSoundGui.setCreateNewProgram(true);
	}

	public void function(int j) {
		super.function(j);
		switch (j) {
		case 4:

			Sound sound = sampler.getSound(soundGui.getSoundIndex());

			if (editSoundGui.getEdit() == 0) {

				int newLength = (int) (sound.getEnd() - sound.getStart());

				if (sound.getStart() > sound.getLoopTo()) {
					if (sound.getLoopTo() > newLength) {
						sound.setLoopTo((int) newLength);
					} else {
						sound.setLoopTo(0);
					}
				} else {
					sound.setLoopTo((int) (sound.getLoopTo() - sound.getStart()));
				}

				sampler.trimSample(soundGui.getSoundIndex(), sound.getStart(), sound.getEnd());

				sound.setStart(0);
				sound.setEnd(newLength);
				sound.setMono(sound.isMono());
			}

			if (editSoundGui.getEdit() == 1) sound.setLoopTo((int) sound.getStart());

			if (editSoundGui.getEdit() == 2) {
				Sound newSample = new Sound(sound.getSampleRate());
				newSample.setName(editSoundGui.getNewName());
				int newSampleSize = (int) (sound.getEnd() - sound.getStart());
				int difference = (int) sound.getStart();
				if (!sound.isMono()) {
					newSampleSize *= 2;
					difference *= 2;
				}
				float[] newSampleData = new float[newSampleSize];
				for (int i = 0; i < newSampleSize; i++) {
					newSampleData[i] = sound.getSampleData()[i + difference];
				}
				newSample.setSampleData(newSampleData);
				newSample.setMono(sound.isMono());
				sampler.getSounds().add(newSample);
				soundGui.setSoundIndex(sampler.getSoundCount() - 1);
			}

			if (editSoundGui.getEdit() == 3) {
				Sound source = sampler.getSound(editSoundGui.getInsertSndNr());
				Sound destination = sampler.getSound(soundGui.getSoundIndex());

				long destinationStart = sound.getStart();
				long sourceLength = source.getSampleData().length;
				if (!source.isMono()) sourceLength /= 2;

				long destinationLength = destination.getSampleData().length;
				if (!destination.isMono()) destinationLength /= 2;

				long newSampleLength = sourceLength + destinationLength;

				if (!destination.isMono()) newSampleLength *= 2;

				float[] sourceData = source.getSampleData();

				float[] destinationData = destination.getSampleData();

				float[] newSampleData = new float[(int) newSampleLength];

				if (destination.isMono()) {
					if (!source.isMono()) {
						sourceData = source.getSampleDataLeft();
					}
					for (int i = 0; i < destinationStart; i++) {
						newSampleData[i] = destinationData[i];
					}

					for (int i = (int) destinationStart; i < destinationStart + sourceLength; i++) {
						newSampleData[i] = sourceData[(int) (i - destinationStart)];
					}

					for (int i = (int) (destinationStart + sourceLength); i < (destinationStart + sourceLength)
							+ (destinationLength - destinationStart); i++) {
						newSampleData[i] = destinationData[(int) (destinationStart
								+ (i - destinationStart - sourceLength))];
					}
				}

				if (!destination.isMono()) {
					float[] destinationDataLeft = destination.getSampleDataLeft();
					float[] destinationDataRight = destination.getSampleDataRight();
					float[] sourceDataLeft = null;
					float[] sourceDataRight = null;
					if (source.isMono()) {
						sourceDataLeft = source.getSampleData();
						sourceDataRight = source.getSampleData();
					} else {
						sourceDataLeft = source.getSampleDataLeft();
						sourceDataRight = source.getSampleDataRight();
					}

					float[] newSampleDataLeft = new float[(int) newSampleLength / 2];
					float[] newSampleDataRight = new float[(int) newSampleLength / 2];

					for (int i = 0; i < destinationStart; i++) {
						newSampleDataLeft[i] = destinationDataLeft[i];
						newSampleDataRight[i] = destinationDataRight[i];
					}

					for (int i = (int) destinationStart; i < destinationStart + sourceLength; i++) {
						newSampleDataLeft[i] = sourceDataLeft[(int) (i - destinationStart)];
						newSampleDataRight[i] = sourceDataRight[(int) (i - destinationStart)];
					}

					for (int i = (int) (destinationStart + sourceLength); i < (destinationStart + sourceLength)
							+ (destinationLength - destinationStart); i++) {
						newSampleDataLeft[i] = destinationDataLeft[(int) (destinationStart
								+ (i - destinationStart - sourceLength))];
						newSampleDataRight[i] = destinationDataRight[(int) (destinationStart
								+ (i - destinationStart - sourceLength))];
					}
					newSampleData = Sampler.mergeToStereo(newSampleDataLeft, newSampleDataRight);
				}
				sound.setSampleData(newSampleData);
				sound.setStart(0);
				sound.setMono(sound.isMono());
				sound.setLoopTo((int) sound.getEnd());
			}

			if (editSoundGui.getEdit() == 4) {
				int start = (int) sound.getStart();
				int end = (int) sound.getEnd();
				int difference = end - start;
				int oldLength = sound.getSampleData().length;
				if (!sound.isMono()) oldLength /= 2;
				int newLength = oldLength - difference;
				int newSampleLength = (int) newLength;
				if (!sound.isMono()) newSampleLength *= 2;

				float[] newSampleData = new float[newSampleLength];

				int multiplier = 1;
				if (!sound.isMono()) multiplier = 2;
				for (int i = 0; i < start * multiplier; i++) {
					newSampleData[i] = sound.getSampleData()[i];
				}

				for (int i = (int) start * multiplier; i < newSampleLength; i++) {
					newSampleData[i] = sound.getSampleData()[(int) (i + (end * multiplier))];
				}
				sound.setSampleData(newSampleData);
				sound.setEnd(sound.getStart());
				sound.setMono(sound.isMono());
				sound.setLoopTo((int) sound.getEnd());
			}

			if (editSoundGui.getEdit() == 5) {
				int start = (int) sound.getStart();
				int end = (int) sound.getEnd();
				if (!sound.isMono()) {
					start *= 2;
					end *= 2;
				}
				for (int i = start; i < end; i++) {
					sound.getSampleData()[i] = 0f;
				}
			}

			if (editSoundGui.getEdit() == 6) {
				int start = (int) sound.getStart();
				int end = (int) sound.getEnd();
				int reverseCounter = end - 1;

				float[] newSampleData = new float[sound.getSampleData().length];

				if (!sound.isMono()) {
					float[] sampleDataLeft = sound.getSampleDataLeft();
					float[] sampleDataRight = sound.getSampleDataRight();
					float[] newSampleDataLeft = sound.getSampleDataLeft();
					float[] newSampleDataRight = sound.getSampleDataRight();

					for (int i = start; i < end; i++) {
						newSampleDataLeft[i] = sampleDataLeft[reverseCounter];
						newSampleDataRight[i] = sampleDataRight[reverseCounter];
						reverseCounter--;
					}
					newSampleData = Sampler.mergeToStereo(newSampleDataLeft, newSampleDataRight);
				} else {

					float[] sampleData = sound.getSampleData();

					for (int i = start; i < end; i++) {
						newSampleData[i] = sampleData[reverseCounter];
						reverseCounter--;
					}
				}

				sound.setSampleData(newSampleData);
				sound.setMono(sound.isMono());
			}

			if (editSoundGui.getEdit() == 7) {

				if (editSoundGui.getTimeStretchRatio() == 10000) return;

				if (!sound.isMono()) {

					TimeStretch ts0 = new TimeStretch(sound.getSampleDataLeft(),
							(float) (editSoundGui.getTimeStretchRatio() / 10000.0), sound.getSampleRate());

					float[] newSampleDataLeft = ts0.getProcessedData();
					TimeStretch ts1 = new TimeStretch(sound.getSampleDataRight(),
							(float) (editSoundGui.getTimeStretchRatio() / 10000.0), sound.getSampleRate());
					float[] newSampleDataRight = ts1.getProcessedData();
					Sound newSample = new Sound(sound.getSampleRate());
					newSample.setSampleData(Sampler.mergeToStereo(newSampleDataLeft, newSampleDataRight));
					newSample.setMono(sound.isMono());
					newSample.setName(editSoundGui.getNewName());
					sampler.getSounds().add(newSample);
				}

				if (sound.isMono()) {
					TimeStretch ts = new TimeStretch(sound.getSampleData(),
							(float) (editSoundGui.getTimeStretchRatio() / 10000.0), sound.getSampleRate());
					Sound newSample = new Sound(sound.getSampleRate());
					newSample.setSampleData(ts.getProcessedData());
					newSample.setMono(sound.isMono());
					newSample.setName(editSoundGui.getNewName());
					sampler.getSounds().add(newSample);
				}
			}

			soundGui.setSoundIndex(sampler.getSoundCount() - 1);

			soundGui.initZones(sampler.getSound(soundGui.getSoundIndex()).getLastFrameIndex() + 1);

			if (editSoundGui.getEdit() == 8) {

				int endMargin = editSoundGui.getEndMargin();
				Sound source = sampler.getSound(soundGui.getSoundIndex());

				for (int i = 0; i < soundGui.getNumberOfZones(); i++) {

					int start = soundGui.getZoneStart(i);
					int end = soundGui.getZoneEnd(i);

					if (i == soundGui.getNumberOfZones() - 1) endMargin = 0;

					Sound zone = sampler.createZone(source, start, end, endMargin);

					zone.setName(editSoundGui.getNewName(i));

					sampler.getSounds().add(zone);

				}

				soundGui.setSoundIndex(sampler.getSoundCount() - soundGui.getNumberOfZones());

				soundGui.initZones(sampler.getSound(soundGui.getSoundIndex()).getLastFrameIndex() + 1);

				if (editSoundGui.getCreateNewProgram()) {

					Program p = new Program();
					p.setName(source.getName());

					for (int i = 0; i < soundGui.getNumberOfZones(); i++) {

						Pad pad = p.getPad(i);
						int note = pad.getNote();
						NoteParameters n = p.getNoteParameters(note);
						n.setSoundNumber(sampler.getSoundCount() - soundGui.getNumberOfZones() + i);

					}

					sampler.addProgram(p);

					MpcSequence s = mpc.getSequencer().getSequence(mpc.getSequencer().getActiveSequenceIndex());

					MpcTrack t = (MpcTrack) s.getTrack(mpc.getSequencer().getActiveTrackIndex());

					if (t.getBusNumber() != 0) {

						sampler.getDrum(t.getBusNumber() - 1).setProgram(sampler.getProgramCount() - 1);

					}
				}
			}
			mainFrame.openScreen(editSoundGui.getPreviousScreenName(), "mainpanel");
			break;
		}
	}
}