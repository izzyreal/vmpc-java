package com.mpc.gui.sampler;

import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mpc.Util;
import com.mpc.gui.Bootstrap;
import com.mpc.gui.Gui;
import com.mpc.gui.MainFrame;
import com.mpc.gui.LayeredScreen;
import com.mpc.sampler.Sampler;

public class SoundObserver implements Observer {
	private Gui gui;
	private SoundGui soundGui;
	private Sampler sampler;

	private JTextField soundNameField;
	private JLabel typeLabel;
	private JLabel rateLabel;
	private JLabel sizeLabel;

	private String csn;
	private JTextField sndField;
	private JTextField convertField;
	private String[] convertNames = { "STEREO TO MONO", "RE-SAMPLE" };
	private JTextField newFsField;
	private JTextField qualityField;
	private JTextField newBitField;
	private JTextField newNameField;

	private String[] qualityNames = { "LOW", "MED", "HIGH" };
	private String[] bitNames = { "16", "12", "8" };
	private JTextField stereoSourceField;
	private JTextField newLNameField;
	private JTextField newRNameField;
	private LayeredScreen slp;
	private JTextField lSourceField;
	private JTextField rSourceField;
	private JTextField newStNameField;

	public SoundObserver(Sampler sampler, MainFrame mainFrame)
			throws UnsupportedEncodingException {

		this.sampler = sampler;
		this.slp = mainFrame.getLayeredScreen();
		this.gui = Bootstrap.getGui();
		csn = slp.getCurrentScreenName();
		soundGui = gui.getSoundGui();
		soundGui.deleteObservers();
		soundGui.addObserver(this);
		
		if (csn.equals("sound")) {
			soundNameField = mainFrame.lookupTextField("soundname");
			typeLabel = mainFrame.lookupLabel("type");
			rateLabel = mainFrame.lookupLabel("rate");
			sizeLabel = mainFrame.lookupLabel("size");
			displaySoundName();
			displayType();
			displayRate();
			displaySize();
		}

		if (csn.equals("deletesound")) {
			sndField = mainFrame.lookupTextField("snd");

			displayDeleteSoundSnd();
		}

		if (csn.equals("convertsound")) {
			convertField = mainFrame.lookupTextField("convert");

			displayConvert();
		}

		if (csn.equals("resample")) {
			newFsField = mainFrame.lookupTextField("newfs");
			qualityField = mainFrame.lookupTextField("quality");
			newBitField = mainFrame.lookupTextField("newbit");
			newNameField = mainFrame.lookupTextField("newname");

			displayNewFs();
			displayQuality();
			displayNewBit();
			displayNewName();
		}

		if (csn.equals("stereotomono")) {
			stereoSourceField = mainFrame.lookupTextField("stereosource");
			newLNameField = mainFrame.lookupTextField("newlname");
			newRNameField = mainFrame.lookupTextField("newrname");

			displayStereoSource();
			displayNewLName();
			displayNewRName();
		}
		
		if (csn.equals("monotostereo")) {
			lSourceField = mainFrame.lookupTextField("lsource");
			rSourceField = mainFrame.lookupTextField("rsource");
			newStNameField = mainFrame.lookupTextField("newstname");
			
			displayLSource();
			displayRSource();
			displayNewStName();
		}
		
		if (csn.equals("copysound")) {
			sndField = mainFrame.lookupTextField("snd");
			newNameField = mainFrame.lookupTextField("newname");
			
			displaySnd();
			displayNewName();
		}
	}

	private void displaySnd() {
		 sndField.setText(sampler.getSoundName(soundGui.getSoundIndex()));		
	}

	private void displayLSource() {
		lSourceField.setText(sampler.getSoundName(soundGui.getSoundIndex()));
		if (sampler.getSound(soundGui.getSoundIndex()).isMono() && sampler.getSound(soundGui.getRSource()).isMono()) {
			slp.drawFunctionBoxes("monotostereo");
		} else {
			slp.drawFunctionBoxes("convertnodoit");
		}
	}

	private void displayRSource() {
		 rSourceField.setText(sampler.getSoundName(soundGui.getRSource()));		
			if (sampler.getSound(soundGui.getSoundIndex()).isMono() && sampler.getSound(soundGui.getRSource()).isMono()) {
				slp.drawFunctionBoxes("monotostereo");
			} else {
				slp.drawFunctionBoxes("convertnodoit");
			}
	}

	private void displayNewStName() {
		 newStNameField.setText(soundGui.getNewStName());		
	}

	private void displayStereoSource() {
		stereoSourceField.setText(sampler.getSoundName(soundGui
				.getSoundIndex()));
		if (sampler.getSound(soundGui.getSoundIndex()).isMono()) {
			slp.drawFunctionBoxes("convertnodoit");
		} else {
			slp.drawFunctionBoxes("stereotomono");
		}
	}

	private void displayNewLName() {
		newLNameField.setText(soundGui.getNewLName());
	}

	private void displayNewRName() {
		newRNameField.setText(soundGui.getNewRName());
	}

	private void displayNewFs() {
		newFsField.setText("" + soundGui.getNewFs());
	}

	private void displayQuality() {
		qualityField.setText(qualityNames[soundGui.getQuality()]);
	}

	private void displayNewBit() {
		newBitField.setText(bitNames[soundGui.getNewBit()]);
	}

	private void displayNewName() {
		newNameField.setText(soundGui.getNewName());
	}

	private void displayConvert() {
		convertField.setText(convertNames[soundGui.getConvert()]);
		if (soundGui.getConvert() == 0
				&& sampler.getSound(soundGui.getSoundIndex()).isMono()) {
			convertField.setText("MONO TO STEREO");
		}
	}

	private void displayDeleteSoundSnd() {
		sndField.setText(sampler.getSoundName(soundGui.getSoundIndex()));
	}

	private void displaySoundName() {
		if (soundGui.getSoundIndex() == -1) {
			soundNameField.setText("");
			return;
		}
		soundNameField
				.setText(sampler.getSoundName(soundGui.getSoundIndex()));
	}

	private void displayType() {
		if (soundGui.getSoundIndex() == -1) {
			typeLabel.setText("");
			return;
		}
		typeLabel
				.setText("Type:"
						+ (sampler.getSound(soundGui.getSoundIndex())
								.isMono() ? "MONO" : "STEREO"));
	}

	private void displayRate() {
		if (soundGui.getSoundIndex() == -1) {
			rateLabel.setText("");
			return;
		}
		rateLabel.setText("Rate: "
				+ sampler.getSound(soundGui.getSoundIndex()).getSampleRate()
				+ "Hz");
	}

	private void displaySize() {
		if (soundGui.getSoundIndex() == -1) {
			sizeLabel.setText("");
			return;
		}
		sizeLabel.setText("Size:"
				+ Util.padLeftSpace(""
						+ sampler.getSound(soundGui.getSoundIndex())
								.getSampleData().length / 500, 4) + "kbytes");
	}

	@Override
	public void update(Observable o, Object arg) {

		switch ((String) arg) {

		case "soundnumber":
			if (csn.equals("sound")) {
				displaySoundName();
				displayType();
				displayRate();
				displaySize();
			}

			if (csn.equals("deletesound")) {
				displayDeleteSoundSnd();
			}
			
			if (csn.equals("stereotomono")) {
				displayStereoSource();
			}
			
			if (csn.equals("monotostereo")) {
				displayLSource();
			}
			
			if (csn.equals("copysound")) {
				displaySnd();
//				displayNewName();
			}
			
			break;

		case "convert":
			displayConvert();
			break;

		case "newfs":
			displayNewFs();
			break;

		case "newbit":
			displayNewBit();
			break;

		case "quality":
			displayQuality();
			break;

		case "newname":
			displayNewName();
			break;

//		case "name":
//			String s = gui.getMainFrame().getFocus(
//					gui.getMainFrame().getLayeredScreen().getWindowPanel());
//			JTextField tf = gui.getMainFrame().lookupTextField(s);
//			tf.setText(gui.getSequencerWindowGui().getName()
//					.substring(Integer.parseInt(s), Integer.parseInt(s) + 1));
//			break;

		case "stereosource" :
			displayStereoSource();
			break;
			
		case "rsource" :
			if (csn.equals("monotostereo")) {
				displayRSource();
			}
			break;
		}
	}
}