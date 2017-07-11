package com.mpc.controls.vmpc;

import com.mpc.audiomidi.DirectToDiskSettings;
import com.mpc.gui.Bootstrap;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.SeqUtil;
import com.mpc.sequencer.Song;

import uk.org.toot.audio.server.NonRealTimeAudioServer;

public class DirectToDiskRecorderControls extends AbstractVmpcControls {

	public void turnWheel(int i) {
		init();
		int notch = getNotch(i);
		checkAllTimes(notch);
		if (param.equals("record")) d2dRecorderGui.setRecord(d2dRecorderGui.getRecord() + notch);
		if (param.equals("sq")) d2dRecorderGui.setSq(d2dRecorderGui.getSq() + notch);
		if (param.equals("song")) d2dRecorderGui.setSong(d2dRecorderGui.getSong() + notch);
		if (param.equals("splitlr")) d2dRecorderGui.setSplitLR(notch > 0);
		if (param.equals("offline")) d2dRecorderGui.setOffline(notch > 0);

		if (param.equals("outputfolder")) {
			nameGui.setName(d2dRecorderGui.getOutputfolder());
			nameGui.setNameLimit(8);
			nameGui.setParameterName("outputfolder");
			mainFrame.openScreen("name", "dialogpanel");
		}
	}

	public void function(int i) {
		init();
		switch (i) {
		case 3:
			mainFrame.openScreen("sequencer", "mainpanel");
			break;
		case 4:
			int seq = d2dRecorderGui.getSq();
			long lengthInFrames;
			String outputFolder = d2dRecorderGui.getOutputfolder();
			boolean split = false;
			MpcSequence sequence = sequencer.getSequence(seq);
			((NonRealTimeAudioServer) mpc.getAudioMidiServices().getAudioServer()).setRealTime(!d2dRecorderGui.isOffline());

			switch (d2dRecorderGui.getRecord()) {
			case 0:
				mainFrame.openScreen("sequencer", "mainpanel");
				lengthInFrames = (long) SeqUtil.sequenceFrameLength(sequence, 0, sequence.getLastTick());
				mpc.getAudioMidiServices()
						.prepareBouncing(new DirectToDiskSettings(lengthInFrames, outputFolder, split));
				sequence.setLoopEnabled(false);
				sequencer.playFromStart();
				break;
			case 1:
				mainFrame.openScreen("sequencer", "mainpanel");
				lengthInFrames = SeqUtil.loopFrameLength(sequence);
				mpc.getAudioMidiServices()
						.prepareBouncing(new DirectToDiskSettings(lengthInFrames, outputFolder, split));
				sequence.setLoopEnabled(false);
				sequencer.move(sequence.getLoopStart());
				sequencer.play();
				break;
			case 2:
				mainFrame.openScreen("sequencer", "mainpanel");
				lengthInFrames = (long) SeqUtil.sequenceFrameLength(sequence, d2dRecorderGui.getTime0(),
						d2dRecorderGui.getTime1());
				mpc.getAudioMidiServices()
						.prepareBouncing(new DirectToDiskSettings(lengthInFrames, outputFolder, split));
				sequence.setLoopEnabled(false);
				sequencer.move(d2dRecorderGui.getTime0());
				sequencer.play();
				break;
			case 3:
				Song song = sequencer.getSong(d2dRecorderGui.getSong());
				if (!song.isUsed()) return;
				lengthInFrames = SeqUtil.songFrameLength(song);
				mpc.getAudioMidiServices()
						.prepareBouncing(new DirectToDiskSettings(lengthInFrames, outputFolder, split));
				mainFrame.openScreen("song", "mainpanel");
				sequencer.setSongModeEnabled(true);
				sequencer.playFromStart();
				Bootstrap.getGui().getSongGui().setLoop(false);
				break;
			case 4:
				mainFrame.openScreen("recordjam", "dialogpanel");
				break;
			}
			break;
		}
	}

}
