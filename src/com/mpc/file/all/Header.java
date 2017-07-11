package com.mpc.file.all;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Header  {
	
	// Loading
	byte[] loadBytes;
	
	
	// Saving
	byte[] saveBytes;
	
	/*
	 *  Constructor and methods for loading
	 */
	Header(byte[] b) {
		loadBytes = b;
	}

	protected byte[] getHeaderArray () {
		return loadBytes;
	}

	protected boolean verifyFileID () {
		boolean verifyFileID = false;
		
		byte[] checkFileID = Arrays.copyOfRange(loadBytes, 0, 16);
		
		String fileIDString = null;
		try {
			fileIDString = new String(checkFileID, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String shouldBe = "MPC2KXL ALL 1.00";
		
		System.out.println(fileIDString);
		
		if (fileIDString.equals(shouldBe)) {
			verifyFileID = true;
		}
			return verifyFileID;
	}
	
	
	/*
	 *  Constructor and methods for saving
	 */
	
	Header() {
		saveBytes = "MPC2KXL ALL 1.00".getBytes();
	}
	
	byte[] getBytes() {
		return saveBytes;
	}
}
