package com.mpc.gui.disk.window;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.mpc.disk.AbstractDisk;
import com.mpc.disk.Disk;
import com.mpc.disk.MpcFile;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;

public class DirectoryObserver implements Observer {

	private DirectoryGui directoryGui;
	
	private Disk disk;
	
	private JTextField a0Field;
	private JTextField a1Field;
	private JTextField a2Field;
	private JTextField a3Field;
	private JTextField a4Field;
	private JTextField b0Field;
	private JTextField b1Field;
	private JTextField b2Field;
	private JTextField b3Field;
	private JTextField b4Field;

	private JTextField[] left;
	private JTextField[] right;
	
	private JLabel topLeftLabel;

	private MainFrame mainFrame;

	private JLabel a0Label;
	private JLabel a1Label;
	private JLabel a2Label;
	private JLabel a3Label;
	private JLabel a4Label;
	private JLabel b0Label;
	private JLabel b1Label;
	private JLabel b2Label;
	private JLabel b3Label;
	private JLabel b4Label;
	private JLabel c0Label;
	private JLabel c1Label;
	private JLabel c2Label;
	private JLabel c3Label;
	private JLabel c4Label;

	public DirectoryObserver(Disk disk, Gui gui) {
		this.disk = disk;
		((Observable)disk).addObserver(this);
		mainFrame = gui.getMainFrame();

		directoryGui = gui.getDirectoryGui();
		directoryGui.addObserver(this);

		a0Field = mainFrame.lookupTextField("a0");
		a1Field = mainFrame.lookupTextField("a1");
		a2Field = mainFrame.lookupTextField("a2");
		a3Field = mainFrame.lookupTextField("a3");
		a4Field = mainFrame.lookupTextField("a4");
		b0Field = mainFrame.lookupTextField("b0");
		b1Field = mainFrame.lookupTextField("b1");
		b2Field = mainFrame.lookupTextField("b2");
		b3Field = mainFrame.lookupTextField("b3");
		b4Field = mainFrame.lookupTextField("b4");

		JTextField[] tfa1 = { a0Field, a1Field, a2Field, a3Field, a4Field };
		JTextField[] tfa2 = { b0Field, b1Field, b2Field, b3Field, b4Field };
		
		left = tfa1;
		right = tfa2;

		topLeftLabel = mainFrame.lookupLabel("topleft");

		a0Label = mainFrame.lookupLabel("a0");
		a1Label = mainFrame.lookupLabel("a1");
		a2Label = mainFrame.lookupLabel("a2");
		a3Label = mainFrame.lookupLabel("a3");
		a4Label = mainFrame.lookupLabel("a4");
		b0Label = mainFrame.lookupLabel("b0");
		b1Label = mainFrame.lookupLabel("b1");
		b2Label = mainFrame.lookupLabel("b2");
		b3Label = mainFrame.lookupLabel("b3");
		b4Label = mainFrame.lookupLabel("b4");
		c0Label = mainFrame.lookupLabel("c0");
		c1Label = mainFrame.lookupLabel("c1");
		c2Label = mainFrame.lookupLabel("c2");
		c3Label = mainFrame.lookupLabel("c3");
		c4Label = mainFrame.lookupLabel("c4");
		
		updateLeft();
		updateRight();

		drawGraphicsLeft();
		drawGraphicsRight();
		
		updateFocus();
		initOpaqueness();
	}

	private void updateLeft() {
		JTextField[] left = { a0Field, a1Field, a2Field, a3Field, a4Field };
		directoryGui.displayLeftFields(left);
	}

	private void updateRight() {
		JTextField[] right = { b0Field, b1Field, b2Field, b3Field, b4Field };
		directoryGui.displayRightFields(right);
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		String parameter = (String) arg1;

		switch (parameter) {
		case "disk":
			updateLeft();
			updateRight();
			drawGraphicsLeft();
			drawGraphicsRight();
			break;

		case "right":
			updateRight();
			drawGraphicsRight();
			break;

		case "left":
			updateLeft();
			drawGraphicsLeft();
			drawGraphicsRight();
			break;

		case "focus":
			updateFocus();
			break;
		}
	}

	private void updateFocus() {
		directoryGui.refreshFocus(left, right);
	}

	private void initOpaqueness() {
		for (JTextField tf : left)
			if (!tf.hasFocus()) tf.setOpaque(false);
		for (JTextField tf : right)
			if (!tf.hasFocus()) tf.setOpaque(false);
	}

	private void drawGraphicsLeft() {

		topLeftLabel.setText("");
		a0Label.setText("");
		a1Label.setText("");
		a2Label.setText("");
		a3Label.setText("");
		a4Label.setText("");

		List<String> fc = directoryGui.getFirstColumn();

		String[] currentDirIcons = { "\u00EF", "\u00F1", "\u00F0" };
		String[] dirIcons = { "\u00EA", "\u00EB", "\u00EC" };
		String notRootDash = "\u00ED";

		String onlyDirIcon = "\u00F3";
		String rootIcon = "\u00EE";

		if (fc.size() == 0) {
			a0Label.setText(rootIcon);
			return;
		}

		topLeftLabel.setText(notRootDash);

		int offset = directoryGui.getYOffsetFirst();

		if (fc.size() - offset == 1) {
			if (fc.size() > 1) {
				a0Label.setText(currentDirIcons[2]);
			} else {
				a0Label.setText(onlyDirIcon);
			}
			return;
		}

		int lastVisibleFileNumber = fc.size() - offset - 1;

		MpcFile firstVisibleFile = directoryGui.getFileFromGrid(0, 0);
		MpcFile lastVisibleFile = null;

		if (lastVisibleFileNumber > 0) {
			if (lastVisibleFileNumber > 4) lastVisibleFileNumber = 4;
			lastVisibleFile = directoryGui.getFileFromGrid(0,
					lastVisibleFileNumber);
		}

		int visibleListLength = lastVisibleFileNumber + 1;

		if (fc.size() - offset == 2) {

			if (firstVisibleFile.getName().equals(disk.getDirectoryName())) {
				a0Label.setText(currentDirIcons[0]);
			} else {
				a0Label.setText(dirIcons[0]);
			}

			if (lastVisibleFile.getName().equals(disk.getDirectoryName())) {
				a1Label.setText(currentDirIcons[2]);
			} else {
				a1Label.setText(dirIcons[2]);
			}
			return;
		}

		JLabel[] aLabels = { a0Label, a1Label, a2Label, a3Label, a4Label };

		if (fc.size() - offset <= 4) {
			if (firstVisibleFile.getName().equals(disk.getDirectoryName())) {
				a0Label.setText(currentDirIcons[0]);
			} else {
				a0Label.setText(dirIcons[0]);
			}

			for (int i = 1; i < visibleListLength - 1; i++) {
				if (fc.get(i).equals(disk.getDirectoryName())) {
					aLabels[i].setText(currentDirIcons[1]);
				} else {
					aLabels[i].setText(dirIcons[1]);
				}
			}

			if (lastVisibleFile.getName().equals(disk.getDirectoryName())) {
				aLabels[visibleListLength - 1].setText(currentDirIcons[2]);
			} else {
				aLabels[visibleListLength - 1].setText(dirIcons[2]);
			}
			return;
		}

		if (fc.size() - offset >= 5) {
			if (firstVisibleFile.getName().equals(disk.getDirectoryName())) {
				if (firstVisibleFile.getName().equals(fc.get(0))) {
					a0Label.setText(currentDirIcons[0]);
				} else {
					a0Label.setText(currentDirIcons[1]);
				}
			} else {
				if (firstVisibleFile.getName().equals(fc.get(0))) {
					a0Label.setText(dirIcons[0]);
				} else {
					a0Label.setText(dirIcons[1]);
				}
			}


			for (int i = 1; i < visibleListLength - 1; i++) {
				if (fc.get(i+offset).equals(disk.getDirectoryName())) {
					aLabels[i].setText(currentDirIcons[1]);
				} else {
					aLabels[i].setText(dirIcons[1]);
				}
				if (i == 3) break;
			}

			if (lastVisibleFile.getName().equals(disk.getDirectoryName())) {
				if (lastVisibleFile.getName().equals(fc.get(fc.size() - 1))) {
					a4Label.setText(currentDirIcons[2]);
				} else {
					a4Label.setText(currentDirIcons[1]);
				}
			} else {
				if (lastVisibleFile.getName().equals(fc.get(fc.size() - 1))) {
					a4Label.setText(dirIcons[2]);
				} else {
					a4Label.setText(dirIcons[1]);
				}
			}
			return;
		}
	}

	private void drawGraphicsRight() {

		b0Label.setText("");
		b1Label.setText("");
		b2Label.setText("");
		b3Label.setText("");
		b4Label.setText("");

		if (disk.getParentFileNames().size() == 0) {
			a0Field.setText(padFileName(a0Field.getText(), "\u00DF"));
			b0Label.setText("\u00E0");
		}

		MpcFile f = directoryGui.getFileFromGrid(0,  0);
		
		if (directoryGui.getFirstColumn().size()
				- directoryGui.getYOffsetFirst() > 0
				&& directoryGui.getFileFromGrid(0, 0).getName().equals(
						disk.getDirectoryName())) {
			b0Label.setText("\u00E0");
			a0Field.setText(padFileName(a0Field.getText(), "\u00DF"));
		}

		if (directoryGui.getFirstColumn().size()
				- directoryGui.getYOffsetFirst() > 1
				&& directoryGui.getFileFromGrid(0, 1).getName().equals(
						disk.getDirectoryName())) {
			b1Label.setText("\u00E0");
			a1Field.setText(padFileName(a1Field.getText(), "\u00DF"));
		}

		if (directoryGui.getFirstColumn().size()
				- directoryGui.getYOffsetFirst() > 2
				&& directoryGui.getFileFromGrid(0, 2).getName().equals(
						disk.getDirectoryName())) {
			b2Label.setText("\u00E0");
			a2Field.setText(padFileName(a2Field.getText(), "\u00DF"));
		}

		if (directoryGui.getFirstColumn().size()
				- directoryGui.getYOffsetFirst() > 3
				&& directoryGui.getFileFromGrid(0, 3).getName().equals(
						disk.getDirectoryName())) {
			b3Label.setText("\u00E0");
			a3Field.setText(padFileName(a3Field.getText(), "\u00DF"));
		}

		if (directoryGui.getFirstColumn().size()
				- directoryGui.getYOffsetFirst() > 4
				&& directoryGui.getFileFromGrid(0, 4).getName().equals(
						disk.getDirectoryName())) {
			b4Label.setText("\u00E0");
			a4Field.setText(padFileName(a4Field.getText(), "\u00DF"));
		}

		if (directoryGui.getFileFromGrid(1, 0) != null
				&& directoryGui.getFileFromGrid(1, 0).isDirectory()) {
			if (directoryGui.getYOffsetSecond() == 0) {
				c0Label.setText("\u00E1");
			} else {
				c0Label.setText("\u00E6");
			}
		} else {
			if (directoryGui.getYOffsetSecond() == 0) {
				c0Label.setText("\u00E5");
			} else {
				c0Label.setText("\u00E3");
			}
		}

		if (directoryGui.getFileFromGrid(1, 1) != null
				&& directoryGui.getFileFromGrid(1, 1).isDirectory()) {
			c1Label.setText("\u00E6");
		} else {
			c1Label.setText("\u00E3");
		}

		if (directoryGui.getFileFromGrid(1, 2) != null
				&& directoryGui.getFileFromGrid(1, 2).isDirectory()) {
			c2Label.setText("\u00E6");
		} else {
			c2Label.setText("\u00E3");
		}

		if (directoryGui.getFileFromGrid(1, 3) != null
				&& directoryGui.getFileFromGrid(1, 3).isDirectory()) {
			c3Label.setText("\u00E6");
		} else {
			c3Label.setText("\u00E3");
		}

		if (directoryGui.getFileFromGrid(1, 4) != null
				&& directoryGui.getFileFromGrid(1, 4).isDirectory()) {
			if (directoryGui.getYOffsetSecond() + 5 == directoryGui
					.getSecondColumn().size()) {
				c4Label.setText("\u00E2");
			} else {
				c4Label.setText("\u00E6");
			}
		} else {
			if (directoryGui.getYOffsetSecond() + 5 == directoryGui
					.getSecondColumn().size()
					|| directoryGui.getSecondColumn().size() <= 5) {
				c4Label.setText("\u00E4");
			} else {
				c4Label.setText("\u00E3");
			}
		}
	}

	private String padFileName(String text, String string) {
		text = StringUtils.rightPad(text.trim(), 8, string);
		return text;
	}
}