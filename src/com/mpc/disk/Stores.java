package com.mpc.disk;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mpc.gui.Bootstrap;

public final class Stores {

	private final List<Store> stores = new ArrayList<Store>();

	public Stores() {
		
		FileSystem fsys = FileSystems.getDefault();
		System.out.println("\n\nfsys class " + fsys.getClass());
		Iterator<FileStore> iterator = fsys.getFileStores().iterator();
		while (iterator.hasNext()) {
			FileStore fs = iterator.next();
			if (fs.type().equals("msdos") || fs.type().equals("FAT")) {
				try {
					String volumeLabel = Bootstrap.osx ? "unknown" : fs.name();
					String path = fs.name();
					if (!Bootstrap.osx) {
						int colonIndex = fs.toString().indexOf(":");
						path = fs.toString().substring(colonIndex-1, colonIndex+1);
					}
					Store store = new Store(path, fs.getTotalSpace(), volumeLabel, true);
					stores.add(store);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	
		
		final String defaultJavaStoreLabel = "MPC2000XL";
		final String defaultJavaStorePath = Bootstrap.storesPath + defaultJavaStoreLabel;
		System.out.println("Stores path " + Bootstrap.storesPath);
		long size = 0;
		try {
			size = FileSystems.getDefault().getFileStores().iterator().next().getTotalSpace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stores.add(new Store(defaultJavaStorePath, size, defaultJavaStoreLabel, false));
		
//		for (Store s : stores)
//			printStore(s);
	}

	private void printStore(Store s) {
		System.out.println("path: " + s.path + "   size: " + s.totalSpace + "   label: "
				+ s.volumeLabel + "   raw: "+ s.raw);
	}

	public final class Store {
		
		public final boolean raw;
		public final String path;
		public final long totalSpace;
		public final String volumeLabel;

		public Store(String path, long totalSpace, String volumeLabel, boolean raw) {
			this.path = path;
			this.totalSpace = totalSpace;
			this.volumeLabel = volumeLabel;
			this.raw = raw;
			System.out.println("initializing store " + path + " with total space " + totalSpace);
		}

		@Override
		public String toString() {
			if (!raw) return volumeLabel + " / " + RawDisk.formatFileSize(totalSpace);
			return path + " / " + volumeLabel + " / " + RawDisk.formatFileSize(totalSpace);
		}
		
	}

	public Store getJavaStore(int store) {
		return getJavaStores().get(store);
	}
	
	public Store getRawStore(int store) {
		return getRawStores().get(store);
	}

	public List<Store> getRawStores() {
		List<Store> rawStores = new ArrayList<Store>();
		for (Store s : stores)
			if (s.raw) rawStores.add(s);
		return rawStores;
	}
	
	public List<Store> getJavaStores() {
		List<Store> javaStores = new ArrayList<Store>();
		for (Store s : stores)
			if (!s.raw) javaStores.add(s);
		return javaStores;
	}
	
}
