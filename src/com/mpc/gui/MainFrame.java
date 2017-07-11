package com.mpc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.simple.parser.ParseException;

import com.mpc.Mpc;
import com.mpc.controls.AbstractControls;
import com.mpc.disk.Disk;
import com.mpc.gui.components.BlinkLabel;
import com.mpc.gui.components.ComponentLookup;
import com.mpc.gui.components.MpcTextField;
import com.mpc.gui.disk.DiskObserver;
import com.mpc.gui.disk.LoadASequenceFromAllObserver;
import com.mpc.gui.disk.window.DeleteAllFilesObserver;
import com.mpc.gui.disk.window.DirectoryObserver;
import com.mpc.gui.disk.window.SaveAllFileObserver;
import com.mpc.gui.midisync.SyncObserver;
import com.mpc.gui.misc.PunchObserver;
import com.mpc.gui.misc.SecondSeqObserver;
import com.mpc.gui.misc.TransObserver;
import com.mpc.gui.other.OthersObserver;
import com.mpc.gui.sampler.DrumObserver;
import com.mpc.gui.sampler.LoopObserver;
import com.mpc.gui.sampler.MixerObserver;
import com.mpc.gui.sampler.PgmAssignObserver;
import com.mpc.gui.sampler.PgmParamsObserver;
import com.mpc.gui.sampler.PurgeObserver;
import com.mpc.gui.sampler.SampleObserver;
import com.mpc.gui.sampler.SndParamsObserver;
import com.mpc.gui.sampler.SoundObserver;
import com.mpc.gui.sampler.TrimObserver;
import com.mpc.gui.sampler.ZoneObserver;
import com.mpc.gui.sampler.window.EditSoundObserver;
import com.mpc.gui.sampler.window.MuteAssignObserver;
import com.mpc.gui.sampler.window.SamplerWindowObserver;
import com.mpc.gui.sampler.window.ZoomObserver;
import com.mpc.gui.sequencer.AssignObserver;
import com.mpc.gui.sequencer.BarCopyObserver;
import com.mpc.gui.sequencer.EditSequenceObserver;
import com.mpc.gui.sequencer.NextSeqObserver;
import com.mpc.gui.sequencer.NextSeqPadObserver;
import com.mpc.gui.sequencer.SequencerObserver;
import com.mpc.gui.sequencer.SongObserver;
import com.mpc.gui.sequencer.StepEditorObserver;
import com.mpc.gui.sequencer.TrMoveObserver;
import com.mpc.gui.sequencer.TrMuteObserver;
import com.mpc.gui.sequencer.UserObserver;
import com.mpc.gui.sequencer.window.Assign16LevelsObserver;
import com.mpc.gui.sequencer.window.EraseObserver;
import com.mpc.gui.sequencer.window.MetronomeSoundObserver;
import com.mpc.gui.sequencer.window.MidiMonitorObserver;
import com.mpc.gui.sequencer.window.SequencerWindowObserver;
import com.mpc.gui.sequencer.window.StepWindowObserver;
import com.mpc.gui.vmpc.AudioObserver;
import com.mpc.gui.vmpc.BufferSizeObserver;
import com.mpc.gui.vmpc.DirectToDiskRecorderObserver;
import com.mpc.gui.vmpc.MidiObserver;
import com.mpc.gui.vmpc.VmpcDiskObserver;
import com.mpc.nvram.NvRam;
import com.mpc.sequencer.MpcSequence;
import com.mpc.sequencer.MpcTrack;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ComponentLookup, Panel, MouseListener {

	private CasingPanel casing = new CasingPanel();
	private ControlPanel controlPanel = new ControlPanel();
	private LedPanel ledPanel = new LedPanel();

	private Mpc mpc;

	public ScreenPanel screenPanel = new ScreenPanel();
	public LayeredScreen layeredScreen = new LayeredScreen();
	private Gui gui;

	final static Color grid1 = new Color(0.95f, 0.94f, 0.94f, 0.4f);

	private KeyLabels keyLabels;

	public MainFrame(Mpc mpc, Gui gui) throws UnsupportedEncodingException, InterruptedException {
		setVisible(true);
		this.gui = gui;

		this.mpc = mpc;

		setLayout(null);
		setLocation(0, 0);

		screenPanel.init();
		add(screenPanel);

		keyLabels = new KeyLabels();
		keyLabels.addKeyLabels(this);

		controlPanel.init();
		add(controlPanel);

		ledPanel.init();
		add(ledPanel);

		casing.init();
		add(casing);

		layeredScreen.init(this);

		screenPanel.add(layeredScreen);

		pack();
		setSize(1298, 994);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				close();
			}
		});
		// this.getGlassPane().addMouseListener(this);
		// this.getGlassPane().setVisible(true);

	}

	public List<Component> getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container) {
				compList.addAll(getAllComponents((Container) comp));
			}
		}
		return compList;
	}

	public void setFocus(String s, JPanel j) {
		List<Component> list = getAllComponents(j);
		for (Component c : list) {
			if (c instanceof JTextField) {
				if (((JTextField) c).getName().equals(s)) {
					((JTextField) c).grabFocus();
				}
			}
		}
	}

	public String getFocus(JPanel j) {
		List<Component> list = getAllComponents(j);
		String s = null;
		for (Component c : list) {
			if (c instanceof JTextField) {
				if (((JTextField) c).hasFocus()) {
					s = c.getName();
				}
			}
		}
		return s;
	}

	public void setBlink(String s, JPanel j, Boolean b) {
		for (Component c : getAllComponents(j)) {
			if (c instanceof BlinkLabel) {
				if (((BlinkLabel) c).getName().equals(s)) {
					((BlinkLabel) c).setBlinking(b);
				}
			}
		}

	}

	public LayeredScreen getLayeredScreen() {
		return layeredScreen;
	}

	public void switchBlink(String s, JPanel j) {
		for (Component c : getAllComponents(j)) {
			if (c instanceof BlinkLabel) {
				if (((BlinkLabel) c).getName().equals(s)) {
					((BlinkLabel) c).setBlinking(!((BlinkLabel) c).getBlinking());
				}
			}
		}

	}

	public MpcTextField lookupTextField(String s) {
		MpcTextField tf = null;
		for (Component c : getAllComponents(layeredScreen.getCurrentPanel())) {
			if (c instanceof JTextField) {
				if (c.getName().equals(s)) {
					tf = ((MpcTextField) c);
				}
			}
		}
		return tf;
	}

	public JLabel lookupLabel(String s) {
		JLabel label = null;
		for (Component c : getAllComponents(layeredScreen.getCurrentPanel())) {
			if ((c instanceof JLabel) && (c.getName() != null)) {
				if (c.getName().equals(s)) {
					label = ((JLabel) c);
				}
			}
		}
		return label;
	}

	public void initObserver() throws UnsupportedEncodingException {

		String csn = layeredScreen.getCurrentScreenName();
		removeObservers();

		if (csn.equals("audio")) new AudioObserver(mpc, this);
		if (csn.equals("buffersize")) new BufferSizeObserver(this);
		if (csn.equals("midi")) new MidiObserver(mpc, this);
		if (csn.equals("directtodiskrecorder")) new DirectToDiskRecorderObserver(mpc, this);
		if (csn.equals("disk")) new VmpcDiskObserver(this);

		if (csn.equals("punch")) new PunchObserver(this);
		if (csn.equals("trans")) new TransObserver(this);
		if (csn.equals("2ndseq")) new SecondSeqObserver(this);
		
		if (csn.equals("others")) new OthersObserver(this);
		
		if (csn.equals("erase")) new EraseObserver(this);
		if (csn.equals("sync")) new SyncObserver(this);
		if (csn.equals("assign")) new AssignObserver(this);
		if (csn.equals("assign16levels")) new Assign16LevelsObserver(this);
		if (csn.equals("metronomesound")) new MetronomeSoundObserver(this);
		if (csn.equals("saveallfile")) new SaveAllFileObserver(this);
		if (csn.equals("loadasequencefromall")) new LoadASequenceFromAllObserver(this);
		if (csn.equals("nextseqpad")) new NextSeqPadObserver(mpc.getSequencer(), this);
		if (csn.equals("nextseq")) new NextSeqObserver(this);
		if (csn.equals("song")) new SongObserver(mpc, this);
		if (csn.equals("trackmute")) new TrMuteObserver(mpc.getSequencer(), this);
		if (checkActiveScreen(Gui.diskNames)) new DiskObserver(mpc, gui);
		if (checkActiveScreen(Gui.seqWindowNames)) new SequencerWindowObserver(mpc, this);
		if (checkActiveScreen(Gui.soundNames)) new SoundObserver(mpc.getSampler(), this);
		if (csn.equals("sample")) new SampleObserver(this, mpc.getSampler());
		if (csn.equals("sequencer")) new SequencerObserver(mpc, this);
		if (csn.equals("directory")) new DirectoryObserver(mpc.getDisk(), gui);
		if (csn.equals("programparams")) new PgmParamsObserver(mpc, this);
		if (csn.equals("programassign")) new PgmAssignObserver(mpc, this);
		if (csn.equals("sequencer_step")) new StepEditorObserver(mpc);
		if (csn.equals("step_tc") || csn.equals("editmultiple") || csn.equals("insertevent"))
			new StepWindowObserver(mpc, this);
		if (csn.equals("mixer") || csn.equals("channelsettings") || csn.equals("mixersetup")) new MixerObserver(mpc);
		if (csn.equals("edit")) new EditSequenceObserver(mpc.getSequencer(), mpc.getSampler(), this);
		if (csn.equals("name")) new NameObserver(mpc, this);
		if (csn.equals("midiinputmonitor") || csn.equals("midioutputmonitor")) new MidiMonitorObserver(mpc, this);
		if (csn.equals("barcopy")) new BarCopyObserver(mpc.getSequencer(), this);
		if (csn.equals("trmove")) new TrMoveObserver(mpc.getSequencer(), this);
		if (csn.equals("user")) new UserObserver(mpc, this);
		if (csn.equals("trim")) new TrimObserver(mpc.getSampler(), this);
		if (csn.equals("loop")) new LoopObserver(mpc.getSampler(), this);
		if (csn.equals("editsound")) new EditSoundObserver(mpc.getSampler(), this);
		if (csn.contains("startfine") || csn.contains("endfine") || csn.equals("looptofine"))
			new ZoomObserver(mpc.getSampler(), this);
		if (csn.equals("zone") || csn.equals("numberofzones")) new ZoneObserver(mpc.getSampler(), this);
		if (csn.equals("params")) new SndParamsObserver(mpc.getSampler(), this);
		if (csn.equals("deleteallfiles")) new DeleteAllFilesObserver(mpc, gui);
		if (checkActiveScreen(Gui.samplerWindowNames)) new SamplerWindowObserver(mpc, this);
		if (csn.equals("purge")) new PurgeObserver(gui);
		if (csn.equals("drum")) new DrumObserver(gui);
		if (csn.equals("muteassign")) new MuteAssignObserver(this);
		
	}

	public boolean checkActiveScreen(String[] sa) {
		for (String s : sa)
			if (layeredScreen.getCurrentScreenName().equals(s)) return true;
		return false;
	}

	public void removeObservers() throws UnsupportedEncodingException {
		mpc.getSequencer().deleteObservers();
		Disk disk = mpc.getDisk();
		if (disk != null) ((Observable) disk).deleteObservers();

		gui.getD2DRecorderGui().deleteObservers();
		gui.getSequencerWindowGui().deleteObservers();
		gui.getStepEditorGui().deleteObservers();
		mpc.getEventHandler().deleteObservers();
		gui.getZoomGui().deleteObservers();
		gui.getSoundGui().deleteObservers();
		gui.getDiskGui().deleteObservers();
		gui.getDirectoryGui().deleteObservers();
		gui.getSamplerGui().deleteObservers();
		gui.getSamplerWindowGui().deleteObservers();
		mpc.getSequencer().getActiveSequence().getTrack(mpc.getSequencer().getActiveTrackIndex()).deleteObservers();
		gui.getMixerSetupGui().deleteObservers();
		gui.getMixerGui().deleteObservers();
	}

	public JComponent getScreen() {
		return screenPanel;
	}

	@Override
	public void openScreen(String screenName, String panelName) {
//		if (!(screenName.equals("sequencer")||screenName.equals("sequencer_step"))) mpc.getSequencer().clearUndoSeq();
		
		if (screenName.equals("sequencer_step") && layeredScreen.getCurrentScreenName().equals("sequencer")) mpc.getSequencer().storeActiveSequenceInPlaceHolder();
//		if (layeredScreen.getCurrentScreenName().equals("sequencer_step") && panelName.equals("mainpanel")) gui.getStepEditorGui().clearSelection();	
		AbstractControls controls = gui.getControls(screenName);

		controlPanel.getDataWheel().setTarget(controls);
		controlPanel.getSlider().setTarget(controls);

		gui.getDeviceGui().restoreSettings();
		if (screenName.equals("save")) gui.getSoundGui().setSoundIndex(0);
		mpc.getSequencer().setSongModeEnabled(screenName.equals("song"));
		ledPanel.setNextSeq(screenName.equals("nextseq") || screenName.equals("nextseqpad"));
		if ((layeredScreen.getCurrentScreenName().equals("nextseq")
				|| layeredScreen.getCurrentScreenName().equals("nextseqpad")) && screenName.equals("trackmute"))
			ledPanel.setNextSeq(true);
		ledPanel.setTrackMute(screenName.equals("trackmute"));
		if (mpc.getSequencer().isPlaying()) {

			if (!screenName.equals("sequencer") && !screenName.equals("selectdrum")
					&& !screenName.equals("programassign") && !screenName.equals("programparams")
					&& !screenName.contains("mixer") && !screenName.equals("trackmute")) {
				return;
			}
		} else {
			getLedPanel().setOverDub(screenName.equals("sequencer_step"));
		}

		if (layeredScreen.getPreviousScreenName() != null
				&& layeredScreen.getPreviousScreenName().equals("sequencer_step")
				&& !layeredScreen.getCurrentScreenName().equals("insertevent")) {
			MpcSequence mpcSequence = mpc.getSequencer().getSequence(mpc.getSequencer().getActiveSequenceIndex());
			MpcTrack track = (MpcTrack) mpcSequence.getTrack(mpc.getSequencer().getActiveTrackIndex());
			track.removeDoubles();
		}
					
		JPanel panel = null;
		if (panelName == "mainpanel") {
			layeredScreen.getWindowPanel().removeAll();
			layeredScreen.getDialogPanel().removeAll();
			layeredScreen.getDialog2Panel().removeAll();
			panel = layeredScreen.getMainPanel();
		}
		if (panelName == "windowpanel") {
			layeredScreen.getDialogPanel().removeAll();
			layeredScreen.getDialog2Panel().removeAll();
			panel = layeredScreen.getWindowPanel();
		}

		if (panelName == "dialogpanel") {
			layeredScreen.getDialog2Panel().removeAll();
			panel = layeredScreen.getDialogPanel();
		}

		if (panelName == "dialog2panel") {
			panel = layeredScreen.getDialog2Panel();
		}

		panel.removeAll();

		try {
			removeObservers();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			layeredScreen.openScreen(screenName, panel, panelName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		panel.repaint();
		try {
			initObserver();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void popupPanel(String string, int i) {
		layeredScreen.removePopup();
		layeredScreen.createPopup(string, i);
		layeredScreen.repaint();
	}

	public void removePopup() {
		layeredScreen.removePopup();
		layeredScreen.repaint();
	}

	public LedPanel getLedPanel() {
		return ledPanel;
	}

	public void close() {
		NvRam.saveUserDefaults();
		NvRam.saveKnobPositions();
		mpc = Bootstrap.getGui().getMpc();
		if (mpc.getDisk() != null) mpc.getDisk().close();
		mpc.getAudioMidiServices().saveServerProperties();
		mpc.getAudioMidiServices().destroyServices();
		mpc.getAudioMidiServices().unloadAsioDrivers();
		System.exit(0);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		e.consume();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		e.consume();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		e.consume();
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}

	private static BufferedImage getScreenShot(Component component) {
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
				BufferedImage.TYPE_BYTE_BINARY);
		Graphics g = image.getGraphics();
		component.paint(g);
		return image.getSubimage(132, 50, 496, 120);
	}

	public ByteArrayInputStream getScreenPng() {
		BufferedImage img = getScreenShot(this);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {

			ImageIO.write(img, "png", baos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public KeyLabels getKeyLabels() {
		return keyLabels;
	}
}
