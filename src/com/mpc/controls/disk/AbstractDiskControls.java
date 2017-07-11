package com.mpc.controls.disk;

import com.mpc.controls.AbstractControls;
import com.mpc.disk.Disk;
import com.mpc.disk.MpcFile;
import com.mpc.gui.NameGui;
import com.mpc.gui.disk.DiskGui;
import com.mpc.gui.disk.window.DirectoryGui;
import com.mpc.gui.disk.window.DiskWindowGui;
import com.mpc.gui.sampler.SoundGui;
import com.mpc.gui.vmpc.DeviceGui;

public abstract class AbstractDiskControls extends AbstractControls {

	protected DirectoryGui directoryGui;
	protected DiskGui diskGui;
	protected DiskWindowGui diskWindowGui;
	protected NameGui nameGui;
	protected Disk disk;
	protected DeviceGui deviceGui;
	protected SoundGui soundGui;
	protected MpcFile selectedFile;

	protected void init() {
		super.init();
		directoryGui = gui.getDirectoryGui();
		nameGui = gui.getNameGui();
		disk = gui.getMpc().getDisk();
		diskGui = gui.getDiskGui();
		diskWindowGui = gui.getDirectoryWindowGui();
		deviceGui = gui.getDeviceGui();
		soundGui = gui.getSoundGui();
		disk = mpc.getDisk();
		if (!csn.equals("loadasequencefromall") && disk != null && disk.getFiles().size() > 0 && diskGui.getFileLoad() < disk.getFiles().size()) selectedFile = diskGui.getSelectedFile();
	}

}