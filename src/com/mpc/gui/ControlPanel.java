package com.mpc.gui;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mpc.Util;
import com.mpc.controls.datawheel.DataWheel;
import com.mpc.controls.slider.Slider;
import com.mpc.nvram.NvRam;

import imagemap.HTMLParser;
import imagemap.ShapeList;

@SuppressWarnings("serial")

public class ControlPanel extends JPanel implements Observer {

	private DataWheel dataWheel;
	private Slider slider;

	private int recordKnob = NvRam.getRecordLevel();
	private int volumeKnob = NvRam.getMasterLevel();

	private int sliderIndex = 99 - (int) (NvRam.getSlider() / 1.28);

	private ShapeList controls;

	private Image[] dataWheelImages;
	private Image[] sliderImages;

	private Image[] recordKnobImages;
	private Image[] volumeKnobImages;

	private JLabel dataWheelLabel;

	private int dataWheelIndex = 0;
	private JLabel sliderLabel;
	private JLabel recKnobLabel;
	private JLabel volKnobLabel;

	public ControlPanel() {
		boolean flyWheelEmu = false;
		dataWheel = new DataWheel(flyWheelEmu);
		slider = new Slider();
		FileReader input = null;
		String htmlCode = "";
		BufferedReader bufRead = null;
		try {
			input = new FileReader(Bootstrap.resPath + "vmpcmap.html");
			bufRead = new BufferedReader(input);
			String myLine = null;
			while ((myLine = bufRead.readLine()) != null)
				if (myLine.trim().length() != 0) htmlCode += myLine;
			bufRead.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		HTMLParser hp = new HTMLParser(htmlCode);
		controls = new ShapeList();
		hp.createShapeList(controls);
		// System.out.println("sl size " + sl.size());
		// for (int i=0;i<sl.size();i++) {
		// Shape s = sl.get_shape(i);
		// System.out.println(s.inside(5, 10) + s.get_href());
		// }

		dataWheelImages = new Image[100];
		sliderImages = new Image[100];
		recordKnobImages = new Image[100];
		volumeKnobImages = new Image[100];

		for (int i = 0; i < 100; i++)
			try {
				dataWheelImages[i] = ImageIO
						.read(new File(Bootstrap.resPath + "/gui/datawheel/" + Util.padLeft4Zeroes(i) + ".png"));
				sliderImages[i] = ImageIO
						.read(new File(Bootstrap.resPath + "/gui/slider/" + Util.padLeft4Zeroes(99 - i) + ".png"));
				recordKnobImages[i] = ImageIO
						.read(new File(Bootstrap.resPath + "/gui/recknob/" + Util.padLeft4Zeroes(i) + ".png"));
				volumeKnobImages[i] = ImageIO
						.read(new File(Bootstrap.resPath + "/gui/volknob/" + Util.padLeft4Zeroes(i) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public DataWheel getDataWheel() {
		return dataWheel;
	}

	public Slider getSlider() {
		return slider;
	}

	public void init() {
		this.setLayout(null);
		setLocation(10 - 17, -90);
		setSize(1300, 1000);
		setVisible(true);
		setOpaque(false);

		dataWheelLabel = new JLabel(new ImageIcon(dataWheelImages[dataWheelIndex]));
		dataWheelLabel.setLocation(379, 421);
		dataWheelLabel.setSize(171, 171);
		add(dataWheelLabel);
		dataWheel.addObserver(this);

		sliderLabel = new JLabel(new ImageIcon(sliderImages[sliderIndex]));
		sliderLabel.setLocation(34, 673);
		sliderLabel.setSize(128, 247);
		add(sliderLabel);
		slider.addObserver(this);

		recKnobLabel = new JLabel(new ImageIcon(recordKnobImages[recordKnob]));
		recKnobLabel.setLocation(1016, 188);
		recKnobLabel.setSize(72, 73);
		add(recKnobLabel);

		volKnobLabel = new JLabel(new ImageIcon(volumeKnobImages[volumeKnob]));
		volKnobLabel.setLocation(1139, 186);
		volKnobLabel.setSize(74, 75);
		add(volKnobLabel);
	}

	public ShapeList getControlShapes() {
		return controls;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof DataWheel) {
			dataWheelIndex += (Integer) arg1;
			if (dataWheelIndex < 0) dataWheelIndex = 99;
			if (dataWheelIndex > 99) dataWheelIndex = 0;
			dataWheelLabel.setIcon(new ImageIcon(dataWheelImages[dataWheelIndex]));
		} else if (arg0 instanceof Slider) {
			sliderIndex = 99 - (int) ((Integer) (arg1) / 1.28);
			sliderLabel.setIcon(new ImageIcon(sliderImages[sliderIndex]));
		}
	}

	public void setVolume(int i) {
		if (i < 0 || i > 99) return;
		Bootstrap.getGui().getMpc().getAudioMidiServices().setMasterLevel(i);
		volumeKnob = i;
		volKnobLabel.setIcon(new ImageIcon(volumeKnobImages[volumeKnob]));
	}

	public int getVolume() {
		return volumeKnob;
	}

	public void setRecord(int i) {
		if (i < 0 || i > 99) return;
		recordKnob = i;
		recKnobLabel.setIcon(new ImageIcon(recordKnobImages[recordKnob]));
	}

	public int getRecord() {
		return recordKnob;
	}

}