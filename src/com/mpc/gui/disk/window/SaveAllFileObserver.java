package com.mpc.gui.disk.window;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.disk.DiskGui;

public class SaveAllFileObserver implements Observer {

	private JTextField fileField;
	private JLabel fileLabel;
	
	private DiskGui diskGui;
	
	public SaveAllFileObserver(MainFrame mainFrame) {
		fileField = mainFrame.lookupTextField("file");
		fileLabel = mainFrame.lookupLabel("file");
		diskGui = Bootstrap.getGui().getDiskGui();
		displayFile();
	}
	
	private void displayFile() {
		fileField.setText(Bootstrap.getGui().getNameGui().getName().substring(0, 1));
		fileLabel.setText(StringUtils.rightPad(Bootstrap.getGui().getNameGui().getName().substring(1), 15) + ".ALL");
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		switch((String)arg1) {
		
		}
	}

}
