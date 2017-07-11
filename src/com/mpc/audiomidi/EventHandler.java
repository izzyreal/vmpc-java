package com.mpc.audiomidi;

import java.util.Observable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.midisync.MidiSyncGui;
import com.mpc.gui.vmpc.MidiGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.Event;
import com.mpc.sequencer.MidiAdapter;
import com.mpc.sequencer.MidiClockEvent;
import com.mpc.sequencer.MixerEvent;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.Sequencer;
import com.mpc.sequencer.TempoChangeEvent;

import uk.org.toot.midi.core.MidiInput;

public class EventHandler extends Observable {

	private Sequencer sequencer;
	private Sampler sampler;
	private MpcMidiPorts mpcMidiPorts;
	private MidiSyncGui msGui;
	private MidiGui midiGui;

	private Mpc mpc;
	private MidiAdapter midiAdapter;

	public EventHandler(Mpc mpc) {
		sequencer = mpc.getSequencer();
		sampler = mpc.getSampler();
		this.mpc = mpc;
		midiAdapter = new MidiAdapter();		
	}

	public void handle(Event event, MpcTrack track) {
		if (mpcMidiPorts == null) {
			mpcMidiPorts = mpc.getMidiPorts();
		}
		if (!track.isOn() && event.getTick() != -1) return;
		handleNoThru(event, track);
		midiOut(event, track);
	}

	public void handleNoThru(Event event, MpcTrack track) {
		if (msGui == null) {
			msGui = Bootstrap.getGui().getMidiSyncGui();
			midiGui = Bootstrap.getGui().getMidiGui();
		}

		if (track.getName().equals("click")) {
			if (!sequencer.isCountEnabled()) return;
			if (sequencer.isRecordingOrOverdubbing() && !Bootstrap.getGui().getSequencerWindowGui().getInRec()
					&& !sequencer.isCountingIn())
				return;

			if (sequencer.isPlaying() && !sequencer.isRecordingOrOverdubbing()
					&& !Bootstrap.getGui().getSequencerWindowGui().getInPlay() && !sequencer.isCountingIn())
				return;

			int eventFrame = mpc.getAudioMidiServices().getFrameSequencer().getEventFrameOffset(event.getTick());
			sampler.playMetronome((NoteEvent) event, eventFrame);
			return;
		} else {
			if (sequencer.isCountingIn() && event.getTick() != -1) return;
		}

		if (event instanceof TempoChangeEvent) {
			TempoChangeEvent tce = (TempoChangeEvent) event;
			sequencer.setPlayTempo(tce.getTempo().floatValue());
			return;
		}

		if (event instanceof MidiClockEvent) {
			ShortMessage clockMsg = ((MidiClockEvent)event).getShortMessage();
			try {
				clockMsg.setMessage(((MidiClockEvent) event).getStatus());
				switch (Bootstrap.getGui().getMidiSyncGui().getOut()) {

				case 0:
					mpcMidiPorts.transmitA(clockMsg);
					break;
				case 1:
					mpcMidiPorts.transmitB(clockMsg);
					break;
				case 2:
					mpcMidiPorts.transmitA(clockMsg);
					mpcMidiPorts.transmitB(clockMsg);
					break;

				}
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

		}

		if (event instanceof NoteEvent) {
			NoteEvent ne = (NoteEvent) event;
			int busNumber = track.getBusNumber();
			if (busNumber != 0) { // Event's destination is a DRUM bus.
				int drum = busNumber - 1;

				if (ne.getDuration() != -1) {

					if (!(sequencer.isSoloEnabled() && track.getTrackIndex() != sequencer.getActiveTrackIndex())) {
						int newVelo = (int) (ne.getVelocity() * (track.getVelocityRatio() / 100.0));
						midiAdapter.process(ne, drum, newVelo);
						int eventFrame = mpc.getAudioMidiServices().getFrameSequencer()
								.getEventFrameOffset(event.getTick());
						mpc.getMms().mpcTransport(track.getTrackIndex(), midiAdapter.get(), 0, ne.getVariationTypeNumber(),
								ne.getVariationValue(), eventFrame);
					}
				}
			}
		}

		if (event instanceof MixerEvent) {
			MixerEvent me = (MixerEvent) event;
			int pad = me.getPad();
			Program p = sampler.getProgram(sampler.getDrumBusProgramNumber(track.getBusNumber()));
			if (me.getParameter() == 0) {
				p.getPadMixer(pad).setLevel(me.getValue());
			} else if (me.getParameter() == 1) {
				p.getPadMixer(pad).setPanning(me.getValue());
			}
		}
	}

	private void midiOut(Event event, MpcTrack track) {

		if (event instanceof NoteEvent) {
			NoteEvent ne = (NoteEvent) event.clone();

			if (Bootstrap.getGui().getTransGui().getTr() == -1
					|| Bootstrap.getGui().getTransGui().getTr() == ne.getTrack()) {
				ne.setNote(ne.getNote() + Bootstrap.getGui().getTransGui().getAmount());
				event = ne;
			}
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
		String notifyLetter = "a";

		if (deviceNumber > 15) {
			deviceNumber -= 16;
			try {
				r = midiGui.getOutBReceiverIndex() == -1 ? null
						: mpcMidiPorts.getReceivers().get(midiGui.getOutBReceiverIndex());
			} catch (Exception e) {
				System.out.println("couldn't send midi");
			}
			notifyLetter = "b";
		}

		if (!(mpc.getAudioMidiServices().isBouncing() && Bootstrap.getGui().getD2DRecorderGui().isOffline())
				&& r != null && track.getDevice() != 0 && msg != null) {
			try {
				// DeviceMidiInput dmi = (DeviceMidiInput) r;
				// long devPos = dmi.getMicroSecondPosition();
				// System.out.println("dev pos " + devPos);
				r.transport(msg, -1);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		if (Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentScreenName().equals("midioutputmonitor")) {

			setChanged();
			notifyObservers(notifyLetter + deviceNumber);

		}
	}
}