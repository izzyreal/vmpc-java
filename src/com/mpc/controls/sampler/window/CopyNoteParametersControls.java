package com.mpc.controls.sampler.window;

import com.mpc.controls.sampler.AbstractSamplerControls;
import com.mpc.sampler.NoteParameters;
import com.mpc.sampler.Program;

public class CopyNoteParametersControls extends AbstractSamplerControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("prog0")) swGui.setProg0(swGui.getProg0() + notch);
		if (param.equals("note0")) swGui.setNote0(swGui.getNote0() + notch);
		if (param.equals("prog1")) swGui.setProg1(swGui.getProg1() + notch);
		if (param.equals("note1")) swGui.setNote1(swGui.getNote1() + notch);

	}

	public void function(int i) {
		super.function(i);
		switch (i) {
		case 4:
			NoteParameters source = sampler.getProgram(swGui.getProg0()).getNoteParameters(swGui.getNote0());
			Program dest = sampler.getProgram(swGui.getProg1());
			NoteParameters clone = null;
			try {
				clone = source.clone();
			} catch (CloneNotSupportedException e1) {
				e1.printStackTrace();
			}
			dest.setNoteParameters(swGui.getNote1(), clone);
			mainFrame.openScreen("programassign", "mainpanel");
			break;
		}
	}

}
