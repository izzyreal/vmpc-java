package com.mpc.gui.sequencer;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.sequencer.Sequencer;

public class NextSeqPadObserver implements Observer {

	private Sequencer sequencer;
	private SamplerGui samplerGui;
	private JTextField sqField;
	private JTextField now0Field;
	private JTextField now1Field;
	private JTextField now2Field;

	private JLabel[] seqs = new JLabel[16];

	private JLabel seq1;
	private JLabel seq2;
	private JLabel seq3;
	private JLabel seq4;
	private JLabel seq5;
	private JLabel seq6;
	private JLabel seq7;
	private JLabel seq8;
	private JLabel seq9;
	private JLabel seq10;
	private JLabel seq11;
	private JLabel seq12;
	private JLabel seq13;
	private JLabel seq14;
	private JLabel seq15;
	private JLabel seq16;

	private JLabel seqNumbers;
	private JLabel bank;
	private JLabel nextSqLabel;

	public NextSeqPadObserver(Sequencer sequencer, MainFrame mainFrame) throws UnsupportedEncodingException {

		this.sequencer = sequencer;
		sequencer.deleteObservers();
		sequencer.addObserver(this);

		samplerGui = Bootstrap.getGui().getSamplerGui();
		samplerGui.deleteObservers();
		samplerGui.addObserver(this);

		sqField = mainFrame.lookupTextField("sq");
		nextSqLabel = mainFrame.lookupLabel("nextsq");
		now0Field = mainFrame.lookupTextField("now0");
		now1Field = mainFrame.lookupTextField("now1");
		now2Field = mainFrame.lookupTextField("now2");

		seq1 = mainFrame.lookupLabel("1");
		seq2 = mainFrame.lookupLabel("2");
		seq3 = mainFrame.lookupLabel("3");
		seq4 = mainFrame.lookupLabel("4");
		seq5 = mainFrame.lookupLabel("5");
		seq6 = mainFrame.lookupLabel("6");
		seq7 = mainFrame.lookupLabel("7");
		seq8 = mainFrame.lookupLabel("8");
		seq9 = mainFrame.lookupLabel("9");
		seq10 = mainFrame.lookupLabel("10");
		seq11 = mainFrame.lookupLabel("11");
		seq12 = mainFrame.lookupLabel("12");
		seq13 = mainFrame.lookupLabel("13");
		seq14 = mainFrame.lookupLabel("14");
		seq15 = mainFrame.lookupLabel("15");
		seq16 = mainFrame.lookupLabel("16");

		seqNumbers = mainFrame.lookupLabel("seqnumbers");
		bank = mainFrame.lookupLabel("bank");

		seqs[0] = seq1;
		seqs[1] = seq2;
		seqs[2] = seq3;
		seqs[3] = seq4;
		seqs[4] = seq5;
		seqs[5] = seq6;
		seqs[6] = seq7;
		seqs[7] = seq8;
		seqs[8] = seq9;
		seqs[9] = seq10;
		seqs[10] = seq11;
		seqs[11] = seq12;
		seqs[12] = seq13;
		seqs[13] = seq14;
		seqs[14] = seq15;
		seqs[15] = seq16;

		sequencer.deleteObservers();
		sequencer.addObserver(this);

		for (int i = 0; i < 16; i++) {
			displaySeq(i);
			setOpaque(i);
			setSeqColor(i);
		}

		displaySq();
		displayNow0();
		displayNow1();
		displayNow2();
		displayBank();
		displaySeqNumbers();
		displayNextSq();
	}

	private void displayNextSq() {
		nextSqLabel.setText(sequencer.getNextSq() == -1 ? ""
				: String.format("%02d-", sequencer.getNextSq()+1)
						+ sequencer.getSequence(sequencer.getNextSq()).getName());
	}

	private int bankoffset() {
		return samplerGui.getBank() * 16;
	}

	private void displayBank() {
		String[] letters = { "A", "B", "C", "D" };
		bank.setText(letters[samplerGui.getBank()]);
	}

	private void displaySeqNumbers() {
		String[] seqn = { "01-16", "17-32", "33-48", "49-64" };
		seqNumbers.setText(seqn[samplerGui.getBank()]);
	}

	private void displaySq() {
		sqField.setText(String.format("%02d-", (sequencer.getActiveSequenceIndex() + 1))
				+ sequencer.getActiveSequence().getName());
	}

	private void setOpaque(int i) {
		seqs[i].setOpaque(true);
	}

	private void displaySeq(int i) {
		seqs[i].setText(sequencer.getSequence(i + bankoffset()).getName().substring(0, 8));
	}

	private void setSeqColor(int i) {
		{
			if (i + bankoffset() == sequencer.getNextSq()) {
				seqs[i].setForeground(Bootstrap.lcdOff);
				seqs[i].setBackground(Bootstrap.lcdOn);
			} else {
				seqs[i].setForeground(Bootstrap.lcdOn);
				seqs[i].setBackground(Bootstrap.lcdOff);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "soloenabled":

		case "bank":
			displayBank();
			displaySeqNumbers();
			refreshSeqs();
			break;

		case "seqnumbername":
			displaySq();
			refreshSeqs();
			break;

		case "nextsqoff":
			nextSqLabel.setText("");
		case "nextsqvalue":
		case "nextsq":
			refreshSeqs();
			displayNextSq();
			break;

		case "clock":
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayNow0();
					displayNow1();
					displayNow2();
				}
			});
			break;

		}
	}

	private void displayNow0() {
		now0Field.setText(String.format("%03d", sequencer.getCurrentBarNumber() + 1));
	}

	private void displayNow1() {
		now1Field.setText(String.format("%02d", sequencer.getCurrentBeatNumber() + 1));
	}

	private void displayNow2() {
		now2Field.setText(String.format("%02d", sequencer.getCurrentClockNumber()));
	}

	private void refreshSeqs() {
		for (int i = 0; i < 16; i++) {
			displaySeq(i);
			setSeqColor(i);
		}
	}
}