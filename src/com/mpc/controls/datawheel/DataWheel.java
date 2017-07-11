package com.mpc.controls.datawheel;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;

import com.mpc.controls.KbMouseController;

public class DataWheel extends Observable implements Runnable, MouseWheelListener {

	private final static double MIN_ROTATION = 0.0000000001;
	private final static double MIN_ROTATION_FLYWHEEL = 0.07;
	
	private final static int MIN_TIME = 4000000;
	private final static int MAX_TIME = 40000000;
	
	public final static int NOTCH_UP = 1000000;
	public final static int NOTCH_DOWN = -1000000;

	public double velocity = 0;

	private DataWheelControllable target;

	private Thread thread;

	private long lastForward = 0;
	private long lastBack = 0;

	private static boolean flyWheelEmulation;

	public DataWheel(boolean flyWheel) {
		DataWheel.flyWheelEmulation = flyWheel;
	}

	public void setTarget(DataWheelControllable controllable) {
		this.target = controllable;
	}

	public void turn(boolean forward) {
		double oldVelocity = velocity;
		long time = System.nanoTime();
		double ratio = 0;
		if (forward) {

			if (time - lastForward < MIN_TIME) return;
			if (time - lastForward > MAX_TIME) lastForward = 0;
			ratio = (1000000.0 / (time - lastForward)) * 100.0;
			ratio = (ratio + ((velocity / 4000.0) * 200.0)) / 2.0;
			lastForward = time;
		} else {
			if (time - lastBack < MIN_TIME) return;
			if (time - lastBack > MAX_TIME) lastBack = 0;
			ratio = (1000000.0 / (time - lastBack)) * 100.0;
			ratio = (ratio - ((velocity / 4000.0) * 200.0)) / 2.0;
			lastBack = time;
		}

		velocity += ((forward ? 100 : -100) * ratio);
		if (velocity > 50000 || velocity < -50000) velocity = oldVelocity;
		if (oldVelocity == 0 && flyWheelEmulation) {

			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {

		while (velocity != 0) {
			if (target != null) target.turnWheel(getIncrement(velocity * 0.00003));

			velocity *= 0.96;

			if (velocity > 0 && velocity < 100) velocity = 0;
			if (velocity < 0 && velocity > -100) velocity = 0;

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (KbMouseController.shiftIsPressed) return;
		setChanged();
		notifyObservers(new Integer((int) (e.getPreciseWheelRotation()*60.0)));
		
		final double rot = e.getPreciseWheelRotation();
		if (rot > (flyWheelEmulation ? 10.0 : 1.0)) return;
		if (target == null) return;
		if (flyWheelEmulation) {
			boolean goingForward = velocity > 0;
			if (velocity != 0 && ((rot > 0 && !goingForward) || (rot < 0 && goingForward))) {
				brakeWheel(rot);
			} else {
				turn(rot > 0);
			}
		} else {
			target.turnWheel(getIncrement(rot / 3.0));
		}
	}

	private void brakeWheel(double preciseWheelRotation) {
		velocity *= Math.abs(preciseWheelRotation) / 15.0;
	}

	private static int getIncrement(double rotation) {
//		System.out.println("rotation " + rotation);
		if (Math.abs(rotation) < (flyWheelEmulation ? MIN_ROTATION_FLYWHEEL : MIN_ROTATION)) return rotation > 0 ? NOTCH_UP : NOTCH_DOWN;
//		return (int) (rotation * 80.0);
		return (int) (rotation * 60.0);
	}
}