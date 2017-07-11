package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.gui.sampler.window.SamplerWindowGui;
import com.mpc.sampler.Sampler;

public class AssignmentViewControls extends AbstractSamplerControls {

	public void up() {
		init();
		if (param.contains("0")) return;
		int nn = program.getPad(samplerGui.getPad() + 4).getNote();
		int focusPadNr = samplerGui.getPad() + 4;
		while (focusPadNr > 15)
			focusPadNr -= 16;
		mainFrame.setFocus(SamplerWindowGui.padFocusNames[focusPadNr], ls.getWindowPanel());
		samplerGui.setPadAndNote(samplerGui.getPad() + 4, nn);
	}

	public void down() {
		init();
		if (param.contains("3")) return;
		int nn = program.getPad(samplerGui.getPad() - 4).getNote();
		int focusPadNr = samplerGui.getPad() - 4;
		while (focusPadNr > 15)
			focusPadNr -= 16;
		mainFrame.setFocus(SamplerWindowGui.padFocusNames[focusPadNr], ls.getWindowPanel());
		samplerGui.setPadAndNote(samplerGui.getPad() - 4, nn);
	}

	public void left() {
		init();
		if (param.startsWith("a")) return;
		super.left();
		int padNr = samplerGui.getPad() - 1;
		int nn = program.getPad(padNr).getNote();
		samplerGui.setPadAndNote(padNr, nn);
	}

	public void right() {
		init();
		if (param.startsWith("d")) return;
		super.right();
		int padNr = samplerGui.getPad() + 1;
		int nn = program.getPad(padNr).getNote();
		samplerGui.setPadAndNote(padNr, nn);
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		Sampler.getLastPad(program).setNote(Sampler.getLastPad(program).getNote() + notch);
	}

	public void pad(int i, int velo) {
		super.pad(i, velo, false, 0);
		String padFocus = SamplerWindowGui.padFocusNames[i];
		mainFrame.lookupTextField(padFocus).grabFocus();
	}
}
