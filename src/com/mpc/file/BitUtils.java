package com.mpc.file;

public class BitUtils {

	public static byte removeUnusedBits(byte b, int[] usedRange) {
		byte cleanedByte = b;
		int[] range_to_clear = invertRange(usedRange);
		for (int pos = range_to_clear[0]; pos < range_to_clear[1]; pos++)
			cleanedByte &= ~(1 << pos);
		return cleanedByte;
	}

	public static int[] invertRange(int[] range) {
		int[] invRange = new int[2];
		if (range[0] < range[1]) {
			invRange[0] = range[1] + 1;
			invRange[1] = Definitions.BITS_PER_BYTE - 1;
		} else {
			invRange[0] = 0;
			invRange[1] = range[0] - 1;
		}
		return invRange;
	}

	public static byte setBit(byte b, int i, boolean on) {
		if (on) {
			return (byte) (b | (1 << i));
		} else {
			return (byte) (b & ~(1 << i));
		}
	}

	public static boolean isBitOn(byte b, int i) {
		if ((b & (1L << i)) != 0) return true;
		return false;
	}

	public static byte stitchBytes(byte b1, int[] usedBits1, byte b2, int[] usedBits2) throws Exception {
		// System.out.println("range 1: " + usedBits1[0] + " to " +
		// usedBits1[1]);
		// System.out.println("range 2: " + usedBits2[0] + " to " +
		// usedBits2[1]);
		boolean byte1occupiesstart = usedBits1[0] == 0;

		if (byte1occupiesstart) {
			if (usedBits2[0] <= usedBits1[1]) throw new Exception(); // check
																		// overlap
			if (usedBits2[0] - 1 != usedBits1[1]) throw new Exception(); // check
																			// contiguous

		} else {
			if (usedBits1[0] <= usedBits2[1]) throw new Exception();
			if (usedBits1[0] - 1 != usedBits2[1]) throw new Exception();
		}

		byte result = 0;
//		System.out.println("");
		for (int i = usedBits1[0]; i < usedBits1[1] + 1; i++) {
			boolean on = isBitOn(b1, i);
//			System.out.println("setting bit " + i + " to " + on);
			result = setBit(result, i, on);
//			System.out.println("bit " + i + " is now " + isBitOn(result, i));
		}

		for (int i = usedBits2[0]; i < usedBits2[1] + 1; i++) {
			boolean on = isBitOn(b2, i);
//			System.out.println("setting bit " + i + " to " + on);
			result = setBit(result, i, on);
//			System.out.println("bit " + i + " is now " + isBitOn(result, i));
		}

		return result;
	}

}
