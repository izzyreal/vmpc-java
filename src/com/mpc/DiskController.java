package com.mpc;

import com.mpc.disk.Disk;
import com.mpc.disk.JavaDisk;
import com.mpc.disk.RawDisk;
import com.mpc.disk.Stores;
import com.mpc.disk.Stores.Store;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.vmpc.DeviceGui;

public class DiskController {

	final static int MAX_DISKS = 7;

	private Stores stores;

	private Disk[] disks;

	DiskController() {
		stores = new Stores();
	}

	void initDisks() {

		disks = new Disk[MAX_DISKS];
		DeviceGui deviceGui = Bootstrap.getGui().getDeviceGui();
		for (int i = 0; i < MAX_DISKS; i++) {

			Disk oldDisk = disks[i];

			if (!deviceGui.isEnabled(i) && oldDisk != null) {
				oldDisk.close();
				disks[i] = null;
				continue;
			}

			if (deviceGui.getStore(i) == -1) continue;

			Store candidate;

			candidate = deviceGui.isRaw(i) && stores.getRawStores().size() > deviceGui.getStore(i)
					? stores.getRawStore(deviceGui.getStore(i)) : stores.getJavaStore(deviceGui.getStore(i));

			if (oldDisk != null) {
				Store oldStore = oldDisk.getStore();
				if (oldStore == candidate) continue;
				oldDisk.close();
			}

			if (deviceGui.isRaw(i) && stores.getRawStores().size() > deviceGui.getStore(i)) {
				try {
					disks[i] = new RawDisk(stores.getRawStore(deviceGui.getStore(i)));
					System.out.println("Create raw disk.");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Couldn't create raw disk. Using Java disk.");
					deviceGui.setAccessType(i, DeviceGui.JAVA);
					deviceGui.setStore(i, 0);
					deviceGui.saveSettings();
					disks[i] = new JavaDisk(stores.getJavaStore(deviceGui.getStore(i)));
				}
			} else {
				deviceGui.setAccessType(i, DeviceGui.JAVA);
				deviceGui.setStore(i, 0);
				deviceGui.saveSettings();
				disks[i] = new JavaDisk(stores.getJavaStore(deviceGui.getStore(i)));
			}

			// if (i == deviceGui.getScsi()) {
			// if (Bootstrap.getGui() != null) {
			// Bootstrap.getGui().getDiskGui().setFileLoad(0);
			// Bootstrap.getGui().getDirectoryGui().setYOffset0(0);
			// Bootstrap.getGui().getDirectoryGui().setYOffset1(0);
			// Bootstrap.getGui().getDirectoryGui().setYPos0(0);
			// Bootstrap.getGui().getDirectoryGui().setXPos(1);
			// }
			// }
		}
	}

	Stores getStores() {
		return stores;
	}

	Disk getDisk() {
		if (disks == null) return null;
		DeviceGui deviceGui = null;
		try {
			deviceGui = Bootstrap.getGui().getDeviceGui();
		} catch (Exception e) {
			return null;
		}
		return disks[deviceGui.getScsi()];
	}

	Disk getDisk(int i) {
		return disks[i];
	}

}
