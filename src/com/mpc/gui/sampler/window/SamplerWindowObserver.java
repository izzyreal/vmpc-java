package com.mpc.gui.sampler.window;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Pad;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

//import com.mpc.sequencer.Track;

public class SamplerWindowObserver implements Observer {

	private String[] letters = { "A", "B", "C", "D" };

	private Mpc mpc;
	private Sampler sampler;
	private MpcSoundPlayerChannel mpcSoundPlayerChannel;
	private Program program;

	private LayeredScreen slp;
	private String csn;

	private SamplerGui samplerGui;
	private SamplerWindowGui swGui;
	private JTextField midiProgramChangeField;
	private JTextField programNameField;
	private JTextField pgmField;
	private JTextField newNameField;
	private JTextField pgm0Field;
	private JTextField pgm1Field;
	private JLabel info0Label;
	private JLabel info1Label;
	private JLabel info2Label;

	// Assignment View
	private JTextField a0Field;
	private JTextField b0Field;
	private JTextField c0Field;
	private JTextField d0Field;
	private JTextField a1Field;
	private JTextField b1Field;
	private JTextField c1Field;
	private JTextField d1Field;
	private JTextField a2Field;
	private JTextField b2Field;
	private JTextField c2Field;
	private JTextField d2Field;
	private JTextField a3Field;
	private JTextField b3Field;
	private JTextField c3Field;
	private JTextField d3Field;

	// Initialize Pad Assign
	private JTextField initPadAssignField;

	// Copy Note Parameters
	private JTextField prog0Field;
	private JTextField prog1Field;
	private JTextField note0Field;
	private JTextField note1Field;

	// Note field in several screens
	private JTextField noteField;

	// Velocity Modulation
	private JTextField veloAttackField;
	private JTextField veloStartField;
	private JTextField veloLevelField;

	// Velo field in several screens -- responds to kb/midi pad input
	private JTextField veloField;

	// Velo/Env to Filter
	private JTextField attackField;
	private JTextField decayField;
	private JTextField amountField;
	private JTextField veloFreqField;

	private JTextField tuneField;

	private JTextField veloPitchField;

	// Auto Chromatic Assignment
	private JTextField autoChromAssSndField;

	private JTextField sourceField;

	private JTextField originalKeyField;

	private JTextField nameForNewSoundField;

	private JTextField assignToNoteField;

	public SamplerWindowObserver(Mpc mpc, MainFrame mainFrame) {

		this.mpc = mpc;
		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		swGui = Bootstrap.getGui().getSamplerWindowGui();
		swGui.deleteObservers();
		swGui.addObserver(this);

		sampler = mpc.getSampler();
		slp = mainFrame.getLayeredScreen();
		csn = slp.getCurrentScreenName();
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		Sampler.getLastNp(program).deleteObservers();
		Sampler.getLastPad(program).deleteObservers();
		Sampler.getLastNp(program).addObserver(this);
		Sampler.getLastPad(program).addObserver(this);

		program.deleteObservers();
		program.addObserver(this);

		mpcSoundPlayerChannel.deleteObservers();
		mpcSoundPlayerChannel.addObserver(this);

		program.deleteObservers();
		program.addObserver(this);

		mpcSoundPlayerChannel.deleteObservers();
		mpcSoundPlayerChannel.addObserver(this);

		if (csn.equals("program")) {

			programNameField = mainFrame.lookupTextField("programname");
			midiProgramChangeField = mainFrame
					.lookupTextField("midiprogramchange");

			displayProgramName();
			displayMidiProgramChange();

		}

		if (csn.equals("deleteprogram")) {

			pgmField = mainFrame.lookupTextField("pgm");

			displayPgm();

		}

		if (csn.equals("createnewprogram")) {

			newNameField = mainFrame.lookupTextField("newname");
			midiProgramChangeField = mainFrame
					.lookupTextField("midiprogramchange");

			displayNewName();
			displayMidiProgramChange();
		}

		if (csn.equals("copyprogram")) {

			pgm0Field = mainFrame.lookupTextField("pgm0");
			pgm1Field = mainFrame.lookupTextField("pgm1");

			displayPgm0();
			displayPgm1();

		}

		if (csn.equals("assignmentview")) {

			info0Label = mainFrame.lookupLabel("info0");
			info1Label = mainFrame.lookupLabel("info1");
			info2Label = mainFrame.lookupLabel("info2");

			a0Field = mainFrame.lookupTextField("a0");
			b0Field = mainFrame.lookupTextField("b0");
			c0Field = mainFrame.lookupTextField("c0");
			d0Field = mainFrame.lookupTextField("d0");

			a1Field = mainFrame.lookupTextField("a1");
			b1Field = mainFrame.lookupTextField("b1");
			c1Field = mainFrame.lookupTextField("c1");
			d1Field = mainFrame.lookupTextField("d1");

			a2Field = mainFrame.lookupTextField("a2");
			b2Field = mainFrame.lookupTextField("b2");
			c2Field = mainFrame.lookupTextField("c2");
			d2Field = mainFrame.lookupTextField("d2");

			a3Field = mainFrame.lookupTextField("a3");
			b3Field = mainFrame.lookupTextField("b3");
			c3Field = mainFrame.lookupTextField("c3");
			d3Field = mainFrame.lookupTextField("d3");

			info0Label.setOpaque(false);

			info1Label.setBackground(Bootstrap.lcdOn);
			info1Label.setOpaque(true);
			info1Label.setForeground(Bootstrap.lcdOff);
			Border empty = new EmptyBorder(0, 2, 1, -3);
			info1Label.setSize(26, 18);
			info1Label.setBorder(empty);

			JTextField[] pads = { a3Field, b3Field, c3Field, d3Field, a2Field,
					b2Field, c2Field, d2Field, a1Field, b1Field, c1Field,
					d1Field, a0Field, b0Field, c0Field, d0Field };

			for (JTextField tf : pads)
				tf.setOpaque(false);

			mainFrame.setFocus(SamplerWindowGui.getFocusFromPadNumber(),
					slp.getWindowPanel());

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayAssignmentView();
				}
			});
		}

		if (csn.equals("keeporretry")) {

			nameForNewSoundField = mainFrame.lookupTextField("namefornewsound");
			assignToNoteField = mainFrame.lookupTextField("assigntonote");
			displayNameForNewSound();
			displayAssignToPad();
		}

		if (csn.equals("initpadassign")) {

			initPadAssignField = mainFrame.lookupTextField("initpadassign");

			displayInitPadAssign();

		}

		if (csn.equals("copynoteparameters")) {

			prog0Field = mainFrame.lookupTextField("prog0");
			note0Field = mainFrame.lookupTextField("note0");
			prog1Field = mainFrame.lookupTextField("prog1");
			note1Field = mainFrame.lookupTextField("note1");

			displayProg0();
			displayNote0();
			displayProg1();
			displayNote1();

		}

		if (csn.equals("velocitymodulation")) {

			noteField = mainFrame.lookupTextField("note");
			veloAttackField = mainFrame.lookupTextField("veloattack");
			veloStartField = mainFrame.lookupTextField("velostart");
			veloLevelField = mainFrame.lookupTextField("velolevel");
			veloField = mainFrame.lookupTextField("velo");

			displayNote();
			displayVeloAttack();
			displayVeloStart();
			displayVeloLevel();
			displayVelo();
		}

		if (csn.equals("veloenvfilter")) {

			noteField = mainFrame.lookupTextField("note");
			attackField = mainFrame.lookupTextField("attack");
			decayField = mainFrame.lookupTextField("decay");
			amountField = mainFrame.lookupTextField("amount");
			veloFreqField = mainFrame.lookupTextField("velofreq");
			veloField = mainFrame.lookupTextField("velo");

			displayNote();
			displayAttack();
			displayDecay();
			displayAmount();
			displayVeloFreq();
			displayVelo();
		}

		if (csn.equals("velopitch")) {

			noteField = mainFrame.lookupTextField("note");
			tuneField = mainFrame.lookupTextField("tune");
			veloPitchField = mainFrame.lookupTextField("velopitch");
			veloField = mainFrame.lookupTextField("velo");

			displayNote();
			displayTune();
			displayVeloPitch();
			displayVelo();
		}

		if (csn.equals("muteassign")) {

			noteField = mainFrame.lookupTextField("note");
			note0Field = mainFrame.lookupTextField("note0");
			note1Field = mainFrame.lookupTextField("note1");

			displayNote();
			displayNote0();
			displayNote1();

		}

		if (csn.equals("autochromaticassignment")) {

			sourceField = mainFrame.lookupTextField("source");
			autoChromAssSndField = mainFrame.lookupTextField("snd");
			originalKeyField = mainFrame.lookupTextField("originalkey");
			tuneField = mainFrame.lookupTextField("tune");
			programNameField = mainFrame.lookupTextField("programname");
			displaySource();
			displayAutoChromAssSnd();
			displayOriginalKey();
			displayTune();
			displayProgramName();
		}

	}

	private void displayNameForNewSound() {
		nameForNewSoundField.setText(sampler.getPreviewSound().getName());
	}

	private void displayAssignToPad() {
		assignToNoteField.setText(Sampler.getLastPad(program).getNote()
				+ "/"
				+ sampler.getPadName(Sampler.getLastPad(program).getNumber()));
	}

	private void displaySource() {

		int nn = Sampler.getLastNp(program).getNumber();
		int pn = program.getPadNumberFromNote(nn);

		String nnName = "" + (nn);
		String padName = pn == -1 ? "OFF" : sampler.getPadName(pn);

		sourceField.setText(nnName + "/" + padName);
	}

	private void displayTune() {
		if (csn.equals("autochromaticassignment")) {
			int value = swGui.getTune();
			String prefix = value < 0 ? "-" : " ";
			tuneField.setText(prefix
					+ Util.padLeftSpace("" + Math.abs(value), 3));
		} else {
			int value = Sampler.getLastNp(program).getTune();
			String prefix = value < 0 ? "-" : " ";
			tuneField.setText(prefix
					+ Util.padLeftSpace("" + Math.abs(value), 3));
		}
	}

	private void displayVeloPitch() {

		int value = Sampler.getLastNp(program).getVelocityToPitch();

		String prefix = value < 0 ? "-" : " ";

		veloPitchField.setText(prefix
				+ Util.padLeftSpace("" + Math.abs(value), 3));
	}

	private void displayAttack() {
		attackField.setText(Util.padLeftSpace(""
				+ Sampler.getLastNp(program).getFilterAttack(), 3));
	}

	private void displayDecay() {
		decayField.setText(Util.padLeftSpace(""
				+ Sampler.getLastNp(program).getFilterDecay(), 3));
	}

	private void displayAmount() {
		amountField.setText(Util.padLeftSpace(""
				+ Sampler.getLastNp(program).getFilterEnvelopeAmount(), 3));
	}

	private void displayVeloFreq() {
		veloFreqField
				.setText(Util.padLeftSpace(""
						+ Sampler.getLastNp(program)
								.getVelocityToFilterFrequency(), 3));
	}

	private void displayNote() {

		NoteParameters noteParameters = Sampler.getLastNp(program);
		int sn = noteParameters.getSndNumber();
		int pn = program.getPadNumberFromNote(noteParameters.getNumber());
		Pad pad = program.getPad(pn);

		String padName = pn != -1 ? sampler.getPadName(pn) : "OFF";
		String sampleName = sn != -1 ? sampler.getSoundName(sn) : "OFF";

		String stereo = pad.getMixerChannel().isStereo() && sn != -1 ? "(ST)"
				: "";

		noteField.setText("" + (noteParameters.getNumber()) + "/" + padName
				+ "-" + Util.padRightSpace(sampleName, 16) + stereo);
	}

	private void displayVeloAttack() {
		veloAttackField.setText(Util.padLeftSpace(
				"" + Sampler.getLastNp(program).getVelocityToAttack(), 3));
	}

	private void displayVeloStart() {
		veloStartField.setText(Util.padLeftSpace(""
				+ Sampler.getLastNp(program).getVelocityToStart(), 3));
	}

	private void displayVeloLevel() {
		veloLevelField.setText(Util.padLeftSpace(""
				+ Sampler.getLastNp(program).getVeloToLevel(), 3));
	}

	private void displayVelo() {
		// TODO Auto-generated method stub

	}

	private void displayProg0() {
		prog0Field.setText(Util.padLeftSpace("" + (swGui.getProg0() + 1), 2)
				+ "-" + sampler.getProgram(swGui.getProg0()).getName());
	}

	private void displayNote0() {

		Program prog = csn.equals("muteassign") ? program : sampler
				.getProgram(swGui.getProg0());

		int nn = csn.equals("muteassign") ? Sampler.getLastNp(prog)
				.getMuteAssignA() : swGui.getNote0();

		int pn = prog.getPadNumberFromNote(nn);
		int sn = nn != -1 ? prog.getNoteParameters(nn).getSndNumber() : -1;

		String nnName = nn == -1 ? "--" : "" + (nn);
		String padName = pn != -1 ? sampler.getPadName(pn) : "OFF";
		String sampleName = sn != -1 ? "-" + sampler.getSoundName(sn) : "-OFF";

		if (nn == -1) sampleName = "";

		note0Field.setText(nnName + "/" + padName + sampleName);
	}

	private void displayProg1() {
		prog1Field.setText(Util.padLeftSpace("" + (swGui.getProg1() + 1), 2)
				+ "-" + sampler.getProgram(swGui.getProg1()).getName());
	}

	private void displayNote1() {

		Program prog = csn.equals("muteassign") ? program : sampler
				.getProgram(swGui.getProg1());

		int nn = csn.equals("muteassign") ? Sampler.getLastNp(prog)
				.getMuteAssignB() : swGui.getNote1();

		int pn = prog.getPadNumberFromNote(nn);
		int sn = nn != -1 ? prog.getNoteParameters(nn).getSndNumber() : -1;

		String nnName = nn == -1 ? "--" : "" + (nn);
		String padName = pn != -1 ? sampler.getPadName(pn) : "OFF";
		String sampleName = sn != -1 ? "-" + sampler.getSoundName(sn) : "-OFF";

		if (nn == -1) sampleName = "";

		note1Field.setText(nnName + "/" + padName + sampleName);
	}

	private void displayInitPadAssign() {
		initPadAssignField.setText(swGui.isInitPadAssignMaster() ? "MASTER"
				: "PROGRAM");
	}

	@Override
	public void update(Observable o, Object arg) {

		program.deleteObservers();
		mpcSoundPlayerChannel.deleteObservers();

		String parameter = (String) arg;

		sampler = mpc.getSampler();
		mpcSoundPlayerChannel = sampler.getDrum(samplerGui.getSelectedDrum());
		program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());

		Sampler.getLastNp(program).deleteObservers();
		Sampler.getLastNp(program).addObserver(this);

		Sampler.getLastPad(program).deleteObserver(this);
		Sampler.getLastPad(program).addObserver(this);

		program.addObserver(this);
		mpcSoundPlayerChannel.addObserver(this);

		switch (parameter) {

		case "newprogramchange":
		case "midiprogramchange":
			displayMidiProgramChange();
			break;

		case "deletepgm":
			displayPgm();
			break;

		case "pgm0":
			displayPgm0();
			break;

		case "pgm1":
			displayPgm1();
			break;

		case "bank":
			displayAssignmentView();
			break;

		case "padandnote":
			if (csn.equals("keeporretry")) {
				displayAssignToPad();
			}
			if (csn.equals("assignmentview")) {
				displayInfo1();
				displayInfo2();
			}

			if (csn.equals("velocitymodulation")) {
				displayNote();
				displayVeloAttack();
				displayVeloStart();
				displayVeloLevel();
			}

			if (csn.equals("veloenvfilter")) {
				displayNote();
				displayAttack();
				displayDecay();
				displayAmount();
				displayVeloFreq();
			}

			if (csn.equals("velopitch")) {
				displayNote();
				displayTune();
				displayVeloPitch();
			}

			if (csn.equals("muteassign")) {
				displayNote();
				displayNote0();
				displayNote1();
			}

			if (csn.equals("autochromaticassignment")) {
				displaySource();
				// displayAutoChromAssSnd();
				break;
			}

			break;

		case "note":

			if (csn.equals("assignmentview")) {
				int pn = SamplerWindowGui.getPadNumberFromFocus();

				displayInfo1();
				displayInfo2();
				displayPad(pn);
			}

			break;

		case "initpadassign":
			displayInitPadAssign();
			break;

		case "prog0":
			displayProg0();
			displayNote0();
			break;

		case "muteassigna":
		case "note0":
			displayNote0();
			break;

		case "prog1":
			displayProg1();
			displayNote1();
			break;

		case "muteassignb":
		case "note1":
			displayNote1();
			break;

		case "velocitytoattack":
			displayVeloAttack();
			break;

		case "velocitytostart":
			displayVeloStart();
			break;

		case "velocitytolevel":
			displayVeloLevel();
			break;

		case "filterattack":
			displayAttack();
			break;

		case "filterdecay":
			displayDecay();
			break;

		case "filterenvelopeamount":
			displayAmount();
			break;

		case "velocitytofilterfrequency":
			displayVeloFreq();
			break;

		case "tune":
			displayTune();
			break;

		case "velocitytopitch":
			displayVeloPitch();
			break;

		case "autochromasssnd":
			displayAutoChromAssSnd();
			break;

		case "originalkey":
			displayOriginalKey();
			break;

		case "namefornewsound":
			displayNameForNewSound();
			break;
			
		}

	}

	private void displayOriginalKey() {
		int nn = swGui.getOriginalKey();
		int pad = program.getPadNumberFromNote(nn);
		String pn = sampler.getPadName(pad);
		originalKeyField.setText("" + nn + "/" + pn);
	}

	private void displayAutoChromAssSnd() {

		int sn = swGui.getAutoChromAssSnd();

		String sampleName = sn == -1 ? "OFF" : sampler.getSoundName(sn);
		String stereo = sn == -1 ? "" : (sampler.getSound(sn).isMono() ? ""
				: "(ST)");
		autoChromAssSndField.setText(Util.padRightSpace(sampleName, 16)
				+ stereo);
	}

	private void displayAssignmentView() {

		for (int i = 0; i < 16; i++)
			displayPad(i);

		displayInfo0();
		displayInfo1();
		displayInfo2();
	}

	private void displayInfo0() {

		info0Label.setText("Bank:" + letters[samplerGui.getBank()] + " Note:");
	}

	private void displayInfo1() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				int nn = program.getPad(
						SamplerWindowGui.getPadNumberFromFocus())
						.getNote();

				info1Label.setText("" + (nn != -1 ? nn : "--"));
			}
		});
	}

	private void displayInfo2() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				int pn = SamplerWindowGui.getPadNumberFromFocus();

				int nn = program.getPad(pn).getNote();

				if (nn == -1) {

					info2Label.setText("=");
					return;

				}

				int sampleNumber = program.getNoteParameters(nn).getSndNumber();

				String sampleName = sampleNumber != -1 ? sampler
						.getSoundName(sampleNumber) : "";

				String stereo = "";

				if (sampleNumber != -1
						&& Sampler.getLastPad(program).getMixerChannel()
								.isStereo() && !sampleName.equals(""))
					stereo = "(ST)";

				info2Label.setText("=" + sampleName + stereo);
			}
		});
	}

	private void displayPad(int i) {

		JTextField[] pads = { a3Field, b3Field, c3Field, d3Field, a2Field,
				b2Field, c2Field, d2Field, a1Field, b1Field, c1Field, d1Field,
				a0Field, b0Field, c0Field, d0Field };

		int nn = program.getPad(i + (16 * samplerGui.getBank()))
				.getNote();

		String sampleName = "";

		if (nn != -1) {

			int sampleNumber = program.getNoteParameters(nn).getSndNumber();

			sampleName = sampleNumber != -1 ? sampler.getSoundName(
					sampleNumber).trim() : "--";

			if (sampleName.length() > 8)
				sampleName = sampleName.substring(0, 8);

		}

		pads[i].setText(sampleName);
	}

	private void displayPgm0() {

		pgm0Field.setText(Util.padLeftSpace("" + (swGui.getPgm0() + 1), 2)
				+ "-" + sampler.getProgram(swGui.getPgm0()).getName());

	}

	private void displayPgm1() {

		pgm1Field.setText(Util.padLeftSpace("" + (swGui.getPgm1() + 1), 2)
				+ "-" + sampler.getProgram(swGui.getPgm1()).getName());

	}

	private void displayNewName() {
		System.out.println("setting new name");
		newNameField.setText(swGui.getNewName());
	}

	private void displayPgm() {
		pgmField.setText(Util.padLeftSpace("" + (swGui.getDeletePgm() + 1), 2)
				+ "-" + sampler.getProgram(swGui.getDeletePgm()).getName());
	}

	private void displayProgramName() {
		if (csn.equals("autochromaticassignment")) {
			programNameField.setText(swGui.getNewName());
		} else {
			programNameField.setText(program.getName());
		}
	}

	private void displayMidiProgramChange() {

		midiProgramChangeField.setText(Util.padLeftSpace(
				"" + program.getMidiProgramChange(), 3));

	}
}