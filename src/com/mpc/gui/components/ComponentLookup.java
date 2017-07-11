package com.mpc.gui.components;

import javax.swing.JLabel;
import javax.swing.JTextField;

public interface ComponentLookup {
	
	public JTextField lookupTextField(String s);
	
	public JLabel lookupLabel(String s);
	
}
