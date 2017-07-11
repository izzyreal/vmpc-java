package com.mpc.gui.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldCharLimit extends PlainDocument {
	private static final long serialVersionUID = 1L;
	private int limit;
	
	public JTextFieldCharLimit(int limitation) {
		 this.limit = limitation;
	}

	public void insertString(int offset, String str, AttributeSet set) throws BadLocationException {
		if(str == null) {
			return;
		} else if((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, set);
		}
	}
}
