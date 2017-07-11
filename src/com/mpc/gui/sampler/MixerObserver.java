package com.mpc.gui.sampler;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.MixerStrip;
import com.mpc.sampler.MixerChannel;
import com.mpc.sampler.Pad;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcSoundPlayerChannel;
import com.mpc.sequencer.MpcTrack;

public class MixerObserver implements Observer {
	private String[] fxPathNames = { "--", "M1", "M2", "R1", "R2" };
	private String[] stereoNames = { "-", "12", "12", "34", "34", "56", "56", "78", "78" };
	private String[] stereoNamesSlash = { "-", "1/2", "1/2", "3/4", "3/4", "5/6", "5/6", "7/8", "7/8" };

	private String[] monoNames = { "-", "1", "2", "3", "4", "5", "6", "7", "8" };
	private MainFrame mainFrame;
	private Gui gui;
	private MixerGui mixGui;
	private LayeredScreen slp;

	private Sequencer sequencer;
	private Sampler sampler;
	private SamplerGui samplerGui;

	private MixerSetupGui mixerSetupGui;

	private int trackNum;
	private int seqNum;

	private int bank;

	private MpcSequence mpcSequence;
	private MpcTrack track;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;

	private Program program;
	private Pad pad;
	private MixerChannel mixerChannel;

	private ArrayList<MixerStrip> mixerStrips;
	private JTextField noteField;
	private JTextField stereoVolumeField;
	private JTextField individualVolumeField;
	private JTextField fxSendLevelField;
	private JTextField panningField;
	private JTextField outputField;
	private JTextField fxPathField;
	private JTextField followStereoField;

	private JTextField masterLevelField;
	private JTextField fxDrumField;
	private JTextField stereoMixSourceField;
	private JTextField indivFxSourceField;
	private JTextField copyPgmMixToDrumField;
	private JTextField recordMixChangesField;

	public MixerObserver(Mpc mpc) throws UnsupportedEncodingException {

		gui = Bootstrap.getGui();
		samplerGui = gui.getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		bank = samplerGui.getBank();

		mixGui = gui.getMixerGui();
		mixGui.deleteObservers();
		mixGui.addObserver(this);

		mainFrame = gui.getMainFrame();
		slp = mainFrame.getLayeredScreen();
		sequencer = mpc.getSequencer();
		sampler = mpc.getSampler();
		mixerSetupGui = gui.getMixerSetupGui();
		mixerSetupGui.deleteObservers();
		mixerSetupGui.addObserver(this);

		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		sequencer.deleteObservers();
		sequencer.addObserver(this);
		mpcSequence.deleteObservers();
		mpcSequence.addObserver(this);
		track.deleteObservers();
		track.addObserver(this);

		if (slp.getCurrentScreenName().equals("mixer")) {

			initPadNameLabels();
			initMixerStrips();

			for (MixerStrip m : mixerStrips) {
				m.initFields();
				m.setColors();
			}

			displayMixerStrips();
			displayFunctionBox();

		}

		if (slp.getCurrentScreenName().equals("channelsettings")) {
			noteField = mainFrame.lookupTextField("note");
			stereoVolumeField = mainFrame.lookupTextField("stereovolume");
			individualVolumeField = mainFrame.lookupTextField("individualvolume");
			fxSendLevelField = mainFrame.lookupTextField("fxsendlevel");
			panningField = mainFrame.lookupTextField("panning");
			outputField = mainFrame.lookupTextField("output");
			fxPathField = mainFrame.lookupTextField("fxpath");
			followStereoField = mainFrame.lookupTextField("followstereo");

			displayChannel();
		}

		if (slp.getCurrentScreenName().equals("mixersetup")) {
			masterLevelField = mainFrame.lookupTextField("masterlevel");
			fxDrumField = mainFrame.lookupTextField("fxdrum");
			stereoMixSourceField = mainFrame.lookupTextField("stereomixsource");
			indivFxSourceField = mainFrame.lookupTextField("indivfxsource");
			copyPgmMixToDrumField = mainFrame.lookupTextField("copypgmmixtodrum");
			recordMixChangesField = mainFrame.lookupTextField("recordmixchanges");

			displaySetup();
		}
	}

	private void initPadNameLabels() {

		int tfCounter3 = 0;
		int tfCounter4 = 0;

		for (Component jc : mainFrame.getAllComponents(slp.getMainPanel())) {

			if (jc instanceof JTextField) {

				JTextField tf = (JTextField) jc;

				if (tf.getName().equals("e3") || tf.getName().equals("e4")) {
					tf.setSize(12, 18);
				}

				if (tf.getName().length() == 2) {

					if (tf.getName().endsWith("3")) {

						tf.setText("0");

						if (tfCounter3 > 8) tf.setText("1");

						tfCounter3++;
					}

					if (tf.getName().endsWith("4")) {

						tf.setText("" + (tfCounter4 + 1));

						tfCounter4++;

						if (tfCounter4 == 9) tfCounter4 = -1;
					}
				}
			}
		}

	}

	private void displaySetup() {
		masterLevelField.setText(Util.padLeftSpace("" + mixerSetupGui.getMasterLevelString(), 5));

		fxDrumField.setText("" + (mixerSetupGui.getFxDrum() + 1));
		stereoMixSourceField.setText(mixerSetupGui.isStereoMixSourceDrum() ? "DRUM" : "PROGRAM");
		indivFxSourceField.setText(mixerSetupGui.isIndivFxSourceDrum() ? "DRUM" : "PROGRAM");
		copyPgmMixToDrumField.setText(mixerSetupGui.isCopyPgmMixToDrumEnabled() ? "YES" : "NO");
		recordMixChangesField.setText(mixerSetupGui.isRecordMixChangesEnabled() ? "YES" : "NO");
	}

	private void initMixerStrips() {
		for (int i = (bank * 16); i < ((bank * 16) + 16); i++) {
			pad = program.getPad(i);
			mixerChannel = pad.getMixerChannel();
			mixerChannel.deleteObservers();
			mixerChannel.addObserver(this);
		}

		mixerStrips = new ArrayList<MixerStrip>();

		for (int i = 0; i < 16; i++) {
			MixerStrip mixerStrip = new MixerStrip(i, bank);
			mixerStrips.add(mixerStrip);
		}

		mixGui.setMixerStrips(mixerStrips);
		displayMixerStrips();
		mixerStrips.get(mixGui.getXPos()).setSelection(mixGui.getYPos());
	}

	private void displayChannel() {
		mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
				.getMixerChannel();
		setNoteField();
		stereoVolumeField.setText(Util.padLeftSpace("" + mixerChannel.getLevel(), 3));
		individualVolumeField.setText(Util.padLeftSpace("" + mixerChannel.getVolumeIndividualOut(), 3));
		fxSendLevelField.setText(Util.padLeftSpace("" + mixerChannel.getFxSendLevel(), 3));
		setPanningField();
		setOutputField();
		fxPathField.setText(fxPathNames[mixerChannel.getFxPath()]);
		followStereoField.setText(mixerChannel.isFollowingStereo() ? "YES" : "NO");
	}

	private void setOutputField() {
		mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
				.getMixerChannel();
		if (mixerChannel.isStereo()) {
			outputField.setText(stereoNamesSlash[mixerChannel.getOutput()]);
		} else {
			outputField.setText(" " + mixerChannel.getOutput());
		}
	}

	private void setNoteField() {
		String soundString = "OFF";
		int sampleNumber = program.getNoteParameters(mixGui.getChannelSettingsNote()).getSndNumber();
		int padNumber = program.getPadNumberFromNote(mixGui.getChannelSettingsNote());
		if (sampleNumber > 0 && sampleNumber < sampler.getSoundCount()) {
			soundString = sampler.getSoundName(sampleNumber);
			if (!sampler.getSound(sampleNumber).isMono()) {
				soundString = soundString + Util.padLeftSpace("(ST)", 23 - soundString.length());
			}
		}

		noteField.setText(
				"" + mixGui.getChannelSettingsNote() + "/" + sampler.getPadName(padNumber) + "-" + soundString);
	}

	private void setPanningField() {
		mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
				.getMixerChannel();
		String panning = "L";
		if (mixerChannel.getPanning() > 0) panning = "R";
		panningField.setText(panning + Util.padLeft2Zeroes(Math.abs(mixerChannel.getPanning())));
		if (mixerChannel.getPanning() == 0) {
			panningField.setText("MID");
		}
	}

	private void displayMixerStrips() {

		for (int i = 0; i < 16; i++) {
			mixerChannel = program.getPad(i + (bank * 16)).getMixerChannel();

			if (mixGui.getTab() == 0) {
				mixerStrips.get(i).setValueA(mixerChannel.getPanning());
				mixerStrips.get(i).setValueB(mixerChannel.getLevel());
			}

			if (mixGui.getTab() == 1) {
				if (mixerChannel.isStereo()) {
					mixerStrips.get(i).setValueAString(stereoNames[mixerChannel.getOutput()]);
				} else {
					mixerStrips.get(i).setValueAString(monoNames[mixerChannel.getOutput()]);
				}
				mixerStrips.get(i).setValueB(mixerChannel.getVolumeIndividualOut());
			}
			if (mixGui.getTab() == 2) {
				mixerStrips.get(i).setValueAString(fxPathNames[mixerChannel.getFxPath()]);
				mixerStrips.get(i).setValueB(mixerChannel.getFxSendLevel());
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		track.deleteObservers();
		mpcSequence.deleteObservers();

		seqNum = sequencer.getActiveSequenceIndex();
		mpcSequence = sequencer.getSequence(seqNum);
		trackNum = sequencer.getActiveTrackIndex();
		track = (MpcTrack) mpcSequence.getTrack(trackNum);

		track.addObserver(this);
		mpcSequence.addObserver(this);

		bank = samplerGui.getBank();

		switch ((String) arg) {

		case "tab":

			for (MixerStrip m : mixerStrips) {
				m.initFields();
				m.setColors();
			}

			displayMixerStrips();
			displayFunctionBox();
			break;

		case "position":

			if (!mixGui.getLink()) {

				for (MixerStrip m : mixerStrips)
					m.setSelection(-1);

				mixerStrips.get(mixGui.getXPos()).setSelection(mixGui.getYPos());

			} else {

				for (MixerStrip m : mixerStrips)
					m.setSelection(mixGui.getYPos());

			}

			break;

		case "volume":

			if (slp.getCurrentScreenName().equals("mixer")) {

				if (!mixGui.getLink()) {

					mixerChannel = program.getPad(mixGui.getXPos() + (bank * 16)).getMixerChannel();

					mixerStrips.get(mixGui.getXPos()).setValueB(mixerChannel.getLevel());
				}

				if (mixGui.getLink()) {

					for (int i = 0; i < 16; i++) {

						MixerChannel mc = program.getPad(i + (bank * 16)).getMixerChannel();

						mixerStrips.get(i).setValueB(mc.getLevel());
					}
				}
			}

			if (slp.getCurrentScreenName().equals("channelsettings")) {

				mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
						.getMixerChannel();

				stereoVolumeField.setText(Util.padLeftSpace("" + mixerChannel.getLevel(), 3));
			}

			break;

		case "panning":

			if (slp.getCurrentScreenName().equals("channelsettings")) {
				mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
						.getMixerChannel();
				setPanningField();
			}

			if (slp.getCurrentScreenName().equals("mixer")) {

				if (!mixGui.getLink()) {

					mixerChannel = program.getPad(mixGui.getXPos() + (bank * 16)).getMixerChannel();

					mixerStrips.get(mixGui.getXPos()).setValueA(mixerChannel.getPanning());
				}

				if (mixGui.getLink()) {

					for (int i = 0; i < 16; i++) {
						MixerChannel mc = program.getPad(i + (bank * 16)).getMixerChannel();
						mixerStrips.get(i).setValueA(mc.getPanning());
					}
				}
			}

			break;

		case "output":

			if (slp.getCurrentScreenName().equals("channelsettings")) {

				mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
						.getMixerChannel();

				setOutputField();
			}

			if (slp.getCurrentScreenName().equals("mixer")) {

				if (!mixGui.getLink()) {
					mixerChannel = program.getPad(mixGui.getXPos() + (bank * 16)).getMixerChannel();
					if (mixerChannel.isStereo()) {
						mixerStrips.get(mixGui.getXPos()).setValueAString(stereoNames[mixerChannel.getOutput()]);
					} else {
						mixerStrips.get(mixGui.getXPos()).setValueAString(monoNames[mixerChannel.getOutput()]);
					}
				}

				if (mixGui.getLink()) {
					for (int i = 0; i < 16; i++) {

						MixerChannel mc = program.getPad(i + bank * 16).getMixerChannel();

						if (mc.isStereo()) {

							mixerStrips.get(i).setValueAString(stereoNames[mc.getOutput()]);

						} else {

							mixerStrips.get(i).setValueAString(monoNames[mc.getOutput()]);

						}
					}
				}
			}

			break;

		case "volumeindividual":

			if (slp.getCurrentScreenName().equals("channelsettings")) {

				mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
						.getMixerChannel();

				individualVolumeField.setText(Util.padLeftSpace("" + mixerChannel.getVolumeIndividualOut(), 3));
			}

			if (slp.getCurrentScreenName().equals("mixer")) {

				if (!mixGui.getLink()) {

					mixerChannel = program.getPad(mixGui.getXPos() + (bank * 16)).getMixerChannel();

					mixerStrips.get(mixGui.getXPos()).setValueB(mixerChannel.getVolumeIndividualOut());

				}

				if (mixGui.getLink()) {

					for (int i = 0; i < 16; i++) {

						MixerChannel mc = program.getPad(i + (bank * 16)).getMixerChannel();

						mixerStrips.get(i).setValueB(mc.getVolumeIndividualOut());
					}
				}
			}

			break;

		case "fxpath":

			if (slp.getCurrentScreenName().equals("channelsettings")) {

				mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
						.getMixerChannel();

				fxPathField.setText(fxPathNames[mixerChannel.getFxPath()]);

			}

			if (slp.getCurrentScreenName().equals("mixer")) {

				if (!mixGui.getLink()) {

					mixerChannel = program.getPad(mixGui.getXPos() + (bank * 16)).getMixerChannel();

					mixerStrips.get(mixGui.getXPos()).setValueAString(fxPathNames[mixerChannel.getFxPath()]);
				}

				if (mixGui.getLink()) {

					for (int i = 0; i < 16; i++) {
						MixerChannel mc = program.getPad(i + (bank * 16)).getMixerChannel();

						mixerStrips.get(i).setValueAString(fxPathNames[mc.getFxPath()]);

					}
				}
			}

			break;

		case "fxsendlevel":

			if (slp.getCurrentScreenName().equals("channelsettings")) {

				mixerChannel = program.getPad(program.getPadNumberFromNote(mixGui.getChannelSettingsNote()))
						.getMixerChannel();

				fxSendLevelField.setText(Util.padLeftSpace("" + mixerChannel.getFxSendLevel(), 3));
			}

			if (slp.getCurrentScreenName().equals("mixer")) {

				if (!mixGui.getLink()) {
					mixerChannel = program.getPad(mixGui.getXPos() + (bank * 16)).getMixerChannel();

					mixerStrips.get(mixGui.getXPos()).setValueB(mixerChannel.getFxSendLevel());

				}

				if (mixGui.getLink()) {

					for (int i = 0; i < 16; i++) {

						MixerChannel mc = program.getPad(i + (bank * 16)).getMixerChannel();

						mixerStrips.get(i).setValueB(mc.getFxSendLevel());

					}
				}
			}

			break;

		case "link":

			if (mixGui.getLink()) {

				for (MixerStrip m : mixerStrips)
					m.setSelection(mixGui.getYPos());

			} else {

				for (MixerStrip m : mixerStrips)
					m.setSelection(-1);

				mixerStrips.get(mixGui.getXPos()).setSelection(mixGui.getYPos());

			}

			displayFunctionBox();

			break;

		case "note":

			if (slp.getCurrentScreenName().equals("channelsettings")) displayChannel();
			break;

		case "followstereo":

			if (slp.getCurrentScreenName().equals("channelsettings"))
				followStereoField.setText(mixerChannel.isFollowingStereo() ? "YES" : "NO");
			break;

		case "bank":
			initPadNameLabels();
			initMixerStrips();

			for (MixerStrip m : mixerStrips) {
				m.initFields();
				m.setColors();
			}

			displayMixerStrips();
			displayFunctionBox();

			break;

		case "masterlevel":

			if (mixerSetupGui.getMasterLevel() != -73) {

				masterLevelField.setText(Util.padLeftSpace("" + mixerSetupGui.getMasterLevel() + "dB", 5));

			} else {

				masterLevelField.setText(Util.padLeftSpace("-\u00D9\u00DAdB", 5));

			}

			break;

		case "fxdrum":

			fxDrumField.setText("" + mixerSetupGui.getFxDrum());

			break;

		case "stereomixsource":

			stereoMixSourceField.setText(mixerSetupGui.isStereoMixSourceDrum() ? "DRUM" : "PROGRAM");

			break;

		case "indivfxsource":

			indivFxSourceField.setText(mixerSetupGui.isIndivFxSourceDrum() ? "DRUM" : "PROGRAM");

			break;

		case "copypgmmixtodrum":

			copyPgmMixToDrumField.setText(mixerSetupGui.isCopyPgmMixToDrumEnabled() ? "YES" : "NO");

			break;

		case "recordmixchanges":

			recordMixChangesField.setText(mixerSetupGui.isRecordMixChangesEnabled() ? "YES" : "NO");

			break;

		}
	}

	public void displayFunctionBox() {
		String link = "";
		if (mixGui.getLink()) link = "_link";
		if (mixGui.getTab() == 0) {
			slp.drawFunctionBoxes("mixer_stereo" + link);
		}
		if (mixGui.getTab() == 1) {
			slp.drawFunctionBoxes("mixer_indiv" + link);
		}
		if (mixGui.getTab() == 2) {
			slp.drawFunctionBoxes("mixer_fxsend" + link);
		}

	}
}