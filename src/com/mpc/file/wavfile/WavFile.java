/*
 * Originally developed for:
 * SE1021
 * Winter 2015-2016
 * Lab 1
 * Name: Dr. Chris Taylor
 * Created: 11/29/2015
 */

package com.mpc.file.wavfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple class for reading, writing, and accessing audio samples
 * associated with a .wav file.
 *
 * The class is based on a similar class developed by A. Greensted.
 * http://www.labbookpages.co.uk/audio/javaWavFiles.html
 * <p>
 * The file format is based on information from:
 * http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
 * http://www.blitter.com/~russtopia/MIDI/~jglatt/tech/wave.htm
 * </p>
 * @author edu.msoe@taylor and A.Greensted
 * @version 20151202.4
 */
public class WavFile {
    private enum IOState {READING, WRITING, CLOSED}
    private final static int BUFFER_SIZE = 4096;

    private final static int FMT_CHUNK_ID = 0x20746D66;
    private final static int DATA_CHUNK_ID = 0x61746164;
    private final static int RIFF_CHUNK_ID = 0x46464952;
    private final static int RIFF_TYPE_ID = 0x45564157;

    private File file;                 // File that will be read from or written to
    private IOState ioState;           // Specifies the IO State of the Wav File (used for sanity
                                       // checking)
    private int bytesPerSample;        // Number of bytes required to store a single sample
    private long numFrames;            // Number of frames within the data section
    private FileOutputStream oStream;  // Output stream used for writing data
    private FileInputStream iStream;   // Input stream used for reading data
    private double floatScale;         // Scaling factor used for int <-> float conversion                
    private double floatOffset;        // Offset factor used for int <-> float conversion                
    private boolean wordAlignAdjust;   // Specify if an extra byte at the end of the data chunk is
                                       // required for word alignment

    // Wav Header
    private int numChannels;           // 2 bytes unsigned, 0x0001 (1) to 0xFFFF (65,535)
    private long sampleRate;           // 4 bytes unsigned, 0x00000001 (1) to 0xFFFFFFFF
                                       // (4,294,967,295)
                                       // Although a java int is 4 bytes, it is signed, so need to
                                       // use a long
    private int blockAlign;            // 2 bytes unsigned, 0x0001 (1) to 0xFFFF (65,535)
    private int validBits;             // 2 bytes unsigned, 0x0002 (2) to 0xFFFF (65,535)

    // Buffering
    private byte[] innerBuffer;        // Local innerBuffer used for IO
    private int bufferPointer;         // Points to the current position in innerBuffer
    private int bytesRead;             // Bytes read after last read into innerBuffer
    private long frameCounter;         // Current number of frames read or written

    public int getNumChannels() {
        return numChannels;
    }

    public long getNumFrames() {
        return numFrames;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public int getValidBits() {
        return validBits;
    }

    /**
     * This constructor should not be called directly.
     */
    private WavFile() {
        innerBuffer = new byte[BUFFER_SIZE];
    }

    /**
     * Constructor for creating a WavFile object to be read from a file.
     * @param filename The filename of the file to be read into this object
     */
    public WavFile(String filename) {
        this();
        this.file = new File(filename);

        try {
            // Create a new file input stream for reading file data
            iStream = new FileInputStream(file);

            // Read the first 12 bytes of the file
            int bytesRead = iStream.read(innerBuffer, 0, 12);
            if(bytesRead!=12) {
                throw new RuntimeException("Not enough wav file bytes for header");
            }
        } catch (IOException e) {
            throw new RuntimeException("Encountered and error reading: " + e.getMessage());
        }

        // Extract parts from the header
        long riffChunkID = getLE(innerBuffer, 0, 4);
        long chunkSize = getLE(innerBuffer, 4, 4);
        long riffTypeID = getLE(innerBuffer, 8, 4);

        // Check the header bytes contains the correct signature
        if(riffChunkID!=RIFF_CHUNK_ID) {
            throw new RuntimeException("Invalid Wav Header data, incorrect riff chunk ID");
        }
        if(riffTypeID!=RIFF_TYPE_ID) {
            throw new RuntimeException("Invalid Wav Header data, incorrect riff type ID");
        }

        // Check that the file size matches the number of bytes listed in header
        if(this.file.length()!=chunkSize+8) {
            throw new RuntimeException("Header chunk size (" + chunkSize +
                    ") does not match file size (" + this.file.length() + ")");
        }

        boolean foundFormat = false;
        boolean foundData = false;

        // Search for the Format and Data Chunks
        while(!foundData) {
            // Read the first 8 bytes of the chunk (ID and chunk size)
            try {
                bytesRead = iStream.read(innerBuffer, 0, 8);
            } catch (IOException e) {
                throw new RuntimeException("Encountered and error reading: " + e.getMessage());
            }
            if(bytesRead==-1) {
                throw new RuntimeException("Reached end of file without finding format chunk");
            }
            if(bytesRead!=8) {
                throw new RuntimeException("Could not read chunk header");
            }

            // Extract the chunk ID and Size
            long chunkID = getLE(innerBuffer, 0, 4);
            chunkSize = getLE(innerBuffer, 4, 4);

            // Word align the chunk size
            // chunkSize specifies the number of bytes holding data. However,
            // the data should be word aligned (2 bytes) so we need to calculate
            // the actual number of bytes in the chunk
            long numChunkBytes = (chunkSize%2 == 1) ? chunkSize+1 : chunkSize;

            if(chunkID==FMT_CHUNK_ID) {
                // Flag that the format chunk has been found
                foundFormat = true;

                // Read in the header info
                try {
                    iStream.read(innerBuffer, 0, 16);
                } catch (IOException e) {
                    throw new RuntimeException("Encountered and error reading: " + e.getMessage());
                }

                // Check this is uncompressed data
                int compressionCode = (int)getLE(innerBuffer, 0, 2);
                if(compressionCode!=1) {
                    throw new RuntimeException("Compression Code " + compressionCode +
                            " not supported");
                }

                // Extract the format information
                numChannels = (int)getLE(innerBuffer, 2, 2);
                sampleRate = getLE(innerBuffer, 4, 4);
                blockAlign = (int)getLE(innerBuffer, 12, 2);
                validBits = (int)getLE(innerBuffer, 14, 2);

                if(numChannels==0) {
                    throw new RuntimeException("Number of channels specified in header is " +
                            "equal to zero");
                }
                if(blockAlign==0) {
                    throw new RuntimeException("Block Align specified in header is equal to zero");
                }
                if(validBits<2) {
                    throw new RuntimeException("Valid Bits specified in header is less than 2");
                }
                if(validBits>64) {
                    throw new RuntimeException("Valid Bits specified in header is greater than " +
                            "64, this is greater than a long can hold");
                }

                // Calculate the number of bytes required to hold 1 sample
                bytesPerSample = (validBits + 7) / 8;
                if(bytesPerSample*numChannels!=blockAlign) {
                    throw new RuntimeException("Block Align does not agree with bytes required " +
                            "for validBits and number of channels");
                }

                // Account for number of format bytes and then skip over
                // any extra format bytes
                numChunkBytes -= 16;
                if(numChunkBytes>0) {
                    try {
                        iStream.skip(numChunkBytes);
                    } catch (IOException e) {
                        throw new RuntimeException("Encountered and error reading: " +
                                e.getMessage());
                    }
                }
            } else if(chunkID==DATA_CHUNK_ID) {
                // Check if we've found the format chunk,
                // If not, throw an exception as we need the format information
                // before we can read the data chunk
                if(!foundFormat) {
                    throw new RuntimeException("Data chunk found before Format chunk");
                }

                // Check that the chunkSize (wav data length) is a multiple of the
                // block align (bytes per frame)
                if(chunkSize%blockAlign!=0) {
                    throw new RuntimeException("Data Chunk size is not multiple of Block Align");
                }

                // Calculate the number of frames
                numFrames = chunkSize / blockAlign;

                // Flag that we've found the wave data chunk
                foundData = true;
            } else {
                // If an unknown chunk ID is found, just skip over the chunk data
                try {
                    iStream.skip(numChunkBytes);
                } catch (IOException e) {
                    throw new RuntimeException("Encountered and error reading: " + e.getMessage());
                }
            }
        }

        // Throw an exception if no data chunk has been found
        if(!foundData) {
            throw new RuntimeException("Did not find a data chunk");
        }

        // Calculate the scaling factor for converting to a normalised double
        if(validBits>8) {
            // If more than 8 validBits, data is signed
            // Conversion required dividing by magnitude of max negative value
            floatOffset = 0;
            floatScale = 1 << (validBits - 1);
        } else {
            // Else if 8 or less validBits, data is unsigned
            // Conversion required dividing by max positive value
            floatOffset = -1;
            floatScale = 0.5 * ((1 << validBits) - 1);
        }

        bufferPointer = 0;
        this.bytesRead = 0;
        frameCounter = 0;
        ioState = IOState.READING;
    }

    /**
     * Constructor for creating a WavFile object to be written to a file.
     * @param filename The filename of the file this object should be written to
     * @param numChannels The number of channels of audio
     * @param numFrames The number of frames of audio
     * @param validBits The number of valid bits
     * @param sampleRate The sample rate for the audio samples
     */
    public WavFile(String filename, int numChannels, long numFrames, int validBits,
                   long sampleRate) {
        this();
        this.file = new File(filename);
        this.numChannels = numChannels;
        this.numFrames = numFrames;
        this.sampleRate = sampleRate;
        bytesPerSample = (validBits + 7) / 8;
        blockAlign = this.bytesPerSample * numChannels;
        this.validBits = validBits;

        // Sanity check arguments
        if(numChannels<1 || numChannels>65535) {
            throw new RuntimeException("Illegal number of channels, valid range 1 to 65536");
        }
        if(numFrames<0) {
            throw new RuntimeException("Number of frames must be positive");
        }
        if(validBits<2 || validBits>65535) {
            throw new RuntimeException("Illegal number of valid bits, valid range 2 to 65536");
        }
        if(sampleRate<0) {
            throw new RuntimeException("Sample rate must be positive");
        }

        try {
            // Create output stream for writing data
            oStream = new FileOutputStream(file);

            // Calculate the chunk sizes
            long dataChunkSize = blockAlign * numFrames;
            long mainChunkSize = 4 +    // Riff Type
                    8 +    // Format ID and size
                    16 +    // Format data
                    8 +     // Data ID and size
                    dataChunkSize;

            // Chunks must be word aligned, so if odd number of audio data bytes
            // adjust the main chunk size
            if (dataChunkSize % 2 == 1) {
                mainChunkSize += 1;
                wordAlignAdjust = true;
            } else {
                wordAlignAdjust = false;
            }

            // Set the main chunk size
            putLE(RIFF_CHUNK_ID, innerBuffer, 0, 4);
            putLE(mainChunkSize, innerBuffer, 4, 4);
            putLE(RIFF_TYPE_ID, innerBuffer, 8, 4);

            // Write out the header
            oStream.write(innerBuffer, 0, 12);

            // Put format data in innerBuffer
            long averageBytesPerSecond = sampleRate * blockAlign;

            putLE(FMT_CHUNK_ID, innerBuffer, 0, 4);           // Chunk ID
            putLE(16, innerBuffer, 4, 4);                     // Chunk Data Size
            putLE(1, innerBuffer, 8, 2);                      // Compression Code (Uncompressed)
            putLE(numChannels, innerBuffer, 10, 2);           // Number of channels
            putLE(sampleRate, innerBuffer, 12, 4);            // Sample Rate
            putLE(averageBytesPerSecond, innerBuffer, 16, 4); // Average Bytes Per Second
            putLE(blockAlign, innerBuffer, 20, 2);            // Block Align
            putLE(validBits, innerBuffer, 22, 2);             // Valid Bits

            // Write Format Chunk
            oStream.write(innerBuffer, 0, 24);

            // Start Data Chunk
            putLE(DATA_CHUNK_ID, innerBuffer, 0, 4);          // Chunk ID
            putLE(dataChunkSize, innerBuffer, 4, 4);          // Chunk Data Size

            // Write Format Chunk
            oStream.write(innerBuffer, 0, 8);
        } catch (IOException e) {
            throw new RuntimeException("Problems with IO: " + e.getMessage());
        }

        // Calculate the scaling factor for converting to a normalised double
        if(this.validBits>8) {
            // If more than 8 validBits, data is signed
            // Conversion required multiplying by magnitude of max positive value
            floatOffset = 0;
            floatScale = Long.MAX_VALUE >> (64 - this.validBits);
        } else {
            // Else if 8 or less validBits, data is unsigned
            // Conversion required dividing by max positive value
            floatOffset = 1;
            floatScale = 0.5 * ((1 << this.validBits) - 1);
        }

        // Finally, set the IO State
        bufferPointer = 0;
        bytesRead = 0;
        frameCounter = 0;
        ioState = IOState.WRITING;
    }

    /**
     * Gets all of the audio samples for the wav file
     * <p>This method should only be called once.</p>
     * <p>Returns null if an IO exception was encountered.</p>
     * @return All of the audio samples for the wav file or null (if an IOException was thrown)
     */
    public ArrayList<Double> getSamples() {
        ArrayList<Double> samples = new ArrayList<>();
        try {
            double[][] buffer = new double[numChannels][BUFFER_SIZE];
            int framesRead = 0;
            do {
                framesRead = readFrames(buffer, BUFFER_SIZE);
                for (int frame=0; frame<framesRead; ++frame) {
                    for (int channel=0; channel<numChannels; ++channel) {
                        samples.add(buffer[channel][frame]);
                    }
                }
            } while (framesRead!=0);
        } catch (IOException e) {
            samples = null;
        }
        return samples;
    }

    /**
     * Writes the audio samples passed to the wav file
     * <p>This method should only be called once.</p>
     * @param samples The audio samples for the wav file
     * @return returns true if successful, false if not (IOException was encountered)
     */
    public boolean setSamples(ArrayList<Double> samples) {
        double[] buffer = new double[samples.size()];
        for(int i=0; i<buffer.length; ++i) {
            buffer[i] = samples.get(i);
        }
        boolean successful = true;
        try{
            writeFrames(buffer, samples.size()/numChannels);
        } catch (IOException e) {
            successful = false;
        }
        return successful;
    }

    /**
     * Frees up resources associated with the object and releases
     * the file associated with the object
     * @return returns true if successful, false if not (IOException was encountered)
     */
    public boolean close() {
        boolean successful = true;
        // Close the input stream and set to null
        if(iStream!=null) {
            try {
                iStream.close();
            } catch (IOException e) {
                successful = false;
            }
            iStream = null;
        }

        if(oStream!=null) {
            try {
                // Write out anything still in the innerBuffer
                if (bufferPointer > 0) {
                    oStream.write(innerBuffer, 0, bufferPointer);
                }

                // If an extra byte is required for word alignment, add it to the end
                if (wordAlignAdjust) {
                    oStream.write(0);
                }
            } catch (IOException e) {
                successful = false;
            }

            try {
                oStream.close();
            } catch (IOException e) {
                successful = false;
            }
            oStream = null;
        }

        ioState = IOState.CLOSED;
        return successful;
    }

    @Override
    public String toString() {
        return "File: " + file
                + "\nChannels: " + numChannels + ", Frames: " + numFrames
                + "\nIO State: " + ioState
                + "\nSample Rate: " + sampleRate + ", Block Align: " + blockAlign
                + "\nValid Bits: " + validBits + ", Bytes per sample: " + bytesPerSample;
    }

    // Get and Put little endian data from innerBuffer
    // ------------------------------------------------

    /**
     * Get little endian data from the innerBuffer
     * @param buffer The buffer with little endian data
     * @param position Position in the buffer
     * @param numBytes Number of bytes to be converted to a long
     * @return The little endian representation of data from the buffer
     */
    private static long getLE(byte[] buffer, int position, int numBytes) {
        --numBytes;
        position += numBytes;

        long value = buffer[position] & 0xFF;
        for(int b=0; b<numBytes; b++) {
            value = (value << 8) + (buffer[--position] & 0xFF);
        }

        return value;
    }

    /**
     * Put the little endian data into the buffer
     * @param value Value to be placed into the buffer
     * @param buffer The buffer with the little endian data
     * @param position Position in the buffer
     * @param numBytes Number of bytes to be converted from the long
     */
    private static void putLE(long value, byte[] buffer, int position, int numBytes) {
        for(int b=0; b<numBytes; b++) {
            buffer[position] = (byte)(value & 0xFF);
            value >>= 8;
            ++position;
        }
    }

    /**
     * Get the number of frames left to read or write.
     * @return The number of frames that have not been processed
     */
    private long getFramesRemaining() {
        return numFrames - frameCounter;
    }

    /**
     * Read audio data from the wav file.
     * @param sampleBuffer Buffer to hold sample data
     * @param numFramesToRead Maximum number of frames to read
     * @return The number of frames actually read
     * @throws IOException Thrown if an input error is encountered
     */
    private int readFrames(double[][] sampleBuffer, int numFramesToRead) throws IOException {
        if(ioState != IOState.READING) {
            throw new IOException("Cannot read from WavFile instance");
        }

        int offset = 0;
        numFramesToRead = (int)Math.min(numFramesToRead, getFramesRemaining());
        for(int frame=0; frame<numFramesToRead; ++frame) {
            for(int channel=0; channel<numChannels; ++channel) {
                sampleBuffer[channel][offset] = floatOffset + (double)readSample() / floatScale;
            }
            ++offset;
            ++frameCounter;
        }

        return numFramesToRead;
    }

    /**
     * Reads one audio sample from the wav file.
     * @return The value of the audio sample
     * @throws IOException Thrown if an input error is encountered
     */
    private long readSample() throws IOException {
        long value = 0;

        for(int b=0; b<bytesPerSample; b++) {
            if(bufferPointer == bytesRead) {
                int read = iStream.read(innerBuffer, 0, BUFFER_SIZE);
                if(read == -1) {
                    throw new RuntimeException("Not enough data available");
                }
                bytesRead = read;
                bufferPointer = 0;
            }

            int val = innerBuffer[bufferPointer];
            if(b<bytesPerSample-1 || bytesPerSample==1) {
                val &= 0xFF;
            }
            value += val << (b * 8);

            bufferPointer++;
        }

        return value;
    }

    /**
     * Write audio data to the wav file.
     * @param sampleBuffer Samples to be written to the file
     * @param numFramesToWrite Maximum number of frames to write
     * @return The number of frames actually written
     * @throws IOException Thrown if an output error is encountered
     */
    private int writeFrames(double[] sampleBuffer, int numFramesToWrite) throws IOException {
        if(ioState != IOState.WRITING) {
            throw new IOException("Cannot write to WavFile instance");
        }

        int offset = 0;
        numFramesToWrite = (int)Math.min(numFramesToWrite, getFramesRemaining());
        for(int frame=0; frame<numFramesToWrite; ++frame) {
            for(int channel=0; channel<numChannels; ++channel) {
                writeSample((long)(floatScale * (floatOffset + sampleBuffer[offset])));
                ++offset;
            }
            ++frameCounter;
        }

        return numFramesToWrite;
    }

    /**
     * Writes one audio sample to the wav file.
     * @param value The value of the audio sample
     * @throws IOException Thrown if an output error is encountered
     */
    private void writeSample(long value) throws IOException {
        for(int b=0; b<bytesPerSample; b++) {
            if(bufferPointer==BUFFER_SIZE) {
                oStream.write(innerBuffer, 0, BUFFER_SIZE);
                bufferPointer = 0;
            }

            innerBuffer[bufferPointer] = (byte)(value & 0xFF);
            value >>= 8;
            ++bufferPointer;
        }
    }

}