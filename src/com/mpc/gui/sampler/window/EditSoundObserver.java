package com.mpc.gui.sampler.window;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.disk.AbstractDisk;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.Background;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.sampler.Sampler;

public class EditSoundObserver implements Observer {
	private Gui gui;
	private EditSoundGui editSoundGui;

	private SequencerWindowGui sequencerWindowGui;

	private Sampler sampler;

	private JTextField editField;

	private JTextField variable0Field;
	private JLabel variable0Label;
	private JTextField ratioField;
	private JLabel ratioLabel;
	private JTextField presetField;
	private JLabel presetLabel;
	private JTextField adjustField;
	private JLabel adjustLabel;

	private String[] editNames = { "DISCARD", "LOOP FROM ST TO END",
			"SECTION \u00C4 NEW SOUND", "INSERT SOUND \u00C4 SECTION START",
			"DELETE SECTION", "SILENCE SECTION", "REVERSE SECTION",
			"TIME STRETCH", "SLICE SOUND" };

	private LayeredScreen slp;
	private String[] timeStretchPresetNames = { "FEM VOX", "MALE VOX",
			"LOW MALE VOX", "VOCAL", "HFREQ RHYTHM", "MFREQ RHYTHM",
			"LFREQ RHYTHM", "PERCUSSION", "LFREQ PERC.", "STACCATO",
			"LFREQ SLOW", "MUSIC 1", "MUSIC 2", "MUSIC 3", "SOFT PERC.",
			"HFREQ ORCH.", "LFREQ ORCH.", "SLOW ORCH." };
	private JLabel endMarginLabel;
	private JTextField endMarginField;
	private JLabel createNewProgramLabel;
	private JTextField createNewProgramField;


	public EditSoundObserver(Sampler sampler, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		this.sampler = sampler;
		
		slp = mainFrame.getLayeredScreen();
		gui = Bootstrap.getGui();
		
		String[] newTimeStretchPresetNames = new String[54];
		int totalCounter = 0;
		
		String[] letters = { "A", "B", "C" };
		
		for (String s : timeStretchPresetNames) {
			
			for (int i = 0; i < 3; i++) {
				newTimeStretchPresetNames[totalCounter] = AbstractDisk.padRightSpace(s, 13)
						+ letters[i];
				totalCounter++;
			}
			
		}
		
		timeStretchPresetNames = newTimeStretchPresetNames;
		timeStretchPresetNames[52] = " SLOW ORCH.  B"; // extra space is
														// intentional OS1.2
														// typo replication :)

		editSoundGui = gui.getEditSoundGui();
		editSoundGui.deleteObservers();
		editSoundGui.addObserver(this);

		sequencerWindowGui = gui.getSequencerWindowGui();
		sequencerWindowGui.deleteObservers();
		sequencerWindowGui.addObserver(this);
		
		editField = mainFrame.lookupTextField("edit");
		variable0Field = mainFrame.lookupTextField("variable0");
		variable0Label = mainFrame.lookupLabel("variable0");
		ratioField = mainFrame.lookupTextField("variable1");
		ratioLabel = mainFrame.lookupLabel("variable1");
		presetField = mainFrame.lookupTextField("variable2");
		presetLabel = mainFrame.lookupLabel("variable2");
		adjustField = mainFrame.lookupTextField("variable3");
		adjustLabel = mainFrame.lookupLabel("variable3");
		endMarginField = mainFrame.lookupTextField("endmargin");
		endMarginLabel = mainFrame.lookupLabel("endmargin");
		createNewProgramField = mainFrame.lookupTextField("createnewprogram");
		createNewProgramLabel = mainFrame.lookupLabel("createnewprogram");
		
		displayEdit();
	}

	private void displayEdit() {
		editField.setText(editNames[editSoundGui.getEdit()]);
		Background b = slp.getCurrentBackground();

		if (editSoundGui.getEdit() == 0) {
			b.setBackgroundName("editsound");
			variable0Field.setVisible(false);
			variable0Label.setVisible(false);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
		}

		if (editSoundGui.getEdit() == 1) {
			b.setBackgroundName("editloopfromsttoend");
			variable0Field.setVisible(false);
			variable0Label.setVisible(false);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
		}

		if (editSoundGui.getEdit() == 2) {
			b.setBackgroundName("editempty");
			variable0Field.setVisible(true);
			variable0Label.setVisible(true);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
			displayVariable();
		}

		if (editSoundGui.getEdit() == 3) {
			b.setBackgroundName("editempty");
			variable0Field.setVisible(true);
			variable0Label.setVisible(true);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
			displayVariable();
		}

		if (editSoundGui.getEdit() == 4) {
			b.setBackgroundName("editexecute");
			variable0Field.setVisible(false);
			variable0Label.setVisible(false);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
		}

		if (editSoundGui.getEdit() == 5) {
			b.setBackgroundName("editexecute");
			variable0Field.setVisible(false);
			variable0Label.setVisible(false);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
		}

		if (editSoundGui.getEdit() == 6) {
			b.setBackgroundName("editexecute");
			variable0Field.setVisible(false);
			variable0Label.setVisible(false);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
		}

		if (editSoundGui.getEdit() == 7) {
			b.setBackgroundName("editempty");
			variable0Field.setVisible(true);
			variable0Label.setVisible(true);
			ratioField.setVisible(true);
			ratioLabel.setVisible(true);
			presetField.setVisible(true);
			presetLabel.setVisible(true);
			adjustField.setVisible(true);
			adjustLabel.setVisible(true);
			endMarginLabel.setVisible(false);
			endMarginField.setVisible(false);
			createNewProgramLabel.setVisible(false);
			createNewProgramField.setVisible(false);
			displayVariable();
		}

		if (editSoundGui.getEdit() == 8) {
			b.setBackgroundName("editempty");
			variable0Field.setVisible(false);
			variable0Label.setVisible(false);
			ratioField.setVisible(false);
			ratioLabel.setVisible(false);
			presetField.setVisible(false);
			presetLabel.setVisible(false);
			adjustField.setVisible(false);
			adjustLabel.setVisible(false);
			endMarginLabel.setVisible(true);
			endMarginField.setVisible(true);
			createNewProgramLabel.setVisible(true);
			createNewProgramField.setVisible(true);
			displayEndMargin();
			displayCreateNewProgram();
			
		}
	}

	private void displayCreateNewProgram() {
		createNewProgramField
				.setText(editSoundGui.getCreateNewProgram() ? "YES" : "NO");
	}

	private void displayEndMargin() {
		endMarginField.setText("" + editSoundGui.getEndMargin());
	}

	private void displayVariable() {
		if (editSoundGui.getEdit() == 2) {
			variable0Label.setSize(9 * 6 * 2, 18);
			variable0Label.setText("New name:");
			variable0Field.setLocation(variable0Label.getSize().width
					+ (19 * 2), 21 * 2 - 2);
			variable0Field.setText(editSoundGui.getNewName());
		}

		if (editSoundGui.getEdit() == 3) {
			String sampleName = sampler.getSoundName(editSoundGui
					.getInsertSndNr());
			variable0Label.setSize(11 * 6 * 2, 18);
			variable0Label.setText("Insert Snd:");
			variable0Field.setLocation(variable0Label.getSize().width + 19 * 2,
					21 * 2 - 2);
			String stereo = "";
			if (!sampler.getSound(editSoundGui.getInsertSndNr())
					.isMono())
				stereo = "(ST)";
			variable0Field.setText(AbstractDisk.padRightSpace(sampleName, 16) + stereo);
		}

		if (editSoundGui.getEdit() == 7) {
			variable0Label.setSize(9 * 6 * 2, 18);
			variable0Label.setText("New name:");
			variable0Field.setLocation(variable0Label.getSize().width
					+ (19 * 2), 21 * 2 - 2);
			variable0Field.setText(editSoundGui.getNewName());
			ratioField.setText((editSoundGui.getTimeStretchRatio() / 100.0)
					+ "%");
			presetField.setText(timeStretchPresetNames[editSoundGui
					.getTimeStretchPresetNumber()]);
			adjustField.setText("" + editSoundGui.getTimeStretchAdjust());
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "edit":
			displayEdit();
			break;

		case "insertsoundnumber":
			displayVariable();
			break;

		case "timestretchratio":
			displayVariable();
			break;

		case "timestretchpresetnumber":
			displayVariable();
			break;

		case "timestretchadjust":
			displayVariable();
			break;

		case "endmargin" :
			displayEndMargin();
			break;
			
		case "createnewprogram" :
			displayCreateNewProgram();
			break;
		}
	}
}