package com.mpc.gui;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.mpc.Mpc;

public class NameObserver implements Observer {

	private MainFrame mainFrame;

	private JTextField a0Field;
	private JTextField a1Field;
	private JTextField a2Field;
	private JTextField a3Field;
	private JTextField a4Field;
	private JTextField a5Field;
	private JTextField a6Field;
	private JTextField a7Field;
	private JTextField a8Field;
	private JTextField a9Field;
	private JTextField a10Field;
	private JTextField a11Field;
	private JTextField a12Field;
	private JTextField a13Field;
	private JTextField a14Field;
	private JTextField a15Field;

	private NameGui nameGui;

	public NameObserver(Mpc mpc, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		nameGui = Bootstrap.getGui().getNameGui();

		this.mainFrame = mainFrame;

		a0Field = mainFrame.lookupTextField("0");
		a1Field = mainFrame.lookupTextField("1");
		a2Field = mainFrame.lookupTextField("2");
		a3Field = mainFrame.lookupTextField("3");
		a4Field = mainFrame.lookupTextField("4");
		a5Field = mainFrame.lookupTextField("5");
		a6Field = mainFrame.lookupTextField("6");
		a7Field = mainFrame.lookupTextField("7");
		a8Field = mainFrame.lookupTextField("8");
		a9Field = mainFrame.lookupTextField("9");
		a10Field = mainFrame.lookupTextField("10");
		a11Field = mainFrame.lookupTextField("11");
		a12Field = mainFrame.lookupTextField("12");
		a13Field = mainFrame.lookupTextField("13");
		a14Field = mainFrame.lookupTextField("14");
		a15Field = mainFrame.lookupTextField("15");

		nameGui.deleteObservers();
		nameGui.addObserver(this);

		displayName();
	}

	private void displayName() {
		a0Field.setText(nameGui.getName().substring(0, 1));
		a1Field.setText(nameGui.getName().substring(1, 2));
		a2Field.setText(nameGui.getName().substring(2, 3));
		a3Field.setText(nameGui.getName().substring(3, 4));
		a4Field.setText(nameGui.getName().substring(4, 5));
		a5Field.setText(nameGui.getName().substring(5, 6));
		a6Field.setText(nameGui.getName().substring(6, 7));
		a7Field.setText(nameGui.getName().substring(7, 8));

		if (nameGui.getNameLimit() > 8) {

			a8Field.setVisible(true);
			a9Field.setVisible(true);
			a10Field.setVisible(true);
			a11Field.setVisible(true);
			a12Field.setVisible(true);
			a13Field.setVisible(true);
			a14Field.setVisible(true);
			a15Field.setVisible(true);

			a8Field.setText(nameGui.getName().substring(8, 9));
			a9Field.setText(nameGui.getName().substring(9, 10));
			a10Field.setText(nameGui.getName().substring(10, 11));
			a11Field.setText(nameGui.getName().substring(11, 12));
			a12Field.setText(nameGui.getName().substring(12, 13));
			a13Field.setText(nameGui.getName().substring(13, 14));
			a14Field.setText(nameGui.getName().substring(14, 15));
			a15Field.setText(nameGui.getName().substring(15, 16));

		} else {
			a8Field.setVisible(false);
			a9Field.setVisible(false);
			a10Field.setVisible(false);
			a11Field.setVisible(false);
			a12Field.setVisible(false);
			a13Field.setVisible(false);
			a14Field.setVisible(false);
			a15Field.setVisible(false);
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "name":
			
			String s = mainFrame.getFocus(mainFrame.getLayeredScreen()
					.getDialogPanel());
			
			JTextField tf = mainFrame.lookupTextField(s);
			
			tf.setText(nameGui.getName().substring(Integer.parseInt(s),
					Integer.parseInt(s) + 1));
			
			break;

		}
	}
}
