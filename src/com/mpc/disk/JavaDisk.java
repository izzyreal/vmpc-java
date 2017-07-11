package com.mpc.disk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mpc.disk.Stores.Store;
import com.mpc.disk.device.Device;
import com.mpc.disk.device.JavaDevice;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.disk.DiskGui;

import de.waldheinz.fs.fat.AkaiNameGenerator;
import de.waldheinz.fs.fat.ShortNameGenerator;

public class JavaDisk extends AbstractDisk implements Disk {

	private boolean busy;

	private Device device;

	private List<File> path;
	private File root;

	private final Store store;

	public JavaDisk(Store store) {

		this.store = store;
		busy = false;

		JavaDevice javaDevice = new JavaDevice(new File(store.path));

		if (javaDevice.isValid()) {

			device = javaDevice;
			path = new ArrayList<File>();
			root = (File) device.getRoot();
			initFiles();
		}
	}

	@Override
	public void setBusy(boolean b) {
		busy = b;
	}

	@Override
	public boolean isBusy() {
		return busy;
	}

	@Override
	public void openLoadASound() {
		setChanged();
		notifyObservers("loadasound");
	}

	@Override
	public void initFiles() {

		files = new ArrayList<MpcFile>();

		int view = 0;

		if (Bootstrap.getGui() != null) {
			DiskGui diskGui = Bootstrap.getGui().getDiskGui();
			view = diskGui.getView();
			// diskGui.setFileLoad(0);
		}
		File[] fileArray = getDir().listFiles();
		Arrays.sort(fileArray, new Comparator<File>() {
			public int compare(File f1, File f2) {
				BasicFileAttributes attr1 = null;
				BasicFileAttributes attr2 = null;
				try {
					attr1 = Files.readAttributes(f1.toPath(), BasicFileAttributes.class);
					attr2 = Files.readAttributes(f2.toPath(), BasicFileAttributes.class);
				} catch (IOException e) {
					return 0;
				}

				return Long.valueOf(attr1.creationTime().toMillis()).compareTo(attr2.creationTime().toMillis());
			}
		});

		Set<String> usedNames = new HashSet<String>();
		boolean renamed = false;
		for (File f : fileArray) {
			MpcFile mpcFile = null;
			if (view != 0) {
				if (f.isFile() && !f.getName().toUpperCase().endsWith(extensions[view])) continue;
			}

			if (f.isFile()) {
				AkaiNameGenerator ang = new AkaiNameGenerator(usedNames);
				String akaiName = ang.generateAkaiName(f.getName());
				usedNames.add(akaiName);
				if (!akaiName.equals(f.getName())) {
					renamed = true;
					mpcFile = new MpcFile(f);
					mpcFile.setName(akaiName);
				}
			} else {
				ShortNameGenerator sng = new ShortNameGenerator(usedNames);
				String sn = sng.generateShortName(f.getName()).asSimpleString();
				if (sn.contains(".")) sn = sn.substring(0, sn.indexOf("."));
				usedNames.add(sn);
				if (!sn.equals(f.getName())) {
					renamed = true;
					mpcFile = new MpcFile(f);
					mpcFile.setName(sn);
				}
			}
			if (mpcFile == null) mpcFile = new MpcFile(f);
			files.add(mpcFile);
			
		}
		
		if (renamed) {
			initFiles();
			return;
		}
		initParentFiles();
	}

	private void initParentFiles() {

		parentFiles = new ArrayList<MpcFile>();

		if (path.size() == 0) return;

		File[] files = null;
		try {
			files = getParentDir().listFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (files != null) for (File f : files)
			if (f.isDirectory()) parentFiles.add(new MpcFile(f));

	}

	@Override
	public boolean isDirectory(MpcFile f) {
		return f.isDirectory();
	}

	@Override
	public String getDirectoryName() {

		if (path == null || path.size() == 0) return "ROOT";

		return path.get(path.size() - 1).getName();
	}

	@Override
	public void close() {
		if (device != null) device.close();
	}

	@Override
	public void flush() {
		device.flush();
	}

	@Override
	public boolean moveBack() {
		if (path.size() <= 0) return false;
		path.remove(path.size() - 1);
		return true;
	}

	@Override
	public boolean moveForward(String directoryName) {
		MpcFile newDir = null;
		for (MpcFile f : files) {
			if (f.getName().trim().equalsIgnoreCase(directoryName.trim())) {

				// safety hack -- makes sure user doesn't nav beyond vmpc env

				if (f.getFile().getAbsolutePath().contains("Mpc")
						&& f.getFile().getAbsolutePath().contains("JavaStores")) {
					newDir = f;
					path.add(newDir.getFile());
				}
			}
		}
		return newDir != null;
	}

	@Override
	public boolean isRoot() {
		return path.size() == 0;
	}

	public File getDir() {
		if (path.size() == 0) return root;
		return path.get(path.size() - 1);
	}

	private File getParentDir() throws IOException {
		if (path.size() == 0) return null;
		if (path.size() == 1) return root;
		return path.get(path.size() - 2);
	}

	public boolean deleteAllFiles(int dwGuiDelete) { // Only available in right
														// column, only removes
														// !dir
		File parentDirectory = null;
		try {
			parentDirectory = getParentDir();
		} catch (IOException e1) {
			return false;
		}

		boolean success = false;
		File[] files = parentDirectory.listFiles();

		for (File f : files) {
			if (!f.isDirectory()) {
				if (dwGuiDelete == 0 || f.getName().toUpperCase().endsWith(extensions[dwGuiDelete])) {
					success = f.delete();
				}
			}
		}
		return success;
	}

	@Override
	public boolean newFolder(String newDirName) {
		File f = new File(getDir().getAbsolutePath() + "/" + newDirName.toUpperCase());
		return f.mkdir();
	}

	@Override
	public boolean deleteDir(MpcFile f) {
		return deleteRecursive(f.getFile());
	}

	private boolean deleteRecursive(File deleteMe) {

		boolean deletedSomething = false;
		boolean deletedCurrentFile = false;

		if (deleteMe.isDirectory()) {

			for (File f : deleteMe.listFiles())
				deleteRecursive(f);

		}

		deletedCurrentFile = deleteMe.delete();
		if (deletedCurrentFile) deletedSomething = true;
		if (!deletedCurrentFile) return false;
		return deletedSomething;
	}

	@Override
	public MpcFile newFile(String newFileName) {
		File f = null;
		try {
			f = new File(getDir().getAbsolutePath() + "/" + newFileName.replaceAll(" ", "_").toUpperCase());
			boolean success = f.createNewFile();
			if (success) return new MpcFile(f);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getAbsolutePath() {
		return getDir().getAbsolutePath();
	}

	@Override
	public Store getStore() {
		return store;
	}

}
