package com.mpc.audiomidi;

import uk.org.toot.audio.system.AudioInput;
import uk.org.toot.audio.system.AudioOutput;

public class MpcAudioPorts {

	final static int ANALOG_OUTPUT_AMOUNT = 10;
	final static int ANALOG_INPUT_AMOUNT = 2;

	private AudioOutput[] outputs;
	private AudioInput[] inputs;

	public MpcAudioPorts() {
		outputs = new AudioOutput[ANALOG_OUTPUT_AMOUNT];

		for (int i = 0; i < ANALOG_OUTPUT_AMOUNT; i++)
			outputs[i] = new MpcAudioOutput();

		inputs = new AudioInput[ANALOG_INPUT_AMOUNT];
		
		for (int i=0; i< ANALOG_INPUT_AMOUNT;i++)
			inputs[i] = new MpcAudioInput();
	}
	
	/*
	 * Valid output names are: L, R, LR, 1, 2, 12, ..., 7, 8, 78 
	 */
	
	public AudioOutput getOutput(int i) {
		return outputs[i];
	}
	
	public AudioInput getInput(int i) {
		return inputs[i];
	}
}
