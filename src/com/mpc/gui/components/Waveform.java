package com.mpc.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.mpc.gui.Bootstrap;

public class Waveform extends JComponent {

	private static final long serialVersionUID = 1L;
	private float[] sampleData;

	private int fineTune = 500;
	private float verticalZoom = 1.6f;
	private float verticalZoomThreshold = 0.06f;

	private Color background;
	private Color waveformSelected;
	private Color notWaveformSelected;
	private Color waveform;
	private Color notWaveform;

	private float increment = 1.0f;
	private long length;
	private int startPixel;
	private int endPixel = 245;

	private float highestPositive = 0f;
	private float lowestNegative = 0f;

	public Waveform() {
		background = Bootstrap.lcdOff;
		waveform = Bootstrap.lcdOn;
		notWaveform = Bootstrap.lcdOff;
		waveformSelected = Bootstrap.lcdOff;
		notWaveformSelected = Bootstrap.lcdOn;
	}

	public void setSampleData(float[] sampleData) {
		this.sampleData = sampleData;
		if (sampleData == null) {
			startPixel = 0;
			endPixel = 0;
			length = 0;
		} else {
			length = sampleData.length;
			if (length > 245) {
				increment = (float) ((length / 244.2));
			} else {
				increment = 1;
			}
		}
		repaint();
	}

	public void setSelectionStart(int i) {
		startPixel = (int) (i / increment);
		repaint();
	}

	public void setSelectionEnd(int i) {
		endPixel = (int) (i / increment) +1;
		repaint();
	}

	public void setSelection(int start, int end) {
		startPixel = (int) (start / increment);
		endPixel = (int) (start / increment)+1;
		repaint();
	}

	public void paintComponent(Graphics g1) {
		if (sampleData != null) {
			Graphics2D g = (Graphics2D) g1;

			BufferedImage waveformImg = new BufferedImage(245, 27,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig = waveformImg.createGraphics();
			int currentSamplePos;
			ig.setStroke(new BasicStroke(1));
			ig.setColor(notWaveform);

			if (startPixel != 0) {
				ig.drawRect(0, 0, startPixel, 27);
				ig.fillRect(0, 0, startPixel, 27);

				ig.setColor(waveform);

				for (int i = 0; i < startPixel; i++) {
					currentSamplePos = (int) (i * increment);

					if (increment > fineTune) {
						lowestNegative = 0;
						highestPositive = 0;
						for (int j = 0; j < fineTune; j++) {
							if (currentSamplePos + j >= sampleData.length)
								break;
								float tempSampleValue = sampleData[currentSamplePos
									+ j];
							if (tempSampleValue < 0
									&& tempSampleValue < lowestNegative)
								lowestNegative = tempSampleValue;
							if (tempSampleValue > 0
									&& tempSampleValue > highestPositive)
								highestPositive = tempSampleValue;
							if (lowestNegative > 0 - verticalZoomThreshold) {
								lowestNegative *= verticalZoom;
							}
							if (highestPositive < verticalZoomThreshold) {
								highestPositive *= verticalZoom;
							}
						}

						int topLineLength = (int) (13 * highestPositive);
						int bottomLineLength = (int) (13 * lowestNegative);

						if (topLineLength != 0) {
							ig.drawLine(i, 13, i, 13 - topLineLength);
						}

						if (bottomLineLength != 0) {
							ig.drawLine(i, 13, i, 13 - bottomLineLength);
						}
					} else {
						float currentSampleValue = sampleData[currentSamplePos];
						if ((currentSampleValue < 0f && currentSampleValue > 0 - verticalZoomThreshold)
								|| (currentSampleValue > 0f && currentSampleValue < verticalZoomThreshold)) {
							currentSampleValue *= 2;
						}
						int lineLength = (int) (13 * currentSampleValue);
						if (lineLength != 0) {
							ig.drawLine(i, 13, i, 13 - lineLength);
						}
					}
				}
			}

			ig.setColor(notWaveformSelected);
			ig.drawRect(startPixel, 0, endPixel-1, 27);
			ig.fillRect(startPixel, 0, endPixel-1, 27);

			ig.setColor(waveformSelected);

			for (int i = startPixel; i < endPixel; i++) {
				currentSamplePos = (int) (i * increment);
				if (increment > fineTune) {
					lowestNegative = 0;
					highestPositive = 0;
					for (int j = 0; j < fineTune; j++) {
						if (currentSamplePos + j >= sampleData.length)
							break;
						float tempSampleValue = sampleData[currentSamplePos
								+ j];
						if (tempSampleValue < 0
								&& tempSampleValue < lowestNegative)
							lowestNegative = tempSampleValue;
						if (tempSampleValue > 0
								&& tempSampleValue > highestPositive)
							highestPositive = tempSampleValue;
						if (lowestNegative > 0 - verticalZoomThreshold) {
							lowestNegative *= verticalZoom;
						}
						if (highestPositive < verticalZoomThreshold) {
							highestPositive *= verticalZoom;
						}
					}

					int topLineLength = (int) (13 * highestPositive);
					int bottomLineLength = (int) (13 * lowestNegative);
				
					if (topLineLength != 0) {
						ig.drawLine(i, 13, i, 13 - topLineLength);
					}

					if (bottomLineLength != 0) {
						ig.drawLine(i, 13, i, 13 - bottomLineLength);
					}
				} else {
					float currentSampleValue = sampleData[currentSamplePos];
					if ((currentSampleValue < 0f && currentSampleValue > 0 - verticalZoomThreshold)
							|| (currentSampleValue > 0f && currentSampleValue < verticalZoomThreshold)) {
						currentSampleValue *= 2;
					}
					int lineLength = (int) (13 * currentSampleValue);
					if (lineLength != 0) {
						ig.drawLine(i, 13, i, 13 - lineLength);
					}
				}
			}

			if (endPixel != 245) {
				ig.setColor(background);
				ig.drawRect(endPixel, 0, 244, 27);
				ig.fillRect(endPixel, 0, 244, 27);
				ig.setColor(waveform);
				for (int i = endPixel; i < 245; i++) {
					currentSamplePos = (int) (i * increment);

					if (increment > fineTune) {
						lowestNegative = 0;
						highestPositive = 0;
						for (int j = 0; j < fineTune; j++) {
							if (currentSamplePos + j >= sampleData.length)
								break;
								float tempSampleValue = sampleData[currentSamplePos
									+ j];
							if (tempSampleValue < 0
									&& tempSampleValue < lowestNegative)
								lowestNegative = tempSampleValue;
							if (tempSampleValue > 0
									&& tempSampleValue > highestPositive)
								highestPositive = tempSampleValue;
							if (lowestNegative > 0 - verticalZoomThreshold) {
								lowestNegative *= verticalZoom;
							}
							if (highestPositive < verticalZoomThreshold) {
								highestPositive *= verticalZoom;
							}
						}

						int topLineLength = (int) (13 * highestPositive);
						int bottomLineLength = (int) (13 * lowestNegative);

						if (topLineLength != 0) {
							ig.drawLine(i, 13, i, 13 - topLineLength);
						}

						if (bottomLineLength != 0) {
							ig.drawLine(i, 13, i, 13 - bottomLineLength);
						}
					} else {
						float currentSampleValue = sampleData[currentSamplePos];
						if ((currentSampleValue < 0f && currentSampleValue > 0 - verticalZoomThreshold)
								|| (currentSampleValue > 0f && currentSampleValue < verticalZoomThreshold)) {
							currentSampleValue *= 2;
						}
						int lineLength = (int) (13 * currentSampleValue);
						if (lineLength != 0) {
							ig.drawLine(i, 13, i, 13 - lineLength);
						}
					}
				}
			}
			g.drawImage(waveformImg, AffineTransform.getScaleInstance(2, 2),
					this);
		}
	}

}