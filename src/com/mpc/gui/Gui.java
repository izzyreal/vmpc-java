package com.mpc.gui;

import java.awt.event.KeyAdapter;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mpc.Mpc;
import com.mpc.controls.AbstractControls;
import com.mpc.controls.Controls;
import com.mpc.controls.KbMouseController;
import com.mpc.controls.disk.FormatControls;
import com.mpc.controls.disk.LoadControls;
import com.mpc.controls.disk.SaveControls;
import com.mpc.controls.disk.SetupControls;
import com.mpc.controls.disk.dialog.CantFindFileControls;
import com.mpc.controls.disk.dialog.DeleteAllFilesControls;
import com.mpc.controls.disk.dialog.DeleteFileControls;
import com.mpc.controls.disk.dialog.DeleteFolderControls;
import com.mpc.controls.disk.dialog.FileAlreadyExistsControls;
import com.mpc.controls.disk.window.DirectoryControls;
import com.mpc.controls.disk.window.LoadAProgramControls;
import com.mpc.controls.disk.window.LoadASequenceControls;
import com.mpc.controls.disk.window.LoadASequenceFromAllControls;
import com.mpc.controls.disk.window.LoadASoundControls;
import com.mpc.controls.disk.window.LoadApsFileControls;
import com.mpc.controls.disk.window.MPC2000XLAllFileControls;
import com.mpc.controls.disk.window.SaveAProgramControls;
import com.mpc.controls.disk.window.SaveASequenceControls;
import com.mpc.controls.disk.window.SaveASoundControls;
import com.mpc.controls.disk.window.SaveAllFileControls;
import com.mpc.controls.disk.window.SaveApsFileControls;
import com.mpc.controls.midisync.SyncControls;
import com.mpc.controls.misc.PunchControls;
import com.mpc.controls.misc.SecondSeqControls;
import com.mpc.controls.misc.TransControls;
import com.mpc.controls.mixer.MixerControls;
import com.mpc.controls.mixer.MixerSetupControls;
import com.mpc.controls.mixer.SelectDrumMixerControls;
import com.mpc.controls.mixer.window.ChannelSettingsControls;
import com.mpc.controls.other.InitControls;
import com.mpc.controls.other.OthersControls;
import com.mpc.controls.other.VerControls;
import com.mpc.controls.other.dialog.NameControls;
import com.mpc.controls.sampler.DrumControls;
import com.mpc.controls.sampler.InitPadAssignControls;
import com.mpc.controls.sampler.LoopControls;
import com.mpc.controls.sampler.PgmAssignControls;
import com.mpc.controls.sampler.PgmParamsControls;
import com.mpc.controls.sampler.PurgeControls;
import com.mpc.controls.sampler.SampleControls;
import com.mpc.controls.sampler.SelectDrumControls;
import com.mpc.controls.sampler.SndParamsControls;
import com.mpc.controls.sampler.TrimControls;
import com.mpc.controls.sampler.ZoneControls;
import com.mpc.controls.sampler.dialog.ConvertSoundControls;
import com.mpc.controls.sampler.dialog.CopyProgramControls;
import com.mpc.controls.sampler.dialog.CopySoundControls;
import com.mpc.controls.sampler.dialog.CreateNewProgramControls;
import com.mpc.controls.sampler.dialog.DeleteAllProgramsControls;
import com.mpc.controls.sampler.dialog.DeleteAllSoundControls;
import com.mpc.controls.sampler.dialog.DeleteProgramControls;
import com.mpc.controls.sampler.dialog.DeleteSoundControls;
import com.mpc.controls.sampler.dialog.MonoToStereoControls;
import com.mpc.controls.sampler.dialog.ResampleControls;
import com.mpc.controls.sampler.dialog.StereoToMonoControls;
import com.mpc.controls.sampler.window.AssignmentViewControls;
import com.mpc.controls.sampler.window.AutoChromaticAssignmentControls;
import com.mpc.controls.sampler.window.CopyNoteParametersControls;
import com.mpc.controls.sampler.window.EditSoundControls;
import com.mpc.controls.sampler.window.KeepOrRetryControls;
import com.mpc.controls.sampler.window.MuteAssignControls;
import com.mpc.controls.sampler.window.NumberOfZonesControls;
import com.mpc.controls.sampler.window.ProgramControls;
import com.mpc.controls.sampler.window.SoundControls;
import com.mpc.controls.sampler.window.VeloEnvFilterControls;
import com.mpc.controls.sampler.window.VeloPitchControls;
import com.mpc.controls.sampler.window.VelocityModulationControls;
import com.mpc.controls.sampler.window.ZoomControls;
import com.mpc.controls.sequencer.AssignControls;
import com.mpc.controls.sequencer.BarCopyControls;
import com.mpc.controls.sequencer.EditSequenceControls;
import com.mpc.controls.sequencer.EraseAllOffTracksControls;
import com.mpc.controls.sequencer.NextSeqControls;
import com.mpc.controls.sequencer.NextSeqPadControls;
import com.mpc.controls.sequencer.SequencerControls;
import com.mpc.controls.sequencer.SongControls;
import com.mpc.controls.sequencer.StepEditorControls;
import com.mpc.controls.sequencer.TrMoveControls;
import com.mpc.controls.sequencer.TrMuteControls;
import com.mpc.controls.sequencer.UserDefaultsControls;
import com.mpc.controls.sequencer.dialog.CopySequenceControls;
import com.mpc.controls.sequencer.dialog.CopyTrackControls;
import com.mpc.controls.sequencer.dialog.DeleteAllSequencesControls;
import com.mpc.controls.sequencer.dialog.DeleteAllTracksControls;
import com.mpc.controls.sequencer.dialog.DeleteSequenceControls;
import com.mpc.controls.sequencer.dialog.DeleteTrackControls;
import com.mpc.controls.sequencer.dialog.MetronomeSoundControls;
import com.mpc.controls.sequencer.window.Assign16LevelsControls;
import com.mpc.controls.sequencer.window.ChangeBars2Controls;
import com.mpc.controls.sequencer.window.ChangeBarsControls;
import com.mpc.controls.sequencer.window.ChangeTsigControls;
import com.mpc.controls.sequencer.window.CountMetronomeControls;
import com.mpc.controls.sequencer.window.EditMultipleControls;
import com.mpc.controls.sequencer.window.EditVelocityControls;
import com.mpc.controls.sequencer.window.EraseControls;
import com.mpc.controls.sequencer.window.InsertEventControls;
import com.mpc.controls.sequencer.window.LoopBarsWindow;
import com.mpc.controls.sequencer.window.MidiInputControls;
import com.mpc.controls.sequencer.window.MidiMonitorControls;
import com.mpc.controls.sequencer.window.MidiOutputControls;
import com.mpc.controls.sequencer.window.MultiRecordingSetupControls;
import com.mpc.controls.sequencer.window.PasteEventControls;
import com.mpc.controls.sequencer.window.SequenceControls;
import com.mpc.controls.sequencer.window.TempoChangeControls;
import com.mpc.controls.sequencer.window.TimeDisplayControls;
import com.mpc.controls.sequencer.window.TimingCorrectControls;
import com.mpc.controls.sequencer.window.TrackControls;
import com.mpc.controls.sequencer.window.TransmitProgramChangesControls;
import com.mpc.controls.vmpc.AudioControls;
import com.mpc.controls.vmpc.AudioMidiDisabledControls;
import com.mpc.controls.vmpc.BufferSizeControls;
import com.mpc.controls.vmpc.DirectToDiskRecorderControls;
import com.mpc.controls.vmpc.MidiControls;
import com.mpc.controls.vmpc.RecordJamControls;
import com.mpc.controls.vmpc.RecordingFinishedControls;
import com.mpc.controls.vmpc.VmpcDiskControls;
import com.mpc.gui.disk.DiskGui;
import com.mpc.gui.disk.window.DirectoryGui;
import com.mpc.gui.disk.window.DiskWindowGui;
import com.mpc.gui.midisync.MidiSyncGui;
import com.mpc.gui.misc.PunchGui;
import com.mpc.gui.misc.SecondSeqGui;
import com.mpc.gui.misc.TransGui;
import com.mpc.gui.other.OthersGui;
import com.mpc.gui.sampler.MixerGui;
import com.mpc.gui.sampler.MixerSetupGui;
import com.mpc.gui.sampler.SamplerGui;
import com.mpc.gui.sampler.SoundGui;
import com.mpc.gui.sampler.window.EditSoundGui;
import com.mpc.gui.sampler.window.SamplerWindowGui;
import com.mpc.gui.sampler.window.ZoomGui;
import com.mpc.gui.sequencer.BarCopyGui;
import com.mpc.gui.sequencer.EditSequenceGui;
import com.mpc.gui.sequencer.SequencerGui;
import com.mpc.gui.sequencer.SongGui;
import com.mpc.gui.sequencer.StepEditorGui;
import com.mpc.gui.sequencer.TrMoveGui;
import com.mpc.gui.sequencer.window.EraseGui;
import com.mpc.gui.sequencer.window.SequencerWindowGui;
import com.mpc.gui.vmpc.AudioGui;
import com.mpc.gui.vmpc.DeviceGui;
import com.mpc.gui.vmpc.DirectToDiskRecorderGui;
import com.mpc.gui.vmpc.MidiGui;

public class Gui {

	public static String[] noteNames = new String[128];
	private String[] someNoteNames = { "C.", "C#", "D.", "D#", "E.", "F.", "F#", "G.", "G#", "A.", "A#", "B." };

	private Map<String, Controls> controls = new LinkedHashMap<String, Controls>();

	private int previousKeyStroke;

	private Mpc mpc;
	private MainFrame mainFrame;

	private EditSequenceGui editSequenceGui;
	private SequencerWindowGui sequencerWindowGui;
	private StepEditorGui stepEditorGui;
	private BarCopyGui barCopyGui;
	private TrMoveGui trMoveGui;
	private SamplerGui samplerGui;
	private SamplerWindowGui samplerWindowGui;
	private MixerGui mixerGui;
	private EditSoundGui editSoundGui;
	private SoundGui soundGui;
	private ZoomGui zoomGui;
	private DirectoryGui directoryGui;
	private DiskGui diskGui;
	private DiskWindowGui diskWindowGui;
	private MidiSyncGui midiSyncGui;
	private NameGui nameGui;
	private KbMouseController kbmc;
	private DeviceGui deviceGui;
	private SongGui songGui;
	private MixerSetupGui mixer;

	private SequencerGui sequencerGui;

	private EraseGui eraseGui;
	private OthersGui othersGui;

	private MidiGui midiGui;
	private AudioGui audioGui;
	private DirectToDiskRecorderGui d2dRecorderGui;
	private PunchGui punchGui;
	private SecondSeqGui secondSeqGui;
	private TransGui transGui;
	
	public static String[] samplerWindowNames = { "program", "deleteprogram", "deleteallprograms", "createnewprogram",
			"copyprogram", "assignmentview", "initpadassign", "copynoteparameters", "velocitymodulation",
			"veloenvfilter", "velopitch", "autochromaticassignment", "keeporretry" };

	public static String[] seqWindowNames = { "copysequence", "copytrack", "countmetronome", "timedisplay",
			"tempochange", "timingcorrect", "changetsig", "changebars", "changebars2", "eraseallofftracks",
			"transmitprogramchanges", "multirecordingsetup", "midiinput", "midioutput", "editvelocity", "sequence",
			"deletesequence", "track", "deletetrack", "deleteallsequences", "deletealltracks", "loopbarswindow" };

	static String[] diskNames = { "load", "save", "format", "setup", "device", "deleteallfiles", "loadaprogram",
			"saveaprogram", "loadasound", "saveasound", "cantfindfile", "filealreadyexists", "loadasequence",
			"saveasequence", "saveapsfile" };

	public static String[] soundNames = { "sound", "deletesound", "deleteallsound", "convertsound", "resample",
			"stereotomono", "monotostereo", "copysound" };

	public static String[] soundGuiNames = { "trim", "loop", "zone" };

	public Gui(Mpc mpc) {

		int octave = -2;
		int noteCounter = 0;
		for (int j = 0; j < 128; j++) {
			String octaveString = "" + octave;
			if (octave == -2) octaveString = "\u00D2";
			if (octave == -1) octaveString = "\u00D3";
			noteNames[j] = someNoteNames[noteCounter] + octaveString;
			noteCounter++;
			if (noteCounter == 12) {
				noteCounter = 0;
				octave++;
			}
		}

		this.mpc = mpc;
		try {
			mainFrame = new MainFrame(mpc, this);
			mainFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		sequencerGui = new SequencerGui();
		stepEditorGui = new StepEditorGui(mainFrame);
		editSequenceGui = new EditSequenceGui();
		sequencerWindowGui = new SequencerWindowGui(mainFrame);
		mpc.getSequencer().init(sequencerWindowGui);
		barCopyGui = new BarCopyGui();
		trMoveGui = new TrMoveGui();

		samplerGui = new SamplerGui();
		samplerWindowGui = new SamplerWindowGui();
		mixerGui = new MixerGui();

		editSoundGui = new EditSoundGui();
		soundGui = new SoundGui();
		zoomGui = new ZoomGui();

		diskGui = new DiskGui(mpc);
		directoryGui = new DirectoryGui(mpc, diskGui);
		diskWindowGui = new DiskWindowGui();

		midiSyncGui = new MidiSyncGui();
		nameGui = new NameGui();

		eraseGui = new EraseGui();

		midiGui = new MidiGui();
		audioGui = new AudioGui();
		d2dRecorderGui = new DirectToDiskRecorderGui();
		
		punchGui = new PunchGui();
		transGui = new TransGui();
		secondSeqGui = new SecondSeqGui();
		
		kbmc = new KbMouseController();
		mainFrame.addMouseListener(kbmc);
		mainFrame.addMouseMotionListener(kbmc);
		mainFrame.addMouseWheelListener(mainFrame.getControlPanel().getDataWheel());
		mainFrame.addMouseWheelListener(mainFrame.getControlPanel().getSlider());
		deviceGui = new DeviceGui();
		songGui = new SongGui();
		mixer = new MixerSetupGui();
		othersGui = new OthersGui();
		
		controls.put("punch", new PunchControls());
		controls.put("trans", new TransControls());
		controls.put("2ndseq", new SecondSeqControls());
		
		controls.put("trim", new TrimControls());
		controls.put("loop", new LoopControls());
		controls.put("zone", new ZoneControls());
		controls.put("numberofzones", new NumberOfZonesControls());
		controls.put("params", new SndParamsControls());
		controls.put("barcopy", new BarCopyControls());
		controls.put("channelsettings", new ChannelSettingsControls());
		controls.put("mixer", new MixerControls());
		controls.put("mixersetup", new MixerSetupControls());
		controls.put("drum", new DrumControls());
		controls.put("purge", new PurgeControls());
		controls.put("directory", new DirectoryControls());
		controls.put("deleteallfiles", new DeleteAllFilesControls());
		controls.put("deletefile", new DeleteFileControls());
		controls.put("deletefolder", new DeleteFolderControls());
		controls.put("load", new LoadControls());
		controls.put("loadasequence", new LoadASequenceControls());
		controls.put("cantfindfile", new CantFindFileControls());
		controls.put("save", new SaveControls());
		controls.put("saveasound", new SaveASoundControls());
		controls.put("format", new FormatControls());
		controls.put("disk", new VmpcDiskControls());
		controls.put("setup", new SetupControls());
		controls.put("loadaprogram", new LoadAProgramControls());
		controls.put("filealreadyexists", new FileAlreadyExistsControls());
		controls.put("saveasequence", new SaveASequenceControls());
		controls.put("saveaprogram", new SaveAProgramControls());
		controls.put("saveapsfile", new SaveApsFileControls());
		controls.put("loadasound", new LoadASoundControls());
		controls.put("edit", new EditSequenceControls());
		controls.put("editsound", new EditSoundControls());
		controls.put("name", new NameControls());
		controls.put("nextseq", new NextSeqControls());
		controls.put("nextseqpad", new NextSeqPadControls());
		controls.put("programassign", new PgmAssignControls());
		controls.put("programparams", new PgmParamsControls());
		controls.put("sample", new SampleControls());
		controls.put("keeporretry", new KeepOrRetryControls());
		controls.put("program", new ProgramControls());
		controls.put("createnewprogram", new CreateNewProgramControls());
		controls.put("copyprogram", new CopyProgramControls());
		controls.put("assignmentview", new AssignmentViewControls());
		controls.put("initpadassign", new InitPadAssignControls());
		controls.put("copynoteparameters", new CopyNoteParametersControls());
		controls.put("veloenvfilter", new VeloEnvFilterControls());
		controls.put("muteassign", new MuteAssignControls());
		controls.put("velopitch", new VeloPitchControls());
		controls.put("velocitymodulation", new VelocityModulationControls());
		controls.put("autochromaticassignment", new AutoChromaticAssignmentControls());
		controls.put("deleteallprograms", new DeleteAllProgramsControls());
		controls.put("deleteprogram", new DeleteProgramControls());
		controls.put("sequencer", new SequencerControls());
		controls.put("sequence", new SequenceControls());
		controls.put("timedisplay", new TimeDisplayControls());
		controls.put("tempochange", new TempoChangeControls());
		controls.put("timingcorrect", new TimingCorrectControls());
		controls.put("loopbarswindow", new LoopBarsWindow());
		controls.put("copytrack", new CopyTrackControls());
		controls.put("copysequence", new CopySequenceControls());
		controls.put("deletealltracks", new DeleteAllTracksControls());
		controls.put("deleteallsequences", new DeleteAllSequencesControls());
		controls.put("deletetrack", new DeleteTrackControls());
		controls.put("deletesequence", new DeleteSequenceControls());
		controls.put("editvelocity", new EditVelocityControls());
		controls.put("midioutput", new MidiOutputControls());
		controls.put("midiinput", new MidiInputControls());
		controls.put("multirecordingsetup", new MultiRecordingSetupControls());
		controls.put("transmitprogramchanges", new TransmitProgramChangesControls());
		controls.put("eraseallofftracks", new EraseAllOffTracksControls());
		controls.put("track", new TrackControls());
		controls.put("changebars", new ChangeBarsControls());
		controls.put("changebars2", new ChangeBars2Controls());
		controls.put("countmetronome", new CountMetronomeControls());
		controls.put("changetsig", new ChangeTsigControls());
		controls.put("song", new SongControls());
		controls.put("sound", new SoundControls());
		controls.put("deletesound", new DeleteSoundControls());
		controls.put("deleteallsound", new DeleteAllSoundControls());
		controls.put("convertsound", new ConvertSoundControls());
		controls.put("monotostereo", new MonoToStereoControls());
		controls.put("copysound", new CopySoundControls());
		controls.put("resample", new ResampleControls());
		controls.put("stereotomono", new StereoToMonoControls());
		controls.put("editmultiple", new EditMultipleControls());
		controls.put("insertevent", new InsertEventControls());
		controls.put("pasteevent", new PasteEventControls());
		controls.put("sequencer_step", new StepEditorControls());
		controls.put("trmove", new TrMoveControls());
		controls.put("trackmute", new TrMuteControls());
		controls.put("user", new UserDefaultsControls());
		ZoomControls zc = new ZoomControls();
		controls.put("startfine", zc);
		controls.put("endfine", zc);
		controls.put("looptofine", zc);
		controls.put("loopendfine", zc);
		controls.put("mpc2000xlallfile", new MPC2000XLAllFileControls());
		controls.put("loadasequencefromall", new LoadASequenceFromAllControls());
		controls.put("selectdrum", new SelectDrumControls());
		controls.put("selectdrum_mixer", new SelectDrumMixerControls());
		controls.put("loadapsfile", new LoadApsFileControls());
		controls.put("saveallfile", new SaveAllFileControls());
		controls.put("metronomesound", new MetronomeSoundControls());
		controls.put("assign16levels", new Assign16LevelsControls());
		controls.put("assign", new AssignControls());
		controls.put("sync", new SyncControls());

		MidiMonitorControls mmc = new MidiMonitorControls();
		controls.put("midiinputmonitor", mmc);
		controls.put("midioutputmonitor", mmc);
		
		controls.put("erase", new EraseControls());
		controls.put("others", new OthersControls());
		controls.put("init", new InitControls());
		controls.put("ver", new VerControls());

		controls.put("midi", new MidiControls());
		controls.put("audio", new AudioControls());
		controls.put("buffersize", new BufferSizeControls());
		controls.put("audiomididisabled", new AudioMidiDisabledControls());
		controls.put("directtodiskrecorder", new DirectToDiskRecorderControls());
		controls.put("recordjam", new RecordJamControls());
		controls.put("recordingfinished", new RecordingFinishedControls());
	
		
		// JmeGui app = new JmeGui();
		// AppSettings settings = new AppSettings(true);
		// settings.setFrameRate(30);
		// app.setSettings(settings);
		// app.start();

	}

	public Mpc getMpc() {
		return mpc;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public StepEditorGui getStepEditorGui() {
		return stepEditorGui;
	}

	public MixerGui getMixerGui() {
		return mixerGui;
	}

	public EditSequenceGui getEditSequenceGui() {
		return editSequenceGui;
	}

	public SequencerWindowGui getSequencerWindowGui() {
		return sequencerWindowGui;
	}

	public void setPreviousKeyStroke(int i) {
		previousKeyStroke = i;
	}

	public MidiSyncGui getMidiSyncGui() {
		return midiSyncGui;
	}

	public BarCopyGui getBarCopyGui() {
		return barCopyGui;
	}

	public TrMoveGui getTrMoveGui() {
		return trMoveGui;
	}

	public EditSoundGui getEditSoundGui() {
		return editSoundGui;
	}

	public SoundGui getSoundGui() {
		return soundGui;
	}

	public int getPreviousKeyStroke() {
		return previousKeyStroke;
	}

	public ZoomGui getZoomGui() {
		return zoomGui;
	}

	public DirectoryGui getDirectoryGui() {
		return directoryGui;
	}

	public DiskGui getDiskGui() {
		return diskGui;
	}

	public DiskWindowGui getDirectoryWindowGui() {
		return diskWindowGui;
	}

	public SamplerGui getSamplerGui() {
		return samplerGui;
	}

	public SamplerWindowGui getSamplerWindowGui() {
		return samplerWindowGui;
	}

	public NameGui getNameGui() {
		return nameGui;
	}

	public KeyAdapter getKb() {
		return kbmc;
	}

	public DeviceGui getDeviceGui() {
		return deviceGui;
	}

	public SongGui getSongGui() {
		return songGui;
	}

	public MixerSetupGui getMixerSetupGui() {
		return mixer;
	}

	public AbstractControls getControls(String s) {
		return (AbstractControls) controls.get(s);
	}

	public SequencerGui getSequencerGui() {
		return sequencerGui;
	}

	public EraseGui getEraseGui() {
		return eraseGui;
	}

	public OthersGui getOthersGui() {
		return othersGui;
	}
	
	public AudioGui getAudioGui() {
		return audioGui;
	}
	
	public MidiGui getMidiGui() {
		return midiGui;
	}

	public DirectToDiskRecorderGui getD2DRecorderGui() {
		return d2dRecorderGui;
	}

	public PunchGui getPunchGui() {
		return punchGui;
	}

	public SecondSeqGui getSecondSeqGui() {
		return secondSeqGui;
	}
	
	public TransGui getTransGui() {
		return transGui;
	}
}