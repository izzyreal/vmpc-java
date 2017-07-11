package com.mpc.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import com.mpc.gui.components.MpcTextField;

public class Widget {

	private LayeredScreen slp;

	public Widget(LayeredScreen slp) {
		this.slp = slp;
	}

	public ArrayList<JComponent> buildParameter(String screenName, String parameterName, String panelName)
			throws FileNotFoundException, IOException, ParseException {
		ArrayList<JComponent> textWidget = new ArrayList<JComponent>();

		JSONArray jsonArrayParameterNames = slp.getJsonArray(screenName, "parameters", panelName);
		int counter = 0;
		int count = jsonArrayParameterNames.size();

		for (int i = 0; i < count; i++) {
			if (jsonArrayParameterNames.get(i).toString().equals(parameterName)) break;
			counter++;
		}

		JSONArray jsonArrayX = slp.getJsonArray(screenName, "x", panelName);
		int xPos = ((Long) jsonArrayX.get(counter)).intValue();

		JSONArray jsonArrayY = slp.getJsonArray(screenName, "y", panelName);
		int yPos = ((Long) jsonArrayY.get(counter)).intValue();

		JSONArray jsonArraySize = slp.getJsonArray(screenName, "tfsize", panelName);
		int tfSize = ((Long) jsonArraySize.get(counter)).intValue();

		JSONArray jsonArrayLabelTexts = slp.getJsonArray(screenName, "labels", panelName);

		MpcTextField tf = new MpcTextField();
		tf.setName(parameterName);
		tf.setForeground(Bootstrap.lcdOn);
		tf.setLocation(xPos * 2 - 2 + ((jsonArrayLabelTexts.get(counter).toString()).length() * 6 * 2), yPos * 2 - 2);
		tf.setSize(tfSize * 6 * 2 + 2, 18);
		tf.setColumns(tfSize);

		if (parameterName.equals("sq")) tf.setSize(tfSize * 6 * 2 + 4, 18);
		if (parameterName.equals("editfunction")) tf.setSize(tfSize * 6 * 2 + 4, 18);

		if (parameterName.equals("tempo") && (screenName.equals("sequencer") || screenName.equals("nextseq"))) {
			tf.setSize(tfSize * 6 * 2 - 4, 18);
		}

		if ((parameterName.startsWith("e") || (parameterName.startsWith("f") || parameterName.equals("initialtempo"))
				&& screenName.equals("tempochange"))) {
			tf.setSize(tfSize * 6 * 2 - 4, 18);
		}

		// tf.setDocument(new JTextFieldCharLimit(2));
		Border empty = new EmptyBorder(3, 2, 1, -3);

		JLabel label = new JLabel();
		label.setBorder(empty);
		label.setText(jsonArrayLabelTexts.get(counter).toString());
		label.setForeground(Bootstrap.lcdOn);
		label.setBackground(Bootstrap.lcdOn);
		label.setLocation(xPos * 2 - 2, yPos * 2 - 2);
		label.setName(parameterName);
		int xSize1 = label.getText().length() * 6 * 2;
		label.setSize(xSize1, 18);
		if (parameterName.equals("directory")) label.setSize(xSize1 + 14, 18);
		if (parameterName.contains("file") && (parameterName.length() == 5 || parameterName.length() == 6))
			label.setSize(xSize1 + 14, 18);
		label.setBorder(empty);

		label.setLabelFor(tf);

		textWidget.add(label);
		textWidget.add(tf);
		return textWidget;
	}

	public ArrayList<JComponent> buildInfoWidget(String arrangement, String notifierName, String panelName)
			throws FileNotFoundException, IOException, ParseException {
		ArrayList<JComponent> infoWidget = new ArrayList<JComponent>();

		JSONArray jsonArrayNotifierNames = slp.getJsonArray(arrangement, "infowidgets", panelName);

		int counter = 0;

		for (int i = 0, count = jsonArrayNotifierNames.size(); i < count; i++) {
			if (jsonArrayNotifierNames.get(i).toString().equals(notifierName)) break;
			counter++;
		}

		JSONArray jsonArrayX = slp.getJsonArray(arrangement, "infox", panelName);
		int xPos = ((Long) jsonArrayX.get(counter)).intValue();

		JSONArray jsonArrayY = slp.getJsonArray(arrangement, "infoy", panelName);
		int yPos = ((Long) jsonArrayY.get(counter)).intValue();

		JSONArray jsonArraySize = slp.getJsonArray(arrangement, "infosize", panelName);
		int labelSize = ((Long) jsonArraySize.get(counter)).intValue();

		JLabel label = new JLabel();
		Border empty = new EmptyBorder(3, 2, 1, -3);
		if (arrangement.equals("trackmute") && notifierName.length() <= 2) empty = new EmptyBorder(0, 2, 1, 0);
		label.setBorder(empty);
		label.setName(notifierName);
		label.setForeground(Bootstrap.lcdOn);
		label.setLocation(xPos * 2, yPos * 2);
		label.setSize(labelSize * 6 * 2 + 4, 18);
		if (arrangement.equals("trackmute") && notifierName.length() <= 2) label.setSize(labelSize * 6 * 2 + 2, 18);

		infoWidget.add(label);
		return infoWidget;
	}
}
