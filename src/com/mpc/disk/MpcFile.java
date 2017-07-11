package com.mpc.disk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import de.waldheinz.fs.ReadOnlyException;
import de.waldheinz.fs.fat.AkaiFatLfnDirectoryEntry;

/*
 *  Wrapper class for java.nio and native file access
 */

public class MpcFile {

	private final boolean raw;
	private final boolean java;

	private final AkaiFatLfnDirectoryEntry entry;
	private final File file;

	public MpcFile(Object fileObject) {

		raw = fileObject instanceof AkaiFatLfnDirectoryEntry;
		java = fileObject instanceof File;

		entry = raw ? (AkaiFatLfnDirectoryEntry) fileObject : null;
		file = java ? (File) fileObject : null;
	}

	public boolean isDirectory() {
		if (raw) return entry.isDirectory();
		else
			return getFile().isDirectory();
	}

	public String getName() {
		if (raw) {
			return entry.getName().replaceAll(" ", "").toUpperCase();
		} else
			return file.getName().toUpperCase();
	}

	public boolean setName(String s) {
		if (raw) {

			try {
				entry.setName(s.toUpperCase());
				return true;
			} catch (IOException e) {
				return false;
			}

		} else
			return file.renameTo(
					new File(file.getParentFile().getAbsolutePath() + "/" + s.replaceAll(" ", "").toUpperCase()));
	}

	public int length() {

		if (raw) {
			int length = 0;
			try {
				length = (int) entry.getFile().getLength();
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
			return length;
		} else
			return (int) file.length();
	}

	public void setFileData(byte[] data) {

		if (raw) {
			int toWrite = data.length;
			int sectorSize = 512;
			int written = 0;
			int sector = 0;
			byte[] block = new byte[sectorSize];
			try {
				while (toWrite - written > sectorSize) {
					System.arraycopy(data, sector * sectorSize, block, 0, sectorSize);
					entry.getFile().write(sector++ * sectorSize, ByteBuffer.wrap(block));
					written += sectorSize;
				}
				int remaining = toWrite - written;
				block = new byte[remaining];
				System.arraycopy(data, sector * sectorSize, block, 0, remaining);
				entry.getFile().write(sector * sectorSize, ByteBuffer.wrap(block));
				entry.getFile().flush();
			} catch (ReadOnlyException | IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean delete() {

		if (raw) {
			try {
//				System.out.println("entry get name " + entry.getName());
				entry.getParent().remove(entry.getName());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return file.delete();
		}

	}

	public File getFile() {
		if (raw) return RawDisk.entryToFile(entry);
		else
			return file;
	}

	public AkaiFatLfnDirectoryEntry getEntry() {
		if (!raw) return null;
		return entry;
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[length()];
		if (raw) {
			final int toRead = length();
			final int sectorSize = 512;
			List<Byte> byteList = new ArrayList<Byte>();
			int sector = 0;
			int read = 0;
			ByteBuffer bb = ByteBuffer.allocate(sectorSize);
			try {
				while (toRead - read > sectorSize) {
					bb.clear();
					entry.getFile().read(sector++ * sectorSize, bb);
					bb.position(0);
					for (int i = 0; i < sectorSize; i++)
						byteList.add(bb.get());
					read += sectorSize;
				}
				final int remaining = toRead - read;
				bb = ByteBuffer.allocate(remaining);
				entry.getFile().read(sector * sectorSize, bb);
				bb.position(0);
				for (int i = 0; i < remaining; i++)
					byteList.add(bb.get());

			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = byteList.get(i);
			return bytes;
		}

		try {
			bytes = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
}