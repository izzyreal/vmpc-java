package com.mpc.gui.sampler.window;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.Sequencer;

public class MuteAssignObserver implements Observer {

	private MainFrame mainFrame;

	private SamplerGui samplerGui;
	
	private JTextField noteField;
	private JTextField note0Field;
	private JTextField note1Field;

	private Program program;

	private Mpc mpc;
	private Sampler sampler;

	private Sequencer sequencer;

	private NoteParameters np;
	
	public MuteAssignObserver(MainFrame mainFrame) {

		this.mainFrame = mainFrame;
		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);
		mpc = Bootstrap.getGui().getMpc();
		sampler = mpc.getSampler();
		sequencer = mpc.getSequencer();
		int drum = Bootstrap.getGui().getSamplerGui().getSelectedDrum();

		noteField = mainFrame.lookupTextField("note");
		note0Field = mainFrame.lookupTextField("note0");
		note1Field = mainFrame.lookupTextField("note1");

		program = sampler
				.getProgram(sampler.getDrumBusProgramNumber(Bootstrap.getGui().getSamplerGui().getSelectedDrum() + 1));

		np = Sampler.getLastNp(program);
		np.deleteObservers();
		np.addObserver(this);
		
		displayNote();
		displayNote0();
		displayNote1();

	}

	private void displayNote() {
		int note = Sampler.getLastNp(program).getNumber();
		int pad = program.getPadNumberFromNote(note);
		String soundName = "OFF";
		String padName = pad == -1 ? "OFF" : sampler.getPadName(pad);
		int sound = program.getNoteParameters(note).getSndNumber();
		if (sound != -1) soundName = sampler.getSoundName(sound);
		noteField.setText(note + "/" + padName + "-" + soundName);
	}

	private void displayNote0() {
		int note0 = Sampler.getLastNp(program).getMuteAssignA();
		if (note0 == 34) {
			note0Field.setText("--");
			return;
		}
		int pad = program.getPadNumberFromNote(note0);
		String soundName = "OFF";
		int sound = program.getNoteParameters(note0).getSndNumber();
		if (sound != -1) soundName = sampler.getSoundName(sound);
		note0Field.setText(note0 + "/" + sampler.getPadName(pad) + "-" + soundName);
	}

	private void displayNote1() {
		int note1 = Sampler.getLastNp(program).getMuteAssignB();
		if (note1 == 34) {
			note1Field.setText("--");
			return;
		}
		int pad = program.getPadNumberFromNote(note1);
		String soundName = "OFF";
		int sound = program.getNoteParameters(note1).getSndNumber();
		if (sound != -1) soundName = sampler.getSoundName(sound);
		note1Field.setText(note1 + "/" + sampler.getPadName(pad) + "-" + soundName);
	}

	public void update(Observable o, Object arg) {
		
		np.deleteObservers();
		np = Sampler.getLastNp(program);
		np.addObserver(this);
		
		switch ((String) arg) {
		case "padandnote":
			displayNote();
			displayNote0();
			displayNote1();
			break;
		case "muteassigna":
			displayNote0();
			break;
		case "muteassignb":
			displayNote1();
			break;
		}

	}

}
