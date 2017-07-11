package com.mpc.gui.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.mpc.gui.Bootstrap;
import com.mpc.gui.MainFrame;
import com.mpc.gui.sampler.MixerGui;

public class MixerStrip {

	private String[] abcd = { "A", "B", "C", "D" };

	private String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p" };

	private List<JComponent> mixerStrip;
	private int columnNumber;

	private JTextField tf0;
	private JTextField tf1;
	private JTextField tf2;
	private JTextField tf3;
	private JTextField tf4;

	private int[] xPos0indiv = { 5, 20, 35, 50, 65, 80, 95, 110, 125, 140, 155,
			170, 185, 200, 215, 230 };
	
	private int[] xPos1indiv = { 12, 27, 42, 57, 72, 87, 102, 117, 132, 147,
			162, 177, 192, 207, 222, 237 };
	
	private int yPos0indiv = 0;
	
	private int yPos1indiv = 4;
	
	private int[] xPos0fx = { 5, 20, 35, 50, 65, 80, 95, 110, 125, 140, 155,
			170, 185, 200, 215, 230 };
	
	private int[] xPos1fx = { 11, 26, 41, 56, 71, 86, 101, 116, 131, 146, 161,
			176, 191, 206, 221, 236 };
	
	private int yPos0fx = 3;
	
	private int yPos1fx = 3;

	private VerticalBar verticalBar;
	private Knob knob;
	private SelectedMixerTopBar selectedMixerTopBar;
	private SelectedMixerBottomBar selectedMixerBottomBar;

	private int selection = -1; // -1 = not selected, 0 = top selected, 1 =
								// bottom selected

	private MainFrame mainFrame = Bootstrap.getGui().getMainFrame();
	private MixerGui mixGui = Bootstrap.getGui().getMixerGui();

	JTextField[] jta;

	public MixerStrip(int columnNumber, int bank) {
		this.columnNumber = columnNumber;
		mixerStrip = new ArrayList<JComponent>();
		verticalBar = Bootstrap.getGui().getMainFrame().getLayeredScreen()
				.getVerticalBarsMixer()[columnNumber];

		verticalBar.setVisible(true);

		knob = Bootstrap.getGui().getMainFrame().getLayeredScreen()
				.getKnobs()[columnNumber];
		selectedMixerTopBar = Bootstrap.getGui().getMainFrame()
				.getLayeredScreen().getSelectedMixerTopBars()[columnNumber];
		selectedMixerBottomBar = Bootstrap.getGui().getMainFrame()
				.getLayeredScreen().getSelectedMixerBottomBars()[columnNumber];

		mixerStrip.add(verticalBar);
		mixerStrip.add(knob);
		mixerStrip.add(selectedMixerTopBar);
		mixerStrip.add(selectedMixerBottomBar);

		tf0 = mainFrame.lookupTextField(letters[columnNumber] + "0");
		tf1 = mainFrame.lookupTextField(letters[columnNumber] + "1");
		tf2 = mainFrame.lookupTextField(letters[columnNumber] + "2");
		tf3 = mainFrame.lookupTextField(letters[columnNumber] + "3");
		tf4 = mainFrame.lookupTextField(letters[columnNumber] + "4");
		
		tf1.setOpaque(false);
		tf2.setOpaque(false);
		
		JTextField[] jtaTemp = { tf0, tf1, tf2, tf3, tf4 };
		jta = jtaTemp;

		tf2.setText(abcd[bank]);
		initFields();
		setColors();
	}

	public List<JComponent> getMixerStrip() {
		return mixerStrip;
	}

	public void setValueA(int i) {
		knob.setValue(i);
	}

	public void setValueB(int i) {
		verticalBar.setValue(i);
	}

	public void initFields() {
		
		if (mixGui.getTab() == 0) {
			knob.setVisible(true);
			tf0.setVisible(true);
			tf0.setText("");
			tf0.setOpaque(false);
			tf1.setVisible(false);
			tf2.setVisible(true);
			tf3.setVisible(true);
			tf4.setVisible(true);
		}
		if (mixGui.getTab() == 1) {
			knob.setVisible(false);
			tf0.setVisible(true);
			tf1.setVisible(true);
			tf0.setLocation((xPos0indiv[columnNumber]-1) * 2, yPos0indiv * 2);
			tf1.setLocation((xPos1indiv[columnNumber]-1) * 2, yPos1indiv * 2);
		}
		
		if (mixGui.getTab() == 2) {
			knob.setVisible(false);
			tf0.setVisible(true);
			tf1.setVisible(true);
			tf0.setLocation(xPos0fx[columnNumber] * 2, yPos0fx * 2);
			tf1.setLocation(xPos1fx[columnNumber] * 2, yPos1fx * 2);
		}
	}

	public void setColors() {
		if (selection == -1) {
			for (JTextField tf : jta) {
				tf.setForeground(Bootstrap.lcdOn);
				tf.setBackground(Bootstrap.lcdOff);
			}
			selectedMixerTopBar.setVisible(false);
			selectedMixerBottomBar.setVisible(false);
			knob.setColor(Bootstrap.lcdOn);
			verticalBar.setColor(Bootstrap.lcdOn);
		}
		if (selection == 0) {
			for (JTextField tf : jta) {
				tf.setForeground(Bootstrap.lcdOn);
				tf.setBackground(Bootstrap.lcdOff);
			}
			jta[0].setForeground(Bootstrap.lcdOff);
			jta[0].setBackground(Bootstrap.lcdOn);
			jta[1].setForeground(Bootstrap.lcdOff);
			jta[1].setBackground(Bootstrap.lcdOn);

			jta[2].setForeground(Bootstrap.lcdOn);
			jta[2].setBackground(Bootstrap.lcdOff);
			jta[3].setForeground(Bootstrap.lcdOn);
			jta[3].setBackground(Bootstrap.lcdOff);
			jta[4].setForeground(Bootstrap.lcdOn);
			jta[4].setBackground(Bootstrap.lcdOff);

			selectedMixerTopBar.setVisible(true);
			selectedMixerBottomBar.setVisible(false);
			knob.setColor(Bootstrap.lcdOff);
			verticalBar.setColor(Bootstrap.lcdOn);
		}

		if (selection == 1) {
			for (JTextField tf : jta) {
				tf.setForeground(Bootstrap.lcdOn);
				tf.setBackground(Bootstrap.lcdOff);
			}
			jta[0].setForeground(Bootstrap.lcdOn);
			jta[0].setBackground(Bootstrap.lcdOff);
			jta[1].setForeground(Bootstrap.lcdOn);
			jta[1].setBackground(Bootstrap.lcdOff);

			jta[2].setForeground(Bootstrap.lcdOff);
			jta[2].setBackground(Bootstrap.lcdOn);
			jta[3].setForeground(Bootstrap.lcdOff);
			jta[3].setBackground(Bootstrap.lcdOn);
			jta[4].setForeground(Bootstrap.lcdOff);
			jta[4].setBackground(Bootstrap.lcdOn);
			selectedMixerTopBar.setVisible(false);
			selectedMixerBottomBar.setVisible(true);
			knob.setColor(Bootstrap.lcdOn);
			verticalBar.setColor(Bootstrap.lcdOff);
		}
	}

	public void setSelection(int i) {
		selection = i;
		setColors();
	}

	public void setValueAString(String string) {
		
		if (mixGui.getTab() == 1) {
			if (string.length() == 1) {
				
				jta[0].setText(string);
				jta[0].setLocation((xPos0indiv[columnNumber] + 2)*2, (yPos0indiv + 2)*2);
				jta[1].setText("");
			}
			
			if (string.length() == 2) {
				jta[0].setLocation(xPos0indiv[columnNumber] *2, yPos0indiv *2);
				jta[0].setText(string.substring(0, 1));
				jta[1].setText(string.substring(1, 2));
			}
		}
		
		if (mixGui.getTab() == 2) {
				jta[0].setText(string.substring(0, 1));
				jta[1].setText(string.substring(1, 2));
		}
	}
}
