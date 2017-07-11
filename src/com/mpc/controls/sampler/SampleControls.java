package com.mpc.controls.sampler;

public class SampleControls extends AbstractSamplerControls {

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			if (!sampler.isRecording() && !sampler.isArmed()) sampler.resetPeak();
			break;
		case 4:
			if (sampler.isRecording()) {
				sampler.cancelRecording();
				return;
			}
			if (sampler.isArmed()) {
				sampler.unArm();
				return;
			}
			break;
		case 5:
			if (!sampler.isRecording()) {
				sampler.arm();
				return;
			}

			if (sampler.isRecording()) {
				sampler.stopRecordingEarlier();
				return;
			}
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (!sampler.isRecording() && !sampler.isArmed()) {
			if (param.equals("input")) {
				int oldInput = samplerGui.getInput();
				samplerGui.setInput(samplerGui.getInput() + notch);
				if (samplerGui.getInput() != oldInput) {
					sampler.setInput(mpc.getAudioMidiServices().getAudioInput(samplerGui.getInput()));
				}
			}
			if (param.equals("threshold")) samplerGui.setThreshold(samplerGui.getThreshold() + notch);
			if (param.equals("mode")) samplerGui.setMode(samplerGui.getMode() + notch);
			if (param.equals("time")) samplerGui.setTime(samplerGui.getTime() + notch);
			if (param.equals("monitor")) samplerGui.setMonitor(samplerGui.getMonitor() + notch);
			if (param.equals("prerec")) samplerGui.setPreRec(samplerGui.getPreRec() + notch);
		}
	}
}
