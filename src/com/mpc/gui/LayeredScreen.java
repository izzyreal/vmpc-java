package com.mpc.gui;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mpc.gui.components.Background;
import com.mpc.gui.components.BlinkLabel;
import com.mpc.gui.components.EnvGraph;
import com.mpc.gui.components.FineWaveform;
import com.mpc.gui.components.HorizontalBar;
import com.mpc.gui.components.Knob;
import com.mpc.gui.components.SelectedMixerBottomBar;
import com.mpc.gui.components.SelectedMixerTopBar;
import com.mpc.gui.components.TwoDots;
import com.mpc.gui.components.Underline;
import com.mpc.gui.components.VerticalBar;
import com.mpc.gui.components.Waveform;
import com.mpc.gui.sequencer.SelectedEventBar;

@SuppressWarnings("serial")
public class LayeredScreen extends JLayeredPane {

	public JPanel mainPanel = new JPanel();
	public JPanel windowPanel = new JPanel();
	public JPanel dialogPanel = new JPanel();
	private JPanel dialog2Panel = new JPanel();
	public JPanel popupPanel = new JPanel();

	private ArrayList<String[]> lastFocus;

	private MainFrame mainFrame;

	private FunctionBox fb;

	private JPanel currentPanel;
	private String currentScreenName = "";

	private String previousScreenName = "";
	private JPanel previousPanel;
	private EnvGraph envGraph;

	private TwoDots twoDots;

	private HorizontalBar[] horizontalBarsStepEditor;
	private SelectedEventBar[] selectedEventBarsStepEditor;
	private HorizontalBar[] horizontalBarsTempoChangeEditor;

	private Underline underline;

	private String previousViewModeText;
	private String previousFromNoteText;
	private String firstTextField;
	private VerticalBar[] verticalBarsMixer;
	private SelectedMixerTopBar[] selectedMixerTopBars;
	private SelectedMixerBottomBar[] selectedMixerBottomBars;
	private Knob[] knobs;
	private Waveform waveform;

	private Background currentBackground;
	private FineWaveform fineWaveform;

	public void init(MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		lastFocus = new ArrayList<String[]>();

		setDefaults(this, -1);

		mainPanel.setName("mainpanel");
		setDefaults(mainPanel, 0);

		windowPanel.setName("windowpanel");
		setDefaults(windowPanel, 1);

		dialogPanel.setName("dialogpanel");
		setDefaults(dialogPanel, 2);

		dialog2Panel.setName("dialog2panel");
		setDefaults(dialog2Panel, 3);

		popupPanel.setName("popuppanel");
		setDefaults(popupPanel, 4);

		JLabel pixelGridLabel = new JLabel();
		ImageIcon pixelGridIcon = new ImageIcon(Bootstrap.resPath + "pixelgrid.png");
		pixelGridLabel.setIcon(pixelGridIcon);
		setDefaults(pixelGridLabel, 4);
	}

	private void setDefaults(JComponent j, int i) {
		j.setLayout(null);
		j.setLocation(0, 0);
		j.setSize(496, 120);
		j.setVisible(true);
		j.setOpaque(false);

		if (i != -1) add(j, new Integer(i), 0);

	}

	public void createPopup(String text, int textXPos) {

		JLabel popupLabel = new JLabel(text);
		popupLabel.setName("popup_label");
		popupLabel.setSize(350, 16);
		popupLabel.setLocation(textXPos, 46);
		popupLabel.setForeground(Bootstrap.lcdOff);

		Background background = new Background();
		background.setBackgroundName("popup");
		background.selectPanel = "popuppanel";
		background.setSize(248 * 2, 60 * 2);
		background.setLocation(0, 0);
		background.setName("popup_bg");
		background.setVisible(true);
		currentBackground = background;
		popupPanel.add(popupLabel);
		popupPanel.add(background);
	}

	public Background getCurrentBackground() {
		return currentBackground;
	}

	public void removeCurrentBackground() {
		currentPanel.remove(currentBackground);
	}

	public void setCurrentBackground(String s) {
		currentBackground = new Background();
		currentBackground.setBackgroundName(s);
		currentBackground.selectPanel = currentPanel.getName();
		currentBackground.setSize(248 * 2, 60 * 2);
		currentBackground.setLocation(0, 0);
		currentBackground.setVisible(true);
		currentPanel.add(currentBackground);
		currentPanel.repaint();
	}

	public void removePopup() {

		for (Component c : popupPanel.getComponents()) {
			if ((c.getName() == "popup_bg") || (c.getName() == "popup_label")) {
				popupPanel.remove(c);
			}
		}
	}

	public void setPopupText(String text) {
		for (Component c : popupPanel.getComponents()) {
			if (c.getName() == "popup_label") {
				((JLabel) c).setText(text);
			}
		}

	}

	public void removeLabels() {

		for (Component c : getComponents()) {
			if ((c instanceof JLabel) && getLayer(c) == 0) {
				remove(c);
			}
		}
	}

	public void removeTextFields() {

		for (Component c : getComponents()) {
			if ((c instanceof JTextField) && getLayer(c) == 0) {
				remove(c);
			}
		}
	}

	void openScreen(String screenName, JPanel panel, String panelName)
			throws FileNotFoundException, IOException, ParseException {
		previousPanel = currentPanel;
		previousScreenName = currentScreenName;
		currentPanel = panel;
		currentScreenName = screenName;

		Widget w = new Widget(this);

		JSONArray jsonArrayParameterNames = getJsonArray(screenName, "parameters", panelName);
		int counter = 0;
		firstTextField = null;
		for (Object o : jsonArrayParameterNames) {
			ArrayList<JComponent> a = w.buildParameter(screenName, o.toString(), panelName);
			for (JComponent jc : a) {
				panel.add(jc);
				if (counter == 0) {
					if (jc instanceof JTextField) {
						firstTextField = jc.getName();
					}
				}
			}
			counter++;
		}

		JSONArray jsonArrayNotifierNames = getJsonArray(screenName, "infowidgets", panelName);
		if (jsonArrayNotifierNames.size() != 0) {
			for (Object o : jsonArrayNotifierNames) {
				ArrayList<JComponent> a = w.buildInfoWidget(screenName, o.toString(), panelName);
				if (a == null) continue;
				for (JComponent jc : a) {
					jc.setLayout(null);
					panel.add(jc);
				}
			}
		}

		returnToLastFocus();

		Background background = new Background();
		background.setBackgroundName(screenName);
		background.selectPanel = panelName;
		background.setSize(248 * 2, 60 * 2);
		background.setLocation(0, 0);
		background.setVisible(true);
		// background.startAnimation();
		currentBackground = background;
		if (screenName.equals("sequencer")) {
			BlinkLabel bl = new BlinkLabel("SOLO");
			bl.setLocation(266, 102);
			bl.setForeground(Bootstrap.lcdOn);
			// bl.setBackground(Bootstrap.lcdOff);
			bl.setSize(50, 20);
			bl.setVisible(true);
			bl.setBlinking(false);
			bl.setName("soloblink");
			panel.add(bl);
		}

		JSONArray fbLabels = getJsonArray(screenName, "fblabels", panelName);
		JSONArray fbTypes = getJsonArray(screenName, "fbtypes", panelName);
		if (fb != null) panel.remove(fb);
		fb = new FunctionBox(screenName, panelName, fbLabels, fbTypes);
		fb.setVisible(true);

		horizontalBarsTempoChangeEditor = new HorizontalBar[4];
		horizontalBarsStepEditor = new HorizontalBar[4];
		selectedEventBarsStepEditor = new SelectedEventBar[4];

		verticalBarsMixer = new VerticalBar[16];
		selectedMixerTopBars = new SelectedMixerTopBar[16];
		selectedMixerBottomBars = new SelectedMixerBottomBar[16];
		knobs = new Knob[16];

		underline = new Underline();

		for (int i = 0; i < 4; i++) {
			horizontalBarsTempoChangeEditor[i] = new HorizontalBar(50);
			horizontalBarsTempoChangeEditor[i].setSize(160, 10);
			horizontalBarsTempoChangeEditor[i].setLocation(382, 26 + (i * 18));
			horizontalBarsTempoChangeEditor[i].setVisible(false);

			horizontalBarsStepEditor[i] = new HorizontalBar(50);
			horizontalBarsStepEditor[i].setSize(160, 10);
			horizontalBarsStepEditor[i].setLocation(396, 26 + (i * 18));
			horizontalBarsStepEditor[i].setVisible(false);

			selectedEventBarsStepEditor[i] = new SelectedEventBar();
			selectedEventBarsStepEditor[i].setSize(496, 18);
			selectedEventBarsStepEditor[i].setLocation(0, 22 + (i * 18));
			selectedEventBarsStepEditor[i].setVisible(false);

			panel.add(horizontalBarsStepEditor[i]);
			panel.add(selectedEventBarsStepEditor[i]);
			panel.add(horizontalBarsTempoChangeEditor[i]);
		}

		for (int i = 0; i < 16; i++) {

			verticalBarsMixer[i] = new VerticalBar();
			verticalBarsMixer[i].setSize(10, 75);
			verticalBarsMixer[i].setLocation(24 + (i * 30), 32);
			verticalBarsMixer[i].setVisible(false);

			selectedMixerTopBars[i] = new SelectedMixerTopBar();
			selectedMixerTopBars[i].setSize(28, 26);
			selectedMixerTopBars[i].setLocation(8 + (i * 30), 0);
			selectedMixerTopBars[i].setVisible(false);

			selectedMixerBottomBars[i] = new SelectedMixerBottomBar();
			selectedMixerBottomBars[i].setSize(28, 80);
			selectedMixerBottomBars[i].setLocation(8 + (i * 30), 30);
			selectedMixerBottomBars[i].setVisible(false);

			knobs[i] = new Knob();
			knobs[i].setSize(26, 26);
			knobs[i].setLocation(10 + (i * 30), 2);
			knobs[i].setVisible(false);

			panel.add(verticalBarsMixer[i]);
			panel.add(knobs[i]);
			panel.add(selectedMixerTopBars[i]);
			panel.add(selectedMixerBottomBars[i]);
		}

		twoDots = new TwoDots();
		twoDots.setSize(496, 50);
		twoDots.setLocation(0, 0);
		twoDots.setVisible(false);

		waveform = new Waveform();
		waveform.setSize(490, 54);
		waveform.setLocation(2, 42);
		waveform.setVisible(true);

		fineWaveform = new FineWaveform();
		fineWaveform.setSize(218, 74);
		fineWaveform.setLocation(46, 32);
		fineWaveform.setVisible(true);

		underline.setSize(((6 * 16) + 1) * 2, 2);
		underline.setLocation(106 * 2, 27 * 2);
		underline.setVisible(true);

		envGraph = new EnvGraph(new int[][] {});
		envGraph.setSize(248 * 2, 60 * 2);
		windowPanel.add(envGraph, 0);

		dialogPanel.add(fineWaveform);
		dialogPanel.add(waveform);
		dialogPanel.add(twoDots);
		dialogPanel.add(underline);

		panel.add(fb);
		panel.add(background);
	}

	public void returnToLastFocus() {
		int focusCounter = 0;
		for (String[] stringArray : lastFocus) {

			if (stringArray[0].equals(currentScreenName)) {
				focusCounter++;
				mainFrame.setFocus(stringArray[1], currentPanel);
			}
		}
		if (focusCounter == 0) {
			String[] sa = { currentScreenName, firstTextField };
			lastFocus.add(sa);
			mainFrame.setFocus(firstTextField, currentPanel);
		}
	}

	public void redrawEnvGraph(int attack, int decay) {
		int[] line1 = { 75, 43, (int) (75 + (attack * 0.17)), 24 };
		int[] line2 = { (int) (119 - (decay * 0.17)), 24, 119, 43 };
		int[] line3 = { (int) (75 + (attack * 0.17)), 24, (int) (119 - (decay * 0.17)), 24 };
		int[][] lines = { line1, line2, line3 };

		envGraph.setCoordinates(lines);
	}

	public JSONArray getJsonArray(String screenName, String whichKey, String panelName)
			throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONArray a = new JSONArray();

		JSONObject allJsonScreens = (JSONObject) parser.parse(new FileReader(Bootstrap.resPath + panelName + ".json"));

		for (Object jsonScreen : allJsonScreens.keySet()) {

			if (((String) jsonScreen).equals(screenName)) {
				JSONObject panel = (JSONObject) allJsonScreens.get((String) jsonScreen);

				for (Object unknownElement : panel.keySet()) {
					if (((String) unknownElement).equals(whichKey)) {
						JSONArray element = (JSONArray) panel.get(((String) unknownElement));
						a = element;
					}
				}
			}
		}
		return a;
	}

	public void setLastFocus(String screenName, String tfName) {
		for (String[] string : lastFocus) {
			if (string[0].equals(screenName)) {
				string[1] = tfName;
			}
		}
	}

	public String getLastFocus(String screenName) {
		String tfName = null;
		for (String[] string : lastFocus) {
			if (string[0].equals(screenName)) {
				tfName = string[1];
			}
		}
		return tfName;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getWindowPanel() {
		return windowPanel;
	}

	public JPanel getCurrentPanel() {
		return currentPanel;
	}

	public void setCurrentScreenName(String screenName) {
		currentScreenName = screenName;
	}

	public String getCurrentScreenName() {
		return currentScreenName;
	}

	public void setPreviousScreenName(String screenName) {
		previousScreenName = screenName;
	}

	public String getPreviousScreenName() {
		return previousScreenName;
	}

	public void setPreviousPanel(JPanel panel) {
		previousPanel = panel;
	}

	public JPanel getPreviousPanel() {
		return previousPanel;
	}

	public void setCurrentPanel(JPanel panel) {
		currentPanel = panel;
	}

	public JPanel getPopupPanel() {
		return popupPanel;
	}

	public EnvGraph getEnvGraph() {
		return envGraph;
	}

	public String getPreviousFromNoteText() {
		return previousFromNoteText;
	}

	public void setPreviousFromNoteText(String text) {
		previousFromNoteText = text;
	}

	public void setPreviousViewModeText(String text) {
		previousViewModeText = text;
	}

	public String getPreviousViewModeText() {
		return previousViewModeText;
	}

	public JTextField lookupTextField(String name) {
		return mainFrame.lookupTextField(name);

	}

	public HorizontalBar[] getHorizontalBarsTempoChangeEditor() {
		return horizontalBarsTempoChangeEditor;
	}

	public HorizontalBar[] getHorizontalBarsStepEditor() {
		return horizontalBarsStepEditor;
	}

	public VerticalBar[] getVerticalBarsMixer() {
		return verticalBarsMixer;
	}

	public SelectedEventBar[] getSelectedEventBarsStepEditor() {
		return selectedEventBarsStepEditor;
	}

	public FunctionBox getFunctionBoxes() {
		return fb;
	}

	public Knob[] getKnobs() {
		return knobs;
	}

	public SelectedMixerTopBar[] getSelectedMixerTopBars() {
		return selectedMixerTopBars;
	}

	public SelectedMixerBottomBar[] getSelectedMixerBottomBars() {
		return selectedMixerBottomBars;
	}

	public void drawFunctionBoxes(String screenName) {
		String panelName = currentPanel.getName();
		currentPanel.remove(fb);
		try {
			JSONArray fbLabels = getJsonArray(screenName, "fblabels", panelName);
			JSONArray fbTypes = getJsonArray(screenName, "fbtypes", panelName);
			fb = new FunctionBox(screenName, panelName, fbLabels, fbTypes);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		fb.setVisible(true);
		currentPanel.add(fb, 2);
		currentPanel.repaint();
	}

	public JPanel getDialogPanel() {
		return dialogPanel;
	}

	public Underline getUnderline() {
		return underline;
	}

	public TwoDots getTwoDots() {
		return twoDots;
	}

	public Waveform getWaveform() {
		return waveform;
	}

	public FineWaveform getFineWaveform() {
		return fineWaveform;
	}

	public String getFirstField() {
		String firstField = "";
		List<String> allFields = new ArrayList<String>();
		for (Component c : mainFrame.getAllComponents(currentPanel)) {
			if (c instanceof JTextField && c.isVisible() && !c.getName().equals("dummy")) {
				allFields.add(c.getName());
				break;
			}
		}
		firstField = allFields.get(0);
		return firstField;
	}

	public String getLastField() {
		String lastField = "";
		List<String> allFields = new ArrayList<String>();
		for (Component c : mainFrame.getAllComponents(currentPanel)) {
			if (c instanceof JTextField && c.isVisible() && !c.getName().equals("dummy")) {
				allFields.add(c.getName());
			}
		}
		lastField = allFields.get(allFields.size() - 1);
		return lastField;
	}

	public JPanel getDialog2Panel() {
		return dialog2Panel;
	}

	public JTextField findAbove(JTextField tf) {
		JTextField found = null;
		final int margin = 160;

		int xcenter = tf.getLocation().x + (tf.getSize().width / 2);

		JLabel tflabel = mainFrame.lookupLabel(tf.getName());

		if (tflabel != null)
			xcenter = tflabel.getLocation().x + (tflabel.getSize().width / 2) + (tf.getSize().width / 2);

		int y = tf.getLocation().y;

		for (int i = currentPanel.getComponentCount() - 1; i >= 0; i--) {
			Component c = currentPanel.getComponent(i);
			if (c instanceof JTextField && c.isFocusable() && c.isVisible()) {
				int candidatexcenter = c.getLocation().x + (c.getSize().width / 2);

				JLabel clabel = mainFrame.lookupLabel(c.getName());

				if (clabel != null)
					candidatexcenter = clabel.getLocation().x + (clabel.getSize().width / 2) + (c.getSize().width / 2);

				int candidatey = c.getLocation().y;
				if (Math.abs(candidatexcenter - xcenter) < margin && candidatey < y) {
					found = (JTextField) c;
					break;
				}
			}
		}
		return found;
	}

	public JTextField findBelow(JTextField tf) {
		JTextField found = null;

		final int margin = 160;

		int xcenter = tf.getLocation().x + (tf.getSize().width / 2);

		JLabel tflabel = mainFrame.lookupLabel(tf.getName());

		if (tflabel != null)
			xcenter = tflabel.getLocation().x + (tflabel.getSize().width / 2) + (tf.getSize().width / 2);

		int y = tf.getLocation().y;

		for (int i = 0; i < currentPanel.getComponentCount(); i++) {
			Component c = currentPanel.getComponent(i);
			if (c instanceof JTextField && c.isFocusable() && c.isVisible()) {
				int candidatexcenter = c.getLocation().x + (c.getSize().width / 2);
				JLabel clabel = mainFrame.lookupLabel(c.getName());

				if (clabel != null)
					candidatexcenter = clabel.getLocation().x + (clabel.getSize().width / 2) + (c.getSize().width / 2);
				int candidatey = c.getLocation().y;
				if (Math.abs(candidatexcenter - xcenter) < margin && candidatey > y) {
					found = (JTextField) c;
					break;
				}
			}
		}
		return found;
	}
}
