package com.mpc.controls;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.LayeredScreen;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.SamplerGui;

import imagemap.Shape;
import imagemap.ShapeList;

public class KbMouseController extends KeyAdapter implements MouseListener, MouseMotionListener {

	private AbstractControls controls = null;

	private Gui gui;
	private MainFrame mainFrame;
	private SamplerGui samplerGui;

	static int jump;
	static LayeredScreen slp;

	private GlobalReleaseControls releaseControls;

	public static boolean goToIsPressed;
	public static Set<Integer> pressedPads = new HashSet<Integer>();
	public static int[] pressedPadVelos = new int[16];
	public static boolean f6IsPressed = false;
	public static boolean altIsPressed = false;
	public static boolean shiftIsPressed = false;
	public static boolean recIsPressed = false;
	public static boolean overdubIsPressed = false;
	public static boolean tapIsPressed = false;
	public static boolean eraseIsPressed;

	private Point volKnobOrigin;
	private Point recKnobOrigin;

	private int lastKnobValue;
	private boolean ctrlIsPressed;

	private void init() {
		gui = Bootstrap.getGui();
		mainFrame = gui.getMainFrame();
		samplerGui = gui.getSamplerGui();
		slp = mainFrame.getLayeredScreen();
		controls = gui.getControls(Util.getCsn());
		releaseControls = new GlobalReleaseControls();
	}

	public void keyPressed(KeyEvent e) {
		init();
		if (gui.getMpc().getAudioMidiServices().isDisabled()) {
			if (!(e.getKeyCode() == KbMapping.numPadShift || e.getKeyCode() == KeyEvent.VK_0
					|| e.getKeyCode() == KbMapping.f4 || e.getKeyCode() == KeyEvent.VK_ALT)
					&& !Util.getCsn().equals("audio")) {
				mainFrame.openScreen("audiomididisabled", "windowpanel");
				return;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ALT) altIsPressed = true;
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlIsPressed = true;

		if (ctrlIsPressed && altIsPressed) mainFrame.getKeyLabels().displayKeys();

		jump = 1;
		if (shiftIsPressed) jump = 10;
		if (shiftIsPressed && ctrlIsPressed) jump = 100;
		if (ctrlIsPressed && altIsPressed) jump = 1000;

		if (ctrlIsPressed && shiftIsPressed && altIsPressed) jump = 10000;

		slp.setLastFocus(Util.getCsn(), e.getComponent().getName());

		if (controls != null) {
			int kc = e.getKeyCode();
			for (int i = 0; i < 4; i++)
				if (kc == KbMapping.bankKeys[i]) controls.bank(i);

			if (!(gui.getMpc().getDisk() != null && gui.getMpc().getDisk().isBusy())) {

				if ((kc < KeyEvent.VK_0 || kc > KeyEvent.VK_9) && kc != KbMapping.numPadEnter) {
					if (Util.getFocus() != null && mainFrame != null
							&& mainFrame.lookupTextField(Util.getFocus()) != null)
						mainFrame.lookupTextField(Util.getFocus()).disableTypeMode();
				}

				if (!Util.getCsn().equals("name")) {
					if (kc == KbMapping.fullLevel) controls.fullLevel();
					if (kc == KbMapping.sixteenLevels) controls.sixteenLevels();
					if (kc == KbMapping.tap) controls.tap();
					if (kc == KbMapping.rec) controls.rec();
					if (kc == KbMapping.overdub) controls.overDub();
					if (kc == KbMapping.stop) controls.stop();
					if (kc == KbMapping.play) controls.play();
					if (kc == KbMapping.playstart) controls.playStart();
					if (kc == KbMapping.prevStepEvent) controls.prevStepEvent();
					if (kc == KbMapping.nextStepEvent) controls.nextStepEvent();
					if (kc == KbMapping.goTo) controls.goTo();
					if (kc == KbMapping.prevBarStart) controls.prevBarStart();
					if (kc == KbMapping.nextBarEnd) controls.nextBarEnd();
					if (kc == KbMapping.nextSeq) controls.nextSeq();
					if (kc == KbMapping.trackMute) controls.trackMute();
					if (kc == KbMapping.mainscreen) controls.mainScreen();
					if (kc == KbMapping.undoSeq) controls.undoSeq();
					if (kc >= KeyEvent.VK_0 && kc <= KeyEvent.VK_9) controls.numpad(kc - KeyEvent.VK_0);
					if (kc == KbMapping.openWindow) controls.openWindow();
				}

				if (kc == KbMapping.erase) controls.erase();

				String csn = Util.getCsn();
				boolean soundCsn = csn.equals("trim") || csn.equals("loop") || csn.equals("zone");
				boolean noSounds = Bootstrap.getGui().getMpc().getSampler().getSoundCount() == 0;
				if (!(soundCsn && noSounds)) {
					if (kc == KeyEvent.VK_LEFT) controls.left();
					if (kc == KeyEvent.VK_RIGHT) controls.right();
					if (kc == KeyEvent.VK_UP) controls.up();
					if (kc == KeyEvent.VK_DOWN) controls.down();
				}
				if (kc == KbMapping.numPadShift) controls.shift();

				if (kc == KbMapping.dataWheelBack) {
					controls.turnWheel(-jump);
					mainFrame.getControlPanel().update(mainFrame.getControlPanel().getDataWheel(),
							new Integer((int) Math.floor(-jump / 2.0)));
				}
				if (kc == KbMapping.dataWheelForward) {
					controls.turnWheel(jump);
					mainFrame.getControlPanel().update(mainFrame.getControlPanel().getDataWheel(),
							new Integer((int) Math.ceil(jump / 2.0)));
				}

				if (kc >= KeyEvent.VK_F1 && kc <= KeyEvent.VK_F6 && !altIsPressed) {
					if (kc == KeyEvent.VK_F6) {
						if (f6IsPressed) {
							return;
						} else {
							f6IsPressed = true;
						}
					}
					controls.function(kc - KeyEvent.VK_F1);
				}
				if (kc == KbMapping.numPadEnter) controls.pressEnter();
				controls.keyEvent(e);
			}

			if (getPressedPad(e) != -1 && !pressedPads.contains(getPressedPad(e)))
				controls.pad(getPressedPad(e), 127, false, 0);

		}

	}

	public void keyReleased(KeyEvent e) {
		init();
		if (gui.getMpc().getAudioMidiServices().isDisabled()) {
			if (!(e.getKeyCode() == KbMapping.numPadShift || e.getKeyCode() == KeyEvent.VK_0
					|| e.getKeyCode() == KbMapping.f4
					|| e.getKeyCode() == KeyEvent.VK_ALT && !Util.getCsn().equals("audio"))
					&& !Util.getCsn().equals("audio")) {
				mainFrame.openScreen("audiomididisabled", "windowpanel");
				return;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) ctrlIsPressed = false;
		if (e.getKeyCode() == KeyEvent.VK_ALT) altIsPressed = false;
		if (gui.getMpc().getDisk() != null && gui.getMpc().getDisk().isBusy()) return;
		releaseControls.keyEvent(e);
		if (e.getKeyCode() == KbMapping.tap) releaseControls.tap();
		if (e.getKeyCode() == KbMapping.numPadShift) releaseControls.shift();
		if (e.getKeyCode() == KbMapping.erase) releaseControls.erase();
		if (!altIsPressed && !ctrlIsPressed) mainFrame.getKeyLabels().removeKeys();
		if (e.getKeyCode() == KeyEvent.VK_F6) f6IsPressed = false;
	}

	public static int getPressedPad(KeyEvent e) {
		for (int i = 0; i < 16; i++)
			if (e.getKeyCode() == KbMapping.padKeys[i]) return i;
		return -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

		init();
		int[] padVelo = Util.getPadAndVelo(e);

		if (padVelo[0] != -1) {
			if (samplerGui == null || controls == null) return;
			controls.pad(padVelo[0], gui.getSequencerGui().isFullLevelEnabled() ? 127 : padVelo[1], false, 0);
			return;
		}

		if (gui.getMpc().getDisk() != null && gui.getMpc().getDisk().isBusy()) return;
		mainFrame = Bootstrap.getGui().getMainFrame();
		Shape control = getShape(e);

		if (control != null && controls != null) {
			String s = control.get_href();
			if (s.equals("play")) controls.play();
			if (s.equals("rec")) controls.rec();
			if (s.equals("playstart")) controls.playStart();
			if (s.equals("overdub")) controls.overDub();
			if (s.equals("stop")) controls.stop();
			if (s.equals("nextseq")) controls.nextSeq();
			if (s.equals("trackmute")) controls.trackMute();
			if (s.equals("mainscreen")) controls.mainScreen();
			if (s.equals("openwindow")) controls.openWindow();
			if (s.equals("taptemponoterepeat")) controls.tap();
			if (s.startsWith("f") && s.length() == 2) controls.function(Integer.parseInt(s.substring(1, 2)) - 1);
			if (s.equals("up")) controls.up();
			if (s.equals("down")) controls.down();
			if (s.equals("left")) controls.left();
			if (s.equals("right")) controls.right();
			if (s.equals("prevstepevent")) controls.prevStepEvent();
			if (s.equals("nextstepevent")) controls.nextStepEvent();
			if (s.equals("goto")) controls.goTo();
			if (s.equals("prevbarstart")) controls.prevBarStart();
			if (s.equals("nextbarend")) controls.nextBarEnd();
			if (s.equals("banka")) controls.bank(0);
			if (s.equals("bankb")) controls.bank(1);
			if (s.equals("bankc")) controls.bank(2);
			if (s.equals("bankd")) controls.bank(3);
			if (s.equals("fulllevel")) controls.fullLevel();
			if (s.equals("16levels")) controls.sixteenLevels();
			if (s.equals("notevariationafter")) controls.after();
			if (s.equals("shift")) controls.shift();
			if (s.equals("recgain")) {
				recKnobOrigin = e.getPoint();
				lastKnobValue = mainFrame.getControlPanel().getRecord();
			}

			if (s.equals("mainvolume")) {
				volKnobOrigin = e.getPoint();
				lastKnobValue = mainFrame.getControlPanel().getVolume();
			}

			if (s.equals("undoseq")) controls.undoSeq();
			if (s.equals("erase")) controls.erase();

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		recKnobOrigin = null;
		volKnobOrigin = null;

		init();
		int[] padVelo = Util.getPadAndVelo(e);
		if (padVelo[0] != -1) {
			if (samplerGui == null) return;
			releaseControls.pad(padVelo[0]);
			return;
		}
		if (gui.getMpc().getDisk() != null && gui.getMpc().getDisk().isBusy()) return;
		mainFrame = Bootstrap.getGui().getMainFrame();
		Shape control = getShape(e);
		if (control != null && controls != null) {
			String s = control.get_href();
			if (s.equals("overdub")) releaseControls.overDub();
			if (s.equals("rec")) releaseControls.rec();
			if (s.equals("taptemponoterepeat")) releaseControls.tap();
			if (s.equals("shift")) releaseControls.shift();
		}
	}

	private Shape getShape(MouseEvent e) {
		ShapeList sl = mainFrame.getControlPanel().getControlShapes();
		for (int i = 0; i < sl.size(); i++) {
			Shape s = sl.get_shape(i);
			if (s.inside(e.getX(), e.getY())) return s;
		}
		return null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (recKnobOrigin == null && volKnobOrigin == null) return;
		if (recKnobOrigin != null) {
			int oy = (int) recKnobOrigin.getY();
			int newValue = oy - e.getY();
			mainFrame.getControlPanel().setRecord(lastKnobValue + newValue);
		}
		if (volKnobOrigin != null) {
			int oy = (int) volKnobOrigin.getY();
			int newValue = oy - e.getY();
			mainFrame.getControlPanel().setVolume(lastKnobValue + newValue);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	public AbstractControls getControls() {
		return controls;
	}
}