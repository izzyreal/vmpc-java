package com.mpc.controls;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.controls.datawheel.DataWheel;
import com.mpc.controls.datawheel.DataWheelControllable;
import com.mpc.controls.slider.SliderControllable;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.NameGui;
import com.mpc.gui.components.MpcTextField;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.gui.sequencer.SequencerGui;
import com.mpc.sampler.Program;
import com.mpc.sampler.Sampler;
import com.mpc.sequencer.MpcTrack;
import com.mpc.sequencer.NoteEvent;
import com.mpc.sequencer.Sequencer;
import com.mpc.tootextensions.MpcSoundPlayerChannel;

public abstract class AbstractControls
		implements Controls, DataWheelControllable, Cursor, NumericPad, Transport, Locate, SliderControllable {

	protected Gui gui;
	protected NameGui nameGui;
	protected SequencerGui sequencerGui;
	protected String param;
	protected String csn;
	protected LayeredScreen ls;
	protected MainFrame mainFrame;
	protected Mpc mpc;
	protected Sequencer sequencer;
	protected Sampler sampler;
	protected MpcTrack track;
	protected MpcSoundPlayerChannel mpcSoundPlayerChannel;
	protected SamplerGui samplerGui;
	protected Program program;
	protected JTextField activeField;
	protected int bank;

	protected KbMouseController kbmc;
	protected String[] typableParams;

	protected int getNotch(int increment) {
		final int notch_inc = (increment == DataWheel.NOTCH_DOWN || increment == DataWheel.NOTCH_UP)
				? (increment > 0 ? 1 : -1) : increment;
		return notch_inc;
	}

	protected void init() {
		gui = Bootstrap.getGui();
		sequencerGui = gui.getSequencerGui();
		mpc = gui.getMpc();
		nameGui = gui.getNameGui();
		samplerGui = gui.getSamplerGui();
		csn = Util.getCsn();
		param = Util.getFocus();
		ls = gui.getMainFrame().getLayeredScreen();
		mainFrame = gui.getMainFrame();
		activeField = mainFrame.lookupTextField(param);
		sampler = mpc.getSampler();
		sequencer = mpc.getSequencer();

		track = (MpcTrack) sequencer.getActiveSequence().getTrack(sequencer.getActiveTrackIndex());

		if (track.getBusNumber() != 0 && !mpc.getAudioMidiServices().isDisabled()) {
			mpcSoundPlayerChannel = sampler.getDrum(track.getBusNumber() - 1);
			program = sampler.getProgram(mpcSoundPlayerChannel.getProgram());
		}

		// if (program == null && !mpc.getAudioMidiServices().isDisabled())
		// program =
		// sampler.getProgram(sampler.getDrum(samplerGui.getTrackDrum()).getProgram());

		bank = gui.getSamplerGui().getBank();
		kbmc = (KbMouseController) gui.getKb();

	}

	@Override
	public void left() {
		init();
		if (param.equals("dummy")) return;
		if (param == ls.getFirstField()) return;
		if (activeField == null) return;
		activeField.transferFocusBackward();
	}

	@Override
	public void right() {
		init();
		if (param.equals("dummy")) return;
		if (param == ls.getLastField()) return;
		if (activeField == null) return;
		activeField.transferFocus();
	}

	@Override
	public void up() {
		init();
		JTextField above = mainFrame.getLayeredScreen().findAbove(
				mainFrame.lookupTextField(mainFrame.getFocus(mainFrame.getLayeredScreen().getCurrentPanel())));
		if (above != null) above.grabFocus();
	}

	@Override
	public void down() {
		init();
		JTextField below = mainFrame.getLayeredScreen().findBelow(
				mainFrame.lookupTextField(mainFrame.getFocus(mainFrame.getLayeredScreen().getCurrentPanel())));
		if (below != null) below.grabFocus();
	}

	@Override
	public void function(int i) {
		init();
		switch (i) {
		case 3:
			if (KbMouseController.altIsPressed) {
				mainFrame.close();
				return;
			}

			if (ls.getCurrentPanel().getName().equals("windowpanel")) {
				if (csn.equals("sequence")) {
					ls.setPreviousScreenName("sequencer");
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (csn.equals("midiinput")) {
					ls.setPreviousScreenName("sequencer");
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (csn.equals("midioutput")) {
					ls.setPreviousScreenName("sequencer");
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (csn.equals("editsound")) {
					ls.setPreviousScreenName(gui.getEditSoundGui().getPreviousScreenName());
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (csn.equals("sound")) {
					ls.setPreviousScreenName(gui.getSoundGui().getPreviousScreenName());
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (csn.equals("program")) {
					ls.setPreviousScreenName(gui.getSamplerGui().getPrevScreenName());
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (csn.equals("name")) {
					gui.getNameGui().setNameBeingEdited(false);
					ls.setLastFocus("name", "0");
				}

				if (csn.equals("numberofzones"))
					gui.getSoundGui().setNumberOfZones(gui.getSoundGui().getPreviousNumberOfzones());

				if (csn.equals("directory")) {
					ls.setPreviousScreenName(gui.getDirectoryGui().getPreviousScreenName());
					ls.setPreviousPanel(ls.getMainPanel());
				}

				if (ls.getPreviousScreenName().equals("load")) {
					if (gui.getDiskGui().getFileLoad() > mpc.getDisk().getFiles().size() - 1)
						gui.getDiskGui().setFileLoad(0);
				}

				mainFrame.openScreen(ls.getPreviousScreenName(), ls.getPreviousPanel().getName());
				ls.repaint();
			}

			break;
		}

	}

	@Override
	public void openWindow() {
	}

	@Override
	public void turnWheel(int i) {
	}

	@Override
	public void keyEvent(KeyEvent e) {
	}

	@Override
	public void pad(int i, int velo, boolean repeat, long tick) {
		init();
		KbMouseController.pressedPads.add(new Integer(i));
		KbMouseController.pressedPadVelos[i] = velo;
		int note = track.getBusNumber() > 0 ? program.getPad(i + (bank * 16)).getNote() : i + (bank * 16) + 35;
		int velocity = velo;
		int pad = i + (bank * 16);

		if (gui.getSequencerGui().isSixteenLevelsEnabled()) {
			if (gui.getSequencerGui().getParameter() == 0) {
				note = gui.getSequencerGui().getNote();
				velocity = (int) (i * (127.0 / 16.0));
				if (velocity == 0) velocity = 1;
			}
		} else {

			if (csn.equals("programparams")) {
				if (note > 34) {
					samplerGui.setPadAndNote(pad, note);
				}
			} else {
				samplerGui.setPadAndNote(pad, note);
			}
		}

		if (csn.equals("assign16levels") && note != 34) gui.getSequencerGui().setNote(note);

		if (KbMouseController.tapIsPressed && sequencer.isPlaying()) {
			if (repeat) generateNoteOn(note, velocity, tick);
		} else {
			generateNoteOn(note, velocity, -1);
		}
	}

	private void generateNoteOn(int nn, int padVelo, long tick) {
		init();
		boolean slider = program != null && nn == program.getSlider().getNote();
		boolean step = csn.equals("sequencer_step");
		if (sequencer.isRecordingOrOverdubbing() || step) {

			NoteEvent n = new NoteEvent();
			n.setNote(nn);
			n.setVelocity(padVelo);
			n.setDuration(step ? 1 : -1);
			n.setTick(sequencer.getTickPosition());
			if (sequencerGui.isSixteenLevelsEnabled() && sequencerGui.getParameter() == 1) {
				int type = sequencerGui.getType();
				int key = sequencerGui.getOriginalKeyPad();
				int diff = program.getPadNumberFromNote(nn) - (bank * 16) - key;
				n.setNote(sequencerGui.getNote());
				n.setVariationTypeNumber(type);
				n.setVariationValue(diff * 5);
			}
			if (slider) setSliderNoteVar(n, program);

			if (step) {
				sequencer.playMetronomeTrack();
				track.addEvent(n);
			} else {
				track.recordNoteOn(n);
			}
		}

		NoteEvent noteEvent = new NoteEvent(nn);
		noteEvent.setVelocity(padVelo);
		noteEvent.setDuration(0);
		noteEvent.setVariationValue(64);
		noteEvent.setTick(tick);

		if (sequencerGui.isSixteenLevelsEnabled() && sequencerGui.getParameter() == 1) {

			int type = sequencerGui.getType();
			int key = sequencerGui.getOriginalKeyPad();
			int padnr = program.getPadNumberFromNote(nn) - (bank * 16);
			if (type == 0) {
				int diff = padnr - key;
				int candidate = 64 + (diff * 5);
				if (candidate > 124) candidate = 124;
				if (candidate < 4) candidate = 4;
				noteEvent.setVariationValue(candidate);
			} else {
				noteEvent.setVariationValue((100 / 16) * padnr);
			}
			noteEvent.setNote(sequencerGui.getNote());
			noteEvent.setVariationTypeNumber(type);
		}
		if (slider) setSliderNoteVar(noteEvent, program);
		mpc.getEventHandler().handle(noteEvent, track);
	}

	public void numpad(int i) {
		init();

		if (!KbMouseController.shiftIsPressed) {
			MpcTextField mtf = mainFrame.lookupTextField(param);
			if (isTypable()) {
				if (!mtf.isTypeModeEnabled()) mtf.enableTypeMode();
				mtf.type(i);
			}
		}

		if (KbMouseController.shiftIsPressed) {
			switch (i) {
			case 0:
				if (!mpc.getAudioMidiServices().isDisabled())
					mpc.getAudioMidiServices().setSelectedServer(mpc.getAudioMidiServices().getActiveServerIndex());
				openMain("audio"); // vMPC proprietary screens access
				return;
			case 1:
				openMain("song");
				return;
			case 2:
				openMain("punch");
				gui.getPunchGui().setTime0(0);
				gui.getPunchGui().setTime1(sequencer.getActiveSequence().getLastTick());
				return;
			case 3:
				if (mpc != null && mpc.getDisk() != null) mpc.getDisk().initFiles();
				if (gui.getDiskGui().getFileLoad() > mpc.getDisk().getFiles().size() - 1)
					gui.getDiskGui().setFileLoad(mpc.getDisk().getFiles().size() - 1);
				openMain("load");
				return;
			case 4:
				openMain("sample");
				return;
			case 5:
				openMain("trim");
				return;
			case 6:
			case 130: // fix for apple
				openMain("selectdrum");
				return;
			case 7:
				openMain("selectdrum_mixer");
				return;
			case 8:
				openMain("others");
				return;
			case 9:
				openMain("sync");
				return;
			}
		}
	}

	public void pressEnter() {
		init();
		if (KbMouseController.shiftIsPressed) openMain("save");
	}

	public void rec() {
		KbMouseController.recIsPressed = true;
		init();
		if (!sequencer.isPlaying()) {
			mainFrame.getLedPanel().setRec(true);
		} else {
			if (sequencer.isRecordingOrOverdubbing()) {
				sequencer.setRecording(false);
				sequencer.setOverdubbing(false);
				mainFrame.getLedPanel().setRec(false);
				mainFrame.getLedPanel().setOverDub(false);
			}
		}
	}

	public void overDub() {
		KbMouseController.overdubIsPressed = true;
		init();
		if (!sequencer.isPlaying()) {
			mainFrame.getLedPanel().setOverDub(true);
		} else {
			if (sequencer.isRecordingOrOverdubbing()) {
				sequencer.setRecording(false);
				sequencer.setOverdubbing(false);
				mainFrame.getLedPanel().setRec(false);
				mainFrame.getLedPanel().setOverDub(false);
			}
		}
	}

	public void stop() {
		init();
		sequencer.stop();
		if (KbMouseController.shiftIsPressed) mpc.getAudioMidiServices().stopBouncing();
	}

	public void play() {
		init();
		if (sequencer.isPlaying()) {
			if (KbMouseController.recIsPressed && !sequencer.isOverDubbing()) {
				sequencer.setOverdubbing(false);
				sequencer.setRecording(true);
				mainFrame.getLedPanel().setOverDub(false);
				mainFrame.getLedPanel().setRec(true);
			} else if (KbMouseController.overdubIsPressed && !sequencer.isRecording()) {
				sequencer.setOverdubbing(true);
				sequencer.setRecording(false);
				mainFrame.getLedPanel().setOverDub(true);
				mainFrame.getLedPanel().setRec(false);
			}
			return;
		}

		if (KbMouseController.recIsPressed) {
			sequencer.rec();
		} else if (KbMouseController.overdubIsPressed) {
			sequencer.overdub();
		} else {
			if (KbMouseController.shiftIsPressed && !mpc.getAudioMidiServices().isBouncing()) {
				gui.getD2DRecorderGui().setSq(sequencer.getActiveSequenceIndex());
				mainFrame.openScreen("directtodiskrecorder", "windowpanel");
			} else {
				sequencer.play();
			}
		}
	}

	public void playStart() {
		init();
		if (sequencer.isPlaying()) return;
		if (KbMouseController.recIsPressed) {
			sequencer.recFromStart();
		} else if (KbMouseController.overdubIsPressed) {
			sequencer.overdubFromStart();
		} else {
			if (KbMouseController.shiftIsPressed) {
				gui.getD2DRecorderGui().setSq(sequencer.getActiveSequenceIndex());
				mainFrame.openScreen("directtodiskrecorder", "windowpanel");
			} else {
				sequencer.playFromStart();
			}
		}
		mainFrame.getLedPanel().setPlay(sequencer.isPlaying());
		mainFrame.getLedPanel().setRec(sequencer.isRecording());
		mainFrame.getLedPanel().setOverDub(sequencer.isOverDubbing());
	}

	protected void openMain(String panelName) {
		init();
		mainFrame.openScreen(panelName, "mainpanel");
	}

	public void mainScreen() {
		init();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainFrame.openScreen("sequencer", "mainpanel");
			}
		});

		sequencer.setSoloEnabled(sequencer.isSoloEnabled());
	}

	public void tap() {
		init();
		KbMouseController.tapIsPressed = true;
		sequencer.tap();
	}

	public void prevStepEvent() {
	}

	public void nextStepEvent() {
	}

	public void goTo() {
		init();
		KbMouseController.goToIsPressed = true;
	}

	public void prevBarStart() {
	}

	public void nextBarEnd() {
	}

	public void nextSeq() {
		init();
		mainFrame.openScreen("nextseq", "mainpanel");
	}

	public void trackMute() {
		init();
		sequencer.setSoloEnabled(false);
		mainFrame.openScreen("trackmute", "mainpanel");
	}

	@Override
	public void bank(int i) {
		init();
		int oldBank = samplerGui.getBank();
		samplerGui.setBank(i);
		int diff = 16 * (i - oldBank);
		int newPadNr = samplerGui.getPad() + diff;
		int newNN = program.getPad(newPadNr).getNote();
		samplerGui.setPadAndNote(newPadNr, newNN);
	}

	@Override
	public void fullLevel() {
		init();
		sequencerGui.setFullLevelEnabled(!sequencerGui.isFullLevelEnabled());
		mainFrame.getLedPanel().setFullLevel(sequencerGui.isFullLevelEnabled());
	}

	@Override
	public void sixteenLevels() {
		init();
		if (sequencerGui.isSixteenLevelsEnabled()) {
			sequencerGui.setSixteenLevelsEnabled(false);
			mainFrame.getLedPanel().setSixteenLevels(false);
		} else {
			mainFrame.openScreen("assign16levels", "windowpanel");
		}
	}

	@Override
	public void after() {
		init();
		if (KbMouseController.shiftIsPressed) {
			mainFrame.openScreen("assign", "mainpanel");
		} else {
			sequencerGui.setAfterEnabled(!sequencerGui.isAfterEnabled());
			mainFrame.getLedPanel().setAfter(sequencerGui.isAfterEnabled());
		}
	}

	@Override
	public void shift() {
		if (KbMouseController.shiftIsPressed) return;
		KbMouseController.shiftIsPressed = true;
	}

	@Override
	public void setSlider(int i) {
	}

	@Override
	public void undoSeq() {
		sequencer.undoSeq();
	}

	public static void setSliderNoteVar(NoteEvent n, Program program) {
		if (n.getNote() != program.getSlider().getNote()) return;
		int sliderParam = program.getSlider().getParameter();
		
		int rangeLow = 0, rangeHigh = 0, sliderRange = 0;
		n.setVariationTypeNumber(sliderParam);
		int sliderValue = Bootstrap.getGui().getMainFrame().getControlPanel().getSlider().getValue();
		double sliderRangeRatio = 0;
		switch (sliderParam) {
		case 0:
			rangeLow = program.getSlider().getTuneLowRange();
			rangeHigh = program.getSlider().getTuneHighRange();
			sliderRange = rangeHigh - rangeLow;
			sliderRangeRatio = sliderRange / 128.0;
			int tuneValue = (int) (sliderValue * sliderRangeRatio * 0.5);
			tuneValue += (120 - rangeHigh) / 2;
			n.setVariationValue(tuneValue);
			break;
		case 1:
			rangeLow = program.getSlider().getDecayLowRange();
			rangeHigh = program.getSlider().getDecayHighRange();
			sliderRange = rangeHigh - rangeLow;
			sliderRangeRatio = sliderRange / 128.0;
			int decayValue = (int) (sliderValue * sliderRangeRatio);
			n.setVariationValue(decayValue);
			break;
		case 2:
			rangeLow = program.getSlider().getAttackLowRange();
			rangeHigh = program.getSlider().getAttackHighRange();
			sliderRange = rangeHigh - rangeLow;
			sliderRangeRatio = sliderRange / 128.0;
			int attackValue = (int) (sliderValue * sliderRangeRatio);
			n.setVariationValue(attackValue);
			break;
		case 3:
			rangeLow = program.getSlider().getFilterLowRange();
			rangeHigh = program.getSlider().getFilterHighRange();
			sliderRange = rangeHigh - rangeLow;
			sliderRangeRatio = sliderRange / 128.0;
			int filterValue = (int) (sliderValue * sliderRangeRatio);
			n.setVariationValue(filterValue);
			break;
		}
	}

	protected boolean isTypable() {
		if (typableParams == null) return false;
		for (String str : typableParams)
			if (str.equals(param)) return true;
		return false;
	}

	public void erase() {
		init();
		KbMouseController.eraseIsPressed = true;
		if (!sequencer.getActiveSequence().isUsed()) return;
		if (sequencer.isOverDubbing()) {

		} else {
			gui.getSequencerWindowGui().setTime0(0);
			gui.getSequencerWindowGui().setTime1(sequencer.getActiveSequence().getLastTick());
			mainFrame.openScreen("erase", "windowpanel");
		}

	}
}
