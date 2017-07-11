package com.mpc.tootextensions;

import static uk.org.toot.misc.Localisation.getString;
import static uk.org.toot.synth.modules.envelope.EnvelopeControlIds.ATTACK;
import static uk.org.toot.synth.modules.envelope.EnvelopeControlIds.DECAY;
import static uk.org.toot.synth.modules.envelope.EnvelopeControlIds.HOLD;

import uk.org.toot.control.CompoundControl;
import uk.org.toot.control.Control;
import uk.org.toot.control.ControlLaw;
import uk.org.toot.control.FloatControl;
import uk.org.toot.control.LinearLaw;
import uk.org.toot.control.LogLaw;


public class MpcEnvelopeControls extends CompoundControl
{
	private final static int MPC_ENVELOPE_ID = 0x02;

	private FloatControl attackControl;
	private FloatControl holdControl;
	private FloatControl decayControl;
	
	private int sampleRate = 44100;

	private float attack, decay, hold; // 0.. coefficients

	private int idOffset = 0;
	
	public MpcEnvelopeControls(int instanceIndex, String name, int idOffset) {
		this(instanceIndex, name, idOffset, 1f);
	}
		
	public MpcEnvelopeControls(int instanceIndex, String name, int idOffset, float timeMultiplier) {
		this(MPC_ENVELOPE_ID, instanceIndex, name, idOffset, timeMultiplier);
	}
		
	public MpcEnvelopeControls(int id, int instanceIndex, String name, final int idOffset, float timeMultiplier) {
		super(id, instanceIndex, name);
		this.idOffset = idOffset;
		createControls();
		deriveSampleRateIndependentVariables();
		deriveSampleRateDependentVariables();
	}
	
    @Override
    protected void derive(Control c) {
		switch ( c.getId()-idOffset ) {
		case ATTACK: attack = deriveAttack(); break;
		case HOLD: hold = deriveHold(); break;
		case DECAY: decay = deriveDecay(); break;
		}    	
    }
    	
	protected void createControls() {
		add(attackControl = createAttackControl(0f, 3000f * 4.7f, 0f)); 	// ms
		add(holdControl = createHoldControl(0f, Float.MAX_VALUE, 0f));
		add(decayControl = createDecayControl(0f, 2600f * 4.7f, 0f));		// ms
	}

    protected void deriveSampleRateIndependentVariables() {
    	hold = deriveHold();
    }

    protected void deriveSampleRateDependentVariables() {
    	attack = deriveAttack();
    	decay = deriveDecay();
    }
    
	protected float deriveHold() {
		return holdControl.getValue(); // 0..1		
	}

    private static float LOG_0_01 = (float)Math.log(0.01);
    // http://www.physics.uoguelph.ca/tutorials/exp/Q.exp.html
	// http://www.musicdsp.org/showArchiveComment.php?ArchiveID=136
    // return k per sample for 99% in specified milliseconds
    protected float deriveTimeFactor(float milliseconds) {
    	    	
    	float ns = milliseconds * sampleRate / 1000;
        float k = LOG_0_01 / ns ; // k, per sample
        return (float)(1f -Math.exp(k));
    }

    protected float deriveAttack() {
        return deriveTimeFactor(attackControl.getValue());
    }

    protected float  deriveDecay() {
        return deriveTimeFactor(decayControl.getValue());
    }

    protected FloatControl createAttackControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        return new FloatControl(ATTACK+idOffset, getString("Attack"), law, 0.1f, init);
    }

	protected FloatControl createHoldControl(float min, float max, float init) {
        ControlLaw law = new LinearLaw(min, max, "samples");
        return new FloatControl(HOLD+idOffset, getString("Hold"), law, 0.1f, init);
	}

    protected FloatControl createDecayControl(float min, float max, float init) {
        ControlLaw law = new LogLaw(min, max, "ms");
        return new FloatControl(DECAY+idOffset, getString("Decay"), law, 0.1f, init);
    }

	public float getAttackCoeff() {
		return attack;
	}

	public float getHold() {
		return hold;
	}

	public float getDecayCoeff() {
		return decay;
	}
}
