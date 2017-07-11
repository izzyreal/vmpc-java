package com.mpc.gui;

import java.util.Observable;

import org.apache.commons.lang3.StringUtils;

import com.mpc.Mpc;
import com.mpc.Util;

public class NameGui extends Observable {

	private String name;
	private boolean editing;
	private String parameterName;
	private int nameLimit;
	
	public void setName(String name) {
		this.name = Util.padRightSpace(name, 16);
		nameLimit = 16;
	}
	
	public void setNameLimit(int i) {
		name = name.substring(0, i);
		nameLimit = i;
	}
	
	public int getNameLimit() {
		return nameLimit;
	}
	
	public void setName(String string, int i) {
		name = name.substring(0, i) + string + name.substring(i+1, name.length());		
	}

	public String getName() {
		return StringUtils.rightPad(name.trim().replaceAll(" ", "_"), nameLimit);
	}

	public void changeNameCharacter(int i, boolean up) {
		String s = name.substring(i, i+1);
		int stringCounter = 0;
		
		for (String string : Mpc.akaiAscii) {
			if (string.equals(s)) {
				break;
			}
			stringCounter++;
		}
		
		if (stringCounter==0 && !up) return;
		if (stringCounter==75 && up) return;
		
		
		int change = -1;
		if (up) change = 1;
		if (stringCounter > 75) {
			s = " ";
		} else {
			s = Mpc.akaiAscii[stringCounter + change];
		}
		name = name.substring(0, i) + s + name.substring(i+1, name.length());
		setChanged();
		notifyObservers("name");
	}
	
	public void setNameBeingEdited(boolean b) {
		editing = b;
	}
	
	public void setParameterName(String s) {
		parameterName = s;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public boolean isNameBeingEdited() {
		return editing;
	}
}