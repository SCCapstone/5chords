package com.five_chords.chord_builder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Class representing a MIDI sound file. This class contains functionality to create and play MIDI sound files.
 * @version 1.1
 * @date 3 April 2016
 * @author Drea, Zach, Theodore
 */
public class SoundHandlerMidi
{
    /** The channel of this soundHandlerMidi. */
    private int channel;

    /** The MIDI file header. */
    private int header[] = new int[] {};

    /** The standard MIDI file footer. */
    private int footer[] = new int[] {0x01, 0xFF, 0x2F, 0x00};

    /** Definition for a new track chunk. */
    private int MTrk[] = new int[] {0x4d, 0x54, 0x72, 0x6B};

    /** Vector to hold the MIDI events. */
    private Vector<int[]> midiEvents;

    /** Vector to hold the MIDI tracks. */
    private Vector< Vector<int[]> > midiTracks;

    /**
     * Converts an array of integers into an array of bytes.
     * @param ints The int array to convert
     * @return A new byte array created by casting each int of the int array to a byte
     */
    public static byte[] intArrayToByteArray (int[] ints)
    {
        int l = ints.length;
        byte[] out = new byte[ints.length];

        for (int i = 0; i < l; i++) {
            out[i] = (byte) ints[i];
        }
        return out;
    }

    /**
     * Writes this soundHandlerMidi to a file.
     * @param filename The name of the file to write to
     * @throws IOException If there is an error writing to the file
     */
    public void writeToFile (String filename) throws IOException
    {
        // Open midi file
        FileOutputStream fos = new FileOutputStream (filename);

        // Write midi header
        fos.write (intArrayToByteArray(header));

        // Write each track
        for (int i = 0; i < midiTracks.size(); i++) {
            fos.write(intArrayToByteArray(MTrk));

            midiEvents = midiTracks.get(i);

            // Calculate the size of track data
            int size = footer.length;
            for (int j = 0; j < midiEvents.size(); j++) {
                size += midiEvents.elementAt(j).length;
            }

            // Write size of track data in big-endian format
            int high = size / 256;
            int low = size - (high * 256);
            fos.write ((byte) 0);
            fos.write ((byte) 0);
            fos.write ((byte) high);
            fos.write((byte) low);

            // Write the events of the track
            for (int j = 0; j < midiEvents.size(); j++) {
                fos.write (intArrayToByteArray(midiEvents.elementAt(j)));
            }

            // Write the footer
            fos.write(intArrayToByteArray(footer));
        }

        // Close the file
        fos.close();
    }

    /**
     * Sets the MIDI file header.
     *
     * @param tracks The number of tracks
     * @param format 0 for single track, 1 for multi track, or 2 for multi song
     */
    public void setHeader (int tracks, int format)
    {
        header = new int[]
        {
                0x4d, 0x54, 0x68, 0x64, // "MThd"
                0x00, 0x00, 0x00, 0x06, // header length, always 6 bytes
                0x00, format, // MIDI tracks format
                0x00, tracks, // number of tracks
                0x00, 0x10 // 16 ticks per quarter
        };
    }

    /**
     * Sets the current channel in the MIDI file.
     * @param channel The new channel
     */
    public void setChannel(int channel)
    {
        this.channel = channel;
    }

    /**
     * Creates a note-on MIDI event.
     * @param delta The delta value
     * @param note The note to play
     * @param velocity The note velocity
     */
    public void noteOn (int delta, int note, int velocity)
    {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x90 + channel;
        data[2] = note;
        data[3] = velocity;
        midiEvents.add (data);
    }

    /**
     * Creates a note-off MIDI event.
     * @param delta The delta value
     * @param note The note to stop
     */
    public void noteOff (int delta, int note)
    {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x80 + channel;
        data[2] = note;
        data[3] = 0;
        midiEvents.add(data);
    }

    /**
     * Creates a bend pitch MIDI event.
     * @param msig The most significant byte of the MIDI pitch bend amount
     * @param lsig The least significant byte of the MIDI pitch bend amount
     */
    public void bendPitch (int msig, int lsig)
    {
        int[] data = new int[4];
        data[0] = 0;
        data[1] = 0xE0 + channel;
        data[2] = lsig;
        data[3] = msig;
        midiEvents.add(data);
    }

    /**
     * Creates a program-change MIDI event (instruments).
     * @param prog The new program
     */
    public void progChange (int prog)
    {
        int[] data = new int[3];
        data[0] = 0;
        data[1] = 0xC0 + channel;
        data[2] = prog;
        midiEvents.add(data);
    }

    /**
     * Commits the current MIDI track.
     */
    public void commitTrack()
    {
        midiTracks.add(midiEvents);
        midiEvents = new Vector<>();
    }

    /**
     * Re-initializes this soundHandlerMidi.
     * @param tracks The number of tracks to include
     * @param format The format of the MIDI file
     */
    public void newMidi(int tracks, int format)
    {
        setHeader(tracks, format);
        midiEvents = new Vector<>();
        midiTracks = new Vector<>();
    }
}
