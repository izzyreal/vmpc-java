package com.mpc.audiomidi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.ShortMessage;

import com.mpc.Mpc;

import uk.org.toot.midi.core.ConnectedMidiSystem;
import uk.org.toot.midi.core.MidiInput;
import uk.org.toot.midi.core.MidiOutput;

public class MpcMidiPorts {

	/*
	 * midiIn1, midiIn2, midiOutA and midiOutB represent the physical MIDI ports
	 * of an MPC. Internally, however, what is a physical MIDI input should be
	 * modeled as a transmitter aka output, and vice versa for physical MIDI
	 * outputs.
	 */

	private List<MidiInput> midiInputs = new ArrayList<MidiInput>();
	private List<MidiOutput> midiOutputs = new ArrayList<MidiOutput>();

	private MidiOutput midiIn1, midiIn2;
	private MidiInput midiOutA, midiOutB;

	private MidiInput mmsInput;
	private Mpc mpc;

	public MpcMidiPorts(ConnectedMidiSystem cms, Mpc mpc) {

		this.mpc = mpc;

		for (MidiInput m : cms.getMidiInputs()) {
			if (!m.getName().contains("Gervill") && !m.getName().contains("MpcMultiSynth"))
				midiInputs.add(m);

			if (m.getName().equals("MpcMultiSynth"))
				mmsInput = m;
		}

		for (MidiOutput m : cms.getMidiOutputs())
			midiOutputs.add(m);
	}

	public List<MidiOutput> getTransmitters() {
		return midiOutputs;
	}

	public List<MidiInput> getReceivers() {
		return midiInputs;
	}

	public void setMidiIn1(int i) {
		if (i > midiOutputs.size() - 1)
			return;
		if (midiIn1 != null) {
			midiIn1.removeConnectionTo(mmsInput);
		}

		if (i < 0) {
			midiIn1 = null;
			return;
		}

		midiIn1 = midiOutputs.get(i);
		midiIn1.addConnectionTo(mpc.getMpcMidiInput(0));
	}

	public void setMidiIn2(int i) {
		if (i > midiOutputs.size() - 1)
			return;
		if (midiIn2 != null)
			midiIn2.removeConnectionTo(mmsInput);

		if (i < 0) {
			midiIn2 = null;
			return;
		}

		midiIn2 = midiOutputs.get(i);
		midiIn2.addConnectionTo(mpc.getMpcMidiInput(1));
	}

	public void setMidiOutA(int i) {
		if (i > midiInputs.size() - 1)
			return;
		if (i < 0) {
			midiOutA = null;
			return;
		}
		midiOutA = midiInputs.get(i);
	}

	public void setMidiOutB(int i) {
		if (i > midiInputs.size() - 1)
			return;
		if (i < 0) {
			midiOutB = null;
			return;
		}
		midiOutB = midiInputs.get(i);
	}

	public String getCurrentMidiIn1DeviceName() {
		if (midiIn1 == null)
			return "NOT CONNECTED";
		return midiIn1.getName();
	}

	public String getCurrentMidiIn2DeviceName() {
		if (midiIn2 == null)
			return "NOT CONNECTED";
		return midiIn2.getName();
	}

	public String getCurrentMidiOutADeviceName() {
		if (midiOutA == null)
			return "NOT CONNECTED";
		return midiOutA.getName();
	}

	public String getCurrentMidiOutBDeviceName() {
		if (midiOutB == null)
			return "NOT CONNECTED";
		return midiOutB.getName();
	}

	public void close() {
	}

	public void transmitA(ShortMessage msg) {
		if (midiOutA == null) return;
		midiOutA.transport(msg, 0);
	}
	
	public void transmitB(ShortMessage msg) {
		if (midiOutA == null) return;
		midiOutB.transport(msg, 0);
	}
	
}
