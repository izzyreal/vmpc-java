package com.mpc.controls.vmpc;

public class MidiControls extends AbstractVmpcControls {

	public void function(int i) {
		init();
		switch(i) {
		case 0:
			openMain("audio");
			break;
		case 2:
			openMain("disk");
			break;
		}
	}
	
	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);

		if (param.equals("in")) midiGui.setIn(midiGui.getIn() + notch);

		if (param.equals("dev0")) {

			if (midiGui.getIn() == 0) {

				int prevTransmitterNumber = midiGui.getIn1TransmitterNumber();
				midiGui.setIn1TransmitterNumber(midiGui.getIn1TransmitterNumber() + notch,
						mpcMidiPorts.getTransmitters().size() - 1);

				if (midiGui.getIn1TransmitterNumber() != prevTransmitterNumber) {
					mpcMidiPorts.setMidiIn1(midiGui.getIn1TransmitterNumber());
				}
			}

			if (midiGui.getIn() == 1) {

				int prevTransmitterNumber = midiGui.getIn2TransmitterNumber();

				midiGui.setIn2TransmitterNumber(midiGui.getIn2TransmitterNumber() + notch,
						mpcMidiPorts.getTransmitters().size() - 1);

				if (midiGui.getIn2TransmitterNumber() != prevTransmitterNumber) {
					mpcMidiPorts.setMidiIn2(midiGui.getIn2TransmitterNumber());
				}
			}
		}

		if (param.equals("out")) midiGui.setOut(midiGui.getOut() + notch);

		if (param.equals("dev1")) {

			if (midiGui.getOut() == 0) {

				int prevReceiverIndex = midiGui.getOutAReceiverIndex();
				System.out.println("prev receiver " + prevReceiverIndex);
				
				midiGui.setOutAReceiverNumber(midiGui.getOutAReceiverIndex() + notch,
						mpcMidiPorts.getReceivers().size() - 1);
				System.out.println("new receiver " + midiGui.getOutAReceiverIndex());
				if (prevReceiverIndex != midiGui.getOutAReceiverIndex()) {
					mpcMidiPorts.setMidiOutA(midiGui.getOutAReceiverIndex());
				}
			}

			if (midiGui.getOut() == 1) {

				int prevReceiverNumber = midiGui.getOutBReceiverIndex();

				midiGui.setOutBReceiverNumber(midiGui.getOutBReceiverIndex() + notch,
						mpcMidiPorts.getReceivers().size() - 1);

				if (prevReceiverNumber != midiGui.getOutBReceiverIndex()) {
					mpcMidiPorts.setMidiOutB(midiGui.getOutBReceiverIndex());
				}
			}
		}
	}
}