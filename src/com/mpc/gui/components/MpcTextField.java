package com.mpc.gui.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.LayeredScreen;

public class MpcTextField extends JTextField implements FocusListener {
	private static final long serialVersionUID = 1L;

	private String csn = "";

	private LayeredScreen ls;

	private boolean split = false;
	private MpcTextField[] letters;
	private int activeSplit = 0;

	private boolean typeModeEnabled;
	private String oldText;

	private boolean scrolling;

	private String originalText;

	private Scroller scroller;

	private static final int BLINKING_RATE = 200; // in ms
	private boolean blinking = false;

	public MpcTextField() {
		// Border empty = new EmptyBorder(0, 2, 0, -3);
		// setMargin(new Insets(-2, -2, -10, -10));
		Border empty = new EmptyBorder(2, 2, 0, -3);
		setBorder(empty);
		setOpaque(true);
		setEditable(false);
		setForeground(Bootstrap.lcdOn);
		setBackground(Bootstrap.lcdOff);
		this.setHighlighter(null);
		addFocusListener(this);
		addKeyListener(Bootstrap.getGui().getKb());

	}

	public void focusGained(FocusEvent fe) {
		JTextField focusEvent = (JTextField) fe.getSource();
		if (fe.getOppositeComponent() == null && focusEvent.getBackground() == Bootstrap.lcdOn) return;
		ls = Bootstrap.getGui().getMainFrame().getLayeredScreen();
		csn = ls.getCurrentScreenName();

		if (csn.equals("trim") || csn.equals("loop")) {
			if (focusEvent.getName().equals("st") || focusEvent.getName().equals("to")) {
				ls.getTwoDots().setSelected(0, true);
			}

			if (focusEvent.getName().equals("end") || focusEvent.getName().equals("endlengthvalue")) {
				ls.getTwoDots().setSelected(1, true);
			}
		}

		if (csn.equals("startfine") || csn.equals("endfine") || csn.equals("looptofine") || csn.equals("loopendfine")) {
			if (focusEvent.getName().equals("start")) {
				ls.getTwoDots().setSelected(2, true);
			}

			if (focusEvent.getName().equals("end")) {
				ls.getTwoDots().setSelected(2, true);
			}

			if (focusEvent.getName().equals("to")) {
				ls.getTwoDots().setSelected(2, true);
			}

			if (focusEvent.getName().equals("end")) {
				ls.getTwoDots().setSelected(2, true);
			}

			if (focusEvent.getName().equals("lngth")) {
				ls.getTwoDots().setSelected(3, true);
			}
		}

		if (csn.equals("directory")) {
			focusEvent.setOpaque(true);
		}

		if ((!csn.equals("name"))) {
			focusEvent.setOpaque(true);
		}

		if (csn.equals("tempochange")) {
			focusEvent.setOpaque(true);
		}

		if (!(csn.equals("name") && Bootstrap.getGui().getNameGui().isNameBeingEdited())) {

			if (focusEvent.getForeground() == Bootstrap.lcdOn) {
				focusEvent.setForeground(Bootstrap.lcdOff);
			} else {
				focusEvent.setForeground(Bootstrap.lcdOn);
			}
			if (focusEvent.getBackground() == Bootstrap.lcdOn) {
				focusEvent.setBackground(Bootstrap.lcdOff);
			} else {
				focusEvent.setBackground(Bootstrap.lcdOn);
			}
		}

		if (csn.equals("trmove")) {

			if (focusEvent.getName().equals("tr1") && !Bootstrap.getGui().getTrMoveGui().isSelected())
				ls.drawFunctionBoxes("trmove_notselected");

		}

		if (csn.equals("name")) {

			if (!Bootstrap.getGui().getNameGui().isNameBeingEdited()) {
				focusEvent.setBackground(Bootstrap.lcdOn);
				focusEvent.setOpaque(true);
			}

		}
	}

	public void focusLost(FocusEvent fe) {
		if (fe.getOppositeComponent() == null) return;
		JTextField focusEvent = (JTextField) fe.getSource();
		if (focusEvent instanceof MpcTextField) ((MpcTextField) focusEvent).setSplit(false);
		ls = Bootstrap.getGui().getMainFrame().getLayeredScreen();
		csn = ls.getCurrentScreenName();

		if (csn.equals("trim") || csn.equals("loop")) {

			if (focusEvent.getName().equals("st") || focusEvent.getName().equals("to"))
				ls.getTwoDots().setSelected(0, false);

			if (focusEvent.getName().equals("end") || focusEvent.getName().equals("endlengthvalue"))
				ls.getTwoDots().setSelected(1, false);

		}

		if (csn.equals("startfine") || csn.equals("endfine") || csn.equals("looptofine") || csn.equals("loopendfine")) {

			if (focusEvent.getName().equals("start")) ls.getTwoDots().setSelected(2, false);

			if (focusEvent.getName().equals("end")) ls.getTwoDots().setSelected(2, false);

			if (focusEvent.getName().equals("to")) ls.getTwoDots().setSelected(2, false);

			if (focusEvent.getName().equals("end")) ls.getTwoDots().setSelected(2, false);

			if (focusEvent.getName().equals("lngth")) ls.getTwoDots().setSelected(3, false);

		}

		if ((!Bootstrap.getGui().getNameGui().isNameBeingEdited() && csn.equals("name"))) focusEvent.setOpaque(false);

		if (csn.equals("tempochange")) focusEvent.setOpaque(false);

		if (!(csn.equals("name") && Bootstrap.getGui().getNameGui().isNameBeingEdited())) {

			if (focusEvent.getForeground() == Bootstrap.lcdOn) {
				focusEvent.setForeground(Bootstrap.lcdOff);
			} else {
				focusEvent.setForeground(Bootstrap.lcdOn);
			}

			if (focusEvent.getBackground() == Bootstrap.lcdOn) {
				focusEvent.setBackground(Bootstrap.lcdOff);
			} else {
				focusEvent.setBackground(Bootstrap.lcdOn);
			}
		}

		if (csn.equals("trmove")) {
			if (focusEvent.getName().equals("tr1") && !Bootstrap.getGui().getTrMoveGui().isSelected())
				ls.drawFunctionBoxes("trmove");
		}

		if (csn.equals("directory") || csn.equals("save")) focusEvent.setOpaque(false);
		if (csn.equals("assignmentview")) focusEvent.setOpaque(false);

	}

	public void setSplit(boolean b) {
		if (split == b) return;
		split = b;
		Container parent = this.getParent();
		if (split) {
			this.setOpaque(false);
			letters = new MpcTextField[this.getText().length()];
			activeSplit = letters.length - 1;
			int x = this.getLocation().x;
			int y = this.getLocation().y;

			for (int i = 0; i < letters.length; i++) {
				letters[i] = new MpcTextField();
				letters[i].setColumns(1);
				letters[i].setOpaque(true);
				letters[i].setName("split");
				letters[i].setLocation(x + (i * 12), y);
				letters[i].setSize(6 * 2 + 2, 18);
				letters[i].setVisible(true);
				letters[i].setFocusable(false);
				parent.add(letters[i], 0);
			}
			setText(this.getText());
			redrawSplit();
			parent.repaint();
		} else {
			if (letters == null) return;
			this.setOpaque(true);
			for (int i = 0; i < letters.length; i++)
				parent.remove(letters[i]);
			parent.repaint();
			activeSplit = 0;
			letters = null;

		}
	}

	public void redrawSplit() {
		for (int i = 0; i < letters.length; i++) {
			letters[i].setForeground(i < activeSplit ? Bootstrap.lcdOff : Bootstrap.lcdOn);
			letters[i].setBackground(i < activeSplit ? Bootstrap.lcdOn : Bootstrap.lcdOff);
			letters[i].setOpaque(i < activeSplit);

		}
	}

	@Override
	public void setText(String s) {
		originalText = s;
		if (s.length() > getColumns()) s = s.substring(0, getColumns());
		super.setText(s);
		if (split) for (int i = 0; i < letters.length; i++) {
			letters[i].setText(s.substring(i, i + 1));
		}
	}

	public boolean isSplit() {
		return split;
	}

	public int getActiveSplit() {
		return activeSplit;
	}

	public boolean setActiveSplit(int i) {
		if (i < 1 || i > letters.length - 1) return false;
		activeSplit = i;
		redrawSplit();
		return true;
	}

	public boolean enableTypeMode() {
		if (typeModeEnabled) return false;
		typeModeEnabled = true;
		oldText = this.getText();
		setFont(Bootstrap.mpc2000fontu);
		setForeground(Bootstrap.lcdOn);
		setOpaque(false);
		setText("");
		return true;
	}

	public int enter() {
		int value = Integer.MAX_VALUE;
		if (!typeModeEnabled) return value;
		setFont(Bootstrap.mpc2000font);
		setForeground(Bootstrap.lcdOff);
		setOpaque(true);
		typeModeEnabled = false;
		try {
			value = Integer.parseInt(getText().replaceAll(" ", ""));
		} catch (Exception e) {
			e.printStackTrace();
			return value;
		}
		setText(oldText);
		return value;
	}

	public void type(int i) {
		// System.out.println()
		String str = getText().replaceAll(" ", "");
		if (str.length() == getColumns()) str = "";
		String newStr = StringUtils.leftPad(str + i, getColumns());
		setText(newStr);
	}

	public boolean isTypeModeEnabled() {
		return typeModeEnabled;
	}

	public void disableTypeMode() {
		if (!typeModeEnabled) return;
		typeModeEnabled = false;
		setFont(Bootstrap.mpc2000font);
		setForeground(Bootstrap.lcdOff);
		setOpaque(true);
		setText(oldText);
	}

	public void enableScrolling(JTextField[] enablers) {
		if (scroller != null) scroller.stopped = true;
		if (this.getColumns() > this.getText().length()) return;
		scroller = new Scroller(this, enablers);
		new Thread(scroller).start();
		scrolling = true;
	}

	class Scroller implements Runnable {

		final MpcTextField tf;
		final JTextField[] enablers;

		final int length;
		final String text;
		final int columns;
		boolean left = false;
		boolean stopped = false;
		int offset = 0;

		Scroller(MpcTextField tf, JTextField[] enablers) {
			this.tf = tf;
			this.enablers = enablers;
			text = tf.originalText;
			length = text.length();
			columns = tf.getColumns();
		}

		public void run() {

			try {
				while (scrolling && !stopped) {
					if (enablers != null) {
						boolean wait = true;
						while (wait) {
							for (JTextField mpctf : enablers) {
								if (mpctf.getName().equals(Util.getFocus())) {
									wait = false;
								}
							}
							if (wait) Thread.sleep(5);
						}
					}

					if (offset + columns == length || offset == 0) {
						left = !left;
						Thread.sleep(500);
					}
					offset = left ? offset + 1 : offset - 1;
					if (!stopped) tf.setText(text.substring(offset, offset + columns));
					Thread.sleep(300);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	class Blinker implements Runnable {

		public void run() {
			final Color ofg = getForeground();
			final Color obg = getBackground();
			while (blinking) {
				Util.sleep(BLINKING_RATE);
				setForeground(getForeground() == ofg ? obg : ofg);
			}
			setForeground(ofg);
		}

	}

	public void startBlinking() {
		blinking = true;
		new Thread(new Blinker()).start();
	}

	public void stopBlinking() {
		blinking = false;
	}

	public boolean getBlinking() {
		return this.blinking;
	}

}