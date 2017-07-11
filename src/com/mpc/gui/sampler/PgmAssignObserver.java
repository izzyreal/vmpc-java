package com.mpc.gui.sampler;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
//import com.mpc.sequencer.Track;
//import com.mpc.sequencer.Sequence;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public class PgmAssignObserver implements Observer {

	private Sampler sampler;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;
	private Program program;

	private String[] soundGenerationModes = { "NORMAL", "SIMULT", "VEL SW", "DCY SW" };

	private JTextField velocityRangeLowerField;
	private JTextField velocityRangeUpperField;
	private JTextField optionalNoteAField;
	private JTextField optionalNoteBField;
	private JTextField pgmField;
	private JTextField selectedPadNumberField;
	private JTextField padNoteField;
	private JTextField padAssignModeField;
	private JTextField selectedNoteField;
	private JTextField sndNumberField;
	private JTextField soundGenerationModeField;

	private JLabel velocityRangeLowerLabel;
	private JLabel velocityRangeUpperLabel;
	private JLabel optionalNoteALabel;
	private JLabel optionalNoteBLabel;
	private JLabel isSoundStereoLabel;
	private SamplerGui samplerGui;

	public PgmAssignObserver(Mpc mpc, MainFrame mainFrame) {

		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		sampler = mpc.getSampler();
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());

		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());
		program.deleteObservers();
		program.addObserver(this);

		Sampler.getLastNp(program).deleteObservers();
		Sampler.getLastNp(program).addObserver(this);

		Sampler.getLastPad(program).deleteObservers();
		Sampler.getLastPad(program).addObserver(this);

		mpcSoundPlayerChannel.deleteObservers();
		mpcSoundPlayerChannel.addObserver(this);

		velocityRangeLowerLabel = mainFrame.lookupLabel("velocityrangelower");
		velocityRangeLowerField = mainFrame.lookupTextField("velocityrangelower");
		velocityRangeUpperLabel = mainFrame.lookupLabel("velocityrangeupper");
		velocityRangeUpperField = mainFrame.lookupTextField("velocityrangeupper");
		optionalNoteALabel = mainFrame.lookupLabel("optionalnotenumbera");
		optionalNoteAField = mainFrame.lookupTextField("optionalnotenumbera");
		optionalNoteBLabel = mainFrame.lookupLabel("optionalnotenumberb");
		optionalNoteBField = mainFrame.lookupTextField("optionalnotenumberb");

		pgmField = mainFrame.lookupTextField("pgm");
		selectedPadNumberField = mainFrame.lookupTextField("pad");
		padNoteField = mainFrame.lookupTextField("padnote");
		padAssignModeField = mainFrame.lookupTextField("padassign");
		selectedNoteField = mainFrame.lookupTextField("note");
		sndNumberField = mainFrame.lookupTextField("snd");
		soundGenerationModeField = mainFrame.lookupTextField("mode");

		isSoundStereoLabel = mainFrame.lookupLabel("issoundstereo");

		displaySoundGenerationMode();

		displayPgm();

		displaySelectedPadName();
		displayPadNote();
		displayPadAssignMode();

		displaySelectedNote();

		displaySoundName();

	}

	private void displayPgm() {
		pgmField.setText(Util.padLeftSpace("" + (mpcSoundPlayerChannel.getProgram() + 1), 2) + "-" + program.getName());
	}

	private void displaySoundName() {
		int sndNumber = Sampler.getLastNp(program).getSndNumber();
		if (sndNumber == -1) {
			sndNumberField.setText("OFF");
			isSoundStereoLabel.setText("");
		} else {
			sndNumberField.setText("" + sampler.getSoundName(sndNumber));
		}

		if (sampler.getSoundCount() != 0 && sndNumber != -1) {
			if (sampler.getSounds().get(sndNumber).isMono()) {
				isSoundStereoLabel.setText("");
			} else {
				isSoundStereoLabel.setText("(ST)");
			}
		}
	}

	private void displayPadAssignMode() {

		padAssignModeField.setText(SamplerGui.isPadAssignMaster() ? "MASTER" : "PROGRAM");

	}

	private void displayPadNote() {
		if (Sampler.getLastPad(program).getNote() == 34) {
			padNoteField.setText("--");
			return;
		}
		padNoteField.setText("" + (Sampler.getLastPad(program).getNote()));
	}

	@Override
	public void update(Observable o, Object arg) {

		String parameter = (String) arg;

		program.deleteObservers();
		mpcSoundPlayerChannel.deleteObservers();

		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		Sampler.getLastNp(program).addObserver(this);
		Sampler.getLastPad(program).addObserver(this);
		// c1 = Sampler.getLastNn(program);
		// c2 = Sampler.getLastPad(program);
		// c1.setNext(c2);

		program.addObserver(this);
		mpcSoundPlayerChannel.addObserver(this);

		switch (parameter) {

		case "pgm":
			displayPgm();
		case "padandnote":
		case "padnotenumber":
			displaySelectedNote();
			displaySelectedPadName();
			displayPadNote();
			displaySoundName();
			displaySoundGenerationMode();
			break;

		case "padassignmode":
			displayPadAssignMode();
			displayPadNote();
			displaySoundName();
			displaySoundGenerationMode();
			break;

		case "samplenumber":
			displaySoundName();
			break;

		case "soundgenerationmode":
			displaySoundGenerationMode();
			break;

		case "ifover1":
			displayVeloRangeLower();
			break;

		case "ifover2":
			displayVeloRangeUpper();
			break;

		case "use1":
			displayOptionalNoteA();
			break;

		case "use2":
			displayOptionalNoteB();
			break;

		}
	}

	private void displaySoundGenerationMode() {

		int sgm = -1;

		if (Sampler.getLastNp(program) != null) {

			sgm = Sampler.getLastNp(program).getSoundGenerationMode();

			soundGenerationModeField.setText("" + soundGenerationModes[sgm]);

			if (sgm != 0) {

				velocityRangeLowerLabel.setVisible(false);
				velocityRangeLowerField.setVisible(false);
				velocityRangeUpperLabel.setVisible(false);
				velocityRangeUpperField.setVisible(false);

				optionalNoteALabel.setVisible(true);
				optionalNoteAField.setVisible(true);
				optionalNoteBLabel.setVisible(true);
				optionalNoteBField.setVisible(true);

				optionalNoteALabel.setText("Also play note:");
				optionalNoteBLabel.setText("Also play note:");

				displayOptionalNoteA();
				displayOptionalNoteB();

			}

			if (sgm == 2 || sgm == 3) {

				optionalNoteALabel.setText("        , use:");
				optionalNoteBLabel.setText("        , use:");

				velocityRangeLowerLabel.setVisible(true);
				velocityRangeLowerField.setVisible(true);
				velocityRangeUpperLabel.setVisible(true);
				velocityRangeUpperField.setVisible(true);

				displayVeloRangeLower();
				displayVeloRangeUpper();
			}

		}

		if (Sampler.getLastNp(program) == null || sgm == -1 || sgm == 0) {

			velocityRangeLowerLabel.setVisible(false);
			velocityRangeLowerField.setVisible(false);
			velocityRangeUpperLabel.setVisible(false);
			velocityRangeUpperField.setVisible(false);
			optionalNoteALabel.setVisible(false);
			optionalNoteAField.setVisible(false);
			optionalNoteBLabel.setVisible(false);
			optionalNoteBField.setVisible(false);
		}

	}

	private void displayVeloRangeUpper() {
		int rangeB = Sampler.getLastNp(program).getVelocityRangeUpper();
		velocityRangeUpperField.setText(Util.padLeftSpace("" + rangeB, 3));
	}

	private void displayVeloRangeLower() {
		int rangeA = Sampler.getLastNp(program).getVelocityRangeLower();
		velocityRangeLowerField.setText(Util.padLeftSpace("" + rangeA, 3));

	}

	private void displayOptionalNoteB() {

		int noteIntB = Sampler.getLastNp(program).getOptionalNoteB();
		int padIntB = program.getPadNumberFromNote(noteIntB);

		String noteB = noteIntB != -1 ? "" + (noteIntB) : "--";
		String padB = padIntB != -1 ? "" + sampler.getPadName(padIntB) : "OFF";

		optionalNoteBField.setText(noteB + "/" + padB);
	}

	private void displayOptionalNoteA() {
		int noteIntA = Sampler.getLastNp(program).getOptionalNoteA();
		int padIntA = program.getPadNumberFromNote(noteIntA);

		String noteA = noteIntA != -1 ? "" + (noteIntA) : "--";
		String padA = padIntA != -1 ? "" + sampler.getPadName(padIntA) : "OFF";
		optionalNoteAField.setText(noteA + "/" + padA);
	}

	private void displaySelectedNote() {
		selectedNoteField.setText("" + Sampler.getLastNp(program).getNumber());
	}

	private void displaySelectedPadName() {
		selectedPadNumberField.setText(sampler.getPadName(samplerGui.getPad()));
	}
}