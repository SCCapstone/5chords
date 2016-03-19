/***************************************************************************************************
 * soundHandler.java
 * @version 1.1
 * @date 14 February 2015
 * @author: Drea,Zach
 **/
package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

public class soundHandler extends MainActivity
{
    /** The tag for this class. */
    private static final String TAG = "soundHandler";

    static final int FULL_VOLUME = 127;
    static final int SUSTAIN_NOTE = 128;
    static final int RELEASE_NOTE = 0;
    static final int NOTE_OFFSET = 60;

    static final int TRUMPET = 57;
    static final int PIANO = 21;
    static final int ORGAN = 20;
    static final int GUITAR = 25;
    static final int VIOLIN = 41;
    static final int FLUTE = 74;

    static MediaPlayer mediaPlayer;
    static soundHandlerMidi midi;
    static String midiFile;
    static int instrument = PIANO;

    /**
     * Static class.
     */
    private soundHandler()
    {   }

    /**
     * Called to initialize the SoundHandler.
     */
    public static void initialize(Activity main)
    {
        midiFile = main.getFilesDir() + "/midi.mid";
        midi = new soundHandlerMidi();
    }

    public static void stopSound() {
        Log.d(TAG, "Stop Sound");
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }
    }

    /****************************************************************
     * Plays a note
     **/
    public static void addNote(int note, int pitch, int channel)
    {
        // TODO: use the sliders to change pitch of note
        //       replace 8192 (centered) with the new pitch
        int mostSignificantbits = (pitch >> 7) & 0x7F;
        int leastSignificantbits = pitch & 0x7F;

        midi.setChannel(channel);
        midi.progChange(instrument);
        midi.bendPitch(mostSignificantbits, leastSignificantbits);
        midi.noteOn(RELEASE_NOTE, note + NOTE_OFFSET, FULL_VOLUME);
        midi.noteOff(SUSTAIN_NOTE, note + NOTE_OFFSET);
        midi.commitTrack();
    }

    /****************************************************************
     * Plays a chord
     **/
    public static void playChord(Activity activity, int[] chord)
    {
        stopSound();

        midi.newMidi(chord.length, 1);
        for (int i=0; i<chord.length; i++) addNote(chord[i], 8192, i);

        try
        {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(activity, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        catch (Exception e)
        { /* Ignored */ }

        Log.d(TAG, "Done Playing Chord");
    }

    /****************************************************************
     * Plays a chord
     **/
    public static void playNote(Activity activity, int note)
    {
        stopSound();

        midi.newMidi(1, 0);
        addNote(note, 8192, 1);

        try
        {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(activity, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        catch (Exception e)
        { /* Ignored */ }

        Log.d(TAG, "Done Playing Note");
    }


    public static void switchInstrument(int i)
    {
        if(i == 0) instrument = TRUMPET;
        if(i == 1) instrument = PIANO;
        if(i == 2) instrument = ORGAN;
        if(i == 3) instrument = GUITAR;
        if(i == 4) instrument = VIOLIN;
        if(i == 5) instrument = FLUTE;
    }

    public static int getInstrument() {
        return instrument;
    }
}
