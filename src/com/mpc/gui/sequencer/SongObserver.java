package com.mpc.gui.sequencer;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.MpcTextField;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.Song;

public class SongObserver implements Observer {

	private Song song;
	private Sequencer sequencer;

	private JTextField songField;

	private JTextField now0Field;
	private JTextField now1Field;
	private JTextField now2Field;

	private JTextField tempoSourceField;
	private JTextField tempoField;
	private JTextField loopField;

	private JTextField step0Field;
	private JTextField step1Field;
	private JTextField step2Field;

	private JTextField sequence0Field;
	private JTextField sequence1Field;
	private JTextField sequence2Field;

	private JTextField reps0Field;
	private JTextField reps1Field;
	private JTextField reps2Field;

	private SongGui songGui;

	public SongObserver(Mpc mpc, MainFrame mainFrame) {

		sequencer = mpc.getSequencer();
		sequencer.deleteObservers();
		sequencer.addObserver(this);

		songGui = Bootstrap.getGui().getSongGui();
		songGui.deleteObservers();
		songGui.addObserver(this);

		song = mpc.getSequencer().getSong(songGui.getSelectedSongIndex());
		song.deleteObservers();
		song.addObserver(this);

		songField = mainFrame.lookupTextField("song");
		now0Field = mainFrame.lookupTextField("now0");
		now1Field = mainFrame.lookupTextField("now1");
		now2Field = mainFrame.lookupTextField("now2");

		tempoSourceField = mainFrame.lookupTextField("temposource");
		tempoField = mainFrame.lookupTextField("tempo");
		loopField = mainFrame.lookupTextField("loop");

		step0Field = mainFrame.lookupTextField("step0");
		step1Field = mainFrame.lookupTextField("step1");
		step2Field = mainFrame.lookupTextField("step2");

		sequence0Field = mainFrame.lookupTextField("sequence0");
		sequence1Field = mainFrame.lookupTextField("sequence1");
		sequence2Field = mainFrame.lookupTextField("sequence2");

		reps0Field = mainFrame.lookupTextField("reps0");
		reps1Field = mainFrame.lookupTextField("reps1");
		reps2Field = mainFrame.lookupTextField("reps2");

		step0Field.setFocusable(false);
		step0Field.setOpaque(false);
		sequence0Field.setFocusable(false);
		sequence0Field.setOpaque(false);
		reps0Field.setFocusable(false);
		reps0Field.setOpaque(false);
		step2Field.setFocusable(false);
		step2Field.setOpaque(false);
		sequence2Field.setFocusable(false);
		sequence2Field.setOpaque(false);
		reps2Field.setFocusable(false);
		reps2Field.setOpaque(false);

		displaySongName();
		displayNow();
		displayTempoSource();
		displayTempo();
		displayLoop();
		displaySteps();

	}

	private void displayTempo() {
		tempoField.setText(Bootstrap.getGui().getMpc().getSequencer().getTempo().toString().replace(".", "\u00CB"));
	}

	private void displayLoop() {
		loopField.setText(songGui.isLoopEnabled() ? "YES" : "NO");
	}

	private void displaySteps() {
		int offset = songGui.getOffset();
		int steps = song.getStepAmount();
		Mpc mpc = Bootstrap.getGui().getMpc();
		Sequencer s = mpc.getSequencer();
		JTextField[] stepArray = { step0Field, step1Field, step2Field };
		JTextField[] sequenceArray = { sequence0Field, sequence1Field, sequence2Field };
		JTextField[] repsArray = { reps0Field, reps1Field, reps2Field };
		for (int i = 0; i < 3; i++) {
			int stepnr = i + offset;
			if (stepnr >= 0 && stepnr < steps) {
				stepArray[i].setText(Util.padLeftSpace("" + (stepnr + 1), 3));
				String seqname = s.getSequence(song.getStep(stepnr).getSequence()).getName();
				sequenceArray[i].setText(Util.padLeft2Zeroes(song.getStep(stepnr).getSequence() + 1) + "-" + seqname);
				repsArray[i].setText("" + song.getStep(stepnr).getRepeats());
			} else {
				stepArray[i].setText("");
				sequenceArray[i].setText((stepnr == steps ? "   (end of song)" : ""));
				repsArray[i].setText("");
			}
		}
	}

	private void displayTempoSource() {
		tempoSourceField.setText(Bootstrap.getGui().getMpc().getSequencer().isTempoSourceSequence() ? "SEQ" : "MAS");
	}

	private void displayNow() {
		now0Field.setText(String.format("%03d", Bootstrap.getGui().getMpc().getSequencer().getCurrentBarNumber() + 1));
		now1Field.setText(String.format("%02d", Bootstrap.getGui().getMpc().getSequencer().getCurrentBeatNumber() + 1));
		now2Field.setText(String.format("%02d", Bootstrap.getGui().getMpc().getSequencer().getCurrentClockNumber()));
	}

	private void displaySongName() {
		songField.setText(Util.padLeft2Zeroes(songGui.getSelectedSongIndex() + 1) + "-" + song.getName());
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		switch ((String) arg1) {

		case "loop":
			displayLoop();
			break;
		
		case "song":
		case "selectedsongindex":
			if (song != null) song.deleteObservers();
			song = Bootstrap.getGui().getMpc().getSequencer().getSong(songGui.getSelectedSongIndex());
			song.deleteObservers();
			song.addObserver(this);
			displaySongName();
			displaySteps();
			displayTempoSource();
			displayTempo();
			break;

		case "used":
		case "songname":
			displaySongName();
			break;

		case "offset":
			displaySteps();
			displayTempo();
			break;

		case "temposource":
			displayTempoSource();
			break;

		case "tempo":
			displayTempo();
			break;

		case "step":
			displaySteps();
			break;

		case "clock":
			displayNow();
			break;
			
		case "play":
			((MpcTextField) sequence1Field).startBlinking();
			((MpcTextField) reps1Field).startBlinking();
			break;
			
		case "stop":
			((MpcTextField) sequence1Field).stopBlinking();
			((MpcTextField) reps1Field).stopBlinking();
			break;
		}
	}
}
