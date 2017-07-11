package com.mpc.gui.vmpc;

import java.util.Observable;
import java.util.Observer;

import com.mpc.Mpc;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.components.MpcTextField;

public class VmpcDiskObserver implements Observer {

	private MpcTextField scsiField;
	private MpcTextField accessTypeField;
	private MpcTextField rootField;
	private DeviceGui deviceGui;
	private Mpc mpc;

	public VmpcDiskObserver(MainFrame mainFrame) {
		Gui gui = Bootstrap.getGui();
		mpc = gui.getMpc();
		deviceGui = gui.getDeviceGui();
		deviceGui.deleteObservers();
		deviceGui.addObserver(this);

		scsiField = mainFrame.lookupTextField("scsi");
		accessTypeField = mainFrame.lookupTextField("accesstype");
		rootField = mainFrame.lookupTextField("root");

		displayScsi();
		displayAccessType();
		displayRoot();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		switch ((String) arg1) {
		case "scsi":
			displayScsi();
		case "accesstype":
			displayAccessType();
		case "root":
			displayRoot();
			break;
		}
	}

	private void displayScsi() {
		scsiField.setText("" + (deviceGui.getScsi() + 1));
	}

	private void displayAccessType() {
		String[] types = { "OFF", "JAVA", "RAW" };
		accessTypeField.setText(types[deviceGui.getAccessType(deviceGui.getScsi())]);

	}

	private void displayRoot() {

		String root = "< SCSI-" + (deviceGui.getScsi() + 1) + " disconnected >";
		int store = deviceGui.getStore(deviceGui.getScsi());
		if (store != -1) {
			if (deviceGui.isRaw(deviceGui.getScsi())) {
				root = mpc.getStores().getRawStore(store).toString();
			} else {
				root = mpc.getStores().getJavaStore(store).toString();
			}
		}
		rootField.setText(root);
	}

}
