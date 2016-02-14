/***************************************************************************************************
 * soundHandlerMidi.java
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class soundHandlerMidi {

    // Standard MIDI file header
    int header[] = new int[] {
        0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
        0x00, 0x00, // single-track format
        0x00, 0x01, // one track
        0x00, 0x10, // 16 ticks per quarter
        0x4d, 0x54, 0x72, 0x6B
    };

    // Standard MIDI file footer
    int footer[] = new int[] {
        0x01, 0xFF, 0x2F, 0x00
    };

    // A Vector to hold our MIDI events
    Vector<int[]> midiEvents;

    public soundHandlerMidi()
    {
        midiEvents = new Vector<int[]>();
    }

    public void writeToFile (String filename) throws IOException
    {
        // open midi file
        FileOutputStream fos = new FileOutputStream (filename);

        // write midi header
        fos.write (intArrayToByteArray (header));

        // calculate the size of track data (excluding header)
        int size = footer.length;
        for (int i = 0; i < midiEvents.size(); i++) {
            size += midiEvents.elementAt(i).length;
        }

        // write size of track data in big-endian format
        int high = size / 256;
        int low = size - (high * 256);
        fos.write ((byte) 0);
        fos.write ((byte) 0);
        fos.write ((byte) high);
        fos.write ((byte) low);

        // Write MIDI events
        for (int i = 0; i < midiEvents.size(); i++) {
            fos.write (intArrayToByteArray (midiEvents.elementAt(i)));
        }

        // write the footer and close file
        fos.write(intArrayToByteArray(footer));
        fos.close();
    }

    // Convert an array of integers into an array of bytes
    public byte[] intArrayToByteArray (int[] ints)
    {
        int l = ints.length;
        byte[] out = new byte[ints.length];
        for (int i = 0; i < l; i++) {
            out[i] = (byte) ints[i];
        }
        return out;
    }

    // create a note-on MIDI event
    public void noteOn (int delta, int note, int velocity)
    {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x90;
        data[2] = note;
        data[3] = velocity;
        midiEvents.add (data);
    }

    // create a note-off MIDI event
    public void noteOff (int delta, int note)
    {
        int[] data = new int[4];
        data[0] = delta;
        data[1] = 0x80;
        data[2] = note;
        data[3] = 0;
        midiEvents.add(data);
    }

    // create a bend pitch MIDI event
    public void bendPitch (int msig, int lsig)
    {
        int[] data = new int[4];
        data[0] = 0;
        data[1] = 0xE0;
        data[2] = lsig;
        data[3] = msig;
        midiEvents.add(data);
    }

    // create a program-change MIDI event (instruments)
    public void progChange (int prog)
    {
        int[] data = new int[3];
        data[0] = 0;
        data[1] = 0xC0;
        data[2] = prog;
        midiEvents.add(data);
    }

    // create a Midi file from provided input
    public void createMidi (String s, int inst, int delta, int note, int veloc, int pitch) throws Exception
    {
        soundHandlerMidi midi = new soundHandlerMidi();
        midi.progChange(inst);

        int msb = (pitch >> 7) & 0x7F;
        int lsb = pitch & 0x7F;

        midi.bendPitch(msb, lsb);

        midi.noteOn(0, note, veloc);
        midi.noteOff(delta, note);

        midi.writeToFile(s);
    }

}
