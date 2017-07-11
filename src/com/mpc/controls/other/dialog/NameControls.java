package com.mpc.controls.other.dialog;

import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import com.mpc.Util;
import com.mpc.controls.other.AbstractOtherControls;
import com.mpc.disk.AbstractDisk;
import com.mpc.file.aps.ApsSaver;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.components.Underline;

public class NameControls extends AbstractOtherControls {

	public void left() {
		init();
		if (param.equals("0")) return;
		super.left();
	}

	public void right() {
		init();
		if (Integer.parseInt(param) == gui.getNameGui().getNameLimit() - 1) return;
		super.right();
	}

	public void turnWheel(int j) {
		init();
		for (int i = 0; i < 16; i++) {
			if (param.equals("" + i)) {
				if (!nameGui.isNameBeingEdited()) nameGui.setNameBeingEdited(j > 0);
				initEditColors();
				nameGui.changeNameCharacter(i, j > 0);
			}
		}
	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen(ls.getPreviousScreenName(), ls.getPreviousPanel().getName());
			resetNameGui();
			break;
		}
	}

	public void keyEvent(KeyEvent e) {
		init();
		if (!(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_MINUS)) {

			for (int i = 0; i < akaiAsciiChar.length; i++) {
				if (e.getKeyChar() == akaiAsciiChar[i]) {
					int offset = 1;
					if (!nameGui.isNameBeingEdited()) offset = 0;
					if (gui.getPreviousKeyStroke() == KeyEvent.VK_LEFT
							|| gui.getPreviousKeyStroke() == KeyEvent.VK_RIGHT)
						offset = 0;
					int position = Integer.parseInt(param) + offset;
					if (position > nameGui.getNameLimit() - 1) position = nameGui.getNameLimit() - 1;
					nameGui.setName(Character.toString(akaiAsciiChar[i]), position);
					if (!nameGui.isNameBeingEdited()) nameGui.setNameBeingEdited(true);
					initEditColors();
					mainFrame.lookupTextField("" + position).setText(Character.toString(akaiAsciiChar[i]));
					mainFrame.lookupTextField("" + position).grabFocus();

				}
			}
		}

		if (e.getKeyCode() != KeyEvent.VK_SHIFT) gui.setPreviousKeyStroke(e.getKeyCode());

		if (e.getKeyCode() == KeyEvent.VK_F5 || e.getKeyCode() == KeyEvent.VK_ENTER) {

			if (nameGui.getParameterName().equals("outputfolder")) {
				gui.getD2DRecorderGui().setOutputFolder(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("directtodiskrecorder", "windowpanel");
			}
			
			if (nameGui.getParameterName().equals("saveallfile")) {
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("saveallfile", "windowpanel");
				return;
			}

			if (nameGui.getParameterName().equals("saveasound")) {
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("saveasound", "windowpanel");
				return;
			}

			if (nameGui.getParameterName().equals("savingpgm")) {
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("saveaprogram", "windowpanel");
				return;
			}

			if (nameGui.getParameterName().equals("savingaps")) {
				new ApsSaver(mpc, Util.getFileName(nameGui.getName()) + ".APS");
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				return;
			}

			if (nameGui.getParameterName().equals("savingmid")) {
				mainFrame.openScreen("saveasequence", "windowpanel");
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				return;
			}

			if (nameGui.getParameterName().contains("default")) {

				if (ls.getPreviousScreenName().equals("track")) {
					sequencer.setDefaultTrackName(nameGui.getName(), sequencer.getActiveTrackIndex());
					nameGui.setNameBeingEdited(false);
					ls.setLastFocus("name", "0");
					mainFrame.openScreen("sequencer", "mainpanel");
					return;
				}

				if (ls.getPreviousScreenName().equals("sequence")) {
					sequencer.setDefaultSequenceName(nameGui.getName());
					nameGui.setNameBeingEdited(false);
					ls.setLastFocus("name", "0");
					mainFrame.openScreen("sequencer", "mainpanel");
					return;
				}
			}

			if (ls.getPreviousScreenName().equals("saveapsfile")) {
				// gui.getDiskGui().setSaveApsName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("saveapsfile", "windowpanel");
			}

			if (ls.getPreviousScreenName().equals("keeporretry")) {

				sampler.getPreviewSound().setName(nameGui.getName());
				// sampler.getSounds().add(sampler.getPreviewSound());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("keeporretry", "windowpanel");

			}

			if (ls.getPreviousScreenName().equals("track")) {

				track.setName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("sequencer", "mainpanel");
				return;

			}

			if (ls.getPreviousScreenName().equals("saveasequence")) {

				// gui.getDiskGui().setSaveSequenceName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("saveasequence", "windowpanel");
			}

			if (ls.getPreviousScreenName().equals("sequence")) {

				mpcSequence.setName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("sequencer", "mainpanel");
				return;

			}

			if (ls.getPreviousScreenName().equals("midioutput")) {

				mpcSequence.setDeviceName(swGui.getDeviceNumber() + 1, nameGui.getName().substring(0, 8));

				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("sequencer", "mainpanel");
				return;
			}

			if (ls.getPreviousScreenName().equals("editsound")) {

				gui.getEditSoundGui().setNewName(nameGui.getName());

				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("editsound", "windowpanel");
				return;
			}

			if (ls.getPreviousScreenName().equals("sound")) {

				mpc.getSampler().getSound(gui.getSoundGui().getSoundIndex()).setName(nameGui.getName());

				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("sound", "windowpanel");
				return;
			}

			if (ls.getPreviousScreenName().equals("resample")) {

				gui.getSoundGui().setNewName(nameGui.getName());

				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("resample", "dialogpanel");
				return;
			}

			if (ls.getPreviousScreenName().equals("stereotomono")) {

				if (nameGui.getParameterName().equals("newlname")) {

					gui.getSoundGui().setNewLName(nameGui.getName());

				}

				if (nameGui.getParameterName().equals("newrname")) {

					gui.getSoundGui().setNewRName(nameGui.getName());
				}

				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("stereotomono", "dialogpanel");
				return;
			}

			if (nameGui.getParameterName().equals("programname")) {
				program.setName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("program", "windowpanel");
				return;
			}

			if (nameGui.getParameterName().equals("createnewprogram")) {
				gui.getSamplerWindowGui().setNewName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("program", "windowpanel");
				return;
			}

			if (nameGui.getParameterName().equals("autochrom")) {
				gui.getSamplerWindowGui().setNewName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("autochromaticassignment", "windowpanel");
				ls.setPreviousScreenName(gui.getSamplerGui().getPrevScreenName());
				ls.setPreviousPanel(ls.getMainPanel());
				return;
			}

			if (ls.getPreviousScreenName().equals("copysound")) {

				gui.getSoundGui().setNewName(nameGui.getName());
				nameGui.setNameBeingEdited(false);
				ls.setLastFocus("name", "0");
				mainFrame.openScreen("copysound", "dialogpanel");
				return;
			}

			if (nameGui.getParameterName().equals("rename")) {

				boolean success;

				String ext = AbstractDisk.splitName(directoryGui.getSelectedFile().getName())[1];

				if (ext.length() > 0) ext = "." + ext;

				success = mpc.getDisk().renameSelectedFile(nameGui.getName().toUpperCase().trim() + ext);

				if (!success) {

					mainFrame.popupPanel("File name exists !!", 120);

					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							try {
								Thread.sleep(1000);
								mainFrame.removePopup();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});

					ls.setPreviousScreenName("directory");
					ls.setPreviousPanel(ls.getWindowPanel());
					return;
				} else {
					mpc.getDisk().flush();
					mpc.getDisk().initFiles();
					nameGui.setNameBeingEdited(false);
					ls.setLastFocus("name", "0");
					mainFrame.openScreen("directory", "windowpanel");
					return;
				}

			}

			if (nameGui.getParameterName().equals("newfolder")) {

				boolean success = mpc.getDisk().newFolder(nameGui.getName().toUpperCase());

				if (success) {
					mpc.getDisk().flush();
					mpc.getDisk().initFiles();

					int counter = 0;

					for (int i = 0; i < mpc.getDisk().getFileNames().size(); i++) {
						if (mpc.getDisk().getFileName(i).equals(nameGui.getName().toUpperCase())) {
							gui.getDiskGui().setFileLoad(counter);
							if (counter > 4) {
								gui.getDirectoryGui().setYOffset1(counter - 4);
							} else {
								gui.getDirectoryGui().setYOffset1(0);
							}
							break;
						}
						counter++;
					}
					mainFrame.openScreen("directory", "windowpanel");
					ls.setPreviousScreenName("load");
					ls.setPreviousPanel(ls.getMainPanel());
					nameGui.setNameBeingEdited(false);
				}

				if (!success) {
					mainFrame.popupPanel("Folder name exists !!", 120);
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							try {
								Thread.sleep(1000);
								mainFrame.removePopup();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					});

				}
				return;
			}
		}

		drawUnderline();
	}

	public void nameKb() {

	}

	private void drawUnderline() {
		if (nameGui.isNameBeingEdited()) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String focus = mainFrame.getFocus(ls.getDialogPanel());
					if (focus == null) return;
					if (focus.length() != 1 && focus.length() != 2) return;
					Underline u = ls.getUnderline();
					for (int i = 0; i < 16; i++) {

						if (i == Integer.parseInt(focus)) {
							u.setState(i, true);
						} else {
							u.setState(i, false);
						}
					}
				}
			});
		}
	}

	private void initEditColors() {

		for (int i = 0; i < 16; i++) {
			mainFrame.lookupTextField("" + i).setOpaque(false);
			mainFrame.lookupTextField("" + i).setBackground(Bootstrap.lcdOff);
		}

		mainFrame.lookupTextField(mainFrame.getFocus(ls.getDialogPanel())).setForeground(Bootstrap.lcdOn);
	}

	private void resetNameGui() {
		nameGui.setNameBeingEdited(false);
		ls.setLastFocus("name", "0");
	}
}