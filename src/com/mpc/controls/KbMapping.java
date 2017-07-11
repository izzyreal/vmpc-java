package com.mpc.controls;

import java.awt.event.KeyEvent;

public class KbMapping {

	/*
	 * User definable keyboard mapping
	 */

	public static int dataWheelBack = KeyEvent.VK_MINUS;
	public static int dataWheelForward = KeyEvent.VK_EQUALS;

	public static int[] bankKeys = { KeyEvent.VK_HOME, KeyEvent.VK_END, KeyEvent.VK_INSERT, KeyEvent.VK_DELETE };

	public static int[] padKeys = { KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V, KeyEvent.VK_A,
			KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F, KeyEvent.VK_B, KeyEvent.VK_N, KeyEvent.VK_M, KeyEvent.VK_COMMA,
			KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_K };

	public static int rec = KeyEvent.VK_L;
	public static int overdub = KeyEvent.VK_SEMICOLON;
	public static int stop = KeyEvent.VK_QUOTE;
	public static int play = KeyEvent.VK_SPACE;
	public static int playstart = KeyEvent.VK_BACK_SLASH;

	public static int mainscreen = KeyEvent.VK_ESCAPE;
	public static int prevStepEvent = KeyEvent.VK_Q;
	public static int nextStepEvent = KeyEvent.VK_W;
	public static int goTo = KeyEvent.VK_E;
	public static int prevBarStart = KeyEvent.VK_R;
	public static int nextBarEnd = KeyEvent.VK_T;
	public static int tap = KeyEvent.VK_Y;
	public static int nextSeq = KeyEvent.VK_OPEN_BRACKET;
	public static int trackMute = KeyEvent.VK_CLOSE_BRACKET;
	public static int openWindow = KeyEvent.VK_I;
	public static int fullLevel = KeyEvent.VK_O;
	public static int sixteenLevels = KeyEvent.VK_P;

	public static int f1 = KeyEvent.VK_F1;
	public static int f2 = KeyEvent.VK_F2;
	public static int f3 = KeyEvent.VK_F3;
	public static int f4 = KeyEvent.VK_F4;
	public static int f5 = KeyEvent.VK_F5;
	public static int f6 = KeyEvent.VK_F6;

	public static int[] numPad = new int[] { KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
			KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9 };
	public static int numPadShift = KeyEvent.VK_SHIFT;
	public static int numPadEnter = KeyEvent.VK_ENTER;
	
	public static int undoSeq = KeyEvent.VK_F10;
	public static int erase = KeyEvent.VK_F11;
	public static int after = KeyEvent.VK_F12;

}
