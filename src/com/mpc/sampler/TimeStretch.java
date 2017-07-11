package com.mpc.sampler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mpc.disk.MpcFile;
import com.mpc.file.wav.MpcWavFile;
import com.mpc.file.wav.WavFileException;

public class TimeStretch {
	private final float TWO_PI = (float) (2 * Math.PI);

	private int oldBlockSize = 1300;
	private float overlap = 0.10f;

	private float[] processedData;
	private float[] newSampleData;


	private int fadeLengthExpand;
	private int specialFadeLength;

	private int sampleRate;

	public TimeStretch(float[] sampleData, float ratio, int sampleRate) {
		this.sampleRate = sampleRate;
		// Calculate into how many segments we want to divide the sampleData.
		// When compressing, these segments shall each be trimmed.
		int numberOfBlocks = (int) Math.ceil(sampleData.length / oldBlockSize);
		if (ratio > 1) {
			overlap = 0.50f;
		}
		// Calculate the block size used in compression.
		int newBlockSizeCompress = (int) (oldBlockSize * (ratio + (ratio * overlap)));

		// Calculate the block size used in expansion.
		int newBlockSizeExpand = (int) (oldBlockSize * ratio);

		// Calculate the fade length used in compression.
		int fadeLengthCompress = (int) (oldBlockSize * ratio * overlap);

		// Calculate the fade length used in expansion.
		fadeLengthExpand = (int) (oldBlockSize * overlap);

		// Calculate the new sample length.
		int newLength = 0;
		if (ratio > 1) {
			newLength = (int) (numberOfBlocks * newBlockSizeExpand);
		}
		if (ratio < 1) {
			newLength = (int) (numberOfBlocks * (newBlockSizeCompress - fadeLengthCompress));
			newLength += fadeLengthCompress;
		}

		// Instantiate final sample data array.
		newSampleData = new float[newLength];

		// Instantiate container for the original segments
		List<float[]> originalSegments = new ArrayList<float[]>();

		// Instantiate counter sample data. Will be recycled.
		int sampleCounter = 0;

		// Divide sampleData in segments and add each segment to the list.
		float[] block = new float[oldBlockSize];
		for (float f : sampleData) {
			block[sampleCounter++] = f;
			if (sampleCounter == oldBlockSize) {
				sampleCounter = 0;
				originalSegments.add(block);
				block = new float[oldBlockSize];
			}
		}

		if (ratio < 1) {
			for (float[] fa : originalSegments) {
				float ampCoEf = 0f;
				float increment = (float) (1.0 / fadeLengthCompress);
				for (int i = 0; i < fadeLengthCompress; i++) {
					fa[i] *= ampCoEf;
					ampCoEf += increment;
				}

				increment -= (2.0 * increment);

				for (int i = newBlockSizeCompress - fadeLengthCompress; i < newBlockSizeCompress; i++) {
					if (fa.length > i) {
						fa[i] *= ampCoEf;
					}
					ampCoEf += increment;
				}
			}

			sampleCounter = 0;
			for (int j = 0; j < fadeLengthCompress; j++) {
				newSampleData[sampleCounter++] = originalSegments.get(0)[j];
			}

			for (int i = 0; i < numberOfBlocks; i++) {
				float[] fa = originalSegments.get(i);
				float[] nextFa;
				if (i != numberOfBlocks - 1) {
					nextFa = originalSegments.get(i + 1);
				} else {
					nextFa = null;
				}

				for (int j = fadeLengthCompress; j < newBlockSizeCompress
						- fadeLengthCompress; j++) {
					newSampleData[sampleCounter++] = fa[j];
				}

				int nextFaCounter = 0;
				for (int j = newBlockSizeCompress - fadeLengthCompress; j < newBlockSizeCompress; j++) {
					if (nextFa != null) {
						if (fa.length > j && nextFa.length > nextFaCounter) {
							newSampleData[sampleCounter++] = fa[j]
									+ nextFa[nextFaCounter++];
						}
					} else {
						if (fa.length > j) {
							newSampleData[sampleCounter++] = fa[j];
						}
					}
				}
			}

			processedData = newSampleData;
		}

		if (ratio > 1) {
			specialFadeLength = 0;
			// Calculate number of whole repetitions required to fill up the new
			// block size
			int repetitions = (int) Math.floor(newBlockSizeExpand
					/ (oldBlockSize - fadeLengthExpand));

			// Calculate how big the leftover is
			int trimmedSegmentLength = newBlockSizeExpand + fadeLengthExpand
					- (repetitions * (oldBlockSize - fadeLengthExpand));

			int totalNrOfRepetitions = repetitions;
			if (trimmedSegmentLength != 0)
				totalNrOfRepetitions++;

			List<float[]> repeatedSegments = new ArrayList<float[]>();
			int segmentCounter = 0;

			for (float[] fa : originalSegments) {

				for (int i = 0; i < repetitions; i++) {

					float[] faCopy = new float[fa.length];
					sampleCounter = 0;
					for (float f : fa) {
						faCopy[sampleCounter++] = f;
					}

					faCopy = fade(0f, 1f, 0, fadeLengthExpand, faCopy);
					faCopy = fade(1f, 0f, oldBlockSize - fadeLengthExpand,
							fadeLengthExpand, faCopy);
					repeatedSegments.add(faCopy);
					// writeWavFile(faCopy, segmentCounter);
					segmentCounter++;
				}

				sampleCounter = 0;
				float[] faCopy = new float[trimmedSegmentLength];

				for (float f : fa) {
					faCopy[sampleCounter++] = f;
					if (sampleCounter == trimmedSegmentLength)
						break;
				}

				if (trimmedSegmentLength != 0) {
					int trimmedSegmentStartIndex = repetitions
							* (oldBlockSize - fadeLengthExpand);

					specialFadeLength = fadeLengthExpand;

					specialFadeLength = newBlockSizeExpand
							- trimmedSegmentStartIndex;

					float fadeToAmplitude = specialFadeLength
							/ (float) fadeLengthExpand;
					faCopy = fade(0, fadeToAmplitude, 0, specialFadeLength,
							faCopy);

					faCopy = fade(fadeToAmplitude, fadeToAmplitude,
							specialFadeLength, trimmedSegmentLength
									- (2 * specialFadeLength), faCopy);

					faCopy = fade(fadeToAmplitude, 0, trimmedSegmentLength
							- specialFadeLength, specialFadeLength, faCopy);

					repeatedSegments.add(faCopy);
					// writeWavFile(faCopy, segmentCounter);
					segmentCounter++;
				}
			}

			List<float[]> consolidatedRepeatedSegments = new ArrayList<float[]>();

			for (int j = 0; j < numberOfBlocks; j++) {

				float[] consolidated = new float[newBlockSizeExpand
						+ fadeLengthExpand];

				sampleCounter = 0;

				for (int k = 0; k < repetitions; k++) {

					float[] repeatedOriginalSegment = repeatedSegments
							.get((j * totalNrOfRepetitions) + k);

					float[] nextSegment = repeatedSegments
							.get((j * totalNrOfRepetitions) + k + 1);

					if (k == 0) {
						for (int i = 0; i < fadeLengthExpand; i++) {
							consolidated[sampleCounter++] = repeatedOriginalSegment[i];
						}
					}

					for (int i = fadeLengthExpand; i < oldBlockSize
							- fadeLengthExpand; i++) {
						consolidated[sampleCounter++] = repeatedOriginalSegment[i];
					}

					int nextSegmentCounter = 0;

					for (int i = oldBlockSize - fadeLengthExpand; i < oldBlockSize; i++) {
						consolidated[sampleCounter++] = repeatedOriginalSegment[i]
								+ nextSegment[nextSegmentCounter++];
					}
				}

				if (trimmedSegmentLength != 0) {
					float[] lastSegment = repeatedSegments
							.get((j * totalNrOfRepetitions)
									+ (totalNrOfRepetitions - 1));

					for (int i = 0; i < specialFadeLength; i++) {
						consolidated[sampleCounter++] = lastSegment[i
								+ fadeLengthExpand];
					}
				}
				consolidatedRepeatedSegments.add(consolidated);
			}

			sampleCounter = 0;

			for (int i = 0; i < fadeLengthExpand; i++) {
				newSampleData[sampleCounter++] = consolidatedRepeatedSegments
						.get(0)[i];
			}

			for (int i = 0; i < numberOfBlocks; i++) {
				float[] currentBlock = consolidatedRepeatedSegments.get(i);
				float[] nextBlock = null;

				if (consolidatedRepeatedSegments.size() > i + 1) {
					nextBlock = consolidatedRepeatedSegments.get(i + 1);
				}

				int currentBlockCounter = fadeLengthExpand;

				for (int j = fadeLengthExpand; j < newBlockSizeExpand; j++) {
					newSampleData[sampleCounter++] = currentBlock[currentBlockCounter++];
				}

				for (int j = 0; j < fadeLengthExpand; j++) {
					if (nextBlock != null) {
						newSampleData[sampleCounter++] = currentBlock[currentBlockCounter++]
								+ nextBlock[j];
					} else {

						if (sampleCounter < newSampleData.length) {
							newSampleData[sampleCounter++] = currentBlock[currentBlockCounter++];
						}
					}
				}
			}
			//
			// int currentBlockCounter = 0;
			// for (int i=0;i<fadeLengthExpand;i++) {
			// newSampleData[sampleCounter++] =
			// consolidatedRepeatedSegments.get(consolidatedRepeatedSegments.size()-1)[currentBlockCounter++];
			// }
			//
			processedData = newSampleData;
			// writeWavFile(processedData, segmentCounter);
			// segmentCounter++;
		}
	}

	public float[] getProcessedData() {
		return processedData;
	}

	private float[] fade(float from, float to, int startIndex, int length,
			float[] fa) {
		float[] temp = new float[fa.length];
		for (int i = 0; i < fa.length; i++) {
			temp[i] = fa[i];
		}

		int hannOffset = 0;
		int hannLength = fadeLengthExpand * 2;

		float ampCoEf = from;

		if (from == to) {
			for (int i = startIndex; i < startIndex + length; i++) {
				fa[i] *= ampCoEf;
			}
			return fa;
		}

		boolean descending = false;
		if (from > to) {
			descending = true;
			if (from < 1) {
				hannOffset = (int) (from * fadeLengthExpand);
			}
		}

		for (int i = 0; i < length; i++) {
			if (descending) {
				ampCoEf = value(hannLength, i + fadeLengthExpand + hannOffset);
			} else {
				ampCoEf = value(hannLength, i);
			}
			temp[startIndex + i] *= ampCoEf;
		}
		return temp;
	}

	protected float value(int length, int index) {
		return 0.5f * (1f - (float) Math.cos(TWO_PI * index / (length - 1f)));
	}
}