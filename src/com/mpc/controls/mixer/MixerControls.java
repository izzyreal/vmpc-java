package com.mpc.controls.mixer;

import com.mpc.sampler.MixerChannel;
import com.mpc.sequencer.MixerEvent;

public class MixerControls extends AbstractMixerControls {

	public void up() {
		init();
		gui.getMixerGui().setYPos(gui.getMixerGui().getYPos() - 1);
	}

	public void down() {
		init();
		gui.getMixerGui().setYPos(gui.getMixerGui().getYPos() + 1);
	}

	public void left() {
		init();
		gui.getMixerGui().setXPos(gui.getMixerGui().getXPos() - 1);
	}

	public void right() {
		init();
		gui.getMixerGui().setXPos(gui.getMixerGui().getXPos() + 1);
	}

	@Override
	public void openWindow() {
		init();
		mixerGui.setChannelSettingsNote(program.getPad(mixerGui.getXPos() + (bank * 16)).getNote());
		mainFrame.openScreen("channelsettings", "windowpanel");
	}

	@Override
	public void function(int f) {
		init();
		switch (f) {
		case 0:
		case 1:
		case 2:
			mixerGui.setTab(f);
			break;
		case 3:
			mainFrame.openScreen("mixersetup", "mainpanel");
			break;
		case 5:
			mixerGui.setLink(!mixerGui.getLink());
			break;
		}

	}

	@Override
	public void turnWheel(int increment) {
		init();
		int notch = getNotch(increment);
		int pad = mixerGui.getXPos() + (bank * 16);
		MixerChannel m = program.getPad(pad).getMixerChannel();

		MixerChannel[] ma = new MixerChannel[16];

		for (int i = 0; i < 16; i++)
			ma[i] = program.getPad(i + (bank * 16)).getMixerChannel();

		if (mixerGui.getTab() == 0) {
			boolean record = sequencer.isRecordingOrOverdubbing() && mixerSetupGui.isRecordMixChangesEnabled();

			if (mixerGui.getYPos() == 0) {
				if (!mixerGui.getLink()) m.setPanning(m.getPanning() + notch);
				if (record) recordMixerEvent(pad, 1, m.getPanning());

				if (mixerGui.getLink()) {
					int padCounter = 0;
					for (MixerChannel mcTemp : ma) {
						mcTemp.setPanning(mcTemp.getPanning() + notch);
						if (record) recordMixerEvent(padCounter++, 1, mcTemp.getPanning());
					}
				}
			}

			if (mixerGui.getYPos() == 1) {
				if (!mixerGui.getLink()) m.setLevel(m.getLevel() + notch);
				if (record) recordMixerEvent(pad, 0, m.getLevel());
				if (mixerGui.getLink()) {
					int padCounter = 0;
					for (MixerChannel mcTemp : ma) {
						mcTemp.setLevel(mcTemp.getLevel() + notch);
						if (record) recordMixerEvent(padCounter++, 0, mcTemp.getLevel());
					}

				}
			}
		}

		if (mixerGui.getTab() == 1) {
			if (mixerGui.getYPos() == 0) {
				if (!mixerGui.getLink()) {
					m.setOutput(m.getOutput() + notch);
					return;
				}

				if (mixerGui.getLink()) {
					for (MixerChannel mcTemp : ma) {
						mcTemp.setOutput(mcTemp.getOutput() + notch);
					}
				}
			}

			if (mixerGui.getYPos() == 1) {
				if (!mixerGui.getLink()) m.setVolumeIndividualOut(m.getVolumeIndividualOut() + notch);
				if (mixerGui.getLink()) {
					for (MixerChannel mcTemp : ma)
						mcTemp.setVolumeIndividualOut(mcTemp.getVolumeIndividualOut() + notch);
				}
			}
		}

		if (mixerGui.getTab() == 2) {
			if (mixerGui.getYPos() == 0) {
				if (!mixerGui.getLink()) m.setFxPath(m.getFxPath() + notch);
				if (mixerGui.getLink()) {
					for (MixerChannel mcTemp : ma)
						mcTemp.setFxPath(mcTemp.getFxPath() + notch);

				}
			}

			if (mixerGui.getYPos() == 1) {
				if (!mixerGui.getLink()) m.setFxSendLevel(m.getFxSendLevel() + notch);
				if (mixerGui.getLink()) {
					for (MixerChannel mcTemp : ma)
						mcTemp.setFxSendLevel(mcTemp.getFxSendLevel() + notch);

				}
			}
		}
	}

	private void recordMixerEvent(int pad, int param, int value) {
		MixerEvent e = new MixerEvent();
		e.setPadNumber(pad);
		e.setParameter(param);
		e.setValue(value);
		e.setTick(sequencer.getTickPosition());
		sequencer.getActiveSequence().getTrack(sequencer.getActiveTrackIndex()).addEvent(e);
	}
}