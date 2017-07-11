package com.mpc.file.aps;

import com.mpc.Mpc;
import com.mpc.disk.MpcFile;
import com.mpc.disk.SoundSaver;
import com.mpc.gui.Bootstrap;

public class ApsSaver {

	private Mpc mpc;
	private String apsFileName;

	public ApsSaver(Mpc mpc, String apsFileName) {
		this.mpc = mpc;
		this.apsFileName = apsFileName;
		System.out.println("\nchecking for file name " + apsFileName + "\n");
		mpc.getDisk().initFiles();
		if (mpc.getDisk().checkExists(apsFileName)) {
			System.out.println("file exists");
			Bootstrap.getGui().getMainFrame().openScreen("filealreadyexists", "windowpanel");
		} else {
			saveAps();
		}
	}

	private void saveAps() {
		mpc.getDisk().setBusy(true);
		System.out.println("using apsFileName " + apsFileName);
		MpcFile file = mpc.getDisk().newFile(apsFileName);
		ApsParser apsParser = new ApsParser(mpc, apsFileName.substring(0, apsFileName.indexOf(".")));
		file.setFileData(apsParser.getBytes());
		final int saveWith = Bootstrap.getGui().getDiskGui().getPgmSave();

		if (saveWith != 0) new SoundSaver(mpc.getSampler().getSounds(), saveWith == 1 ? false : true);

		Bootstrap.getGui().getMainFrame().openScreen("save", "mainpanel");
		mpc.getDisk().setBusy(false);
	}
}
