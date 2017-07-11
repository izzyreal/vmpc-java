package com.mpc.gui.vmpc;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.audiomidi.AudioMidiServices;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;

public class BufferSizeObserver implements Observer {

	private JTextField framesField;
	private AudioMidiServices ams;

	public BufferSizeObserver(MainFrame mainFrame) {
		ams = Bootstrap.getGui().getMpc().getAudioMidiServices();
		ams.deleteObservers();
		framesField = mainFrame.lookupTextField("frames");
		displayFrames();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((String) arg1) {
		case "frames":
			displayFrames();
			break;
		}
	}

	private void displayFrames() {
		int frames = ams.getBufferSize();
		framesField.setText("" + frames);
	}

}
