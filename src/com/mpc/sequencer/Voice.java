package com.mpc.sequencer;

import com.mpc.tootextensions.MpcEnvelopeControls;
import com.mpc.tootextensions.MpcEnvelopeGenerator;
import com.mpc.tootextensions.MpcNoteParameters;
import com.mpc.tootextensions.MpcSoundOscillatorVariables;
import com.mpc.tootextensions.MpcSoundPlayerChannel;
import com.mpc.tootextensions.MpcVoice;
import com.mpc.tootextensions.MuteInfo;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.control.BooleanControl;
import uk.org.toot.control.FloatControl;
import uk.org.toot.synth.modules.filter.StateVariableFilter;
import uk.org.toot.synth.modules.filter.StateVariableFilterControls;

public class Voice implements MpcVoice {

	private static final float TIME_RATIO = 5.46f;

	private static final float STATIC_ATTACK_LENGTH = 2 * TIME_RATIO;
	private static final float STATIC_DECAY_LENGTH = 20 * TIME_RATIO;

	private static final float MAX_ATTACK_LENGTH_MS = 3000.0f;
	private static final float MAX_DECAY_LENGTH_MS = 2600.0f;

	private static final float MAX_ATTACK_LENGTH_SAMPLES = (float) (MAX_ATTACK_LENGTH_MS * 44.1);
	private static final float MAX_DECAY_LENGTH_SAMPLES = (float) (MAX_DECAY_LENGTH_MS * 44.1);

	private static final int ATTACK_INDEX = 0;
	private static final int HOLD_INDEX = 1;
	private static final int DECAY_INDEX = 2;

	private static final int RESO_INDEX = 1;
	private static final int MIX_INDEX = 2;
	private static final int BANDPASS_INDEX = 3;

	private MpcSoundOscillatorVariables oscVars;
	private int tune;
	private float increment; // frame increment per sample

	private float position; // current position in frames

	private float[] sampleDataLeft;
	private float[] sampleDataRight;

	private int padNumber = -1;

	private MpcEnvelopeGenerator staticEnv;
	private MpcEnvelopeGenerator ampEnv;
	private MpcEnvelopeGenerator filterEnv;

	private float initialFilterValue;

	private boolean staticDecay;

	private MpcNoteParameters np;

	private float amplitude;

	private StateVariableFilter svf0;

	private int start;
	private int end;

	private FloatControl attack;
	private FloatControl hold;
	private FloatControl decay;

	private FloatControl fattack;
	private FloatControl fhold;
	private FloatControl fdecay;

	final private FloatControl sattack;
	final private FloatControl shold;
	final private FloatControl sdecay;

	private FloatControl reso;
	private FloatControl mix;
	private BooleanControl bandpass;

	private MpcEnvelopeControls ampEnvControls;
	private MpcEnvelopeControls staticEnvControls;
	private MpcEnvelopeControls filterEnvControls;
	private StateVariableFilterControls svfControls;

	private boolean finished = true;
	private boolean readyToPlay = false;

	private final int stripNumber;
	public float inverseNyquist = 2f / 44100;

	private StateVariableFilter svf1;

	private MpcSoundPlayerChannel parent;

	private MuteInfo muteInfo;

	private int track;

	private int frameOffset = 0;

	private boolean basic;

	private int decayCounter;

	private boolean enableEnvs;

	private final static int SVF_OFFSET = 0x30;
	private final static int AMPENV_OFFSET = 0x40;

	private final float[] EMPTY_FRAME = { 0f, 0f };
	private final float[] TEMP_FRAME = { 0f, 0f };

	private float veloFactor;

	private int veloToStart;

	private int attackValue;

	private int decayValue;

	private int veloToAttack;

	private int decayMode;

	private int veloToLevel;

	private int playableSampleLength;

	private float attackMs;

	private int finalDecayValue;

	private float decayMs;

	private int attackLengthSamples;

	private int decayLengthSamples;

	private int holdLengthSamples;

	private int staticEnvHoldSamples;

	private float veloToLevelFactor;

	private int filtParam;

	private float envAmplitude;

	private float staticEnvAmp;

	private float sample;

	private float frac;

	private int k;

	private int j;

	private int[] leftPairs;

	private int[] rightPairs;

	private float[] left;

	private float[] right;

	private int count;

	private float[] frame;
	
	public Voice(int stripNumber, boolean basic) {

		muteInfo = new MuteInfo();
		this.stripNumber = stripNumber;
		this.basic = basic;

		staticEnvControls = new MpcEnvelopeControls(0, "StaticAmpEnv", AMPENV_OFFSET);

		staticEnv = new MpcEnvelopeGenerator(staticEnvControls);

		sattack = (FloatControl) staticEnvControls.getControls().get(ATTACK_INDEX);
		shold = (FloatControl) staticEnvControls.getControls().get(HOLD_INDEX);
		sdecay = (FloatControl) staticEnvControls.getControls().get(DECAY_INDEX);

		if (!basic) {

			ampEnvControls = new MpcEnvelopeControls(0, "AmpEnv", AMPENV_OFFSET);
			filterEnvControls = new MpcEnvelopeControls(0, "StaticAmpEnv", AMPENV_OFFSET);

			ampEnv = new MpcEnvelopeGenerator(ampEnvControls);
			filterEnv = new MpcEnvelopeGenerator(filterEnvControls);


			svfControls = new StateVariableFilterControls(0, "Filter", SVF_OFFSET);

			svf0 = new StateVariableFilter(svfControls);
			svf1 = new StateVariableFilter(svfControls);

			fattack = (FloatControl) filterEnvControls.getControls().get(ATTACK_INDEX);
			fhold = (FloatControl) filterEnvControls.getControls().get(HOLD_INDEX);
			fdecay = (FloatControl) filterEnvControls.getControls().get(DECAY_INDEX);

			attack = (FloatControl) ampEnvControls.getControls().get(ATTACK_INDEX);
			hold = (FloatControl) ampEnvControls.getControls().get(HOLD_INDEX);
			decay = (FloatControl) ampEnvControls.getControls().get(DECAY_INDEX);

			reso = (FloatControl) svfControls.getControls().get(RESO_INDEX); // 0..1
			mix = (FloatControl) svfControls.getControls().get(MIX_INDEX); // 0..1
			bandpass = (BooleanControl) svfControls.getControls().get(BANDPASS_INDEX);

		}
	}

	public void init(int track, int velocity, int padNumber, MpcSoundOscillatorVariables oscVars, MpcNoteParameters np,
			int varType, int varValue, int muteNote, int muteDrum, int frameOffset, boolean enableEnvs) {

		this.enableEnvs = enableEnvs;
		this.frameOffset = frameOffset;
		this.track = track;
		finished = false;
		readyToPlay = false;
		muteInfo.setNote(muteNote);
		muteInfo.setDrum(muteDrum);
		this.np = np;
		this.padNumber = padNumber;
		this.oscVars = oscVars;
		staticDecay = false;

		veloFactor = (float) (velocity / 127.0);
		veloToStart = 0;
		attackValue = 0;
		decayValue = 2;
		veloToAttack = 0;
		decayMode = 0;
		veloToLevel = 100;
		tune = oscVars.getTune();

		if (np != null) {
			tune += np.getTune();
			veloToStart = np.getVelocityToStart();
			attackValue = np.getAttack();
			decayValue = np.getDecay();
			veloToAttack = np.getVelocityToAttack();
			decayMode = np.getDecayMode();
			veloToLevel = np.getVeloToLevel();
		}

		switch (varType) {
		case 0:
			tune += (varValue - 64) * 2;
			break;
		case 1:
			decayValue = varValue;
			decayMode = 1;
			break;
		case 2:
			attackValue = varValue;
			break;
		}

		increment = (float) Math.pow(2.0, ((double) tune) / 120.0);

		start = (int) (oscVars.getStart() + (veloFactor * (veloToStart / 100.0) * oscVars.getLastFrameIndex()));

		end = oscVars.getEnd();
		position = start;

		if (!oscVars.isMono()) {
			sampleDataLeft = oscVars.getSampleDataLeft();
			sampleDataRight = oscVars.getSampleDataRight();
		}

		playableSampleLength = oscVars.isLoopEnabled() ? Integer.MAX_VALUE
				: (int) ((end - start) / increment);

		attackMs = (float) ((attackValue / 100.0) * MAX_ATTACK_LENGTH_MS);
		attackMs += (float) ((veloToAttack / 100.0) * MAX_ATTACK_LENGTH_MS * veloFactor);

		finalDecayValue = decayValue < 2 ? 2 : decayValue;

		decayMs = (float) ((finalDecayValue / 100.0) * MAX_DECAY_LENGTH_MS);

		attackLengthSamples = (int) (attackMs * 44.1);
		decayLengthSamples = (int) (decayMs * 44.1);

		if (attackLengthSamples > MAX_ATTACK_LENGTH_SAMPLES) attackLengthSamples = (int) MAX_ATTACK_LENGTH_SAMPLES;
		if (decayLengthSamples > MAX_DECAY_LENGTH_SAMPLES) decayLengthSamples = (int) MAX_DECAY_LENGTH_SAMPLES;

		holdLengthSamples = playableSampleLength - attackLengthSamples - decayLengthSamples;
		staticEnvHoldSamples = (int) (playableSampleLength
				- (((STATIC_ATTACK_LENGTH + STATIC_DECAY_LENGTH) / TIME_RATIO) * 44.1));

		staticEnv.reset();
		sattack.setValue(STATIC_ATTACK_LENGTH); // Reuse env-control for static
		shold.setValue(staticEnvHoldSamples);
		sdecay.setValue(STATIC_DECAY_LENGTH);

		veloToLevelFactor = (float) (veloToLevel / 100.0);

		amplitude = (float) ((veloFactor * veloToLevelFactor) + 1.0f - veloToLevelFactor);
		amplitude *= (oscVars.getSndLevel() / 100.0);

		if (!basic) {
			
			ampEnv.reset();
			
			attack.setValue(decayMode == 1 ? 0 : attackMs * TIME_RATIO);
			hold.setValue(decayMode == 1 ? 0 : holdLengthSamples);
			decay.setValue(decayMs * TIME_RATIO);

			filtParam = np.getFilterFrequency();
			if (varType == 3) filtParam = varValue;
			initialFilterValue = (float) ((filtParam + (veloFactor * np.getVelocityToFilterFrequency())));
			initialFilterValue = (float) (17.0 + (initialFilterValue * 0.75));

			filterEnv.reset();
			
			fattack.setValue((float) ((np.getFilterAttack() / 500.0) * MAX_ATTACK_LENGTH_SAMPLES));
			fhold.setValue(0);
			fdecay.setValue((float) ((np.getFilterDecay() / 500.0) * MAX_DECAY_LENGTH_SAMPLES));

			reso.setValue((float) ((1.0 / 16.0) + (np.getFilterResonance() / 26.0)));
			mix.setValue(0f);
			bandpass.setValue(false);

			svf0.update();
			svf1.update();
		}

		decayCounter = 0;
		readyToPlay = true;
	}

	float[] getFrame() {
		if (!readyToPlay || finished) return EMPTY_FRAME;

		if (frameOffset > 0) {
			frameOffset--;
			return EMPTY_FRAME;
		}

		// Never make decay happen before the generator would.
		
		envAmplitude = basic ? 1f : ampEnv.getEnvelope(false);

		// Make decay happen if voice is prematurely ended.
		staticEnvAmp = enableEnvs ? staticEnv.getEnvelope(staticDecay) : 1f;
		envAmplitude *= staticEnvAmp;

		float filterEnvFactor = 0;

		float filterFreq = 0;

		if (!basic) {
			filterFreq = MpcSoundPlayerChannel.midiFreq((initialFilterValue * 1.44f)) * inverseNyquist;
			filterEnvFactor = (float) ((filterEnv.getEnvelope(false) * (np.getFilterEnvelopeAmount() / 100.0)));
			filterFreq += MpcSoundPlayerChannel.midiFreq(144) * inverseNyquist * filterEnvFactor;
		}

		if (oscVars.isMono()) {
			TEMP_FRAME[0] = getSample(oscVars.getSampleData(), true) * envAmplitude * amplitude;
			if (!basic) TEMP_FRAME[0] = svf0.filter(TEMP_FRAME[0], filterFreq);
			TEMP_FRAME[1] = TEMP_FRAME[0];
		} else {

			TEMP_FRAME[0] = getSample(sampleDataLeft, false) * envAmplitude * amplitude;
			TEMP_FRAME[1] = getSample(sampleDataRight, true) * envAmplitude * amplitude;

			if (!basic) {
				TEMP_FRAME[0] = svf0.filter(TEMP_FRAME[0], filterFreq);
				TEMP_FRAME[1] = svf1.filter(TEMP_FRAME[1], filterFreq);
			}
		}
		return TEMP_FRAME;
	}

	private float getSample(float[] fa, boolean advance) {
		if (oscVars.isLoopEnabled() && position > end - 1) position = start;

		if (position > end - 1 || (staticEnv != null && staticEnv.isComplete())
				|| (ampEnv != null && ampEnv.isComplete())) {
			finished = true;
			return 0f;
		}

		sample = 0;
	
		k = (int) Math.ceil(position); // index of sample after
		j = k - 1; // index of sample before
		if (j == -1) j = 0;

		frac = position - j; // fractional position between

		sample = (fa[j] * (1.0f - frac)) + (fa[k] * frac);

		if (advance) position += increment;
		return sample;
	}

	public int getPadNumber() {
		return padNumber;
	}

	@Override
	public void open() throws Exception {
	}

	@Override
	public int processAudio(AudioBuffer buffer) {
		// buffer.setMetaInfo(new MetaInfo("" + track));
		buffer.makeSilence();

		if (finished) {
			return AUDIO_SILENCE;
		}

		leftPairs = buffer.getChannelFormat().getLeft();
		rightPairs = buffer.getChannelFormat().getRight();
		left = buffer.getChannel(leftPairs[0]);
		right = buffer.getChannel(rightPairs[0]);
		count = buffer.getSampleCount();

		for (int i = 0; i < count; i++) {

			frame = getFrame();
			left[i] = frame[0];
			right[i] = frame[1];
			if (decayCounter != 0) {
				if (decayCounter == 1) startDecay();
				decayCounter--;
			}
		}

		if (finished) {
			padNumber = -1;
			if (parent != null) parent.kill(this);
		}

		return AUDIO_OK;
	}

	@Override
	public void close() throws Exception {
	}

	public void startDecay() {
		staticDecay = true;
	}

	public int getVoiceOverlap() {
		return np.getVoiceOverlap();
	}

	public int getStripNumber() {
		return stripNumber;
	}

	public boolean isDecaying() {
		return staticDecay;
	}

	public MuteInfo getMuteInfo() {
		return muteInfo;
	}

//	@Override
	public void setParent(MpcSoundPlayerChannel parent) {
		this.parent = parent;
	}

	@Override
	public void startDecay(int offset) {
		if (offset > 0) {
			decayCounter = offset;
		} else {
			startDecay();
		}
	}
}