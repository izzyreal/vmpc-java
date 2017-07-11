package com.mpc.tootextensions;


/**
 * An AHD Envelope Generator, modeled after an MPC's,
 * including its linear equations.
 * 
 */

public class MpcEnvelopeGenerator {
	public enum State {
		ATTACK, HOLD, DECAY, COMPLETE
	};

	private State state = State.ATTACK;

	private float envelope = 0f;
	private int holdCounter = 0;
	private MpcEnvelopeControls vars;
	
	public MpcEnvelopeGenerator(MpcEnvelopeControls vars) {
		this.vars = vars;
	}

	public float getEnvelope(boolean decay) {
		
		if (decay && state != State.COMPLETE)
			state = State.DECAY; // !!!
		
		if (state == State.HOLD && holdCounter >= vars.getHold())
			state = State.DECAY;
		
		switch (state) {
		case ATTACK:
			envelope += vars.getAttackCoeff(); 	// * (1f - envelope)
			if (envelope > 0.99f) {
				state = State.HOLD;
			}
			break;
		case HOLD:
			holdCounter++;
			break;
		case DECAY:
			envelope -= vars.getDecayCoeff();		// * envelope
			if (envelope < 0.001f) { // -60dB cutoff !!!
				envelope = 0f;
				state = State.COMPLETE;
			}
			break;
		case COMPLETE:
			break;
		}
		return envelope;
	}

	public boolean isComplete() {
		return state == State.COMPLETE;
	}
	
	public void reset() {
		state = State.ATTACK;
		holdCounter = 0;
		envelope = 0f;
	}
}