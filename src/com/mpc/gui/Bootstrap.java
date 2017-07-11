package com.mpc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.mpc.Mpc;
import com.mpc.file.wav.WavFileException;
import com.mpc.nvram.NvRam;

public class Bootstrap {
	public static int keyFontSize = 16;
	// public static Font keyFont = new Font("Anarchy Sans", Font.BOLD,
	// keyFontSize);
	public static Font keyFont = new Font("Source Code Pro Bold", Font.PLAIN, keyFontSize);
	// public static Font keyFont = new Font("Fantasque Sans Mono", Font.PLAIN,
	// keyFontSize);
	// public static Font keyFont = new Font("Rubik Mono One Regular",
	// Font.PLAIN, keyFontSize);

	public static Font mpc2000font;
	public static Font mpc2000fontu;
	public static Font fontsmall;

	public final static Color lcdOnOriginal = new Color(86, 61, 145);
	public static Color lcdOn = new Color(86, 61, 145);
	public final static Color lcdOff = new Color(234, 251, 218);

	public static boolean osx = System.getProperty("os.name").contains("OS X");

	public final static String home = System.getProperty("user.home") + "/";
	public final static String resPath = (osx ? home : home) + "Mpc/resources/";
	public final static String tempPath = (osx ? home : home) + "Mpc/temp/";
	public final static String storesPath = (osx ? home : home) + "Mpc/JavaStores/";
	private final static UserDefaults userDefaults = NvRam.load();

	private static Mpc mpc;

	private static Gui gui;
	private static StartUp startUp;

	private static VisualOutputStream logfos;
	private static BufferedOutputStream logbos;

	public static void main(String[] args)
			throws InterruptedException, FileNotFoundException, IOException, WavFileException {

//		logfos = new VisualOutputStream(null);
//		PrintStream logps = new PrintStream(logfos);
//		System.setOut(logps);
//		System.setErr(logps);

		System.setProperty("awt.useSystemAAFontSettings", "on");
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			File fontFile = new File(resPath + "mpc2000xl.ttf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			ge.registerFont(font);
			mpc2000font = new Font("mpc2000xl", Font.PLAIN, 16);
			Hashtable<TextAttribute, Object> map = new Hashtable
					    <TextAttribute, Object>();
			map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
			mpc2000fontu = mpc2000font.deriveFont(map);
			UIManager.put("Label.font", mpc2000font);
			UIManager.put("TextField.font", mpc2000font);
			
			fontsmall = new Font("mpc2000xl", Font.PLAIN, 8);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}

		createUI();
//		 JFrame jf = new JFrame();
//		 jf.setVisible(true);
//		 jf.pack();
//		 jf.setSize(500, 500);
//		 JTextArea jta = new
//		 JTextArea(System.getProperty("java.version")+"\n");
//		 System.out.print(System.getProperty("java.library.path") + "\n");
//		 jta.setSize(500, 500);
//		 jta.setVisible(true);
//		 jf.add(jta);
//		logfos.setTextArea(jta);
	}

	private static void createUI() throws InterruptedException, FileNotFoundException, IOException, WavFileException {
		mpc = new Mpc();
		mpc.init();
		gui = new Gui(mpc);
		mpc.startMidi();
		startUp = new StartUp();
		startUp.startUp();
		if (mpc.getAudioMidiServices().isDisabled()) {
			gui.getMainFrame().openScreen("audiomididisabled", "windowpanel");
		}
	}

	public static Gui getGui() {
		return gui;
	}

	public static UserDefaults getUserDefaults() {
		return userDefaults;
	}

	public static class VisualOutputStream extends OutputStream {

		private JTextArea jta;

		private String cache;

		public VisualOutputStream(JTextArea jta) {
			this.jta = jta;
		}

		@Override
		public void write(int b) throws IOException {
			String s = new String(new byte[] { (byte) b }, "UTF-8");
			if (jta == null) {
				cache += s;
			} else {
				jta.setText(jta.getText() + s);
				jta.repaint();
			}
		}
		
		public void setTextArea(JTextArea jta) {
			this.jta = jta;
			jta.setText(cache);
		}

	}

}