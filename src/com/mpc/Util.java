package com.mpc;

import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.swing.JPanel;

import com.mpc.gui.Bootstrap;

public class Util {

	private static double velocity;

	public static String padLeftSpace(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	public static String padRightSpace(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft2Zeroes(Object o) {
		if (o instanceof String) {
			String str = (String) o;
			for (int i = str.length();i<2;i++)
				str = 0 + str;
			return str;
		}
		return String.format("%02d", (Integer) o);
	}
	
	public static String padLeft3Zeroes(int i) {
		return String.format("%03d", i);
	}

	public static String padLeft4Zeroes(int i) {
		return String.format("%04d", i);
	}

	public static int[] getPadAndVelo(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int padSize = 93;
		int emptySize = 23;
		int padPosX = 785;
		int padPosY = 343;
		int xPos = -1;
		int yPos = -1;
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				int xborderl = padPosX + (i * padSize) + (i * (emptySize + (i * 2)));
				int xborderr = xborderl + padSize;
				int yborderu = padPosY + (j * padSize) + (j * (emptySize + (j * 2)));
				int yborderd = yborderu + padSize;
				int centerx = xborderl + (padSize / 2);
				int centery = yborderu + (padSize / 2);
				if (x > xborderl && x < xborderr && y > yborderu && y < yborderd) {
					xPos = i;
					yPos = j;
					int distcx = Math.abs(centerx - x);
					int distcy = Math.abs(centery - y);
					velocity = 127 - ((127.0 / 46.0) * ((distcx + distcy) / 2.0));
					break;
				}
			}
		}

		// System.out.println("x " + xPos + " y " + yPos);
		if (yPos == -1 || yPos == -1) return new int[] { -1, -1 };
		int padNumber = -1;
		int[] column0 = { 12, 8, 4, 0 };
		int[] column1 = { 13, 9, 5, 1 };
		int[] column2 = { 14, 10, 6, 2 };
		int[] column3 = { 15, 11, 7, 3 };
		int[][] columns = new int[4][];
		columns[0] = column0;
		columns[1] = column1;
		columns[2] = column2;
		columns[3] = column3;
		padNumber = columns[xPos][yPos];
		return new int[] { padNumber, (int) velocity };
	}

	public static String[] splitName(String s) {

		if (!s.contains(".")) return (new String[] { s, "" });

		int i = s.lastIndexOf(".");

		return (new String[] { s.substring(0, i), s.substring(i + 1) });
	}

	public static String padFileName16(String s) {

		if (!s.contains(".")) return s;

		int periodIndex = s.lastIndexOf(".");
		String name = s.substring(0, periodIndex);
		String ext = s.substring(periodIndex);
		name = padRightSpace(name, 16);
		return name + ext;
	}

	private static short bytePairToShort(byte[] pair) {
		ByteBuffer intBuffer1 = ByteBuffer.wrap(pair);
		intBuffer1.order(ByteOrder.LITTLE_ENDIAN);
		return intBuffer1.getShort();
	}

	public static int bytePairToUnsignedInt(byte[] pair) {
		short lastTick0 = bytePairToShort(pair);
		int intVal = lastTick0 >= 0 ? lastTick0 : 0x10000 + lastTick0;
		return intVal;
	}

	public static byte[] unsignedIntToBytePair(int intVal) {
		short s = (short) (intVal <= 32768 ? intVal : intVal - 0x10000);
		return shortToBytePair(s);
	}

	private static byte[] shortToBytePair(short s) {
		byte[] ba = new byte[2];
		ByteBuffer intBuffer = ByteBuffer.allocate(2);
		intBuffer.order(ByteOrder.LITTLE_ENDIAN);
		intBuffer.putShort(s);
		intBuffer.rewind();
		ba[0] = intBuffer.get();
		ba[1] = intBuffer.get();
		return ba;
	}

	public static byte[] stitchByteArrays(List<byte[]> byteArrays) {
		int totalSize = 0;
		for (byte[] ba : byteArrays)
			totalSize += ba.length;
		byte[] result = new byte[totalSize];
		int counter = 0;
		for (byte[] ba : byteArrays)
			for (byte b : ba)
				result[counter++] = b;
		return result;
	}

	public static byte[] get32BitIntBytes(int i) {
		byte[] intBytes = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(intBytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(i);
		return intBytes;
	}

	public static String getFocus() {
		JPanel panel = Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentPanel();
		String cn = Bootstrap.getGui().getMainFrame().getFocus(panel);
		return cn;
	}

	public static String getCsn() {
		String csn = Bootstrap.getGui().getMainFrame().getLayeredScreen().getCurrentScreenName();
		return csn;
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getFileName(String name) {
		return name.trim().replaceAll(" ", "_").toUpperCase();
	}
	
}
