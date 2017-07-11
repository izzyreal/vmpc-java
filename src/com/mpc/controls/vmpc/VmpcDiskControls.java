package com.mpc.controls.vmpc;

import com.mpc.controls.disk.AbstractDiskControls;

public class VmpcDiskControls extends AbstractDiskControls {

	public void function(int i) {
		init();
		switch (i) {
		case 0:
			openMain("audio");
			break;
		case 1:
			openMain("midi");
			break;
		}
	}

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		if (param.equals("scsi")) deviceGui.setScsi(deviceGui.getScsi() + notch);

		if (param.equals("accesstype"))
			deviceGui.setAccessType(deviceGui.getScsi(), deviceGui.getAccessType(deviceGui.getScsi()) + notch);

		if (param.equals("root")) {
			boolean raw = deviceGui.isRaw(deviceGui.getScsi());
			int max = raw ? mpc.getRawStoresAmount() : mpc.getJavaStoresAmount();
			if (deviceGui.getStore(deviceGui.getScsi()) == max - 1) return;
			deviceGui.setStore(deviceGui.getScsi(), deviceGui.getStore(deviceGui.getScsi()) + 1);
		}
	}
}
