package com.mpc.gui.disk.window;

import java.util.List;
import java.util.Observable;

import javax.swing.JTextField;

import com.mpc.Mpc;
import com.mpc.Util;
import com.mpc.disk.MpcFile;
import com.mpc.gui.disk.DiskGui;

public class DirectoryGui extends Observable {

	private Mpc mpc;
	private DiskGui diskGui;

	private int xPos = 0;
	private int yPos0 = 0;
	private int yOffset0 = 0;
	private int yOffset1 = 0;

	private String previousScreenName;

	public DirectoryGui(Mpc mpc, DiskGui diskGui) {
		this.mpc = mpc;
		this.diskGui = diskGui;
	}

	public void left() {
		String prevDirName = mpc.getDisk().getDirectoryName();
		if (xPos == 1) {
			xPos--;
			setChanged();
			notifyObservers("focus");
			return;
		} else {
			if (mpc.getDisk().moveBack()) {
				mpc.getDisk().initFiles();
				diskGui.setFileLoad(0);
				yPos0 = 0;

				for (int i = 0; i < mpc.getDisk().getParentFileNames().size(); i++) {
					if (mpc.getDisk().getParentFileNames().get(i).equals(mpc.getDisk().getDirectoryName())) {
						yOffset0 = i;
						break;
					}
				}

				for (int i = 0; i < mpc.getDisk().getFileNames().size(); i++) {
					if (mpc.getDisk().getFileNames().get(i).equals(prevDirName)) {
						yOffset1 = i;
						diskGui.setFileLoad(i);
						break;
					}
				}

				if (yOffset1 > mpc.getDisk().getFileNames().size() - 1) yOffset1 = 0;
				if (mpc.getDisk().getParentFileNames().size() == 0) yOffset0 = 0;

				setChanged();
				notifyObservers("disk");
				setChanged();
				notifyObservers("focus");
			}
		}
	}

	public void right() {
		if (xPos == 0) {
			xPos++;
			setChanged();
			notifyObservers("focus");
			return;

		} else {

			if (getSelectedFile() == null || mpc.getDisk().getFileNames().size() == 0
					|| !getSelectedFile().isDirectory())
				return;

			MpcFile f = getSelectedFile();
			if (f == null) return;
			if (!mpc.getDisk().moveForward(f.getName())) return;

			mpc.getDisk().initFiles();

			yPos0 = 0;
			yOffset1 = 0;
			diskGui.setFileLoad(0);

			for (int i = 0; i < mpc.getDisk().getParentFileNames().size(); i++) {
				if (mpc.getDisk().getParentFileNames().get(i).equals(f.getName())) {
					yOffset0 = i;
					break;
				}
			}

			yPos0 = 0;
			yOffset1 = 0;
			diskGui.setFileLoad(0);

			setChanged();
			notifyObservers("disk");
			setChanged();
			notifyObservers("focus");
		}
	}

	public void up() {
		if (xPos == 0) {

			if (yOffset0 == 0 && yPos0 == 0) return;

			if (yPos0 == 0) {
				yOffset0--;
				String newDirectoryName = mpc.getDisk().getParentFileNames().get(yOffset0);
				if (mpc.getDisk().moveBack()) {
					mpc.getDisk().initFiles();
					mpc.getDisk().moveForward(newDirectoryName);
					mpc.getDisk().initFiles();
					diskGui.setFileLoad(0);
					yOffset1 = 0;
					setChanged();
					notifyObservers("disk");
				}
				return;
			}

			String newDirectoryName = mpc.getDisk().getParentFileNames().get(yPos0 - 1 + yOffset0);
			if (mpc.getDisk().moveBack()) {
				mpc.getDisk().initFiles();
				mpc.getDisk().moveForward(newDirectoryName);
				mpc.getDisk().initFiles();
				yPos0--;
				yOffset1 = 0;
				diskGui.setFileLoad(0);
				setChanged();
				notifyObservers("disk");
				setChanged();
				notifyObservers("focus");
			}
			return;

		} else {
			if (diskGui.getFileLoad() == 0) return;
			int yPos = diskGui.getFileLoad() - yOffset1;
			if (yPos == 0) {
				yOffset1--;
				diskGui.setFileLoad(diskGui.getFileLoad() - 1);
				setChanged();
				notifyObservers("right");
				return;
			}
			diskGui.setFileLoad(diskGui.getFileLoad() - 1);
			setChanged();
			notifyObservers("focus");
		}

	}

	public void down() {
		if (xPos == 0) {
			if (mpc.getDisk().isRoot()) return;
			if (yOffset0 + yPos0 >= mpc.getDisk().getParentFileNames().size() - 1) return;

			if (yPos0 == 4) {
				yOffset0++;
				String newDirectoryName = mpc.getDisk().getParentFileNames().get(4 + yOffset0);
				if (mpc.getDisk().moveBack()) {
					mpc.getDisk().moveForward(newDirectoryName);
					mpc.getDisk().initFiles();
					diskGui.setFileLoad(0);
					yOffset1 = 0;
					setChanged();
					notifyObservers("disk");
				}
				return;
			}

			String newDirectoryName = mpc.getDisk().getParentFileNames().get(yPos0 + 1 + yOffset0);
			if (mpc.getDisk().moveBack()) {
				mpc.getDisk().initFiles();
				mpc.getDisk().moveForward(newDirectoryName);
				mpc.getDisk().initFiles();
				yPos0++;
				yOffset1 = 0;
				diskGui.setFileLoad(0);
				setChanged();
				notifyObservers("disk");
				setChanged();
				notifyObservers("focus");
			}
			return;
		} else {

			if (diskGui.getFileLoad() == mpc.getDisk().getFileNames().size() - 1) return;
			if (mpc.getDisk().getFileNames().size() == 0) return;
			int yPos = diskGui.getFileLoad() - yOffset1;
			if (yPos == 4) {
				yOffset1++;
				diskGui.setFileLoad(diskGui.getFileLoad() + 1);
				setChanged();
				notifyObservers("right");
				return;
			}
			diskGui.setFileLoad(diskGui.getFileLoad() + 1);
			setChanged();
			notifyObservers("focus");
		}
	}

	public MpcFile getSelectedFile() {
		int yPos = yPos0;
		if (xPos == 1) yPos = diskGui.getFileLoad() - yOffset1;
		return getFileFromGrid(xPos, yPos);
	}

	public MpcFile getFileFromGrid(int x, int y) {
		MpcFile f = null;
		if (x == 0 && mpc.getDisk().getParentFileNames().size() > y + yOffset0)
			f = mpc.getDisk().getParentFile(y + yOffset0);
		if (x == 1 && mpc.getDisk().getFileNames().size() > y + yOffset1) f = mpc.getDisk().getFile(y + yOffset1);
		return f;
	}

	public void displayLeftFields(JTextField[] tfa) {
		for (int i = 0; i < 5; i++)
			if (i + yOffset0 > mpc.getDisk().getParentFileNames().size() - 1) {
				tfa[i].setText("");
			} else {
				tfa[i].setText(mpc.getDisk().getParentFileNames().get(i + yOffset0));
			}
		if (mpc.getDisk().isRoot()) tfa[0].setText("ROOT");
	}

	public void displayRightFields(JTextField[] tfa) {
		for (int i = 0; i < 5; i++)
			if (i + yOffset1 > mpc.getDisk().getFileNames().size() - 1) {
				tfa[i].setText("");
			} else {
				String fileName = mpc.getDisk().getFileName(i + yOffset1);
				String name = Util.padRightSpace(Util.splitName(fileName)[0], 16);
				String ext = Util.splitName(fileName)[1];
				if (ext.length() > 0) ext = "." + ext;

				tfa[i].setText(name + ext);
			}
	}

	public void refreshFocus(JTextField[] tfa0, JTextField[] tfa1) {
		if (xPos == 0) tfa0[yPos0].grabFocus();
		if (xPos == 1) tfa1[diskGui.getFileLoad() - yOffset1].grabFocus();
	}

	public int getYOffsetFirst() {
		return yOffset0;
	}

	public int getYOffsetSecond() {
		return yOffset1;
	}

	public List<String> getFirstColumn() {
		return mpc.getDisk().getParentFileNames();
	}

	public List<String> getSecondColumn() {
		return mpc.getDisk().getFileNames();
	}

	public int getXPos() {
		return xPos;
	}

	public int getYpos0() {
		return yPos0;
	}

	public void setPreviousScreenName(String s) {
		previousScreenName = s;
	}

	public String getPreviousScreenName() {
		return previousScreenName;
	}

	public void findYOffset0() {
		for (int i = 0; i < mpc.getDisk().getParentFileNames().size(); i++) {
			if (mpc.getDisk().getParentFile(i).getName().equals(mpc.getDisk().getDirectoryName())) {
				yOffset0 = i;
				yPos0 = 0;
				break;
			}
		}
	}

	public void setYOffset0(int i) {
		yOffset0 = i;
	}

	public void setYOffset1(int i) {
		if (i < 0) return;
		yOffset1 = i;
	}

//	public void setXPos(int i) {
//		xPos = i;
//	}
//
	public void setYPos0(int i) {
		yPos0 = i;
	}
}