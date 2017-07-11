package com.mpc.disk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mpc.disk.Stores.Store;
import com.mpc.disk.device.Device;
import com.mpc.disk.device.RawOsxDevice;
import com.mpc.disk.device.RawWindowsDevice;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.disk.DiskGui;
import com.mpc.gui.disk.window.DirectoryGui;

import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.fat.AkaiFatLfnDirectory;
import de.waldheinz.fs.fat.AkaiFatLfnDirectoryEntry;

public class RawDisk extends AbstractDisk implements Disk {

	private boolean busy;

	private static Device device;

	private List<AkaiFatLfnDirectoryEntry> path;
	private AkaiFatLfnDirectory root;
	private final Store store;

	public RawDisk(Store store) throws Exception {
		this.store = store;
		busy = false;
		try {
			if (Bootstrap.osx) device = new RawOsxDevice(store.path);
			if (!Bootstrap.osx) device = new RawWindowsDevice("\\\\.\\" + store.path, this, store.totalSpace);
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}

		if (device != null && device.getFileSystem() != null) {
			path = new ArrayList<AkaiFatLfnDirectoryEntry>();
			root = (AkaiFatLfnDirectory) device.getRoot();
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
		root = (AkaiFatLfnDirectory) device.getRoot();

		refreshPath();

		files = new ArrayList<MpcFile>();
		int view = 0;

		if (Bootstrap.getGui() != null) {
			DiskGui diskGui = Bootstrap.getGui().getDiskGui();
			view = diskGui.getView();
		}

		Iterator<FsDirectoryEntry> iterator = null;

		if (path.size() == 0) {
			iterator = root.iterator();
		} else {
			try {
				iterator = path.get(path.size() - 1).getDirectory().iterator();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		while (iterator.hasNext()) {

			AkaiFatLfnDirectoryEntry entry = (AkaiFatLfnDirectoryEntry) iterator.next();
			if (entry.getName().startsWith(".") || entry.getName().length() == 0) continue;
			if (view != 0) {
				if (entry.isFile() && !entry.getName().toUpperCase().endsWith(extensions[view])) continue;
			}
			files.add(new MpcFile(entry));
		}

		initParentFiles();
	}

	private void initParentFiles() {
		parentFiles = new ArrayList<MpcFile>();
		if (path.size() == 0) return;
		Iterator<FsDirectoryEntry> iterator = path.get(path.size() - 1).getParent().iterator();
		while (iterator.hasNext()) {
			AkaiFatLfnDirectoryEntry entry = (AkaiFatLfnDirectoryEntry) iterator.next();
			if (entry.getName().startsWith(".") || entry.getName().length() == 0) continue;
			if (entry.isValid() && entry.isDirectory()) parentFiles.add(new MpcFile(entry));
		}
	}

	@Override
	public boolean isDirectory(MpcFile f) {
		return f.isDirectory();
	}

	@Override
	public String getDirectoryName() {
		if (path.size() == 0) return "ROOT";
		return path.get(path.size() - 1).getName();
	}

	@Override
	public void close() {
		device.close();
	}

	@Override
	public void flush() {
		device.flush();
	}

	@Override
	public boolean moveBack() {

		if (path.size() == 0) return false;

		path.remove(path.size() - 1);
		refreshPath();
		return true;
	}

	@Override
	public boolean moveForward(String directoryName) {
		AkaiFatLfnDirectoryEntry entry = null;
		try {
			entry = getDir().getEntry(directoryName);
			if (entry == null || entry.isFile()) return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		path.add(entry);
		refreshPath();
		return true;
	}

	@Override
	public boolean isRoot() {
		return path.size() == 0;
	}

	private AkaiFatLfnDirectory getDir() throws IOException {
		if (path.size() == 0) return root;
		return path.get(path.size() - 1).getDirectory();
	}

	private AkaiFatLfnDirectory getParentDir() throws IOException {
		if (path.size() == 0) return root;
		if (path.size() == 1) return root;
		return path.get(path.size() - 2).getDirectory();
	}

	private void refreshPath() {
		root = (AkaiFatLfnDirectory) device.getRoot();
		if (path.size() > 0) {
			List<AkaiFatLfnDirectoryEntry> refreshedPath = new ArrayList<AkaiFatLfnDirectoryEntry>();
			AkaiFatLfnDirectory directory = root;
			for (AkaiFatLfnDirectoryEntry e : path) {
				refreshedPath.add(directory.getEntry(e.getName()));
				try {
					directory = directory.getEntry(e.getName()).getDirectory();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			path = refreshedPath;
		}
	}

	public static File entryToFile(AkaiFatLfnDirectoryEntry entry) {
		// String sn = mpcFile.realEntry.getShortName().asSimpleString();
		FileOutputStream fos;
		ByteBuffer buffer;
		String pathString = Bootstrap.tempPath;

		try {
			fos = new FileOutputStream(pathString + "/" + entry.getName());
			int length = (int) entry.getFile().getLength();
			int bufferLength = length;
			int clusterSize = 2048;
			if (length > clusterSize) {
				bufferLength = clusterSize;

				int numberOfWholeBuffers = (int) Math.floor(length / clusterSize);

				for (int j = 0; j < numberOfWholeBuffers; j++) {
					buffer = ByteBuffer.allocateDirect(bufferLength);
					entry.getFile().read((j * clusterSize), buffer);
					byte[] data = new byte[bufferLength];
					buffer.position(0);
					for (int i = 0; i < data.length; i++)
						data[i] = buffer.get();
					fos.write(data);
				}

				int remaining = length - (numberOfWholeBuffers * clusterSize);

				buffer = ByteBuffer.allocateDirect(remaining);

				entry.getFile().read(numberOfWholeBuffers * clusterSize, buffer);
				byte[] data = new byte[bufferLength];

				buffer.position(0);

				for (int i = 0; i < remaining; i++)
					data[i] = buffer.get();

				fos.write(data);

			} else {

				buffer = ByteBuffer.allocateDirect(bufferLength);

				entry.getFile().read(0, buffer);
				byte[] data = new byte[bufferLength];
				buffer.position(0);
				for (int i = 0; i < data.length; i++)
					data[i] = buffer.get();
				fos.write(data);
			}

			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new File(pathString + "/" + entry.getName());
	}

	@Override
	public boolean deleteDir(MpcFile f) {
		AkaiFatLfnDirectoryEntry entry = f.getEntry();
		boolean left = Bootstrap.getGui().getDirectoryGui().getXPos() == 0;
		try {
			deleteFilesRecursive(entry);
			return deleteDirsRecursive(entry, left);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean deleteFilesRecursive(AkaiFatLfnDirectoryEntry entry) throws IOException {

		if (entry.getName().startsWith(".") || entry.getName().length() == 0) return false;

		boolean deletedSomething = false;
		boolean deletedCurrentFile = false;

		if (entry.isDirectory()) {

			Iterator<FsDirectoryEntry> iterator = entry.getDirectory().iterator();

			List<AkaiFatLfnDirectoryEntry> toRemove = new ArrayList<AkaiFatLfnDirectoryEntry>();

			while (iterator.hasNext())

				toRemove.add((AkaiFatLfnDirectoryEntry) iterator.next());

			for (AkaiFatLfnDirectoryEntry subEntry : toRemove)
				deleteFilesRecursive(subEntry);

		}

		if (entry.isFile()) {

			try {
				entry.getParent().remove(entry.getName());
				deletedCurrentFile = true;
			} catch (IOException e) {
				deletedCurrentFile = false;
				// e.printStackTrace();
			}
		}

		if (deletedCurrentFile) deletedSomething = true;

		return deletedSomething;
	}

	private boolean deleteDirsRecursive(AkaiFatLfnDirectoryEntry entry, boolean checkExist) throws IOException {

		if (entry.getName().startsWith(".") || entry.getName().length() == 0) return false;

		DirectoryGui dirGui = Bootstrap.getGui().getDirectoryGui();

		boolean deletedSomething = false;
		boolean deletedCurrentDir = false;

		if (entry.isFile()) return false;

		List<AkaiFatLfnDirectoryEntry> list = new ArrayList<AkaiFatLfnDirectoryEntry>();
		Iterator<FsDirectoryEntry> iterator = entry.getDirectory().iterator();

		while (iterator.hasNext())
			list.add((AkaiFatLfnDirectoryEntry) iterator.next());

		for (AkaiFatLfnDirectoryEntry fe : list) {

			if (fe.isDirectory()) deleteDirsRecursive(fe, false);

		}

		try {

			entry.getParent().remove(entry.getName());
			deletedCurrentDir = true;

		} catch (IOException e) {

			deletedCurrentDir = false;
			e.printStackTrace();

		}

		if (deletedCurrentDir) deletedSomething = true;

		if (!deletedCurrentDir) throw new FileNotFoundException();

		if (checkExist) {

			int deletedDirNumber = dirGui.getYOffsetFirst() + dirGui.getYpos0();
			int newDirNumber = deletedDirNumber;
			AkaiFatLfnDirectory parent = getParentDir();

			parentFiles.remove(deletedDirNumber);

			if (dirGui.getYOffsetFirst() + dirGui.getYpos0() > parentFiles.size() - 1) {

				if (dirGui.getYOffsetFirst() != 0) {
					dirGui.setYOffset0(dirGui.getYOffsetFirst() - 1);
				} else {
					if (dirGui.getYpos0() > 0) dirGui.setYPos0(dirGui.getYpos0() - 1);
				}
			}

			if (newDirNumber > parentFiles.size() - 1) newDirNumber--;

			if (parentFiles.size() == 0) {

				path.remove(path.size() - 1);
				refreshPath();

			} else {

				path.remove(path.size() - 1);

				path.add(parent.getEntry(parentFiles.get(newDirNumber).getName()));

				refreshPath();
			}
		}
		return deletedSomething;
	}

	public boolean deleteAllFiles(int dwGuiDelete) { // Only available in right
														// column, only removes
														// !dir

		AkaiFatLfnDirectory parentDirectory = null;
		try {
			parentDirectory = getParentDir();
		} catch (IOException e1) {
			return false;
		}
		System.out.println("parent directory " + parentDirectory);
		boolean success = false;

		Iterator<FsDirectoryEntry> iterator = parentDirectory.iterator();

		List<AkaiFatLfnDirectoryEntry> list = new ArrayList<AkaiFatLfnDirectoryEntry>();

		while (iterator.hasNext())
			list.add((AkaiFatLfnDirectoryEntry) iterator.next());

		for (AkaiFatLfnDirectoryEntry f : list) {

			if (!f.isDirectory()) {

				if (dwGuiDelete == 0 || f.getName().toUpperCase().endsWith(extensions[dwGuiDelete])) {

					try {

						f.getParent().remove(f.getName());

					} catch (IOException e) {
						// e.printStackTrace();
						success = false;
					}

					success = true;

				}
			}
		}

		return success;
	}

	@Override
	public boolean newFolder(String newDirName) {

		try {
			getDir().addDirectory(newDirName);
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public MpcFile newFile(String newFileName) {
		try {
			AkaiFatLfnDirectoryEntry newEntry = getDir().addFile(newFileName.replaceAll(" ", "_"));
			return new MpcFile(newEntry);
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("IOException. Prolly filename already exists.");
			return null;
		}
	}

	@Override
	public String getAbsolutePath() {
		String pathString = "";
		for (AkaiFatLfnDirectoryEntry a : path)
			pathString += "/" + a.getName();
		return device.getAbsolutePath() + pathString;
	}

	@Override
	public Store getStore() {
		return store;
	}

}