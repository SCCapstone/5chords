package com.five_chords.chord_builder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class soundHandlerMidi {

    int channel;

    // MIDI file header
    int header[] = new int[] {};

    // Standard MIDI file footer
    int footer[] = new int[] {
            0x01, 0xFF, 0x2F, 0x00
    };

    // "MTrk" defines a new track chunk
    int MTrk[] = new int[] {
            0x4d, 0x54, 0x72, 0x6B
    };

    // Vectors to hold our MIDI events and tracks
    private Vector<int[]> midiEvents;
    private Vector< Vector<int[]> > midiTracks;

    public void writeToFile (String filename) throws IOException {

        // open midi file
        FileOutputStream fos = new FileOutputStream (filename);

        // write midi header
        fos.write (intArrayToByteArray(header));

        for (int i = 0; i < midiTracks.size(); i++) {
            fos.write(intArrayToByteArray(MTrk));

            midiEvents = midiTracks.get(i);

            // calculate the size of track data
            int size = footer.length;
            for (int j = 0; j < midiEvents.size(); j++) {
                size += midiEvents.elementAt(j).length;
            }

            // write size of track data in big-endian format
            int high = size / 256;
            int low = size - (high * 256);
            fos.write ((byte) 0);
            fos.write ((byte) 0);
            fos.write ((byte) high);
            fos.write((byte) low);

            for (int j = 0; j < midiEvents.size(); j++) {
                fos.write (intArrayToByteArray(midiEvents.elementAt(j)));
            }

            fos.write(intArrayToByteArray(footer));
        }

        // close file
        fos.close();
    }

    // Convert an array of integers into an array of bytes
    public byte[] intArrayToByteArray (int[] ints) {
        int l = ints.length;
        byte[] out = new byte[ints.length];
        for (int i = 0; i < l; i++) {
            out[i] = (byte) ints[i];
        }
        return out;
    }

    /***********************************************
     * Set the MIDI file header
     *
     * @param tracks: number of tracks
     * @param format: 0 single track
     *                1 multi track
     *                2 multi song
     */
    public void setHeader (int tracks, int format) {
        header = new int[] {
                0x4d, 0x54, 0x68, 0x64, // "MThd"
                0x00, 0x00, 0x00, 0x06, // header length, always 6 bytes
                0x00, format, // MIDI tracks format
                0x00, tracks, // number of tracks
                0x00, 0x10 // 16 ticks per quarter
        };
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    // create a note-on MIDI event
    public void noteOn (int delta, int note, int velocity) {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x90 + channel;
        data[2] = note;
        data[3] = velocity;
        midiEvents.add (data);
    }

    // create a note-off MIDI event
    public void noteOff (int delta, int note) {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x80 + channel;
        data[2] = note;
        data[3] = 0;
        midiEvents.add(data);
    }

    // create a bend pitch MIDI event
    public void bendPitch (int msig, int lsig) {
        int[] data = new int[4];
        data[0] = 0;
        data[1] = 0xE0 + channel;
        data[2] = lsig;
        data[3] = msig;
        midiEvents.add(data);
    }

    // create a program-change MIDI event (instruments)
    public void progChange (int prog) {
        int[] data = new int[3];
        data[0] = 0;
        data[1] = 0xC0 + channel;
        data[2] = prog;
        midiEvents.add(data);
    }

    public void commitTrack() {
        midiTracks.add(midiEvents);
        midiEvents = new Vector<>();
    }

    public void newMidi(int tracks, int format) {
        setHeader(tracks, format);
        midiEvents = new Vector<>();
        midiTracks = new Vector<>();
    }

}
