package com.mpc.controls.slider;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;

import com.mpc.controls.KbMouseController;
import com.mpc.nvram.NvRam;

public class Slider extends Observable implements MouseWheelListener {

	private SliderControllable target;
	private int value = NvRam.getSlider();

	public void setTarget(SliderControllable controllable) {
		this.target = controllable;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!KbMouseController.shiftIsPressed) return;
		setValue(value + (int) (e.getPreciseWheelRotation() * 60.0));
		target.setSlider(value);
	}

	public void setValue(int i) {
		if (i < 0 || i > 127) return;
		value = i;
		setChanged();
		notifyObservers(value);
	}

	public int getValue() {
		return value;
	}
	
}