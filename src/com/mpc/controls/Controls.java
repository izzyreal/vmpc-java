package com.mpc.controls;

import java.awt.event.KeyEvent;

public interface Controls {

	void mainScreen();

	void openWindow();

	void function(int f);

	void keyEvent(KeyEvent e);

	void pad(int i, int velo, boolean repeat, long tick);

	void tap();

	void fullLevel();

	void sixteenLevels();

	void nextSeq();

	void trackMute();

	void bank(int i);

	void after();

	void shift();

	void undoSeq();

	void erase();

}
