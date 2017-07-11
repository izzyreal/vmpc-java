package com.mpc.audiomidi;

import java.util.Observable;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import com.mpc.Mpc;
import com.mpc.controls.AbstractControls;
import com.mpc.controls.KbMouseController;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.midisync.MidiSyncGui;
import com.mpc.gui.vmpc.MidiGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.EventAdapter;
import com.mpc.sequencer.MidiAdapter;
import com.mpc.sequencer.MidiClockEvent;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;

import uk.org.toot.midi.core.MidiInput;

public class MpcMidiInput extends Observable implements MidiInput {

	protected final int index;
	private Sequencer sequencer;
	private Sampler sampler;
	private MpcMidiPorts mpcMidiPorts;
	private MidiSyncGui msGui;
	private MidiGui midiGui;
	private Mpc mpc;
	private MidiAdapter midiAdapter;
	private EventAdapter eventAdapter;
	private String notify;
	
	public MpcMidiInput(int index, Mpc mpc) {
		this.mpc = mpc;
		sequencer = mpc.getSequencer();
		sampler = mpc.getSampler();
		mpcMidiPorts = mpc.getMidiPorts();
		this.index = index;
		midiAdapter = new MidiAdapter();
		eventAdapter = new EventAdapter();
	}

	@Override
	public String getName() {
		return "mpcmidiin" + index;
	}

	@Override
	public void transport(MidiMessage msg, long timestamp) {
		if (msGui == null) msGui = Bootstrap.getGui().getMidiSyncGui();

		eventAdapter.process(msg, Bootstrap.getGui().getSequencerWindowGui());
		int status = msg.getStatus();
		notify = index == 0 ? "a" : "b";
		int channel = ((ShortMessage) msg).getChannel();
		notify += channel;
		setChanged();
		notifyObservers(notify);
		if (status == ShortMessage.CONTROL_CHANGE) {
			Bootstrap.getGui().getMainFrame().getControlPanel().getSlider().setValue(msg.getMessage()[2]);
		}

		if (status == ShortMessage.POLY_PRESSURE || status == ShortMessage.NOTE_ON || status == ShortMessage.NOTE_OFF) {
			int note = msg.getMessage()[1];
			int velo = msg.getMessage()[2];

			MpcSequence s = sequencer.getActiveSequence();
			int bus = s.getTrack(sequencer.getActiveTrackIndex()).getBusNumber();

			if (bus != 0) {
				int pgm = sampler.getDrumBusProgramNumber(bus);
				Program p = sampler.getProgram(pgm);
				int pad = p.getPadNumberFromNote(note);

				if (pad != -1) {
					switch (status) {
					case ShortMessage.POLY_PRESSURE:
						KbMouseController.pressedPadVelos[pad] = velo;
						break;
					case ShortMessage.NOTE_ON:
						if (velo > 0) {
							KbMouseController.pressedPads.add(pad);
						} else {
							KbMouseController.pressedPads.remove(pad);
						}
						break;
					case ShortMessage.NOTE_OFF:
						KbMouseController.pressedPads.remove(pad);
						break;
					}
				}
			}
		}

		if (eventAdapter.get() == null) return;
		if (eventAdapter.get() instanceof MidiClockEvent && Bootstrap.getGui().getMidiSyncGui().getIn() == index) {

			if (Bootstrap.getGui().getMidiSyncGui().getModeIn() != 0) {
				MidiClockEvent mce = (MidiClockEvent) eventAdapter.get();
				switch (mce.getStatus()) {
				case ShortMessage.START:
					sequencer.playFromStart();
					break;
				case ShortMessage.STOP:
					sequencer.stop();
					break;
				case ShortMessage.TIMING_CLOCK:
					// sequencer.getTootSeq().clock();
					break;
				}
			}
		}

		if (eventAdapter.get() instanceof NoteEvent) {
			NoteEvent n = (NoteEvent) eventAdapter.get();
			n.setTick(-1);

			// n.setVariationTypeNumber(0);
			// n.setVariationValue(64);
			MpcSequence s = sequencer.isPlaying() ? sequencer.getCurrentlyPlayingSequence()
					: sequencer.getActiveSequence();
			MpcTrack track = (MpcTrack) s.getTrack(n.getTrack());
			Program p = sampler.getProgram(sampler.getDrumBusProgramNumber(track.getBusNumber()));
			((KbMouseController) Bootstrap.getGui().getKb()).getControls();
			AbstractControls.setSliderNoteVar(n, p);
			int pad = p.getPadNumberFromNote(n.getNote());
			Bootstrap.getGui().getSamplerGui().setPadAndNote(pad, n.getNote());
			mpc.getEventHandler().handleNoThru(n, track);

			if (sequencer.isRecordingOrOverdubbing()) {
				n.setDuration(n.getVelocity() == 0 ? 0 : -1);
				n.setTick(sequencer.getTickPosition());
				if (n.getVelocity() == 0) {
					track.recordNoteOff(n);
				} else {
					track.recordNoteOn(n);
				}
			}
			switch (Bootstrap.getGui().getSequencerWindowGui().getSoftThru()) {
			case 0:
				return;
			case 1:
				midiOut(eventAdapter.get(), track);
				break;
			case 2:
				transportOmni(msg, "a");
				break;
			case 3:
				transportOmni(msg, "b");
				break;
			case 4:
				transportOmni(msg, "a");
				transportOmni(msg, "b");
				break;
			}
		}
	}

	private void midiOut(Event event, MpcTrack track) {
		if (msGui == null) {
			msGui = Bootstrap.getGui().getMidiSyncGui();
			midiGui = Bootstrap.getGui().getMidiGui();
		}
		MidiMessage msg = event.getShortMessage();
		int deviceNumber = track.getDevice() - 1;
		if (deviceNumber != -1 && deviceNumber < 32) {
			int channel = deviceNumber;
			if (channel > 15) channel -= 16;
			midiAdapter.process(event, channel, -1);
			msg = midiAdapter.get();
		}

		MidiInput r = null;
		try {
			r = (midiGui.getOutAReceiverIndex() == -1 || mpcMidiPorts.getReceivers().size() == 0) ? null
					: mpcMidiPorts.getReceivers().get(midiGui.getOutAReceiverIndex());

		} catch (Exception e) {
			System.out.println("couldn't send midi");
		}
		
		notify = "a";

		if (deviceNumber > 15) {
			deviceNumber -= 16;
			try {
				r = midiGui.getOutBReceiverIndex() == -1 ? null
						: mpcMidiPorts.getReceivers().get(midiGui.getOutBReceiverIndex());
			} catch (Exception e) {
				System.out.println("couldn't send midi");
			}
			notify = "b";
		}

		if (r != null && track.getDevice() != 0) {
			try {
				r.transport(msg, -1);
			} catch (IllegalStateException e) {
				// Nothing to do.
			}
		}
		if (Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentScreenName().equals("midioutputmonitor")) {

			setChanged();
			notifyObservers(notify+ deviceNumber);

		}
	}

	private void transportOmni(MidiMessage msg, String outputLetter) {
		if (msGui == null) msGui = Bootstrap.getGui().getMidiSyncGui();
		MidiInput r = mpcMidiPorts.getReceivers()
				.get(outputLetter.equals("a") ? midiGui.getOutAReceiverIndex() : midiGui.getOutBReceiverIndex());
		r.transport(msg, 0);
		if (msg instanceof ShortMessage) {
			if (Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentScreenName()
					.equals("midioutputmonitor")) {
				setChanged();
				notifyObservers(outputLetter + ((ShortMessage) msg).getChannel());
			}
		}
	}

}
