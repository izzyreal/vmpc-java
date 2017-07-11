package com.mpc.controls.sequencer.window;

import java.awt.event.KeyEvent;

import com.mpc.controls.sequencer.AbstractSequencerControls;
import com.mpc.sequencer.MpcTrack;

public class MultiRecordingSetupControls extends AbstractSequencerControls {

	private int yPos;

	protected void init() {
		super.init();
		yPos = 0;
		if (param.length() == 2) yPos = Integer.parseInt(param.substring(1, 2));
	}

	public void left() {
		init();
		if (csn.equals("multirecordingsetup") && param.startsWith("a")) return;
		super.left();
	}

	public void right() {
		init();
		if (param.startsWith("c")) return;
		super.right();
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.startsWith("b")) {
			swGui.setMrsTrack(yPos + swGui.getMrsYOffset(), swGui.getVisibleMrsLines()[yPos].getTrack() + notch);
			swGui.setMrsOut(yPos + swGui.getMrsYOffset(),
					((MpcTrack) mpcSequence.getTrack(swGui.getVisibleMrsLines()[yPos].getTrack())).getDevice());
		}

		if (param.startsWith("c")) {
			swGui.setMrsOut(yPos + swGui.getMrsYOffset(), swGui.getVisibleMrsLines()[yPos].getOut() + notch);
			((MpcTrack) mpcSequence.getTrack(swGui.getVisibleMrsLines()[yPos].getTrack()))
					.setDeviceNumber(swGui.getVisibleMrsLines()[yPos].getOut());
		}
	}

	public void keyEvent(KeyEvent e) {

		if (yPos == 0) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN || (param.startsWith("a") && e.getKeyCode() == KeyEvent.VK_EQUALS)) {
				mainFrame.lookupTextField(param.substring(0, 1) + (yPos + 1)).grabFocus();
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP || (param.startsWith("a") && e.getKeyCode() == KeyEvent.VK_MINUS)) {
				swGui.setMrsYOffset(swGui.getMrsYOffset() - 1);
				return;
			}
		}

		if (yPos == 1) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN || (param.startsWith("a") && e.getKeyCode() == KeyEvent.VK_EQUALS)) {
				mainFrame.lookupTextField(param.substring(0, 1) + (yPos + 1)).grabFocus();
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP || (param.startsWith("a") && e.getKeyCode() == KeyEvent.VK_MINUS)) {
				mainFrame.lookupTextField(param.substring(0, 1) + (yPos - 1)).grabFocus();
				return;
			}
		}

		if (yPos == 2) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN || (param.startsWith("a") && e.getKeyCode() == KeyEvent.VK_EQUALS)) {
				swGui.setMrsYOffset(swGui.getMrsYOffset() + 1);
				return;
				// }
			}
			if (e.getKeyCode() == KeyEvent.VK_UP || (param.startsWith("a") && e.getKeyCode() == KeyEvent.VK_MINUS)) {
				mainFrame.lookupTextField(param.substring(0, 1) + (yPos - 1)).grabFocus();
				return;
			}
		}

	}
}
